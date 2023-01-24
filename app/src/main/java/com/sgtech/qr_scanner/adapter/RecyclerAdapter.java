package com.sgtech.qr_scanner.adapter;

import static com.sgtech.qr_scanner.KeyClass.*;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtech.qr_scanner.*;
import com.sgtech.qr_scanner.KeyClass;
import com.sgtech.qr_scanner.activities.ResultActivity;
import com.sgtech.qr_scanner.database.DataModel;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<DataModel> arrayList;

    public RecyclerAdapter(Context context, ArrayList<DataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel model = arrayList.get(arrayList.size() - 1 - position);
        holder.typeTxt.setText(model.getData_type());
        holder.typeTxt.setSelected(true);
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra("SEE_DATA", "Yes");
        if (Objects.equals(model.getData_type(), URL_VALUE)) {
            holder.typeImg.setImageResource(R.drawable.link_icon);
            intent.putExtra(TYPE_VALUE, URL_VALUE);
            intent.putExtra(URL_URL, model.getData_name());
        } else if (Objects.equals(model.getData_type(), TEXT_TYPE)) {
            holder.typeImg.setImageResource(R.drawable.text_fields_icon);
            intent.putExtra(TYPE_VALUE, TEXT_TYPE);
            intent.putExtra(TEXT_TITLE, model.getData_name());
        } else if (Objects.equals(model.getData_type(), KeyClass.PHONE_TYPE)) {
            holder.typeImg.setImageResource(R.drawable.phone_icon);
            intent.putExtra(TYPE_VALUE, PHONE_TYPE);
            intent.putExtra(PHONE_NUMBER, String.valueOf(model.getData_name()));
            context.startActivity(intent);
        } else if (Objects.equals(model.getData_type(), KeyClass.WIFI_TYPE)) {
            holder.typeImg.setImageResource(R.drawable.wifi_icon);
            intent.putExtra(TYPE_VALUE, WIFI_TYPE);
            intent.putExtra(WIFI_SSID, model.getData_name());
            context.startActivity(intent);
        }
        holder.layout.setOnClickListener(v -> context.startActivity(intent));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView typeImg;
        TextView typeTxt;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTxt = itemView.findViewById(R.id.type);
            layout = itemView.findViewById(R.id.layout);
            typeImg = itemView.findViewById(R.id.typeImg);
        }
    }
}
