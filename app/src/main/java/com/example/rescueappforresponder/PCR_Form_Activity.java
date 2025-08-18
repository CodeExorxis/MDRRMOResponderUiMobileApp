package com.example.rescueappforresponder;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescueappforresponder.databinding.ActivityPcrFormBinding;

public class PCR_Form_Activity extends AppCompatActivity {

   ActivityPcrFormBinding root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityPcrFormBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());

        // Toggle for Call Information
        root.toggleCallInfo.setOnClickListener(v -> {
            if (root.layoutCallInfo.getVisibility() == View.GONE) {
                root.layoutCallInfo.setVisibility(View.VISIBLE);
                root.toggleCallInfo.setImageResource(R.drawable.ic_drop_up);
            } else {
                root.layoutCallInfo.setVisibility(View.GONE);
                root.toggleCallInfo.setImageResource(R.drawable.ic_drop_down);
            }
        });


    }
}