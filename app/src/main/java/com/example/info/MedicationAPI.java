package com.example.info;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Retrofit 인터페이스 정의
public interface MedicationAPI {

    // 약물 정보를 가져오기 위한 GET 요청 메서드 정의
    @GET("/get_combined_drug_info")
    Call<List<Medication>> getCombinedDrugInfo(
            @Query("itemName") String itemName,  // 약물 이름을 쿼리 파라미터로 전달
            @Query("pageNo") int pageNo  // 페이지 번호를 쿼리 파라미터로 전달
    );
}