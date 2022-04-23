package com.example.doctorappointment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorappointment.doctor.DoctorListActivity;

import java.util.List;

public class HospitalInfoAdapter extends RecyclerView.Adapter<MyHospitalInfoViewHolder> {

    private Context myContext;
    private List<HospitalInfoModel> myHospitalInfoDataList;

    public HospitalInfoAdapter(Context myContext, List<HospitalInfoModel> myHospitalInfoDataList) {
        this.myContext = myContext;
        this.myHospitalInfoDataList = myHospitalInfoDataList;
    }

    @NonNull
    @Override
    public MyHospitalInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_info_signle_row, parent,false);
        return new MyHospitalInfoViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHospitalInfoViewHolder holder, int position) {

        HospitalInfoModel hospitalInfoModel = myHospitalInfoDataList.get(position);

        Glide.with(myContext)
                .load(hospitalInfoModel.getImage())
                .into(holder.hospitalImageView);

        holder.hospitalName.setText(hospitalInfoModel.getHospitalName());
        holder.hospitalAddress.setText(hospitalInfoModel.getHospitalAddress());
        holder.hospitalContact.setText(hospitalInfoModel.getHospitalContact());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // myContext.startActivity(new Intent(myContext, DoctorListActivity.class));

                Intent intent = new Intent(myContext, DoctorListActivity.class);
                intent.putExtra("hospitalId", hospitalInfoModel.getHospitalID());
                myContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return myHospitalInfoDataList.size();
    }
}
