package com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.AllDevFragment;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.cloud.fire.activity.AllSmoke.AllSmokeActivity;
import com.smart.cloud.fire.activity.AllSmoke.AllSmokePresenter;
import com.smart.cloud.fire.adapter.ShopCameraAdapter;
import com.smart.cloud.fire.adapter.ShopSmokeAdapter;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragment;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentPresenter;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentView;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.XCDropDownListViewMapSearch;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;


/**
 * Created by Administrator on 2016/10/28.
 */
public class AllDevFragment extends MvpFragment<AllSmokePresenter> implements ShopInfoFragmentView {


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
    private LinearLayoutManager linearLayoutManager;
    private ShopSmokeAdapter shopSmokeAdapter;
    private int lastVisibleItem;
    private Context mContext;
    private List<Smoke> list;
    private int loadMoreCount;
    private boolean research = false;
    private String page;
    private String userID;
    private int privilege;
    private AllSmokePresenter mShopInfoFragmentPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_dev, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext=getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        page = "1";
        list = new ArrayList<>();
        if(MyApp.app.getPrivilege()!=1){//@@9.29 1级
            smokeTotal.setVisibility(View.VISIBLE);
        }
        refreshListView();
        mvpPresenter.getAllSmoke(userID, privilege + "", page,"1", list, 1,false,AllDevFragment.this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","1",AllDevFragment.this);//@@9.5
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
            ((AllSmokeActivity)getActivity()).refreshFragment();
//            refreshView();
        }
    });

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
//            if (research) {
//                if(shopSmokeAdapter!=null){
//                    shopSmokeAdapter.changeMoreStatus(ShopCameraAdapter.NO_DATA);
//                }
//                return;
//            }
            if(research){//@@9.5 条件查询分页
                if(shopSmokeAdapter==null){
                    return;
                }
                int count = shopSmokeAdapter.getItemCount();
//                int itemCount = lastVisibleItem+2;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem+1 == count) {
                    if(loadMoreCount>=20){
                        page = Integer.parseInt(page) + 1 + "";
//                        mvpPresenter.getAllSmoke(userID, privilege + "", page,"1", list, 1,true,AllDevFragment.this);//@@7.17
                        mvpPresenter.getNeedSmoke(userID, privilege + "", page
                                ,((AllSmokeActivity)getActivity()).getParentId1()
                                ,((AllSmokeActivity)getActivity()).getAreaId1()
                                ,((AllSmokeActivity)getActivity()).getShopTypeId1(),"1", AllDevFragment.this);//显示设备。
                    }else{
                        T.showShort(mContext,"已经没有更多数据了");
                    }
                }
                return;
            }//@@9.5
            if(shopSmokeAdapter==null){
                return;
            }
            int count = shopSmokeAdapter.getItemCount();
//                int itemCount = lastVisibleItem+2;
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem+1 == count) {
                if(loadMoreCount>=20){
                    page = Integer.parseInt(page) + 1 + "";
                    mvpPresenter.getAllSmoke(userID, privilege + "", page,"1", list, 1,false,AllDevFragment.this);//@@7.17
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

     public void refreshView() {
        page = "1";
        list.clear();
        mvpPresenter.getAllSmoke(userID, privilege + "", page,"1", list, 1,true,AllDevFragment.this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","1",AllDevFragment.this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected AllSmokePresenter createPresenter() {
        mShopInfoFragmentPresenter = new AllSmokePresenter((AllSmokeActivity)getActivity());
        return mShopInfoFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "AllDevFragment";
    }

    @Override
    public void getDataSuccess(List<?> smokeList,boolean search) {
        research = search;
        if(search!=false&&!page.equals("1")){
            page="1";
        }//@@9.5
        loadMoreCount = smokeList.size();
        list.clear();
        list.addAll((List<Smoke>)smokeList);
        shopSmokeAdapter = new ShopSmokeAdapter(mContext, list);
        recyclerView.setAdapter(shopSmokeAdapter);
        swipereFreshLayout.setRefreshing(false);
//        shopSmokeAdapter.changeMoreStatus(ShopSmokeAdapter.NO_DATA);
    }

    @Override
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
        swipereFreshLayout.setRefreshing(false);
        if(shopSmokeAdapter!=null){
            shopSmokeAdapter.changeMoreStatus(ShopSmokeAdapter.NO_DATA);
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
        list.addAll((List<Smoke>)smokeList);
        shopSmokeAdapter.changeMoreStatus(ShopSmokeAdapter.LOADING_MORE);
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
    public void getLostCount(String count) {
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
