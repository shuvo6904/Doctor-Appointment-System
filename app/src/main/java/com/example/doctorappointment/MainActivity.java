package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorappointment.doctorModel.ApplyDoctorModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView navHeaderUsername, navHeaderEmail;
    View hView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference;
    private String userId, flag, alert;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    RecyclerView recyclerView;
    HospitalInfoAdapter hospitalInfoAdapter;
    List<HospitalInfoModel> myHospitalInfoList;
    DatabaseReference databaseReference, databaseReference1;
    ValueEventListener eventListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("User Home Page");

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

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading Hospital...");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myHospitalInfoList = new ArrayList<>();

        hospitalInfoAdapter = new HospitalInfoAdapter(MainActivity.this, myHospitalInfoList);
        recyclerView.setAdapter(hospitalInfoAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital List");

        progressDialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myHospitalInfoList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    HospitalInfoModel hospitalInfoModel = dataSnapshot.getValue(HospitalInfoModel.class);
                    myHospitalInfoList.add(hospitalInfoModel);

                }

                hospitalInfoAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        drawerLayout = findViewById(R.id.drawerId);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.navigationId);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);

        navHeaderUsername = hView.findViewById(R.id.navHeaderUserNameId);
        navHeaderEmail = hView.findViewById(R.id.navHeaderEmailId);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                navHeaderUsername.setText(value.getString("name"));
                navHeaderEmail.setText(value.getString("email"));
            }
        });

        databaseReference1 = FirebaseDatabase.getInstance().getReference("Applied Doctor Data").child(userId);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    flag = "exists";

                    ApplyDoctorModel model = snapshot.getValue(ApplyDoctorModel.class);

                    if (model.getApproved()) {
                        alert = "You approved as doctor. Now you can login as doctor. Please logout and again login.";
                    } else {
                        alert = "Wait for admin approval. It will take some time.";
                    }


                } else {
                    flag = "";
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

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

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.menuLogoutId:
                fAuth.signOut();
                removeSharedPreference();
                startActivity(new Intent(getApplicationContext(), PatientLoginActivity.class));
                finish();
                return true;

            case R.id.menuResetPassId:
                startActivity(new Intent(getApplicationContext(), ResetPassActivity.class));
                return true;

            case R.id.menuApplyDoctorId:

                if (flag == "exists") {
                    //Toast.makeText(MainActivity.this, "Already Applied For Doctor", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("You Already Applied");
                    builder.setMessage(alert);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    startActivity(new Intent(getApplicationContext(), DoctorApplyActivity.class));
                }
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.homeMenuId) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        if (item.getItemId() == R.id.profileMenuId) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (item.getItemId() == R.id.myAppointmentMenuId) {
            startActivity(new Intent(this, MyAppointmentActivity.class));
        }

        if (item.getItemId() == R.id.emergencyMenuId){
            startActivity(new Intent(this, ContactAmbulanceActivity.class));
        }

        if (item.getItemId() == R.id.contactUsMenuId){
            startActivity(new Intent(this, ContactUsActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}