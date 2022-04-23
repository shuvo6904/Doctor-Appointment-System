package com.example.doctorappointment.doctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorappointment.R;
import com.example.doctorappointment.doctorModel.ApplyDoctorModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingDoctorAdapter extends RecyclerView.Adapter<PendingDoctorAdapter.PendingDoctorViewHolder> {

    Context context;
    List<ApplyDoctorModel> doctorInfoList;

    public PendingDoctorAdapter(Context context, List<ApplyDoctorModel> doctorInfoList) {
        this.context = context;
        this.doctorInfoList = doctorInfoList;
    }

    @NonNull
    @Override
    public PendingDoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.pending_doc_info_single_row, parent, false);


        return new PendingDoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingDoctorViewHolder holder, int position) {

        ApplyDoctorModel model = doctorInfoList.get(position);

        holder.doctorName.setText("Name : " + model.getDoctorName());
        holder.hospitalName.setText("Hospital Name : " + model.getHospitalName());
        holder.specializedArea.setText("Specialized Area : " + model.getSpecializedField());
        holder.degree.setText("Degree : " + model.getDocApplyDegree());
        holder.visitFee.setText("Visiting Fee : " + model.getVisitFee() + " Taka");

        Glide.with(context)
                .load(model.getDocImageUrl())
                .into(holder.profileImgView);

        Glide.with(context)
                .load(model.getDocImageIdentityUrl())
                .into(holder.identityImgView);

        holder.approve.setBackgroundColor(Color.BLACK);

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strHospitalId = holder.hospitalId.getText().toString();

                if (strHospitalId.isEmpty()){
                    holder.hospitalId.setError("Required Field");
                    holder.hospitalId.requestFocus();
                    return;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("hospitalId", strHospitalId);
                map.put("approved", true);

                FirebaseDatabase.getInstance().getReference().child("Applied Doctor Data")
                        .child(model.getUserId()).updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Map<String, Object> edited = new HashMap<>();
                                edited.put("userType", "Doctor");
                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(model.getUserId());
                                documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(context, "Successfully Approve Doctor", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return doctorInfoList.size();
    }

    public class PendingDoctorViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, hospitalName, specializedArea, visitFee, degree;
        EditText hospitalId;
        Button approve;
        ImageView identityImgView, profileImgView;


        public PendingDoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorName = itemView.findViewById(R.id.pendingDoctorNameId);
            hospitalName = itemView.findViewById(R.id.pendingHospitalNameId);
            specializedArea = itemView.findViewById(R.id.pendingSpecializedAreaId);
            visitFee = itemView.findViewById(R.id.pendingVisitFeeId);
            hospitalId = itemView.findViewById(R.id.pendingPageHospitalId);
            approve = itemView.findViewById(R.id.approveAsDocId);
            degree = itemView.findViewById(R.id.pendingDegreeId);
            identityImgView = itemView.findViewById(R.id.imageViewId);
            profileImgView = itemView.findViewById(R.id.profileImageViewId);


        }
    }
}
