package com.smart.cloud.fire.activity.AccountManage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.dataSelector.GetTeamDataSelectorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;

public class OwnAreaListActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.add_user_btn)
    ImageView add_user_btn;

    @Bind(R.id.userid_tv)
    TextView userid_tv;

    private LinearLayoutManager linearLayoutManager;
    private OwnAreaListAdapter mAdapter;
    private Context mContext;
    private List<Area> list;
    private AccountEntity mAccount;
    private Area mArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_area_list);

        ButterKnife.bind(this);
        mContext = this;
        mAccount=(AccountEntity) getIntent().getSerializableExtra("account");
        mArea=(Area) getIntent().getSerializableExtra("area");
        userid_tv.setText("账号:"+mAccount.getUserId());
        list = new ArrayList<>();
        refreshListView();
        getOwnAreaList();
        add_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArea();
            }
        });
    }

    private void refreshListView() {
        //设置刷新时动画的颜色，可以设置4个
        swipereFreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipereFreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipereFreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mProgressBar.setVisibility(View.VISIBLE);

        //下拉刷新。。
        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOwnAreaList();
            }
        });
    }

    private void getOwnAreaList() {
        Observable mObservable = BasePresenter.apiStores1.getOwnAreaList(mAccount.getUserId(),mAccount.getPrivilege()+"",mArea.getAreaId());

        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<AllAreaEntity>() {
            @Override
            public void onSuccess(AllAreaEntity model) {
                int result = model.getErrorCode();
                if (result == 0) {
                    list = model.getList();
                    if(list.size()>0){
                        mAdapter = new OwnAreaListAdapter(mContext, list,mAccount);
                        recyclerView.setAdapter(mAdapter);
                    }else{

                    }
                    swipereFreshLayout.setRefreshing(false);
                } else {
                    T.showShort(mContext, "无数据");
                    swipereFreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                T.showShort(mContext, "网络错误");
            }

            @Override
            public void onCompleted() {
                mProgressBar.setVisibility(View.GONE);
            }
        }));
    }


    private void addArea() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_area_view, null);
        final EditText name_et = (EditText) view.findViewById(R.id.name_et);
        Button commit_btn = (Button) view.findViewById(R.id.commit);

        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=name_et.getText().toString();
                if(Utils.isNullString(name)){
                    T.showShort(mContext,"请先完善信息");
                    return;
                }

                String url = ConstantValues.SERVER_IP_NEW + "addArea?userId=" + mAccount.getUserId()
                        + "&areaName=" + name
                        + "&pAreaId="+mArea.getAreaId();

                VolleyHelper helper = VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int code = response.getInt("errorCode");
                                    if (code == 0) {
                                        T.showShort(mContext, "添加成功");
                                        getOwnAreaList();
                                        dialog.dismiss();
                                    } else {
                                        T.showShort(mContext, response.getString("error"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext, "网络错误");
                        dialog.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
            }
        });
    }
}


