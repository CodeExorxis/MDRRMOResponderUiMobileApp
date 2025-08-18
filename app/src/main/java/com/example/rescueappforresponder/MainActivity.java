package com.example.rescueappforresponder;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescueappforresponder.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {

    // Declare binding
    private ActivityMainBinding root;
    String selectedStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());

        //showing the status report category
        root.btnStatusReport.setOnClickListener(v -> {
            root.statusReportCategories.setVisibility(View.VISIBLE);
            root.btnStatusReport.setVisibility(View.GONE);
            root.btnPcrReport.setVisibility(View.GONE);
        });
        //Showing the PCR report category
        root.btnPcrReport.setOnClickListener(v -> {
            root.PCRFormCategory.setVisibility(View.VISIBLE);
            root.btnStatusReport.setVisibility(View.GONE);
            root.btnPcrReport.setVisibility(View.GONE);
        });

        // --- Vehicular options ---
        root.btnOnScene.setOnClickListener(v -> {
            selectedStatus = "On Scene";
            resetSelectectedStatus();
            root.btnOnScene.setBackgroundResource(R.drawable.btn_selected_flat);
        });

        root.btnTransferPatient.setOnClickListener(v -> {
            selectedStatus = "Transferring Patient";
            resetSelectectedStatus();
            root.btnTransferPatient.setBackgroundResource(R.drawable.btn_selected_flat);
        });

        root.btnCompleted.setOnClickListener(v -> {
            selectedStatus = "Completed";
            resetSelectectedStatus();
            root.btnCompleted.setBackgroundResource(R.drawable.btn_selected_flat);
        });

        root.btnSubmitStatusReport.setOnClickListener(v -> {
            if (selectedStatus.isEmpty()) {
                Toast.makeText(this, "Please select a vehicular condition.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Submitted: " + selectedStatus, Toast.LENGTH_SHORT).show();
            root.statusReportCategories.setVisibility(View.GONE);
            root.btnStatusReport.setVisibility(View.VISIBLE);
            root.btnPcrReport.setVisibility(View.VISIBLE);
            resetSelectectedStatus();
            selectedStatus = "";
        });
    }
    private void resetSelectectedStatus() {
        root.btnOnScene.setBackgroundResource(R.drawable.btn_flat_red);
        root.btnTransferPatient.setBackgroundResource(R.drawable.btn_flat_red);
        root.btnCompleted.setBackgroundResource(R.drawable.btn_flat_red);
    }
}