package com.example.info;

public interface OnMedicationClickListener {
    void onMedicationClick(Medication medication);  // Medication 객체를 인자로 받음
    void onMedicationDelete(int position);
}
