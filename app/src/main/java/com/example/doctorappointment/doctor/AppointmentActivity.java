package com.example.doctorappointment.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doctorappointment.MyAppointmentActivity;
import com.example.doctorappointment.R;
import com.example.doctorappointment.doctorModel.AppointmentDataModel;
import com.example.doctorappointment.doctorModel.VisitingScheduleModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AppointmentActivity extends AppCompatActivity {

    Spinner spinner;
    ValueEventListener eventListener;
    DatabaseReference databaseReference, appointDoctorDataRef;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    private String name, specializedArea, docUserId, patientUserId, time, strPatientName, strPatientPhone, pushKey, hospitalName, degree, visitingFee, imageUri;
    private TextView docName, docSpecializedArea, docDegree, docHospitalName, docVisitingFee;
    Button appointDataButton;
    private EditText patientName, patientPhone;
    private CircleImageView circleImageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        this.setTitle("Appointment Page");

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

        progressDialog = new ProgressDialog(AppointmentActivity.this);
        progressDialog.setMessage("Please Wait....");

        spinner = (Spinner) findViewById(R.id.spinnerId);

        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,R.layout.sample_spinner_view, list);
        spinner.setAdapter(adapter);

        docName = (TextView) findViewById(R.id.appointDoctorNameId);
        docSpecializedArea = (TextView) findViewById(R.id.appointDoctorSpecializedId);
        docDegree = findViewById(R.id.appointDocDegreeId);
        docHospitalName = findViewById(R.id.appointDocHospitalId);
        docVisitingFee = findViewById(R.id.appointDocFeeId);

        appointDataButton = (Button) findViewById(R.id.confirmAppointId);
        patientName = (EditText) findViewById(R.id.patientNameId);
        patientPhone = (EditText) findViewById(R.id.patientPhoneNumId);

        circleImageView = findViewById(R.id.appointmentPageProfileImageId);

        patientUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        name = getIntent().getStringExtra("name");
        specializedArea = getIntent().getStringExtra("designation");
        docUserId = getIntent().getStringExtra("userId");
        hospitalName = getIntent().getStringExtra("hospitalName");
        degree = getIntent().getStringExtra("docDegree");
        visitingFee = getIntent().getStringExtra("visitingFee");
        imageUri = getIntent().getStringExtra("imageUri");

        appointDataButton.setBackgroundColor(Color.BLACK);

        fetchSpinnerData(docUserId);

        setDoctorData(name, specializedArea, hospitalName, degree, visitingFee, imageUri);

        appointDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strPatientName = patientName.getText().toString();
                strPatientPhone = patientPhone.getText().toString();

                if (strPatientName.isEmpty()){
                    patientName.setError("Required Field");
                    patientName.requestFocus();
                    return;
                }

                if (strPatientPhone.isEmpty()){
                    patientPhone.setError("Required Field");
                    patientPhone.requestFocus();
                    return;
                }

                time = (String) spinner.getSelectedItem();

                storeAppointmentData(name, specializedArea, hospitalName, degree, visitingFee, imageUri, strPatientName, strPatientPhone, time, docUserId, patientUserId);

            }
        });



    }

    private void storeAppointmentData(String name, String specializedArea, String hospitalName, String degree, String visitingFee, String imageUri, String strPatientName, String strPatientPhone, String time, String docUserId, String patientUserId) {

        progressDialog.show();

        appointDoctorDataRef = FirebaseDatabase.getInstance().getReference();
        pushKey = appointDoctorDataRef.push().getKey();

        AppointmentDataModel dataModel = new AppointmentDataModel(

                name,
                specializedArea,
                degree,
                hospitalName,
                visitingFee,
                imageUri,
                strPatientName,
                strPatientPhone,
                time,
                docUserId,
                patientUserId,
                "Pending",
                pushKey

        );




        appointDoctorDataRef.child("Appointment Details")
                .child(pushKey)
                .setValue(dataModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            progressDialog.dismiss();
                            Toast.makeText(AppointmentActivity.this, "Appointment Successful", Toast.LENGTH_SHORT).show();
                            /*patientName.setText("");
                            patientPhone.setText("");*/

                            startActivity(new Intent(AppointmentActivity.this, MyAppointmentActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

                Toast.makeText(AppointmentActivity.this,e.getMessage() , Toast.LENGTH_SHORT).show();
                /*patientName.setText("");
                patientPhone.setText("");*/
            }
        });

    }

    private void setDoctorData(String name, String specializedArea, String hospitalName, String degree, String visitingFee, String imageUri) {

        docName.setText(name);
        docSpecializedArea.setText(specializedArea);
        docDegree.setText(degree);
        docHospitalName.setText("Hospital: " + hospitalName);
        docVisitingFee.setText("Visiting Fee: " + visitingFee + " Tk");

        Glide.with(AppointmentActivity.this)
                .load(imageUri)
                .into(circleImageView);


    }





    private void fetchSpinnerData(String userId) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Visiting Schedule").child(userId);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               for (DataSnapshot dataSnapshot : snapshot.getChildren()){


                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){

                            list.add(dataSnapshot2.getValue().toString());
                            adapter.notifyDataSetChanged();


                        }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}