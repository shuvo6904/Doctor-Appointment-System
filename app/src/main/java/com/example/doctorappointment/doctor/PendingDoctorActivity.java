package com.example.doctorappointment.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctorappointment.R;
import com.example.doctorappointment.doctorModel.ApplyDoctorModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class PendingDoctorActivity extends AppCompatActivity {

    PendingDoctorAdapter pendingDoctorAdapter;
    RecyclerView pendingDocRecyclerView;
    List<ApplyDoctorModel> pendingDocInfoList;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_doctor);
        this.setTitle("Pending Doctor Request");

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.adminStatusBarColor));
        }

        pendingDocRecyclerView = (RecyclerView) findViewById(R.id.pendingDoctorInfoRecyclerId);
        pendingDocInfoList = new ArrayList<>();

        pendingDoctorAdapter = new PendingDoctorAdapter(this, pendingDocInfoList);
        pendingDocRecyclerView.setAdapter(pendingDoctorAdapter);

        setRecyclerData();


    }

    private void setRecyclerData() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Applied Doctor Data");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingDocInfoList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){

                    ApplyDoctorModel applyDoctorModel = snapshot1.getValue(ApplyDoctorModel.class);

                    if (!applyDoctorModel.getApproved()){

                        pendingDocInfoList.add(applyDoctorModel);

                    }

                }

                pendingDoctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(PendingDoctorActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}