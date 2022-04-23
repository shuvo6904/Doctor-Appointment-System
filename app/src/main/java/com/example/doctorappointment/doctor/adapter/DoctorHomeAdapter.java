package com.example.doctorappointment.doctor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorappointment.R;
import com.example.doctorappointment.doctorModel.AppointmentDataModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DoctorHomeAdapter extends RecyclerView.Adapter<DoctorHomeAdapter.DoctorHomeViewHolder> {

    private Context context;
    private List<AppointmentDataModel> docHomeDataList;

    public DoctorHomeAdapter(Context context, List<AppointmentDataModel> docHomeDataList) {
        this.context = context;
        this.docHomeDataList = docHomeDataList;
    }

    @NonNull
    @Override
    public DoctorHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.doctor_home_single_row, parent, false);
        return new DoctorHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorHomeViewHolder holder, int position) {

        AppointmentDataModel appointmentDataModel = docHomeDataList.get(position);

        holder.status.setText(appointmentDataModel.getAppointStatus());
        holder.patientName.setText(appointmentDataModel.getPatientName());
        holder.patientPhone.setText(appointmentDataModel.getPatientPhone());
        holder.visitingTime.setText(appointmentDataModel.getTime());
        holder.hospitalName.setText("Hospital: " + appointmentDataModel.getHospitalName());

        holder.accept.setBackgroundColor(Color.BLACK);
        holder.delete.setBackgroundColor(Color.BLACK);

        if (appointmentDataModel.getAppointStatus().contentEquals("Accepted")){
            holder.accept.setVisibility(View.GONE);
        }

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("appointStatus", "Accepted");

                FirebaseDatabase.getInstance().getReference().child("Appointment Details")
                        .child(appointmentDataModel.getPushKey()).updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Appointment Accepted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("Appointment Details")
                        .child(appointmentDataModel.getPushKey()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return docHomeDataList.size();
    }

    public class DoctorHomeViewHolder extends RecyclerView.ViewHolder {

        private TextView status, patientName, patientPhone, visitingTime, hospitalName;

        private Button accept, delete;

        public DoctorHomeViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.statusId);
            patientName = itemView.findViewById(R.id.docHomePatientNameId);
            patientPhone = itemView.findViewById(R.id.docHomePatientPhoneId);
            visitingTime = itemView.findViewById(R.id.docHomeTimeId);
            hospitalName = itemView.findViewById(R.id.docHomeHospitalNameId);
            accept = itemView.findViewById(R.id.acceptBtnId);
            delete = itemView.findViewById(R.id.deleteBtnId);

        }
    }
}
