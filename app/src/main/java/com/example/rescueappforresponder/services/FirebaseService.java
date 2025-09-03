package com.example.rescueappforresponder.services;

import com.example.rescueappforresponder.models.Incident;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {
    private static FirebaseService instance;
    private final FirebaseFirestore db;

    private FirebaseService() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    // Real-time listener for incidents assigned to a specific team
    public void listenForIncidents(String teamName, IncidentListener listener) {
        db.collection("incidents")
                .whereEqualTo("respondingTeam", teamName)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return;
                    }

                    List<Incident> incidents = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Incident incident = doc.toObject(Incident.class);
                        if (incident != null) {
                            incidents.add(incident);
                        }
                    }
                    listener.onIncidentsChanged(incidents);
                });
    }

    public interface IncidentListener {
        void onIncidentsChanged(List<Incident> incidents);
    }
}
