package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientRegistrationActivity extends AppCompatActivity {

    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegistrationBtn;
    TextView mBackLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);
        this.setTitle("Register Page");

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0890A2")));
        /*bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);*/

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColor));
        }

        mFullName = findViewById(R.id.userNameId);
        mEmail = findViewById(R.id.userEmailId);
        mPassword = findViewById(R.id.userPasswordId);
        mPhone=findViewById(R.id.userPhoneId);
        mRegistrationBtn=findViewById(R.id.userRegButtonId);
        mBackLoginBtn=findViewById(R.id.userBackloginId);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        mRegistrationBtn.setBackgroundColor(Color.BLACK);

        mRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email =mEmail.getText().toString().trim();
                String password =mPassword.getText().toString().trim();
                String fullName =mFullName.getText().toString().trim();
                String phone =mPhone.getText().toString().trim();

                if(TextUtils.isEmpty(fullName)){
                    mFullName.setError("Name is required");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    mPhone.setError("Phone Number is required");
                    return;
                }

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

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {

                            userID = fAuth.getCurrentUser().getUid();

                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name", fullName);
                            user.put("email", email);
                            user.put("number", phone);
                            user.put("userType", "Primary User");
                            user.put("userProfileImg", "");
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                }
                            });

                            Toast.makeText(PatientRegistrationActivity.this,"User Created",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(PatientRegistrationActivity.this,"Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });

        mBackLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PatientLoginActivity.class));
            }
        });
    }
}