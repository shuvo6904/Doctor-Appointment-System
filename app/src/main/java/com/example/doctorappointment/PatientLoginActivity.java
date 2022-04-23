package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doctorappointment.doctor.DoctorHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientLoginActivity extends AppCompatActivity {

    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mBackRegBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);

        this.setTitle("Login Page");

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0890A2")));
       /* bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);*/

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColor));
        }

        mEmail=findViewById(R.id.userEmailId);
        mPassword=findViewById(R.id.userPasswordId);
        progressBar=findViewById(R.id.loginProgressBar);
        fAuth=FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mLoginBtn=findViewById(R.id.userLoginbuttonId);
        mBackRegBtn=findViewById(R.id.userBackRegId);

        mLoginBtn.setBackgroundColor(Color.BLACK);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email =mEmail.getText().toString().trim();
                String password =mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if (password.length()<6){
                    mPassword.setError("Password Must Be Grater Than 6 Digits");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            userId = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userId);

                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if (documentSnapshot.exists()){

                                        String strUserType = documentSnapshot.getString("userType");

                                        if (strUserType.contentEquals("Primary User")){

                                            saveSharedPreferences(strUserType);
                                            Toast.makeText(PatientLoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            finish();
                                        }

                                        else if (strUserType.contentEquals("Doctor")){

                                            saveSharedPreferences(strUserType);
                                            Toast.makeText(PatientLoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(getApplicationContext(), DoctorHomeActivity.class));
                                            finish();

                                        }

                                        else{

                                            saveSharedPreferences(strUserType);
                                            Toast.makeText(PatientLoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(getApplicationContext(),AdminHomeActivity.class));
                                            finish();

                                        }


                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });



                        }
                        else {
                            Toast.makeText(PatientLoginActivity.this,"Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }



                    }
                });

            }
        });
        mBackRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PatientRegistrationActivity.class));
            }
        });
    }

    private void saveSharedPreferences(String strUserType) {

        SharedPreferences sharedPreferences = getSharedPreferences("userTypeSharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", strUserType);
        editor.apply();

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("userTypeSharedPreferences", MODE_PRIVATE);
        if (fAuth.getCurrentUser() != null && sharedPreferences.getString("userType", "Primary User").contentEquals("Primary User")){
            startActivity(new Intent(PatientLoginActivity.this, MainActivity.class));
            finish();

        }

        else if (fAuth.getCurrentUser() != null && sharedPreferences.getString("userType", "Primary User").contentEquals("Doctor")){
            startActivity(new Intent(PatientLoginActivity.this, DoctorHomeActivity.class));
            finish();

        }

        else if (fAuth.getCurrentUser() != null && sharedPreferences.getString("userType", "Primary User").contentEquals("Admin")){
            startActivity(new Intent(PatientLoginActivity.this, AdminHomeActivity.class));
            finish();

        }






    }
}