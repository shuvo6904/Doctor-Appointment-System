package com.example.doctorappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactUsActivity extends AppCompatActivity {

    private Button button;
    private EditText name, email, phone, msg;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        this.setTitle("Contact Us");

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0890A2")));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBarColor));

            button = findViewById(R.id.buttonSubmit);
            button.setBackgroundColor(Color.BLACK);

            name = findViewById(R.id.userContactNameId);
            email = findViewById(R.id.userContactEmailId);
            phone = findViewById(R.id.userContactPhoneId);
            msg = findViewById(R.id.userContactMsgId);

            reference = FirebaseDatabase.getInstance().getReference();



            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ContactDataModel dataModel = new ContactDataModel(
                            name.getText().toString(),
                            email.getText().toString(),
                            phone.getText().toString(),
                            msg.getText().toString()
                    );

                    reference.child("Contact Us Data").push().setValue(dataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(ContactUsActivity.this, "Message Successfully Sent", Toast.LENGTH_SHORT).show();

                            name.setText("");
                            email.setText("");
                            phone.setText("");
                            msg.setText("");

                        }
                    });




                }
            });
        }


    }
}