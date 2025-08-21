package com.example.rescueappforresponder;

import android.content.Intent;
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

        // --- Status Report Options ---
        root.btnOnScene.setOnClickListener(v -> {
            selectedStatus = "On Scene";
            resetSelectedStatus();
            root.btnOnScene.setBackgroundResource(R.drawable.btn_selected_flat);
        });

        root.btnTransferPatient.setOnClickListener(v -> {
            selectedStatus = "Transferring Patient";
            resetSelectedStatus();
            root.btnTransferPatient.setBackgroundResource(R.drawable.btn_selected_flat);
        });

        root.btnCompleted.setOnClickListener(v -> {
            selectedStatus = "Completed";
            resetSelectedStatus();
            root.btnCompleted.setBackgroundResource(R.drawable.btn_selected_flat);
        });

        root.btnSubmitStatusReport.setOnClickListener(v -> {
            if (selectedStatus.isEmpty()) {
                Toast.makeText(this, "Please select a Status Report.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Submitted: " + selectedStatus, Toast.LENGTH_SHORT).show();
            root.statusReportCategories.setVisibility(View.GONE);
            root.btnStatusReport.setVisibility(View.VISIBLE);
            root.btnPcrReport.setVisibility(View.VISIBLE);
            resetSelectedStatus();
            selectedStatus = "";
        });

        root.btnPCRForm.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PCR_Form_Activity.class);
            startActivity(intent);
        });

        root.btnRefusalForm.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, transport_refusal_Activity.class);
            startActivity(intent);
        });
    }


    private void resetSelectedStatus() {
        root.btnOnScene.setBackgroundResource(R.drawable.btn_flat_red);
        root.btnTransferPatient.setBackgroundResource(R.drawable.btn_flat_red);
        root.btnCompleted.setBackgroundResource(R.drawable.btn_flat_red);
        root.btnPCRForm.setBackgroundResource(R.drawable.btn_flat_red);
        root.btnRefusalForm.setBackgroundResource(R.drawable.btn_flat_red);
    }
}