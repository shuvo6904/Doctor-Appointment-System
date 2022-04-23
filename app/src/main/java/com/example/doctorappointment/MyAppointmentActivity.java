package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.doctorappointment.doctor.DoctorHomeActivity;
import com.example.doctorappointment.doctorModel.AppointmentDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentActivity extends AppCompatActivity {

    private PatientMyAppointAdapter adapter;
    private RecyclerView recyclerView;
    private List<AppointmentDataModel> dataModelList;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);
        this.setTitle("My Appointment");

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

        progressDialog = new ProgressDialog(MyAppointmentActivity.this);
        progressDialog.setMessage("Loading My Appointment...");

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.myAppointRecyclerId);
        dataModelList = new ArrayList<>();
        adapter = new PatientMyAppointAdapter(MyAppointmentActivity.this, dataModelList);
        recyclerView.setAdapter(adapter);

        progressDialog.show();

    Query patientQuery = FirebaseDatabase.getInstance().getReference("Appointment Details")
            .orderByChild("patientUserId")
            .equalTo(userId);


    patientQuery.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            dataModelList.clear();

            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                AppointmentDataModel patientAppointDataModel = dataSnapshot.getValue(AppointmentDataModel.class);
                dataModelList.add(patientAppointDataModel);
            }
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

            Toast.makeText(MyAppointmentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }
    });

    }
}