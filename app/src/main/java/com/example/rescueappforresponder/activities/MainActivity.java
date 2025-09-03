package com.example.rescueappforresponder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rescueappforresponder.R;
import com.example.rescueappforresponder.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding root;
    private String selectedStatus = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // --- REPLACED: FusedLocationProviderClient with LocationManager ---
    private LocationManager locationManager;
    // -------------------------------------------------------------------

    private WebView mapWebView;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration incidentListener;

    // Queue for incidents
    private final Queue<IncidentData> incidentQueue = new LinkedList<>();
    private boolean isDialogShowing = false;

    // Activity visibility
    private boolean isActivityVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "Starting MainActivity onCreate");

            root = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(root.getRoot());

            // Initialize Firebase
            initializeFirebase();

            // Setup WebView with error handling
            setupWebView();

            // --- REPLACED: FusedLocationProviderClient with LocationManager ---
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            checkMapReadinessAndAddInitialMarkers();
            // -------------------------------------------------------------------

            // Button setup
            setupButtons();

            Log.d(TAG, "MainActivity onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "App initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); // Close the activity if initialization fails
        }
    }

    private void initializeFirebase() {
        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "User is authenticated: " + currentUser.getUid());
                listenForIncidentReports();
            } else {
                Log.w(TAG, "User is not authenticated");
                Toast.makeText(this, "Please login to receive incident reports", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: ", e);
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    private void setupWebView() {
        try {
            mapWebView = findViewById(R.id.mapWebView);
            if (mapWebView != null) {
                WebSettings webSettings = mapWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);

                mapWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.d(TAG, "Map page finished loading.");
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        Log.e(TAG, "WebView error: " + description + " for URL: " + failingUrl);
                    }
                });

                mapWebView.loadUrl("file:///android_asset/map.html");
            } else {
                Log.e(TAG, "MapWebView is null - check your layout file");
            }
        } catch (Exception e) {
            Log.e(TAG, "WebView setup failed: ", e);
        }
    }

    private void checkMapReadinessAndAddInitialMarkers() {
        if (mapWebView != null) {
            mapWebView.evaluateJavascript("javascript:isMapReady()", isReady -> {
                if (Boolean.parseBoolean(isReady)) {
                    Log.d(TAG, "Map is ready. Adding initial markers and getting location.");
                    // Now it's safe to get the user's location and add markers
                    getUserLocation();
                    listenForIncidentReports();
                } else {
                    Log.d(TAG, "Map not ready, retrying in 500ms.");
                    // Retry after a short delay
                    mapWebView.postDelayed(this::checkMapReadinessAndAddInitialMarkers, 500);
                }
            });
        }
    }

    private void setupButtons() {
        try {
            if (root.btnStatusReport != null) {
                root.btnStatusReport.setOnClickListener(v -> {
                    if (root.statusReportCategories != null) {
                        root.statusReportCategories.setVisibility(View.VISIBLE);
                        root.btnStatusReport.setVisibility(View.GONE);
                        if (root.btnPcrReport != null) root.btnPcrReport.setVisibility(View.GONE);
                    }
                });
            }

            if (root.btnPcrReport != null) {
                root.btnPcrReport.setOnClickListener(v -> {
                    if (root.PCRFormCategory != null) {
                        root.PCRFormCategory.setVisibility(View.VISIBLE);
                        if (root.btnStatusReport != null) root.btnStatusReport.setVisibility(View.GONE);
                        root.btnPcrReport.setVisibility(View.GONE);
                    }
                });
            }

            if (root.btnOnScene != null) root.btnOnScene.setOnClickListener(v -> selectStatus("On Scene", root.btnOnScene));
            if (root.btnTransferPatient != null) root.btnTransferPatient.setOnClickListener(v -> selectStatus("Transferring Patient", root.btnTransferPatient));
            if (root.btnCompleted != null) root.btnCompleted.setOnClickListener(v -> selectStatus("Completed", root.btnCompleted));

            if (root.btnSubmitStatusReport != null) {
                root.btnSubmitStatusReport.setOnClickListener(v -> {
                    if (selectedStatus.isEmpty()) {
                        Toast.makeText(this, "Please select a Status Report.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, "Submitted: " + selectedStatus, Toast.LENGTH_SHORT).show();
                    if (root.statusReportCategories != null) root.statusReportCategories.setVisibility(View.GONE);
                    if (root.btnStatusReport != null) root.btnStatusReport.setVisibility(View.VISIBLE);
                    if (root.btnPcrReport != null) root.btnPcrReport.setVisibility(View.VISIBLE);
                    resetSelectedStatus();
                    selectedStatus = "";
                });
            }

            if (root.btnPCRForm != null) root.btnPCRForm.setOnClickListener(v -> {
                try { startActivity(new Intent(MainActivity.this, PCR_Form_Activity.class)); }
                catch (Exception e) { Log.e(TAG, "Error starting PCR Form Activity: ", e); Toast.makeText(this, "Error opening PCR Form", Toast.LENGTH_SHORT).show(); }
            });

            if (root.btnRefusalForm != null) root.btnRefusalForm.setOnClickListener(v -> {
                try { startActivity(new Intent(MainActivity.this, transport_refusal_Activity.class)); }
                catch (Exception e) { Log.e(TAG, "Error starting Refusal Form Activity: ", e); Toast.makeText(this, "Error opening Refusal Form", Toast.LENGTH_SHORT).show(); }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up buttons: ", e);
        }
    }

    private void selectStatus(String status, View button) {
        try {
            selectedStatus = status;
            resetSelectedStatus();
            if (button != null) button.setBackgroundResource(R.drawable.btn_selected_flat);
        } catch (Exception e) {
            Log.e(TAG, "Error selecting status: ", e);
        }
    }

    private void resetSelectedStatus() {
        try {
            if (root.btnOnScene != null) root.btnOnScene.setBackgroundResource(R.drawable.btn_flat_red);
            if (root.btnTransferPatient != null) root.btnTransferPatient.setBackgroundResource(R.drawable.btn_flat_red);
            if (root.btnCompleted != null) root.btnCompleted.setBackgroundResource(R.drawable.btn_flat_red);
            if (root.btnPCRForm != null) root.btnPCRForm.setBackgroundResource(R.drawable.btn_flat_red);
            if (root.btnRefusalForm != null) root.btnRefusalForm.setBackgroundResource(R.drawable.btn_flat_red);
        } catch (Exception e) {
            Log.e(TAG, "Error resetting status: ", e);
        }
    }

    private void checkLocationPermission() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Show explanation dialog
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Location Permission Required")
                            .setMessage("This app needs location permission to show your position on the map.")
                            .setPositiveButton("Grant Permission", (dialog, which) -> {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_CODE);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                Toast.makeText(this, "Location features will be limited", Toast.LENGTH_SHORT).show();
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                getUserLocation();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking location permission: ", e);
        }
    }

    // --- NEW: getUserLocation using LocationManager ---
    private void getUserLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Location permissions not granted");
                return;
            }

            if (locationManager == null) {
                Log.e(TAG, "LocationManager is null");
                return;
            }

            Location lastKnownLocation = null;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d(TAG, "Trying GPS provider");
            }

            if (lastKnownLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d(TAG, "Trying Network provider");
            }

            if (lastKnownLocation != null) {
                Log.d(TAG, "Location obtained: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());
                updateMapLocation(lastKnownLocation);
            } else {
                requestLocationUpdate();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting user location: ", e);
        }
    }

    // --- NEW: requestLocationUpdate method ---
    private void requestLocationUpdate() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "New location received: " + location.getLatitude() + ", " + location.getLongitude());
                    updateMapLocation(location);
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d(TAG, "Provider enabled: " + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d(TAG, "Provider disabled: " + provider);
                }
            };

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000, // 5 seconds
                        10,   // 10 meters
                        locationListener
                );
                Log.d(TAG, "Requesting GPS location updates");
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000, // 5 seconds
                        10,   // 10 meters
                        locationListener
                );
                Log.d(TAG, "Requesting Network location updates");
            } else {
                Log.w(TAG, "No location providers available");
                Toast.makeText(this, "Please enable GPS or Network location", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error requesting location update: ", e);
        }
    }

    // --- REPLACED: updateMapLocation with the new version ---
    private void updateMapLocation(Location location) {
        try {
            if (mapWebView != null && location != null) {
                String js = "javascript:updateLocation(" + location.getLatitude() + "," + location.getLongitude() + ")";
                mapWebView.post(() -> {
                    try {
                        mapWebView.evaluateJavascript(js, result -> {
                            Log.d(TAG, "Location updated on map: " + location.getLatitude() + ", " + location.getLongitude());
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating map location: ", e);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateMapLocation: ", e);
        }
    }
    // -----------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkMapReadinessAndAddInitialMarkers();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
        showNextIncidentDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    private void listenForIncidentReports() {
        if (db == null) return;

        incidentListener = db.collection("incidents")
                .whereIn("status", Arrays.asList("Pending", "Acknowledged", "In Progress"))
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String documentId = dc.getDocument().getId();
                            String status = dc.getDocument().getString("status");

                            Map<String, Object> locationMap = (Map<String, Object>) dc.getDocument().get("location");

                            double lat = 0.0, lng = 0.0;
                            if (locationMap != null) {
                                try {
                                    // Now safely get the lat and lng from the location map
                                    lat = (double) locationMap.get("lat");
                                    lng = (double) locationMap.get("lng");
                                } catch (Exception e) {
                                    Log.e(TAG, "Failed to parse lat/lng from location map", e);
                                }
                            }

                            String type = dc.getDocument().getString("emergencyType");
                            String severity = dc.getDocument().getString("emergencySeverity");
                            if (type == null) type = "Unknown";
                            if (severity == null) severity = "N/A";

                            // Escape quotes for JavaScript
                            type = type.replace("'", "\\'");
                            severity = severity.replace("'", "\\'");
                            status = status != null ? status.replace("'", "\\'") : "Unknown";

                            // Log all incoming data for debugging
                            Log.d(TAG, "Document change detected: " + dc.getType() + " for incident ID: " + documentId + ", Status: " + status);
                            Log.d(TAG, "Coordinates: lat=" + lat + ", lng=" + lng);

                            switch (dc.getType()) {
                                case ADDED:
                                case MODIFIED:
                                    // Check if coordinates are valid before adding or updating a marker
                                    if (lat != 0.0 && lng != 0.0 && mapWebView != null) {
                                        addIncidentMarkerToMap(documentId, lat, lng, type, severity, status);
                                    } else {
                                        Log.e(TAG, "Skipping marker creation for incident " + documentId + ". Invalid coordinates.");
                                    }

                                    // Only process new incidents for the dialog queue
                                    if ("Pending".equals(status)) {
                                        String latStr = dc.getDocument().getString("latitude");
                                        String lngStr = dc.getDocument().getString("longitude");
                                        incidentQueue.add(new IncidentData(
                                                documentId, type, severity,
                                                dc.getDocument().getString("reporter"),
                                                dc.getDocument().getString("locationText"),
                                                dc.getDocument().getString("notes"),
                                                latStr, lngStr
                                        ));
                                        if (isActivityVisible) showNextIncidentDialog();
                                    }
                                    break;

                                case REMOVED:
                                    removeIncidentMarkerFromMap(documentId);
                                    break;
                            }
                        }
                    }
                });
    }

    private void addIncidentMarkerToMap(String incidentId, double lat, double lng, String type, String severity, String status) {
        if (mapWebView != null) {
            String js = "javascript:addIncidentMarker(" + lat + "," + lng + ",'" + type + "','" + severity + "','" + status + "','" + incidentId + "')";
            mapWebView.post(() -> {
                try {
                    mapWebView.evaluateJavascript(js, result -> {
                        if (result != null) {
                            Log.d(TAG, "Marker added successfully for incident: " + incidentId);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error adding incident marker: ", e);
                }
            });
        }
    }

    // 5. Method to remove incident markers (optional)
    private void removeIncidentMarkerFromMap(String incidentId) {
        if (mapWebView != null) {
            String js = "javascript:removeIncidentMarker('" + incidentId + "')";
            mapWebView.post(() -> {
                try {
                    mapWebView.evaluateJavascript(js, null);
                    Log.d(TAG, "Marker removed for incident: " + incidentId);
                } catch (Exception e) {
                    Log.e(TAG, "Error removing incident marker: ", e);
                }
            });
        }
    }

    private void showNextIncidentDialog() {
        if (!isActivityVisible || isDialogShowing || incidentQueue.isEmpty()) return;

        isDialogShowing = true;
        IncidentData incident = incidentQueue.poll();
        if (incident == null) {
            isDialogShowing = false;
            return;
        }

        try {
            // Vibration feedback
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(500);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error with vibration: ", e);
        }

        if (!isFinishing() && !isDestroyed()) {
            new AlertDialog.Builder(this)
                    .setTitle("🚨 New Incident Report")
                    .setMessage("Type: " + incident.type +
                            "\nSeverity: " + incident.severity +
                            "\nReporter: " + incident.reporter +
                            "\nLocation: " + incident.location +
                            "\nNotes: " + incident.notes)
                    .setCancelable(false)
                    .setPositiveButton("Acknowledge", (dialog, which) -> {
                        // Update status in Firestore
                        updateIncidentStatus(incident.documentId, "Acknowledged");
                        Toast.makeText(this, "Incident acknowledged", Toast.LENGTH_SHORT).show();

                        // The marker will be automatically updated through the Firestore listener
                        // No need to manually add marker here anymore

                        isDialogShowing = false;
                        showNextIncidentDialog();
                    })
                    .setNegativeButton("Dismiss", (dialog, which) -> {
                        dialog.dismiss();
                        isDialogShowing = false;
                        showNextIncidentDialog();
                    })
                    .show();
        } else {
            isDialogShowing = false;
        }
    }

    private void updateIncidentStatus(String documentId, String newStatus) {
        if (db == null || mAuth.getCurrentUser() == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("acknowledgedAt", FieldValue.serverTimestamp());
        updates.put("acknowledgedBy", mAuth.getCurrentUser().getUid());

        db.collection("incidents").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document successfully updated: " + documentId);
                    // Update the marker status on the map
                    updateMarkerStatus(documentId, newStatus);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error updating document: " + documentId, e));
    }

    private void updateMarkerStatus(String incidentId, String newStatus) {
        if (mapWebView != null) {
            String js = "javascript:updateIncidentStatus('" + incidentId + "','" + newStatus + "')";
            mapWebView.post(() -> {
                try {
                    mapWebView.evaluateJavascript(js, null);
                    Log.d(TAG, "Updated marker status for incident: " + incidentId);
                } catch (Exception e) {
                    Log.e(TAG, "Error updating marker status: ", e);
                }
            });
        }
    }

    private static class IncidentData {
        String documentId;
        String type;
        String severity;
        String reporter;
        String location;
        String notes;
        Double latitude;
        Double longitude;

        IncidentData(String documentId, String type, String severity, String reporter,
                     String location, String notes, String latitudeStr, String longitudeStr) {
            this.documentId = documentId;
            this.type = type;
            this.severity = severity;
            this.reporter = reporter;
            this.location = location;
            this.notes = notes;
            this.latitude = safeParseDouble(latitudeStr);
            this.longitude = safeParseDouble(longitudeStr);
        }

        private static Double safeParseDouble(String val) {
            try {
                if (val == null || val.isEmpty()) return 0.0;
                return Double.parseDouble(val);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Failed to parse double: " + val);
                return 0.0;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { if (incidentListener != null) incidentListener.remove(); } catch (Exception ignored) {}
    }
}