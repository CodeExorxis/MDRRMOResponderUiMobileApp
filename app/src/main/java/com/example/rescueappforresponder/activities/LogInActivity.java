package com.example.rescueappforresponder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rescueappforresponder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Views
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btn_login);

        // Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtUsername.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password is required");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            ensureUserDocument(user); // make sure Firestore doc exists
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error.";
                        Toast.makeText(LogInActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void ensureUserDocument(FirebaseUser user) {
        db.collection("mdrrmo-users").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc != null && doc.exists()) {
                            checkUserRole(doc);
                        } else {
                            // Create a new user doc if not exists
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("uid", user.getUid());
                            userData.put("email", user.getEmail());
                            userData.put("role", "responder"); // Default role if not set
                            userData.put("status", "Active");

                            db.collection("mdrrmo-users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> checkUserRoleFromUid(user.getUid()))
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LogInActivity.this, "Error creating user doc: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                    });
                        }
                    } else {
                        Toast.makeText(LogInActivity.this, "Error fetching user doc.", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                });
    }

    private void checkUserRoleFromUid(String uid) {
        db.collection("mdrrmo-users").document(uid).get()
                .addOnSuccessListener(this::checkUserRole)
                .addOnFailureListener(e -> {
                    Toast.makeText(LogInActivity.this, "Error checking role: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                });
    }

    private void checkUserRole(DocumentSnapshot document) {
        String role = document.getString("role");
        if ("responder".equals(role)) {
            Toast.makeText(LogInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LogInActivity.this, "Access denied. Only responders can log in.", Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            ensureUserDocument(currentUser);
        }
    }
}
