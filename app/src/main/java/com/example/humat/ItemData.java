package com.example.humat;

//ListView 표시될 한 개의 Item
public class ItemData {
    // Member Variable-------------------------------------------------------
    private String name;
    private String phone;
    private String address;

    // Constructor Method ---------------------------------------------------
    public ItemData(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // Member Variable 제어 메서드 ---------------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
