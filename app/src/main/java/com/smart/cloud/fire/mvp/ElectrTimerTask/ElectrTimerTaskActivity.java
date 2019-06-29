package com.smart.cloud.fire.mvp.ElectrTimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.mvp.electric.AutoTimeSettingActivity;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.view.WheelView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class ElectrTimerTaskActivity extends MvpActivity<ElectrTimerTaskPresenter> implements ElectrTimerTaskView{

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.add)
    TextView add;



    ElectrTimerTaskAdapter mAdapter;
    ElectrTimerTaskPresenter mPresenter;
    private Context mContext;
    private String mac;
    private List<TimerTaskEntity> list;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electr_timer_task);

        ButterKnife.bind(this);
        mContext=this;
        mac=getIntent().getStringExtra("mac");
        list = new ArrayList<>();
        refreshListView();
        mPresenter.getAllTimerTask(mac);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, AutoTimeSettingActivity.class);
                i.putExtra("mac",mac);
                startActivity(i);
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
        linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //下拉刷新。。
        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getAllTimerTask(mac);
            }
        });
    }

    @Override
    protected ElectrTimerTaskPresenter createPresenter() {
        mPresenter=new ElectrTimerTaskPresenter(this);
        return null;
    }

    @Override
    public void getDataSuccess(List<?> smokeList, boolean research) {
        if(list!=null){
            list.clear();
        }
        list.addAll((List<TimerTaskEntity>)smokeList);
        linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ElectrTimerTaskAdapter(mContext, list,mPresenter);
        recyclerView.setAdapter(mAdapter);
        swipereFreshLayout.setRefreshing(false);
    }

    @Override
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }
}
