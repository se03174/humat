package com.example.humat;

import java.io.Serializable;

public class ChatData implements Serializable {
    private String msg;
    private String nickname;

    public String getMsg(){
        return msg;
    }
    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname=nickname;
    }
}
