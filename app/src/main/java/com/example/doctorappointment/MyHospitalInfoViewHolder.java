package com.example.doctorappointment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyHospitalInfoViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView hospitalName, hospitalAddress, hospitalContact;
    ImageView hospitalImageView;

    public MyHospitalInfoViewHolder(@NonNull View itemView) {
        super(itemView);

        cardView = itemView.findViewById(R.id.hospitalInfoCardId);
        hospitalName = itemView.findViewById(R.id.hospitalNameTVId);
        hospitalAddress = itemView.findViewById(R.id.hospitalAddressTVId);
        hospitalContact = itemView.findViewById(R.id.hospitalContactTVId);
        hospitalImageView = itemView.findViewById(R.id.hospitalImageViewId);
    }
}
