package com.smart.cloud.fire.activity.AccountManage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.base.ui.BaseActivity;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;

import static com.smart.cloud.fire.global.MyApp.entity;

public class UserOfAreaActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.add_user_btn)
    ImageView add_user_btn;


    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.tip_tv)
    LinearLayout tip_tv;


    private LinearLayoutManager linearLayoutManager;
    private UserOfAreaAdapter mAdapter;
    private Context mContext;
    private List<AccountEntity> list;
    private Area mArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_of_area);

        ButterKnife.bind(this);
        mContext = this;
        mArea=(Area) getIntent().getSerializableExtra("area");
        name_tv.setText("名称:"+mArea.getAreaName());
        list = new ArrayList<>();
        mAdapter = new UserOfAreaAdapter(mContext, list,mArea);
        recyclerView.setAdapter(mAdapter);
        refreshListView();
        getSubAccount(mArea.getAreaId());
        add_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(add_user_btn);
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
                getSubAccount(mArea.getAreaId());
            }
        });
    }

    private void getSubAccount(String areaId) {
        Observable mObservable = BasePresenter.apiStores1.getAccountOfArea(areaId);

        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<AllAccountEntity>() {
            @Override
            public void onSuccess(AllAccountEntity model) {
                int result = model.getErrorCode();
                if (result == 0) {
                    list = model.getList();
                    if(list.size()>0){
                        mAdapter.changeDatas(list);
                        tip_tv.setVisibility(View.GONE);
                    }else{
                        tip_tv.setVisibility(View.VISIBLE);
                    }
                } else {
                    list.clear();
                    mAdapter.changeDatas(list);
                    T.showShort(mContext, "无数据");
                    tip_tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                T.showShort(mContext, "网络错误");
            }

            @Override
            public void onCompleted() {
                mProgressBar.setVisibility(View.GONE);
                swipereFreshLayout.setRefreshing(false);
            }
        }));
    }



    private void showPopupMenu(final View view) {
        if(entity.getGrade()>1){
            T.showShort(mContext,"您没有添加用户权限");
            return;
        }

        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_bind_account_of_area, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.phone:
                        bindByPhone();
                        break;
//                    case R.id.other:
////                        bindByOther();
//                        break;
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    private void bindByOther() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.bind_account_by_other_view, null);

//        final EditText phone_et = (EditText) view.findViewById(R.id.phone_et);
        Button commit_btn = (Button) view.findViewById(R.id.commit);

        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = "";
                if(Utils.isNullString(phone)){
                    T.showShort(mContext,"请先完善信息");
                    return;
                }

                String url = ConstantValues.SERVER_IP_NEW + "bindParentArea?userId=" + phone
                        + "&areaId=" + mArea.getAreaId() ;

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
                                        getSubAccount(MyApp.entity.getUserId());
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

    private void bindByPhone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.bind_account_by_phone_view, null);

        final EditText phone_et = (EditText) view.findViewById(R.id.phone_et);
        Button commit_btn = (Button) view.findViewById(R.id.commit);

        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = phone_et.getText().toString();
                if(Utils.isNullString(phone)){
                    T.showShort(mContext,"请先完善信息");
                    return;
                }

                String url = ConstantValues.SERVER_IP_NEW + "bindArea?userId=" + phone
                        + "&areaId=" + mArea.getAreaId() ;

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
                                        getSubAccount(mArea.getAreaId());
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

