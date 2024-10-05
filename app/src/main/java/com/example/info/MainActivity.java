package com.example.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 홈 아이콘 클릭 리스너
        ImageView homeIcon = findViewById(R.id.homeIcon);
        homeIcon.setOnClickListener(this::onHomeIconClick);

        // 개인정보 관리 아이콘 클릭 리스너
        ImageView personalInfoIcon = findViewById(R.id.personalInfoIcon);
        personalInfoIcon.setOnClickListener(this::onPersonalInfoIconClick);

        // 복용 약 관리 아이콘 클릭 리스너
        ImageView medicationManagementIcon = findViewById(R.id.medicineManagementIcon);
        medicationManagementIcon.setOnClickListener(this::onMedicineManagementIconClick);
    }

    // '홈' 아이콘 클릭 시
    public void onHomeIconClick(View view) {
        // 홈 화면으로 이동 (여기에 로직 추가 가능)
    }

    // '개인정보 관리' 아이콘 클릭 시 PersonalInfoActivity로 이동
    public void onPersonalInfoIconClick(View view) {
        Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
        startActivity(intent);
    }

    // '복용 약 관리' 아이콘 클릭 시 MedicationManagementActivity로 이동
    public void onMedicineManagementIconClick(View view) {
        Intent intent = new Intent(MainActivity.this, MedicationManagementActivity.class);
        startActivity(intent);
    }
}
