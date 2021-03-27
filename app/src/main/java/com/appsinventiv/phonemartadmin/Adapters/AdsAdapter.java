package com.appsinventiv.phonemartadmin.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.appsinventiv.phonemartadmin.Activities.AdPage;
import com.appsinventiv.phonemartadmin.Models.AdModel;
import com.appsinventiv.phonemartadmin.R;
import com.appsinventiv.phonemartadmin.Utils.AppConfig;
import com.appsinventiv.phonemartadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by AliAh on 09/01/2018.
 */

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {

    List<AdModel> itemList;
    Context ctx;
    AdsCallback callback;

    public AdsAdapter(Context ctx, List<AdModel> itemList, AdsCallback callback) {
        this.itemList = itemList;
        this.ctx = ctx;
        this.callback = callback;
    }

    public void setItemList(List<AdModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_layout, parent, false);
        AdsAdapter.ViewHolder viewHolder = new AdsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AdModel model = itemList.get(position);
        holder.title.setText(model.getTitle());
        holder.status.setText(model.getStatus());
        holder.location.setText(model.getArea() + ", " + model.getCity());
        holder.price.setText("Rs " + model.getPrice());
        holder.time.setText(CommonUtils.getFormattedDate(model.getTime()));
//        Glide.with(ctx).load(model.getImages()).into(holder.thumbnail);
        String[] imagesList = model.getImages().split(",");
        if (imagesList[0].contains("apollo-singapore")) {
            Glide.with(ctx).load(imagesList[0] + ";s=200x200").into(holder.thumbnail);

        } else {
            Glide.with(ctx).load(AppConfig.BASE_URL_Image + imagesList[0]).into(holder.thumbnail);
        }
        if (model.getStatus().equalsIgnoreCase("active")) {
            holder.activeSwtich.setChecked(true);
        } else {
            holder.activeSwtich.setChecked(false);
        }
        holder.activeSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        model.setStatus("active");
                    } else {
                        model.setStatus("pending");
                    }
                    holder.status.setText(model.getStatus());
                    callback.onChangeStatus(model, isChecked);
                }
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setStatus("rejected");
                holder.status.setText(model.getStatus());
                callback.onReject(model);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, AdPage.class);
                i.putExtra("adId", "" + model.getId());
                ctx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, time, location, status;
        Switch activeSwtich;
        ImageView thumbnail, reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            activeSwtich = itemView.findViewById(R.id.activeSwtich);
            location = itemView.findViewById(R.id.location);
            reject = itemView.findViewById(R.id.reject);
            status = itemView.findViewById(R.id.status);

        }
    }

    public interface AdsCallback {
        public void onChangeStatus(AdModel model, boolean isChecked);

        public void onReject(AdModel model);

    }
}
