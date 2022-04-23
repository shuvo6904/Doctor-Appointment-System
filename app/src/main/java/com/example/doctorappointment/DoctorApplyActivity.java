package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.doctorappointment.doctorModel.ApplyDoctorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DoctorApplyActivity extends AppCompatActivity {

    EditText applyDoctorName, applyHospitalName, applySpecializedField, applyVisitFee, applyDocDegree;
    DatabaseReference databaseReference;
    String userID;
    private Button submitBtn, chooseDocImage, chooseDocIdentityImage;
    private ImageView docImageView, docIdentityImageView;
    private Uri profileUri, identityUri;
    private StorageReference profileRef, identityRef;
    private String strProfileUri, strIdentityUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_apply);

        this.setTitle("Apply as Doctor");

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

        progressDialog = new ProgressDialog(DoctorApplyActivity.this);
        progressDialog.setMessage("Please Wait...");

        applyDoctorName = (EditText) findViewById(R.id.applyDoctorNameId);
        applyHospitalName = (EditText) findViewById(R.id.applyHospitalNameId);
        applySpecializedField = (EditText) findViewById(R.id.applyDoctorSpecializedId);
        applyVisitFee = (EditText) findViewById(R.id.applyDoctorVisitFeeId);
        applyDocDegree = findViewById(R.id.applyDocDegreeId);

        docImageView = findViewById(R.id.docImageId);
        chooseDocImage = findViewById(R.id.docImageChooseButton);

        docIdentityImageView = findViewById(R.id.docIdentityImageId);
        chooseDocIdentityImage = findViewById(R.id.docIdentityImageChooseButton);


        submitBtn = findViewById(R.id.applySubmitId);
        submitBtn.setBackgroundColor(Color.BLACK);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chooseDocImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });

        chooseDocIdentityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadIdentityImage();
            }
        });
    }

    private void uploadIdentityImage() {

        Intent identityPicker = new Intent(Intent.ACTION_PICK);
        identityPicker.setType("image/*");
        startActivityForResult(identityPicker, 3000);
    }

    private void uploadProfileImage() {
        Intent profilePicker = new Intent(Intent.ACTION_PICK);
        profilePicker.setType("image/*");
        startActivityForResult(profilePicker, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2000){
            if (resultCode == Activity.RESULT_OK){
                profileUri = data.getData();
                docImageView.setImageURI(profileUri);
            }
        }

        if (requestCode == 3000){
            if (resultCode == Activity.RESULT_OK){
                identityUri = data.getData();
                docIdentityImageView.setImageURI(identityUri);
            }
        }
    }

    public void submitDoctorApply(View view) {

        if (profileUri == null || identityUri == null){
            Toast.makeText(DoctorApplyActivity.this, "Please choose  profile picture and identity card front page", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.show();

        profileRef = FirebaseStorage.getInstance().getReference().child("Doctor/" + userID + "/profile.jpg");
        identityRef = FirebaseStorage.getInstance().getReference().child("Doctor/" + userID + "/identity.jpg");

        profileRef.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> profileUriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!profileUriTask.isComplete());
                Uri profileImgUri = profileUriTask.getResult();
                strProfileUri = profileImgUri.toString();

                identityRef.putFile(identityUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> identityUriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!identityUriTask.isComplete());
                        Uri identityImgUri = identityUriTask.getResult();
                        strIdentityUri = identityImgUri.toString();

                        ApplyDoctorModel applyDoctorModel = new ApplyDoctorModel(

                                applyDoctorName.getText().toString(),
                                applyHospitalName.getText().toString(),
                                applySpecializedField.getText().toString(),
                                applyVisitFee.getText().toString(),
                                "",
                                false,
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                applyDocDegree.getText().toString(),
                                strProfileUri,
                                strIdentityUri


                        );

                        databaseReference.child("Applied Doctor Data")
                                .child(userID)
                                .setValue(applyDoctorModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "You have successfully applied for Doctor", Toast.LENGTH_SHORT).show();
                                            //finish();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                    }
                });

                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

            }
        });


    }
}