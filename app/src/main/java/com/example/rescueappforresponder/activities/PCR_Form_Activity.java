package com.example.rescueappforresponder.activities;

import static android.widget.Toast.makeText;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescueappforresponder.R;
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


                                                        //Incident Details Layout

        // Toggle for Incident Details
        root.toggleIncidentDetails.setOnClickListener(v -> {
            if (root.layoutIncidentDetails.getVisibility() == View.GONE) {
                root.layoutIncidentDetails.setVisibility(View.VISIBLE);
                root.toggleIncidentDetails.setImageResource(R.drawable.ic_drop_up);
            } else {
                root.layoutIncidentDetails.setVisibility(View.GONE);
                root.toggleIncidentDetails.setImageResource(R.drawable.ic_drop_down);
            }
        });
                                                //Radio Group Nature of Reported Incident
        root.RbNatureOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                root.edtOtherNatureReport.setVisibility(View.VISIBLE);
            } else {
                root.edtOtherNatureReport.setVisibility(View.GONE);
            }
        });

                                                        //Radio Group Patient Found At
        root.RbFoundOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                root.edtOtherFound.setVisibility(View.VISIBLE);
            } else {
                root.edtOtherFound.setVisibility(View.GONE);
            }
        });

                                                        //Radio Group Patient Condition
        root.RbOtherPatientCondition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                root.edtOtherPatientCondition.setVisibility(View.VISIBLE);
            } else {
                root.edtOtherPatientCondition.setVisibility(View.GONE);
            }
        });


                                                            //Assessment  Layout

        // Toggle for Assessment
        root.toggleAssessment.setOnClickListener(v -> {
            if (root.layoutAssessment.getVisibility() == View.GONE) {
                root.layoutAssessment.setVisibility(View.VISIBLE);
                root.toggleAssessment.setImageResource(R.drawable.ic_drop_up);
            } else {
                root.layoutAssessment.setVisibility(View.GONE);
                root.toggleAssessment.setImageResource(R.drawable.ic_drop_down);
            }
        });


                                                             //Vital Signs  Layout

        // Toggle for Vital Signs
        root.toggleVitalSigns.setOnClickListener(v -> {
            if (root.layoutVitalSigns.getVisibility() == View.GONE) {
                root.layoutVitalSigns.setVisibility(View.VISIBLE);
                root.toggleVitalSigns.setImageResource(R.drawable.ic_drop_up);
            } else {
                root.layoutVitalSigns.setVisibility(View.GONE);
                root.toggleVitalSigns.setImageResource(R.drawable.ic_drop_down);
            }
        });

        String[] eyeOpeningOptions = {"Select", "4 - Spontaneous", "3 - To Speech", " 2 - To Pain", "1 - None"};

        ArrayAdapter<String> adapterEyeOpening = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                eyeOpeningOptions
        );

        root.spinnerEyeOpening.setAdapter(adapterEyeOpening);

        String[] verbalResponseOptions = {"Select", "5 - Oriented", "4 - Confused", "3 - Inappropriate", " 2 - Incomprehensible", "1 - None"};

        ArrayAdapter<String> adapterVerbalResponse = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                verbalResponseOptions
        );

        root.spinnerVerbalResponse.setAdapter(adapterVerbalResponse);

        String[] motorResponseOptions = {"Select", "5 - Oriented", "4 - Confused", "3 - Inappropriate", " 2 - Incomprehensible", "1 - None"};

        ArrayAdapter<String> adapterMotorResponse = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                motorResponseOptions
        );

        root.spinnerMotorResponse.setAdapter(adapterMotorResponse);

        //Buttons of Safe drafts and Submit

        root.btnSubmit.setOnClickListener(v -> {
            makeText(this, "", Toast.LENGTH_SHORT).show();makeText(PCR_Form_Activity.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PCR_Form_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }
}