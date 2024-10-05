package com.example.info;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText ageInput;
    private RadioGroup genderGroup;
    private Switch pregnancySwitch;
    private Switch nursingSwitch;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalinfo);

        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderSection);
        pregnancySwitch = findViewById(R.id.pregnancySwitch);
        nursingSwitch = findViewById(R.id.nursingSwitch);
        resetButton = findViewById(R.id.resetButton);

        // 저장된 데이터 불러오기
        loadSavedData();

        // 초기화 버튼 클릭 리스너
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetToDefault();
            }
        });
    }

    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PersonalInfo", MODE_PRIVATE);

        // 나이 데이터
        String age = sharedPreferences.getString("age", "18");
        ageInput.setText(age);

        // 성별 데이터
        int genderId = sharedPreferences.getInt("gender", R.id.radioMale);
        genderGroup.check(genderId);

        // 임신부 여부
        boolean isPregnant = sharedPreferences.getBoolean("pregnancy", false);
        pregnancySwitch.setChecked(isPregnant);

        // 수유부 여부
        boolean isNursing = sharedPreferences.getBoolean("nursing", false);
        nursingSwitch.setChecked(isNursing);
    }

    public void onHomeIconClick(View view) {
        finish();  // MainActivity로 돌아가기
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 데이터를 저장
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PersonalInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 나이 저장
        editor.putString("age", ageInput.getText().toString());

        // 성별 저장
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        editor.putInt("gender", selectedGenderId);

        // 임신부 저장
        editor.putBoolean("pregnancy", pregnancySwitch.isChecked());

        // 수유부 저장
        editor.putBoolean("nursing", nursingSwitch.isChecked());

        editor.apply();  // 비동기로 저장
    }

    private void resetToDefault() {
        // 기본값으로 설정
        ageInput.setText("00");
        genderGroup.check(R.id.radioMale);
        pregnancySwitch.setChecked(false);
        nursingSwitch.setChecked(false);

        // 저장된 데이터 삭제
        SharedPreferences sharedPreferences = getSharedPreferences("PersonalInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // 저장된 모든 데이터 삭제
        editor.apply();  // 비동기로 저장
    }
}

