package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.doctorappointment.doctor.PendingDoctorActivity;
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

public class AdminHomeActivity extends AppCompatActivity {

    EditText hospitalName, hospitalAddress, hospitalContact, hospitalID;
    FirebaseDatabase database;
    DatabaseReference hospitalListRef;
    FirebaseAuth fAuth;
    private StorageReference storageReference;
    private ImageView hospitalImageView;
    private Button chooseImgBtn, submit;
    private String hospitalImageUri;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        this.setTitle("Admin Home Page");

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

        progressDialog = new ProgressDialog(AdminHomeActivity.this);
        progressDialog.setMessage("Adding Hospital....");

        hospitalName = (EditText) findViewById(R.id.hospitalNameId);
        hospitalAddress = (EditText) findViewById(R.id.hospitalAddressId);
        hospitalContact = (EditText) findViewById(R.id.hospitalContactNumberId);
        hospitalID = (EditText) findViewById(R.id.hospitalIDId);

        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        hospitalListRef = database.getReference("Hospital List");
        hospitalImageView = findViewById(R.id.hospitalImageId);

        submit = findViewById(R.id.submitId);
        submit.setBackgroundColor(Color.BLACK);

        chooseImgBtn = findViewById(R.id.imageChooseButton);
        chooseImgBtn.setBackgroundColor(Color.BLACK);
        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();
            }
        });





    }

    private void uploadImage() {

        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, 1000);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                hospitalImageView.setImageURI(imageUri);
                //uploadHospitalImageToFirebase(imageUri);
            }
        }
    }

    /*private void uploadHospitalImageToFirebase(Uri imageUri) {

        storageReference = FirebaseStorage.getInstance().getReference().child("Hospital/" + hospitalID.getText().toString() + "/hospital.jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                hospitalImageUri = urlImage.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater1 = getMenuInflater();
        inflater1.inflate(R.menu.menu_admin_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuAdminLogoutId:
                fAuth.signOut();
                removeSharedPreference();
                startActivity(new Intent(getApplicationContext(), PatientLoginActivity.class));
                finish();
                return true;

            case R.id.menuAdminResetPassId:
                startActivity(new Intent(getApplicationContext(), ResetPassActivity.class));
                return true;

            case R.id.menuRequestDoctorId:
                startActivity(new Intent(getApplicationContext(), PendingDoctorActivity.class));
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

    public void submitHospitalInfo(View view) {



        String strHospitalID = hospitalID.getText().toString().trim();
        String strHospitalName = hospitalName.getText().toString().trim();
        String strHospitalAddress = hospitalAddress.getText().toString().trim();
        String strHospitalContact = hospitalContact.getText().toString().trim();

        if (strHospitalID.isEmpty()){
            hospitalID.setError("Required Field");
            hospitalID.requestFocus();
            return;
        }

        if (strHospitalName.isEmpty()){
            hospitalName.setError("Required Field");
            hospitalName.requestFocus();
            return;
        }

        if (strHospitalAddress.isEmpty()){
            hospitalAddress.setError("Required Field");
            hospitalAddress.requestFocus();
            return;
        }

        if (strHospitalContact.isEmpty()){
            hospitalContact.setError("Required Field");
            hospitalContact.requestFocus();
            return;
        }

        if (imageUri == null){
            Toast.makeText(AdminHomeActivity.this, "Please choose a hospital image", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference().child("Hospital/" + hospitalID.getText().toString() + "/hospital.jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                hospitalImageUri = urlImage.toString();

                HospitalInfoModel hospitalInfoModel = new HospitalInfoModel(
                        strHospitalID,
                        strHospitalName,
                        strHospitalAddress,
                        strHospitalContact,
                        hospitalImageUri

                );

                hospitalListRef.child(strHospitalID)
                        .setValue(hospitalInfoModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(AdminHomeActivity.this, "Hospital Information Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            hospitalID.setText("");
                            hospitalName.setText("");
                            hospitalAddress.setText("");
                            hospitalContact.setText("");
                            hospitalImageView.setImageDrawable(getDrawable(R.drawable.ic_baseline_image_24));
                            hospitalID.requestFocus();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();

                        Toast.makeText(AdminHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
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