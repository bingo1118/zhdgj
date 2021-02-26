package com.smart.cloud.fire.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.activity.Electric.ElectricDevActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.main.DevByPlaceIdActivity;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ShopType shopType= placeTypeList.get(position);
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
        ((MyViewHolder) holder).more_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,((MyViewHolder) holder).more_iv);
                popupMenu.getMenuInflater().inflate(R.menu.menu_place_more,popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.rename) {
                            showRenameDialog(position,shopType.getPlaceTypeId(),shopType.getPlaceTypeName());
                        }else if(item.getItemId()==R.id.delete){
                            showDeleteDialog(position,shopType.getPlaceTypeId());
                        }
                        return false;
                    }
                });
                popupMenu.show();
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
        @Bind(R.id.more_iv)
        ImageView more_iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void showDeleteDialog(final int position, final String placeTypeId) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确认删除该分组?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url= ConstantValues.SERVER_IP_NEW+"deletePlace?userId="+ MyApp.getUserID()
                                +"&privilege="+ MyApp.getPrivilege()
                                +"&placeId="+ placeTypeId;

                        VolleyHelper helper=VolleyHelper.getInstance(mContext);
                        final RequestQueue mQueue = helper.getRequestQueue();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if(response.getInt("errorCode")==0){
                                                T.showShort(mContext,"删除成功");
                                                placeTypeList.remove(position);
                                                notifyDataSetChanged();
                                            }else{
                                                T.showShort(mContext,response.getString("error"));
                                            }
                                        } catch (JSONException e) {
                                            T.showShort(mContext,"删除失败");
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                T.showShort(mContext,"删除失败");
                            }
                        });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        mQueue.add(jsonObjectRequest);
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.show();
    }

    private void showRenameDialog(final int position, final String id, final String name) {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(mContext);
        editText.setText(name);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mContext);
        inputDialog.setTitle("分组重命名").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url= ConstantValues.SERVER_IP_NEW+"setPlaceTypeName?userId="+ MyApp.getUserID()
                                +"&privilege="+ MyApp.getPrivilege()
                                +"&placeId="+ id
                                +"&placeName="+ editText.getText().toString();

                        VolleyHelper helper=VolleyHelper.getInstance(mContext);
                        final RequestQueue mQueue = helper.getRequestQueue();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if(response.getInt("errorCode")==0){
                                                T.showShort(mContext,"修改成功");
                                                placeTypeList.get(position).setPlaceTypeName(editText.getText().toString());
                                                notifyDataSetChanged();
                                            }else{
                                                T.showShort(mContext,"修改失败");
                                            }
                                        } catch (JSONException e) {
                                            T.showShort(mContext,"修改失败");
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                T.showShort(mContext,"修改失败");
                            }
                        });
                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        mQueue.add(jsonObjectRequest);
                    }
                }).show();
    }
}
