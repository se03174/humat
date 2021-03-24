package com.example.humat;

public class itemData2 {
    // Member Variable -------------------------------------------------------------
    private String  food_name;
    private int     imgResId;

    // Constructor Method -----------------------------------------------------------
    public itemData2(String food_name, int imgResId) {
        this.food_name = food_name;
        this.imgResId = imgResId;
    }

    // Member Variable 제어 메서드 -----------------------------------------------------


    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }
}
