package com.example.humat;

public class UserDTO {
    private String email;
    private String name;
    private String uid;

    public UserDTO(String email, String name, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
    }

    public UserDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
