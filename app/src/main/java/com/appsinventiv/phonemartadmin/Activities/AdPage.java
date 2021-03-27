package com.appsinventiv.phonemartadmin.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;


import com.appsinventiv.phonemartadmin.Adapters.SliderAdapter;
import com.appsinventiv.phonemartadmin.Models.AdModel;
import com.appsinventiv.phonemartadmin.NetworkResponses.ApiResponse;
import com.appsinventiv.phonemartadmin.R;
import com.appsinventiv.phonemartadmin.Utils.AppConfig;
import com.appsinventiv.phonemartadmin.Utils.CommonUtils;
import com.appsinventiv.phonemartadmin.Utils.SharedPrefs;
import com.appsinventiv.phonemartadmin.Utils.UserClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.gson.JsonObject;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdPage extends AppCompatActivity {

    TextView title, price, price1, date1, category, time, description, views, username, viewMore, location;
    ViewPager mViewPager;

    SliderAdapter adapter;
    long viewCount = 0;
    String adId, adIdFromLink, adPicUrl;
    ProgressDialog progressDialog;
    LinearLayout call, sms, whatsapp;
    String phoneNumber;

    ImageView back;
    ImageView ad;
    LinearLayout favourite, report, shareAd;
    ArrayList<String> userLikedAds = new ArrayList<>();
    AdModel adModel;
    NestedScrollView scrollView;
    AdModel adDetails;
    String country;
    ImageView sponser;
    LinearLayout contactLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_page);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        this.setTitle("Ad Page");
        onNewIntent(getIntent());
        contactLayout = findViewById(R.id.contactLayout);

        sponser = findViewById(R.id.sponser);

        back = findViewById(R.id.back);
        report = findViewById(R.id.report);

        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        time = findViewById(R.id.time);
        username = findViewById(R.id.username);
        price1 = findViewById(R.id.price1);
        date1 = findViewById(R.id.date1);
        category = findViewById(R.id.category);
        viewMore = findViewById(R.id.viewMoreAds);
        description = findViewById(R.id.description);
        views = findViewById(R.id.views);
        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        whatsapp = findViewById(R.id.whatsapp);
        location = findViewById(R.id.location);
        favourite = findViewById(R.id.favourite);
        shareAd = findViewById(R.id.sharead);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        Intent intent = getIntent();
        adId = intent.getStringExtra("adId");
        init(adId);

        sponser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tecno-mobile.com/pk/home/#/"));
                startActivity(i);
            }
        });

        shareAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Please checkout this ad on Phone Mart.\n\nhttp://phonemart.co/ad/" + adId + "\n\nOr download app from PlayStore\nhttps://play.google.com/store/apps/details?id=com.appinventiv.phonemart");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Whatsapp");
                startActivity(Intent.createChooser(shareIntent, "Share ad via.."));
            }
        });



        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(i);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                startActivity(i);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!phoneNumber.startsWith("+92")) {
                    phoneNumber = phoneNumber.substring(1);
                    phoneNumber = "+92" + phoneNumber;
                }
                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
//

            }
        });

    }


    public void init(String id) {
        mViewPager = findViewById(R.id.viewPager);

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", adId);
        Call<ApiResponse> call = getResponse.viewAd(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    adDetails = response.body().getAdModel();
                    if (adDetails != null) {
                        adModel = adDetails;
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String formatedPrice = formatter.format(adDetails.getPrice());
                        title.setText(adDetails.getTitle());
                        location.setText(adDetails.getCity());
                        price.setText("Rs " + formatedPrice);
                        price1.setText("Rs " + formatedPrice);
                        time.setText(getFormattedDate(AdPage.this, adDetails.getTime()));
                        date1.setText(getFormattedDate(AdPage.this, adDetails.getTime()));
                        category.setText(adDetails.getCategory());
                        String[] imgs = adDetails.getImages().split(",");
                        List<String> stringList = new ArrayList<String>(Arrays.asList(imgs)); //new ArrayList is only needed if you absolutely need an ArrayList

                        adapter = new SliderAdapter(AdPage.this, stringList);
                        mViewPager.setAdapter(adapter);

                        description.setText("" + adDetails.getDescription());
                        phoneNumber = adDetails.getUser().getPhone();
                        username.setText(adDetails.getUser().getName());
                        if (adDetails.getUser().getPhone().startsWith("15")) {
                            contactLayout.setVisibility(View.GONE);
                        } else {
                            contactLayout.setVisibility(View.VISIBLE);

                        }


                    } else {
                        CommonUtils.showToast(response.message());
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                CommonUtils.showToast(t.getMessage());
            }
        });

    }
//


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        updateViews();

        finish();
    }


    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM ";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM , h:mm aa", smsTime).toString();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
