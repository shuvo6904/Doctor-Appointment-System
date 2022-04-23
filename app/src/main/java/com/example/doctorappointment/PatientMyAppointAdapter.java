package com.example.doctorappointment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorappointment.doctorModel.AppointmentDataModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientMyAppointAdapter extends RecyclerView.Adapter<PatientMyAppointAdapter.PatientMyAppointViewHolder> {

    private Context context;
    private List<AppointmentDataModel> myAppointDataList;

    public PatientMyAppointAdapter(Context context, List<AppointmentDataModel> myAppointDataList) {
        this.context = context;
        this.myAppointDataList = myAppointDataList;
    }

    @NonNull
    @Override
    public PatientMyAppointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.patient_my_appoint_single_row, parent, false);
        return new PatientMyAppointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientMyAppointViewHolder holder, int position) {

        AppointmentDataModel myAppointDataModel = myAppointDataList.get(position);

        holder.cancelBtn.setBackgroundColor(Color.rgb(255, 102, 102));

        holder.docName.setText(myAppointDataModel.getDoctorName());
        holder.docDesignation.setText(myAppointDataModel.getDoctorSpecializedArea());
        holder.hospitalName.setText(myAppointDataModel.getHospitalName());
        holder.appointTime.setText(myAppointDataModel.getTime());
        holder.patientName.setText("Patient Name: " + myAppointDataModel.getPatientName());
        holder.appointStatus.setText(myAppointDataModel.getAppointStatus());

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myAppointDataModel.getAppointStatus().contentEquals("Pending")){

                    Map<String, Object> map = new HashMap<>();
                    map.put("appointStatus", "Cancelled");

                    FirebaseDatabase.getInstance().getReference().child("Appointment Details")
                            .child(myAppointDataModel.getPushKey()).updateChildren(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Appointment Cancel", Toast.LENGTH_LONG).show();
                                }
                            });

                }else if (myAppointDataModel.getAppointStatus().contentEquals("Accepted")){

                    Toast.makeText(context, "After accepting appointment, you will not able cancel", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(context, "Already cancelled appointment", Toast.LENGTH_SHORT).show();

                }

            }
        });

        /*if (myAppointDataModel.getAppointStatus().contentEquals("Cancelled")){
            holder.cancelBtn.setVisibility(View.INVISIBLE);
        }*/



    }

    @Override
    public int getItemCount() {
        return myAppointDataList.size();
    }

    public class PatientMyAppointViewHolder extends RecyclerView.ViewHolder {

        private TextView docName, docDesignation, hospitalName, appointTime, patientName, appointStatus;
        private Button cancelBtn;
        public PatientMyAppointViewHolder(@NonNull View itemView) {
            super(itemView);

            docName = itemView.findViewById(R.id.docNameId);
            docDesignation = itemView.findViewById(R.id.docDesignationId);
            hospitalName = itemView.findViewById(R.id.appointHospitalNameId);
            appointTime = itemView.findViewById(R.id.appointTimeId);
            patientName = itemView.findViewById(R.id.appointPatientNameId);
            appointStatus = itemView.findViewById(R.id.patientAppointStatusId);

            cancelBtn = itemView.findViewById(R.id.appointCancelId);

        }
    }
}
