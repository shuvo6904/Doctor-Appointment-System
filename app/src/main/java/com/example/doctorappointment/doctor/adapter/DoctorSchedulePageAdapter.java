package com.example.doctorappointment.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorappointment.R;
import com.example.doctorappointment.doctorModel.VisitingScheduleModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DoctorSchedulePageAdapter extends RecyclerView.Adapter<DoctorSchedulePageAdapter.DoctorSchedulePageViewHolder> {

    Context context;
    List<VisitingScheduleModel> visitingScheduleList;

    public DoctorSchedulePageAdapter(Context context, List<VisitingScheduleModel> visitingScheduleList) {
        this.context = context;
        this.visitingScheduleList = visitingScheduleList;
    }

    @NonNull
    @Override
    public DoctorSchedulePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.visiting_day_list, parent, false);
        return new DoctorSchedulePageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorSchedulePageViewHolder holder, int position) {

        VisitingScheduleModel visitingScheduleModel = visitingScheduleList.get(position);
        holder.day.setText(visitingScheduleModel.getSchedule());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId = FirebaseAuth.getInstance().getUid();
                String clickedValue = visitingScheduleList.get(holder.getAdapterPosition()).getSchedule();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                Query query = rootRef.child("Visiting Schedule")
                        .child(userId)
                        .orderByChild("schedule")
                        .equalTo(clickedValue);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snp: snapshot.getChildren()) {
                                rootRef.child("Visiting Schedule").child(userId).child(snp.getKey()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                                else Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });


    }

    @Override
    public int getItemCount() {
        return visitingScheduleList.size();
    }

    public class DoctorSchedulePageViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        private ImageView delete;
        public DoctorSchedulePageViewHolder(@NonNull View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.dayId);
            delete = itemView.findViewById(R.id.deleteImageId);

        }
    }
}
