package com.example.doctorappointment.doctor;


import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorappointment.DoctorScheduleActivity;
import com.example.doctorappointment.PatientLoginActivity;
import com.example.doctorappointment.R;
import com.example.doctorappointment.doctor.adapter.DoctorHomeAdapter;
import com.example.doctorappointment.doctorModel.AppointmentDataModel;
import com.example.doctorappointment.doctorModel.VisitingScheduleModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class DoctorHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

     TextView headerUserName, headerEmail;
     View headerView;
     DocumentReference documentReference;
     String userId;

    ActionBarDrawerToggle docToggle;
    DrawerLayout docDrawerLayout;

     RecyclerView recyclerView;
     DoctorHomeAdapter doctorHomeAdapter;
     //DatabaseReference databaseReference;
     List<AppointmentDataModel> dataModelList;

     FirebaseAuth firebaseAuth;

     private ProgressDialog progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        this.setTitle("Doctor Home");

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

        progressDialog = new ProgressDialog(DoctorHomeActivity.this);
        progressDialog.setMessage("Loading Appointment....");

        firebaseAuth = FirebaseAuth.getInstance();

        docDrawerLayout = findViewById(R.id.doctorDrawerId);
        NavigationView docNavigationView = findViewById(R.id.doctorNavigationId);

        docToggle = new ActionBarDrawerToggle(this, docDrawerLayout, R.string.nav_open, R.string.nav_close);
        docDrawerLayout.addDrawerListener(docToggle);
        docToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        docToggle.syncState();
        bar.setDisplayHomeAsUpEnabled(true);


        docNavigationView.setNavigationItemSelectedListener(this);

        headerView = docNavigationView.getHeaderView(0);

        headerUserName = headerView.findViewById(R.id.docNavHeaderUserNameId);
        headerEmail = headerView.findViewById(R.id.docNavHeaderEmailId);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        documentReference = FirebaseFirestore.getInstance().collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                headerUserName.setText(value.getString("name"));
                headerEmail.setText(value.getString("email"));
            }
        });

        recyclerView = findViewById(R.id.docHomeRecyclerId);

        dataModelList = new ArrayList<>();
        doctorHomeAdapter = new DoctorHomeAdapter(DoctorHomeActivity.this, dataModelList);
        recyclerView.setAdapter(doctorHomeAdapter);

        //databaseReference = FirebaseDatabase.getInstance().getReference("Appointment Details");

        progressDialog.show();

        Query query = FirebaseDatabase.getInstance().getReference("Appointment Details")
                .orderByChild("docUserId")
                .equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataModelList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    AppointmentDataModel dataModel = dataSnapshot.getValue(AppointmentDataModel.class);
                    dataModelList.add(dataModel);
                }

                progressDialog.dismiss();
                doctorHomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();

                Toast.makeText(DoctorHomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (docToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.docHomeMenuId){
            startActivity(new Intent(this, DoctorHomeActivity.class));
            finish();
        }

        if (item.getItemId() == R.id.docVisitingScheduleMenuId){
            startActivity(new Intent(this, DoctorScheduleActivity.class));
            //finish();
        }

        if (item.getItemId() == R.id.docLogoutMenuId){
            FirebaseAuth.getInstance().signOut();
            removeSharedPreference();
            startActivity(new Intent(this, PatientLoginActivity.class));
            finish();
        }


        docDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void removeSharedPreference() {

        SharedPreferences sharedPreferences = getSharedPreferences("userTypeSharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userType");
        editor.clear();
        editor.apply();
    }

}