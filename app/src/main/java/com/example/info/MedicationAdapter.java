package com.example.info;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {
    private final List<Medication> medicationList;
    private final OnMedicationClickListener listener;

    public MedicationAdapter(List<Medication> medicationList, OnMedicationClickListener listener) {
        this.medicationList = medicationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);
        holder.medicationNameTextView.setText(medication.getItemName());
        holder.efficacyTextView.setText(medication.getEfficacy());

        // 이미지 로드
        if (medication.getImageUrl() != null && !medication.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(medication.getImageUrl())
                    .placeholder(R.drawable.ic_default_image)
                    .into(holder.medicationImageView);
        } else {
            holder.medicationImageView.setImageResource(R.drawable.ic_default_image);
        }

        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> listener.onMedicationClick(medication));

        // 삭제 버튼 클릭
        holder.deleteIcon.setOnClickListener(v -> listener.onMedicationDelete(position));

        // 추가 버튼 클릭 시 처리
        holder.addButton.setOnClickListener(v -> listener.onAddMedicationClick(medication));
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView medicationNameTextView;
        TextView efficacyTextView;
        ImageView medicationImageView;
        ImageView deleteIcon;
        Button addButton; // 추가 버튼

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationNameTextView = itemView.findViewById(R.id.medicationName);
            efficacyTextView = itemView.findViewById(R.id.medicationEfficacy);
            medicationImageView = itemView.findViewById(R.id.medicationImage);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            addButton = itemView.findViewById(R.id.addButton); // 추가 버튼
        }
    }

    public interface OnMedicationClickListener {
        void onMedicationClick(Medication medication);
        void onMedicationDelete(int position);
        void onAddMedicationClick(Medication medication); // 새로운 인터페이스 메서드
    }
}
