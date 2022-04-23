package com.example.doctorappointment.doctor.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorappointment.R;
import com.example.doctorappointment.doctor.AppointmentActivity;
import com.example.doctorappointment.doctorModel.ApplyDoctorModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListViewHolder> {

    Context context;
    List<ApplyDoctorModel> doctorInfoList;

    public DoctorListAdapter(Context context, List<ApplyDoctorModel> doctorInfoList) {
        this.context = context;
        this.doctorInfoList = doctorInfoList;
    }

    @NonNull
    @Override
    public DoctorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.doctor_info_single_row, parent, false);

        return new DoctorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListViewHolder holder, int position) {

        holder.appointButton.setBackgroundColor(Color.BLACK);

        ApplyDoctorModel applyDoctorModel = doctorInfoList.get(position);

        holder.doctorName.setText(applyDoctorModel.getDoctorName());
        holder.doctorSpecializedArea.setText(applyDoctorModel.getSpecializedField());
        holder.doctorDegree.setText(applyDoctorModel.getDocApplyDegree());
        holder.docVisitingFee.setText("Visiting Fee: " + applyDoctorModel.getVisitFee() + "Taka");
        holder.docHospitalName.setText("Hospital : " + applyDoctorModel.getHospitalName());

        Glide.with(context)
                .load(applyDoctorModel.getDocImageUrl())
                .into(holder.imageView);


        holder.appointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.startActivity(new Intent(context, AppointmentActivity.class));\
                Intent intent = new Intent(context, AppointmentActivity.class);
                intent.putExtra("name", applyDoctorModel.getDoctorName());
                intent.putExtra("designation", applyDoctorModel.getSpecializedField());
                intent.putExtra("userId", applyDoctorModel.getUserId());
                intent.putExtra("hospitalName", applyDoctorModel.getHospitalName());
                intent.putExtra("docDegree", applyDoctorModel.getDocApplyDegree());
                intent.putExtra("visitingFee", applyDoctorModel.getVisitFee());
                intent.putExtra("imageUri", applyDoctorModel.getDocImageUrl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return doctorInfoList.size();
    }

    public class DoctorListViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView doctorName, doctorSpecializedArea, doctorDegree, docVisitingFee, docHospitalName;
        Button appointButton;

        public DoctorListViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.docListPageProfileImageId);
            doctorName = itemView.findViewById(R.id.doctorNameId);
            doctorSpecializedArea = itemView.findViewById(R.id.doctorSpecializedAreaId);
            doctorDegree = itemView.findViewById(R.id.doctorDegreeId);
            docVisitingFee = itemView.findViewById(R.id.docVisitingFeeId);
            docHospitalName = itemView.findViewById(R.id.doctorPageHospitalNameId);
            appointButton = itemView.findViewById(R.id.docAppointBtnId);

        }
    }
}
