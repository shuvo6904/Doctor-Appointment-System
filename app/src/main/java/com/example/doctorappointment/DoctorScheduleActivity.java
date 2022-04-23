package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.doctorappointment.doctor.adapter.DoctorSchedulePageAdapter;
import com.example.doctorappointment.doctorModel.VisitingScheduleModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    String[] visitingDayDropDownArray, visitingStartTimeDropDownArray, visitingEndTimeDropDownArray;
    TextInputLayout dayLayout, startLayout, endLayout;
    AutoCompleteTextView selectedDay, selectedStartTime, selectedEndTime;

    Button visitingDay;

    String day, visit, userId;

    DatabaseReference reference, databaseReference;

    DoctorSchedulePageAdapter doctorSchedulePageAdapter;
    RecyclerView recyclerView;
    List<VisitingScheduleModel> visitingScheduleList;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedule);
        this.setTitle("Visiting Schedule Page");


        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0890A2")));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColor));
        }

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();


        visitingDay = (Button) findViewById(R.id.addVisitDayId);
        visitingDay.setBackgroundColor(Color.BLACK);

        visitingDayDropDownArray = getResources().getStringArray(R.array.visit_day);
        dayLayout = (TextInputLayout) findViewById(R.id.visitingDayLayoutId);
        selectedDay = (AutoCompleteTextView) findViewById(R.id.selectedDayId);

        visitingStartTimeDropDownArray = getResources().getStringArray(R.array.visit_starting_time);
        startLayout = (TextInputLayout) findViewById(R.id.visitingStartTimeLayoutId);
        selectedStartTime = (AutoCompleteTextView) findViewById(R.id.selectedStartingTimeId);

        visitingEndTimeDropDownArray = getResources().getStringArray(R.array.visit_ending_time);
        endLayout = (TextInputLayout) findViewById(R.id.visitingEndTimeLayoutId);
        selectedEndTime = (AutoCompleteTextView) findViewById(R.id.selectedEndingTimeId);


        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(DoctorScheduleActivity.this, R.layout.sample_spinner_view, visitingDayDropDownArray);
        selectedDay.setAdapter(startAdapter);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(DoctorScheduleActivity.this, R.layout.sample_spinner_view, visitingStartTimeDropDownArray);
        selectedStartTime.setAdapter(dayAdapter);

        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(DoctorScheduleActivity.this, R.layout.sample_spinner_view, visitingEndTimeDropDownArray);
        selectedEndTime.setAdapter(endAdapter);

        visitingDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisitingDay();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.docHomePageRecyclerId);
        visitingScheduleList = new ArrayList<>();
        doctorSchedulePageAdapter = new DoctorSchedulePageAdapter(this, visitingScheduleList);
        recyclerView.setAdapter(doctorSchedulePageAdapter);
        setVisitingDayData(userId);

    }

    private void setVisitingDayData(String userId) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Visiting Schedule").child(userId);
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                visitingScheduleList.clear();

                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){

                        VisitingScheduleModel visitingScheduleModel = dataSnapshot1.getValue(VisitingScheduleModel.class);
                        visitingScheduleList.add(visitingScheduleModel);



                }

                doctorSchedulePageAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DoctorScheduleActivity.this, error.getMessage(), Toast.LENGTH_LONG);

            }
        });



    }

    private void setVisitingDay() {

        day = selectedDay.getText().toString();

        visit = selectedDay.getText().toString() + " " + selectedStartTime.getText().toString() + " to " + selectedEndTime.getText().toString() ;

        VisitingScheduleModel visitingScheduleModel = new VisitingScheduleModel(visit);


        reference.child("Visiting Schedule")
                .child(fAuth.getUid())
                .child(day)
                .setValue(visitingScheduleModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DoctorScheduleActivity.this, "New Schedule Added Successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoctorScheduleActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuLogoutId:
                fAuth.signOut();
                removeSharedPreference();
                startActivity(new Intent(getApplicationContext(), PatientLoginActivity.class));
                finish();
                return true;

            case R.id.menuResetPassId:
                startActivity(new Intent(getApplicationContext(), ResetPassActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }

    private void removeSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("userTypeSharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userType");
        editor.clear();
        editor.apply();
    }

}