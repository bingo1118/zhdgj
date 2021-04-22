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
import android.widget.Switch;
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

public class AreaListActivity extends AppCompatActivity {

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
    @Bind(R.id.gts)
    GetTeamDataSelectorView gts;

    private LinearLayoutManager linearLayoutManager;
    private AreaListAdapter mAdapter;
    private Context mContext;
    private List<Area> list;
    private AccountEntity mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list);

        ButterKnife.bind(this);
        mContext = this;
        mAccount=(AccountEntity) getIntent().getSerializableExtra("account");
        userid_tv.setText("账号:"+mAccount.getUserId());
        list = new ArrayList<>();
        refreshListView();
        getOwnAreaList(mAccount.getUserId());
        add_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindArea(gts.getCheckedModel().getModelId());
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
                getOwnAreaList(mAccount.getUserId());
            }
        });
    }

    private void getOwnAreaList(String userID) {
        Observable mObservable = BasePresenter.apiStores1.getOwnAreaList(mAccount.getUserId(),mAccount.getPrivilege()+"","");

        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<AllAreaEntity>() {
            @Override
            public void onSuccess(AllAreaEntity model) {
                int result = model.getErrorCode();
                if (result == 0) {
                    list = model.getList();
                    if(list.size()>0){
                        mAdapter = new AreaListAdapter(mContext, list,mAccount);
                        recyclerView.setAdapter(mAdapter);
                    }
                    swipereFreshLayout.setRefreshing(false);
                } else {
                    T.showShort(mContext, "无数据");
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


    private void bindArea(final String areaid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定绑定该区域?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String url = ConstantValues.SERVER_IP_NEW + "bindArea?userId=" + mAccount.getUserId()
                        + "&areaId=" + areaid;

                VolleyHelper helper = VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int code = response.getInt("errorCode");
                                    if (code == 0) {
                                        T.showShort(mContext, "成功");
                                        getOwnAreaList(mAccount.getUserId());
                                    } else {
                                        T.showShort(mContext, "已绑定该区域");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext, "网络错误");
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
            }
        });
        builder.show();
    }
}
