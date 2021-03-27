package com.appsinventiv.phonemartadmin.NetworkResponses;

import com.appsinventiv.phonemartadmin.Models.AdModel;
import com.appsinventiv.phonemartadmin.Models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private User user = null;
    @SerializedName("fcm_key")
    @Expose
    private String fcm_key = null;
    @SerializedName("ads")
    @Expose
    private List<AdModel> adsList = null;

    public String getFcm_key() {
        return fcm_key;
    }

    public void setFcm_key(String fcm_key) {
        this.fcm_key = fcm_key;
    }

    public List<AdModel> getAdsList() {
        return adsList;
    }

    public void setAdsList(List<AdModel> adsList) {
        this.adsList = adsList;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
