package com.example.humat;

public class RecommenderDTO {
    String name;
    String data_and_time;
    String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData_and_time() {
        return data_and_time;
    }

    public void setData_and_time(String data_and_time) {
        this.data_and_time = data_and_time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public RecommenderDTO(String name, String data_and_time, String comment) {
        this.name = name;
        this.data_and_time = data_and_time;
        this.comment = comment;
    }

    public RecommenderDTO() {
    }
}
