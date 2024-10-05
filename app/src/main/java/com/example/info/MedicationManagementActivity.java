package com.example.info;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MedicationManagementActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "medication_prefs";
    private static final String MEDICATION_LIST_KEY = "medication_list";
    private static final String BASE_URL = "http://10.0.2.2:5000/";  // Flask 서버 URL
    private RecyclerView medicationList;
    private MedicationAdapter adapter;
    private List<Medication> medications = new ArrayList<>();
    private Gson gson = new Gson();
    private MedicationAPI medicationAPI;
    private int currentPage = 1;  // 페이징을 위한 현재 페이지 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medication_management);

        // OkHttp 클라이언트에 타임아웃 설정 및 HTTP/1.1 강제 사용
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // 연결 타임아웃 60초로 증가
                .readTimeout(60, TimeUnit.SECONDS)     // 읽기 타임아웃 60초로 증가
                .writeTimeout(60, TimeUnit.SECONDS)    // 쓰기 타임아웃 60초로 증가
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))  // HTTP/1.1 강제 사용
                .build();

        // Retrofit 인스턴스 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)  // OkHttp 클라이언트 설정 추가
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        medicationAPI = retrofit.create(MedicationAPI.class);

        medicationList = findViewById(R.id.medicationList);
        Button addMedicationButton = findViewById(R.id.addMedicationButton);
        ImageView homeIcon = findViewById(R.id.homeIcon);

        // RecyclerView 설정
        adapter = new MedicationAdapter(medications, new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onMedicationClick(Medication medication) {
                // 약물 클릭 시 이벤트 처리
            }

            @Override
            public void onMedicationDelete(int position) {
                deleteMedication(position);
            }

            @Override
            public void onAddMedicationClick(Medication medication) {
                // 약물 추가 시 처리
                medications.add(medication);
                adapter.notifyDataSetChanged();
                saveMedications();
                Toast.makeText(MedicationManagementActivity.this, "약물이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        medicationList.setLayoutManager(new LinearLayoutManager(this));
        medicationList.setAdapter(adapter);

        // 저장된 약물 목록 로드
        loadMedications();

        // '약 추가' 버튼 클릭 시 검색 다이얼로그 띄우기
        addMedicationButton.setOnClickListener(v -> showSearchDialog());

        // 홈 아이콘 클릭 시 메인 화면으로 이동
        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MedicationManagementActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });
    }

    // 검색 창을 띄우는 메서드
    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("약물 검색");

        // SearchView와 "추가" 버튼을 담은 레이아웃 생성
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        SearchView searchView = dialogView.findViewById(R.id.searchView);

        // 검색창에서 검색어 입력
        searchView.setQueryHint("검색어를 입력하세요");

        // 검색 이벤트 처리
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    currentPage = 1;  // 새 검색을 시작할 때 페이지 번호를 초기화
                    searchForMedication(query, dialogView);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // '닫기' 버튼 추가
        builder.setNegativeButton("닫기", (dialog, which) -> dialog.dismiss());

        builder.setView(dialogView);
        builder.show();
    }

    // 약물 검색 API 호출 메서드
    private void searchForMedication(String query, View dialogView) {
        // Flask API로 요청을 보냄 (itemName과 pageNo 전달)
        Call<List<Medication>> call = medicationAPI.getCombinedDrugInfo(query, currentPage);
        call.enqueue(new Callback<List<Medication>>() {
            @Override
            public void onResponse(Call<List<Medication>> call, Response<List<Medication>> response) {
                if (response.isSuccessful()) {
                    List<Medication> results = response.body();
                    if (results != null && !results.isEmpty()) {
                        displaySearchResults(results, dialogView);
                        currentPage++;  // 페이지 증가
                    } else {
                        Toast.makeText(MedicationManagementActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API Error", "Response Code: " + response.code());
                    try {
                        // 응답 본문을 문자열로 로그로 출력
                        Log.e("API Error", "Response Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Error", "Failed to read response body", e);
                    }
                    Toast.makeText(MedicationManagementActivity.this, "API 요청 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Medication>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch data", t);
                Toast.makeText(MedicationManagementActivity.this, "API 요청 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 검색 결과를 다이얼로그 내에 표시하는 메서드
    private void displaySearchResults(List<Medication> results, View dialogView) {
        RecyclerView searchResultsList = dialogView.findViewById(R.id.searchResultsList);
        searchResultsList.setLayoutManager(new LinearLayoutManager(this));

        MedicationAdapter searchAdapter = new MedicationAdapter(results, new MedicationAdapter.OnMedicationClickListener() {
            @Override
            public void onMedicationClick(Medication medication) {
                // 선택한 약물을 복용 중인 약물 목록에 추가
                medications.add(medication);
                adapter.notifyDataSetChanged();
                saveMedications();
            }

            @Override
            public void onMedicationDelete(int position) {
                // 삭제 기능은 필요 없다면 비워둘 수 있음
            }

            @Override
            public void onAddMedicationClick(Medication medication) {
                // 복용 중인 약물 목록에 추가
                medications.add(medication);
                adapter.notifyDataSetChanged();
                saveMedications();
                Toast.makeText(MedicationManagementActivity.this, "약물이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        searchResultsList.setAdapter(searchAdapter);
    }

    // 약물 삭제 메서드
    private void deleteMedication(int position) {
        medications.remove(position);
        adapter.notifyItemRemoved(position);
        saveMedications();
    }

    // 약물 목록을 SharedPreferences에 저장하는 메서드
    private void saveMedications() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 약물 목록을 JSON으로 변환하여 저장
        String json = gson.toJson(medications);
        editor.putString(MEDICATION_LIST_KEY, json);
        editor.apply();
    }

    // 약물 목록을 SharedPreferences에서 불러오는 메서드
    private void loadMedications() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(MEDICATION_LIST_KEY, null);

        // 저장된 약물이 있다면 복원
        if (json != null) {
            Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
            medications = gson.fromJson(json, type);
            adapter = new MedicationAdapter(medications, new MedicationAdapter.OnMedicationClickListener() {
                @Override
                public void onMedicationClick(Medication medication) {
                    // 처리할 로직
                }

                @Override
                public void onMedicationDelete(int position) {
                    deleteMedication(position);
                }

                @Override
                public void onAddMedicationClick(Medication medication) {
                    // 약물 추가 처리
                    medications.add(medication);
                    adapter.notifyDataSetChanged();
                    saveMedications();
                }
            });
            medicationList.setAdapter(adapter);
        }
    }
}
