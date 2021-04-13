package com.smart.cloud.fire.activity.AccountManage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;

public class AccountManageActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.add_user_btn)
    ImageView add_user_btn;


    private LinearLayoutManager linearLayoutManager;
    private AccountListAdapter mAdapter;
    private Context mContext;
    private List<AccountEntity> list;
    private int loadMoreCount;
    private boolean research = false;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);

        ButterKnife.bind(this);
        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        list = new ArrayList<>();
        refreshListView();
        getSubAccount(userID);
        add_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
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
                getSubAccount(userID);
            }
        });
    }

    private void getSubAccount(String userID) {
        Observable mObservable = BasePresenter.apiStores1.getSubAccountList(userID);

        BasePresenter.addSubscription(mObservable, new SubscriberCallBack<>(new ApiCallback<AllAccountEntity>() {
            @Override
            public void onSuccess(AllAccountEntity model) {
                int result = model.getErrorCode();
                if (result == 0) {
                    list = model.getList();
                    mAdapter = new AccountListAdapter(mContext, list);
                    recyclerView.setAdapter(mAdapter);
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


    private void addUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_account_view, null);
        final EditText name_et = (EditText) view.findViewById(R.id.name_et);
        final EditText phone_et = (EditText) view.findViewById(R.id.phone_et);
        final EditText pwd_et = (EditText) view.findViewById(R.id.pwd_et);
        final EditText grade_et = (EditText) view.findViewById(R.id.grade_et);
        final EditText p_userid_et = (EditText) view.findViewById(R.id.p_userid_et);
        Button commit_btn = (Button) view.findViewById(R.id.commit);
        final int grade = 1;
        String gradeString = "个人账号";
        switch (grade) {
            case 0:
                gradeString = "超级账号";
                break;
            case 1:
                gradeString = "一级账号";
                break;
            case 2:
                gradeString = "二级账号";
                break;
            case 3:
                gradeString = "三级账号";
                break;
            case 4:
                gradeString = "个人账号";
                break;


        }
        grade_et.setText(gradeString);
        final Switch add_enable = (Switch) view.findViewById(R.id.add_enable);
        final Switch cut_enable = (Switch) view.findViewById(R.id.cut_enable);
        final Switch txt_enable = (Switch) view.findViewById(R.id.txt_enable);

        final Dialog dialog = builder.setView(view).create();
        dialog.show();
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int cut = cut_enable.isChecked() ? 1 : 0;
                final int add = add_enable.isChecked() ? 1 : 0;
                final int txt = txt_enable.isChecked() ? 1 : 0;

                String url = ConstantValues.SERVER_IP_NEW + "addSubAccount?userId=" + phone_et.getText().toString()
                        + "&name=" + name_et.getText().toString()
                        + "&pwd="+pwd_et.getText().toString()
                        + "&grade="+grade
                        + "&p_userid="+p_userid_et.getText().toString()
                        + "&cut=" + cut
                        + "&add=" + add
                        + "&txt=" + txt;

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
                                    } else {
                                        T.showShort(mContext, response.getString("error"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
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
