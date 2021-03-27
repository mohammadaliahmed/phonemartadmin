package com.appsinventiv.phonemartadmin.Activities;

import android.os.Bundle;


import com.appsinventiv.phonemartadmin.Adapters.AdsAdapter;
import com.appsinventiv.phonemartadmin.Models.AdModel;
import com.appsinventiv.phonemartadmin.NetworkResponses.ApiResponse;
import com.appsinventiv.phonemartadmin.R;
import com.appsinventiv.phonemartadmin.Utils.AppConfig;
import com.appsinventiv.phonemartadmin.Utils.CommonUtils;
import com.appsinventiv.phonemartadmin.Utils.NotificationAsync;
import com.appsinventiv.phonemartadmin.Utils.SharedPrefs;
import com.appsinventiv.phonemartadmin.Utils.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {

    RecyclerView recycler;
    AdsAdapter adsAdapter;
    private List<AdModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adsAdapter = new AdsAdapter(this, itemList, new AdsAdapter.AdsCallback() {
            @Override
            public void onChangeStatus(AdModel model, boolean isChecked) {
                approveAd(model, isChecked);
            }

            @Override
            public void onReject(AdModel model) {
                rejectAd(model);
            }
        });
        recycler.setAdapter(adsAdapter);

        getDataFromServer();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                SharedPrefs.setFcmKey(token);
                updateFcmKey(token);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.showToast(e.getMessage());
            }
        });

    }

    private void rejectAd(AdModel model) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", model.getId());
        Call<ApiResponse> call = getResponse.rejectAd(map);


        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Ad Rejected");
                    if (response.body().getFcm_key() != null) {
                        sendNotification(response.body().getFcm_key(), "You ad has been rejected", "Click to view");
                    }
                    //TODO
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void getDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);

        Call<ApiResponse> call = getResponse.getRecentAds(map);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getAdsList() != null && response.body().getAdsList().size() > 0) {
                        itemList = response.body().getAdsList();
                        adsAdapter.setItemList(itemList);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });
    }

    private void updateFcmKey(String fcmKey) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("fcmKey", fcmKey);

        Call<ApiResponse> call = getResponse.updateFcmKey(map);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }


    private void sendNotification(String to, String title, String message) {

        NotificationAsync notificationAsync = new NotificationAsync(this);

        notificationAsync.execute(
                "ali",
                to,
                title,
                message,
                "type",
                "approved");
    }

    private void approveAd(AdModel model, boolean isChecked) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", model.getId());
        Call call = null;
        if (isChecked) {
            call = getResponse.approveAd(map);
        } else {
            call = getResponse.pendingAd(map);
        }

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    CommonUtils.showToast("Done");
                    if (isChecked) {
                        if (response.body().getFcm_key() != null) {
                            sendNotification(response.body().getFcm_key(), "You ad is approved", "Click to view");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
}
