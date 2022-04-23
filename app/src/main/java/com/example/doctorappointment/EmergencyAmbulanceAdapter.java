package com.example.doctorappointment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyAmbulanceAdapter extends RecyclerView.Adapter<EmergencyAmbulanceAdapter.EmergencyAmbulanceViewHolder> {

    private Context context;
    private String [] name, phone;

    public EmergencyAmbulanceAdapter(Context context, String[] name, String[] phone) {
        this.context = context;
        this.name = name;
        this.phone = phone;
    }

    @NonNull
    @Override
    public EmergencyAmbulanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.emergency_ambulance_row_item, parent, false);

        return new EmergencyAmbulanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyAmbulanceViewHolder holder, int position) {

        holder.driverName.setText("Driver Name:  " + name [position]);
        holder.driverPhone.setText("Phone:  " + phone [position] + "   ");

        holder.driverPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                intentCall.setData(Uri.parse("tel:" + phone [holder.getAdapterPosition()]));
                context.startActivity(intentCall);
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public class EmergencyAmbulanceViewHolder extends RecyclerView.ViewHolder {

        TextView driverName, driverPhone;

        public EmergencyAmbulanceViewHolder(@NonNull View itemView) {
            super(itemView);

            driverName = itemView.findViewById(R.id.driverNameId);
            driverPhone = itemView.findViewById(R.id.driverPhoneId);

        }
    }
}
