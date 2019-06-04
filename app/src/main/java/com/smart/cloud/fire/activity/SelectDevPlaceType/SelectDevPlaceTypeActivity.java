package com.smart.cloud.fire.activity.SelectDevPlaceType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.adapter.SelectDevPlaceTypeAdapter;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class SelectDevPlaceTypeActivity extends Activity {

    @Bind(R.id.place_items)
    RecyclerView place_items;

    Context mContext;
    String userid;
    int privilege;
    private List<ShopType> placeTypeList;
    private SelectDevPlaceTypeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dev_place_type);

        ButterKnife.bind(this);
        mContext=this;
        userid = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        getData(userid);
        initView();
    }

    private void getData(String userid) {
        placeTypeList=new ArrayList<>();
        String url="";
            url= ConstantValues.SERVER_IP_NEW+"getPlaceTypeId?userId="+userid+"&privilege="+privilege;

        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        final RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            placeTypeList.clear();
                            if(response.getInt("errorCode")==0){
                                JSONArray array=response.getJSONArray("placeType");
                                for(int i=0;i<array.length();i++){
                                    JSONObject o= array.getJSONObject(i);
                                    ShopType temp=new ShopType();
                                    temp.setPlaceTypeId(o.getString("placeTypeId"));
                                    temp.setPlaceTypeName(o.getString("placeTypeName"));
                                    placeTypeList.add(temp);
                                }
                            }
                            adapter=new SelectDevPlaceTypeAdapter(mContext,placeTypeList);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                            place_items.setLayoutManager(layoutManager);
                            //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
                            place_items.setHasFixedSize(true);
                            //创建并设置Adapter
                            place_items.setAdapter(adapter);
                            place_items.setItemAnimator(new DefaultItemAnimator());
                            place_items.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                        } catch (JSONException e) {
                            T.showShort(mContext,"无位置信息");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"网络错误");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    private void initView() {

    }
}
