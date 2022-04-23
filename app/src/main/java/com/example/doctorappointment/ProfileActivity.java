package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Button profileEmailVerifyButton;
    private FirebaseUser user;
    private FirebaseAuth fAuth;
    private DocumentReference documentReference;
    private CircleImageView profileImage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private TextView profileName, proEditableName, proEditablePhnNum, proEditableEmail, editProfile;
    private Uri proImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.setTitle("Profile Page");

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

        profileEmailVerifyButton = (Button) findViewById(R.id.verifiedEmailButtonId);
        profileImage = findViewById(R.id.editableProfileImageViewId);
        profileName = (TextView) findViewById(R.id.profileNameId);
        proEditableName = (TextView) findViewById(R.id.editableProfileNameId);
        proEditablePhnNum = (TextView) findViewById(R.id.editableProfilePhnNumId);
        proEditableEmail = (TextView) findViewById(R.id.editableProfileEmailId);
        editProfile = findViewById(R.id.editProfileId);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (user != null)
            user.reload();

        if (!user.isEmailVerified()){
            profileEmailVerifyButton.setVisibility(View.VISIBLE);
        }

        profileEmailVerifyButton.setBackgroundColor(Color.BLACK);

        storageReference = FirebaseStorage.getInstance().getReference().child("Users/" + user.getUid() + "/user_profile_image.jpg");

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profileName.setText(value.getString("name"));
                proEditableName.setText("Name: " + value.getString("name"));
                proEditablePhnNum.setText("Phone: " + value.getString("number"));
                proEditableEmail.setText("Email: " + value.getString("email"));

                if (value.getString("userProfileImg").isEmpty()) {
                    profileImage.setImageResource(R.drawable.profile_img);
                } else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            /*Picasso.get().load(uri).into(profileImage);*/

                            Glide.with(ProfileActivity.this)
                                    .load(uri)
                                    .into(profileImage);
                        }
                    });
                }

            }
        });

        profileEmailVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(ProfileActivity.this, "Email verification link has been sent.", Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        /*Log.d("tag", "onFailure : Email not sent " + e.getMessage());*/
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
            }
        });








    }

    public void fabBtnProfile(View view) {

        Intent profilePhotoPic = new Intent(Intent.ACTION_PICK);
        profilePhotoPic.setType("image/*");
        startActivityForResult(profilePhotoPic, 1500);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1500){
            if (resultCode == Activity.RESULT_OK){
                proImgUri = data.getData();
                //profileImage.setImageURI(proImgUri);
                uploadProImageToFirebase(proImgUri);
            }
        }
    }

    private void uploadProImageToFirebase(Uri proImgUri) {

        storageReference.putFile(proImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String proImageUri = proImgUri.toString();

                Map<String, Object> edited = new HashMap<>();
                edited.put("userProfileImg", proImageUri);
                documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .into(profileImage);

            }
        });



    }
}







