package com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.Electric;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.activity.Electric.ElectricDevActivity;
import com.smart.cloud.fire.activity.Electric.ElectricDevPresenter;
import com.smart.cloud.fire.activity.Electric.ElectricDevView;
import com.smart.cloud.fire.adapter.ElectricFragmentAdapter;
import com.smart.cloud.fire.adapter.ShopCameraAdapter;
import com.smart.cloud.fire.adapter.ShopSmokeAdapter;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.electric.ElectricActivity;
import com.smart.cloud.fire.mvp.electric.ElectricDXActivity;
import com.smart.cloud.fire.mvp.electric.ElectricSXActivity;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragment;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentPresenter;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentView;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/11/1.
 */
public class ElectricFragment extends MvpFragment<ElectricDevPresenter> implements ElectricDevView {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.smoke_total)
    LinearLayout smokeTotal;//@@9.5
    @Bind(R.id.total_num)
    TextView totalNum;
    @Bind(R.id.online_num)
    TextView onlineNum;
    @Bind(R.id.offline_num)
    TextView offlineNum;
    private ElectricFragmentAdapter electricFragmentAdapter;
    private ElectricDevPresenter electricDevPresenter;
    private Context mContext;
    private String userID;
    private int privilege;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private List<Electric> list;
    private int loadMoreCount;
    private boolean research = false;
    private String page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_electric, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        refreshListView();
        smokeTotal.setVisibility(View.VISIBLE);//@@9.5
        list = new ArrayList<>();
        page = "1";
        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,false,ElectricFragment.this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","3",ElectricFragment.this);//@@9.5
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

        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                refreshView();
                ((ElectricDevActivity)getActivity()).refreshView();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (research) {
                    if(electricFragmentAdapter!=null){
                        electricFragmentAdapter.changeMoreStatus(ShopCameraAdapter.NO_DATA);
                    }
                    return;
                }
                int count = electricFragmentAdapter.getItemCount();
                int itemCount = lastVisibleItem+1;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemCount == count) {
                    if(loadMoreCount>=20){
                        page = Integer.parseInt(page) + 1 + "";
                        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,true,ElectricFragment.this);
                    }else{
                        T.showShort(mContext,"已经没有更多数据了");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected ElectricDevPresenter createPresenter() {
        electricDevPresenter = new ElectricDevPresenter((ElectricDevActivity)getActivity());
        return electricDevPresenter;
    }

    public void refreshView() {
        page = "1";
        list.clear();
        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,true,ElectricFragment.this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","3",ElectricFragment.this);
    }

    @Override
    public String getFragmentName() {
        return "ElectricFragment";
    }

    @Override
    public void getDataSuccess(List<?> smokeList,boolean search) {
        loadMoreCount = smokeList.size();
        list.clear();
        list.addAll((List<Electric>)smokeList);
        electricFragmentAdapter = new ElectricFragmentAdapter(mContext, list);
        recyclerView.setAdapter(electricFragmentAdapter);
        swipereFreshLayout.setRefreshing(false);
        electricFragmentAdapter.changeMoreStatus(ShopSmokeAdapter.NO_DATA);
        electricFragmentAdapter.setOnItemClickListener(new ElectricFragmentAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view, Electric data){
                Intent intent ;
                if(data.getDeviceType()==6){
                    intent = new Intent(mContext, ElectricDXActivity.class);
                }else if(data.getDeviceType()==7){
                    intent = new Intent(mContext, ElectricSXActivity.class);
                }else{
                    intent = new Intent(mContext, ElectricActivity.class);
                }
                intent.putExtra("ElectricMac",data.getMac());
                intent.putExtra("devType",data.getDeviceType());
                intent.putExtra("repeatMac",data.getRepeater());
                startActivity(intent);
            }
        });
    }

    @Override
    public void getDataFail(String msg) {
        swipereFreshLayout.setRefreshing(false);
        T.showShort(mContext, msg);
        if(electricFragmentAdapter!=null){
            electricFragmentAdapter.changeMoreStatus(ShopSmokeAdapter.NO_DATA);
        }
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingMore(List<?> smokeList) {
        loadMoreCount = smokeList.size();
        list.addAll((List<Electric>)smokeList);
        electricFragmentAdapter.changeMoreStatus(ShopSmokeAdapter.LOADING_MORE);
    }

    @Override
    public void getLostCount(String count) {

    }

    @Override
    public void getAreaType(ArrayList<?> shopTypes, int type) {
    }

    @Override
    public void getAreaTypeFail(String msg, int type) {

    }

    @Override
    public void unSubscribe(String type) {
    }

    @Override
    public void getChoiceArea(Area area) {
    }

    @Override
    public void getChoiceShop(ShopType shopType) {
    }

    @Override
    public void getSmokeSummary(SmokeSummary smokeSummary) {
        totalNum.setText(smokeSummary.getAllSmokeNumber()+"");
        onlineNum.setText(smokeSummary.getOnlineSmokeNumber()+"");
        offlineNum.setText(smokeSummary.getLossSmokeNumber()+"");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
