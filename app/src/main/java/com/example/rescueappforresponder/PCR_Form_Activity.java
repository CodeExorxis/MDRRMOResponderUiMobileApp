package com.example.rescueappforresponder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescueappforresponder.databinding.ActivityPcrFormBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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



        // Time Picker for "Time of Call"
        root.edtTimeOfCall.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // digital style
                    (view, selectedHour, selectedMinute) -> {
                        // Format to 12-hour with AM/PM
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        cal.set(Calendar.MINUTE, selectedMinute);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        String formattedTime = sdf.format(cal.getTime());

                        root.edtTimeOfCall.setText(formattedTime);
                    },
                    hour,
                    minute,
                    false
            );

            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });

        // Time Picker for "Time of Call"
        root.edtTimeActivated.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // digital style
                    (view, selectedHour, selectedMinute) -> {
                        // Format to 12-hour with AM/PM
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        cal.set(Calendar.MINUTE, selectedMinute);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        String formattedTime = sdf.format(cal.getTime());

                        root.edtTimeActivated.setText(formattedTime);
                    },
                    hour,
                    minute,
                    false
            );

            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });

        // This is the Date Picker for Date of Response
        root.edtDateOfResponse.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, selectedYear);
                        cal.set(Calendar.MONTH, selectedMonth);
                        cal.set(Calendar.DAY_OF_MONTH, selectedDay);

                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        root.edtDateOfResponse.setText(sdf.format(cal.getTime()));
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.setTitle("Select Date");
            datePickerDialog.show();
        });
                                    //The radio button "Others method of Inform By" to show and collect user input
        root.RbOthers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                root.edtOtherInformedBy.setVisibility(View.VISIBLE);
            } else {
                root.edtOtherInformedBy.setVisibility(View.GONE);
            }
        });
                                //The radio button "Others method of Inform Through" to show and collect user input
        root.RbOtherInformThrough.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                root.edtOtherInformThrough.setVisibility(View.VISIBLE);
            } else {
                root.edtOtherInformThrough.setVisibility(View.GONE);
            }
        });
                    //Patient Information Layout

        // Toggle for Patient Information
        root.togglePatientInfo.setOnClickListener(v -> {
            if (root.layoutPatientInfo.getVisibility() == View.GONE) {
                root.layoutPatientInfo.setVisibility(View.VISIBLE);
                root.togglePatientInfo.setImageResource(R.drawable.ic_drop_up);
            } else {
                root.layoutPatientInfo.setVisibility(View.GONE);
                root.togglePatientInfo.setImageResource(R.drawable.ic_drop_down);
            }
        });

        String[] sexOptions = {"Male", "Female", "Other", "Prefer not to say"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                sexOptions
        );

        root.spinnerSex.setAdapter(adapter);

        // This is the Date Picker for Date of Response
        root.edtDateBirthPatientInfo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, selectedYear);
                        cal.set(Calendar.MONTH, selectedMonth);
                        cal.set(Calendar.DAY_OF_MONTH, selectedDay);

                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        root.edtDateBirthPatientInfo.setText(sdf.format(cal.getTime()));
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.setTitle("Select Date");
            datePickerDialog.show();
        });

    }
}