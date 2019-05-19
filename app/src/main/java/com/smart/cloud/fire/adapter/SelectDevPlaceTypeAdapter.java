package com.smart.cloud.fire.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.cloud.fire.activity.Electric.ElectricDevActivity;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.main.DevByPlaceIdActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Rain on 2019/4/1.
 */
public class SelectDevPlaceTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ShopType> placeTypeList;
    private Context mContext;
    private LayoutInflater mInflater;

    public SelectDevPlaceTypeAdapter(Context mContext, List<ShopType> placeTypeList) {
        this.placeTypeList = placeTypeList;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.place_item, parent, false);
        //这边可以做一些属性设置，甚至事件监听绑定
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ShopType shopType=placeTypeList.get(position);
        ((MyViewHolder) holder).textView.setText(shopType.getPlaceTypeName());
        ((MyViewHolder) holder).textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, DevByPlaceIdActivity.class);
                intent.putExtra("ID",shopType.getPlaceTypeId());
                intent.putExtra("NAME",shopType.getPlaceTypeName());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placeTypeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.textview)
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
