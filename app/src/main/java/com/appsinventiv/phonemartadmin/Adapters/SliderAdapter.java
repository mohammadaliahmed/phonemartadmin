package com.appsinventiv.phonemartadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.appsinventiv.phonemartadmin.R;
import com.appsinventiv.phonemartadmin.Utils.AppConfig;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by AliAh on 25/12/2017.
 */

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public  List<String> pictures;


    public SliderAdapter(Context context, List<String> pictures) {
        this.context = context;

        this.pictures = pictures;
    }


    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout) object);

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.product_slider, container, false);
        ImageView imageView = view.findViewById(R.id.slider_image);
        if (pictures.get(position).contains("apollo-singapore")) {
            Glide.with(context)
                    .load(pictures.get(position) + ";s=500x500")
                    .into(imageView);

        } else {
            Glide.with(context)
                    .load(AppConfig.BASE_URL_Image + pictures.get(position))
                    .into(imageView);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
