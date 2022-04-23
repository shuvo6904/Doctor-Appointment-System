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
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorappointment.R;
import com.example.doctorappointment.doctor.adapter.DoctorListAdapter;
import com.example.doctorappointment.doctorModel.ApplyDoctorModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListActivity extends AppCompatActivity {

    DoctorListAdapter doctorListAdapter;
    RecyclerView recyclerView;
    List<ApplyDoctorModel> doctorInfoList;
    //DatabaseReference databaseReference;
    //ValueEventListener valueEventListener;
    String hospitalID;
    //private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        this.setTitle("Doctor List");

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


        recyclerView = (RecyclerView) findViewById(R.id.doctorListRecyclerId);
        doctorInfoList = new ArrayList<>();

        doctorListAdapter = new DoctorListAdapter(this, doctorInfoList);
        recyclerView.setAdapter(doctorListAdapter);

        hospitalID = getIntent().getStringExtra("hospitalId");

        Query query = FirebaseDatabase.getInstance().getReference("Applied Doctor Data")
                .orderByChild("hospitalId")
                .equalTo(hospitalID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorInfoList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ApplyDoctorModel applyDoctorModel = dataSnapshot.getValue(ApplyDoctorModel.class);
                    doctorInfoList.add(applyDoctorModel);
                }

                doctorListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DoctorListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

       /* eventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorInfoList.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ApplyDoctorModel applyDoctorModel = dataSnapshot.getValue(ApplyDoctorModel.class);
                    doctorInfoList.add(applyDoctorModel);
                }

                doctorListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DoctorListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });*/


    }



}