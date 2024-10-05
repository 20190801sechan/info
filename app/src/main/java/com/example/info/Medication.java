package com.example.info;

public class Medication {

    private String itemName; // 약물명
    private String efficacy; // 약물 효능
    private String imageUrl; // 약물 이미지 URL

    // 생성자
    public Medication(String itemName, String efficacy, String imageUrl) {
        this.itemName = itemName;
        this.efficacy = efficacy;
        this.imageUrl = imageUrl;
    }

    // Getter 메서드
    public String getItemName() {
        return itemName;
    }

    public String getEfficacy() {
        return efficacy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setter 메서드
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setEfficacy(String efficacy) {
        this.efficacy = efficacy;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
