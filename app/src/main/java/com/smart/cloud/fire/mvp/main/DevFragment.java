package com.smart.cloud.fire.mvp.main;

/**
 * Created by Rain on 2018/6/23.
 */
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smart.cloud.fire.activity.AddDev.AddDevActivity;
import com.smart.cloud.fire.activity.Electric.ElectricDevPresenter;
import com.smart.cloud.fire.activity.Electric.ElectricDevView;
import com.smart.cloud.fire.adapter.ElectricFragmentAdapter;
import com.smart.cloud.fire.adapter.ShopCameraAdapter;
import com.smart.cloud.fire.adapter.ShopSmokeAdapter;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.electric.ElectricActivity;
import com.smart.cloud.fire.mvp.electric.ElectricBigActivity;
import com.smart.cloud.fire.mvp.electric.ElectricDXActivity;
import com.smart.cloud.fire.mvp.electric.ElectricSXActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.AreaChooceListView;
import com.smart.cloud.fire.view.XCDropDownListViewMapSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;



public class DevFragment extends MvpFragment<ElectricDevPresenter> implements ElectricDevView {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
//    @Bind(R.id.smoke_total)
//    LinearLayout smokeTotal;//@@9.5
//    @Bind(R.id.total_num)
//    TextView totalNum;
//    @Bind(R.id.online_num)
//    TextView onlineNum;
//    @Bind(R.id.offline_num)
//    TextView offlineNum;
    @Bind(R.id.area_condition)
    AreaChooceListView areaCondition;//区域下拉选择。。
    @Bind(R.id.shop_type_condition)
    XCDropDownListViewMapSearch shopTypeCondition;//商铺类型下拉选择。。
    @Bind(R.id.add_dev_fire)
    ImageView add_dev_fire;
    @Bind(R.id.add_fire)
    ImageView add_fire;
    @Bind(R.id.lin1)
    LinearLayout lin1;//搜素界面。。
    @Bind(R.id.search_fire)
    ImageView searchFire;//搜索按钮。。
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

    private boolean visibility = false;
    List<Area> parent = null;//@@8.31
    Map<String, List<Area>> map = null;//@@8.31
    private ShopType mShopType;
    private Area mArea;
    private String areaId = "";
    private String parentId="";//@@9.1
    private String shopTypeId = "";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected ElectricDevPresenter createPresenter() {
        electricDevPresenter = new ElectricDevPresenter(this);
        return electricDevPresenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dev_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.add_fire,R.id.add_dev_fire,R.id.area_condition,R.id.shop_type_condition,R.id.search_fire})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_fire://显示查询条件按钮。。
                if (visibility) {
                    visibility = false;
                    lin1.setVisibility(View.GONE);
                    if (areaCondition.ifShow()) {
                        areaCondition.closePopWindow();
                    }
                    if (shopTypeCondition.ifShow()) {
                        shopTypeCondition.closePopWindow();
                    }
                } else {
                    visibility = true;
                    areaCondition.setEditText("");
                    shopTypeCondition.setEditText("");
                    areaCondition.setEditTextHint("区域");
                    shopTypeCondition.setEditTextHint("类型");
                    lin1.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.area_condition://地区类型下拉列表。。
                if (areaCondition.ifShow()) {
                    areaCondition.closePopWindow();
                } else {
                    RequestQueue mQueue = Volley.newRequestQueue(mContext);
                    String url= ConstantValues.SERVER_IP_NEW+"/getAreaInfo?userId="+userID+"&privilege="+privilege;
                    StringRequest stringRequest = new StringRequest(url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject=new JSONObject(response);
                                        if(jsonObject.getInt("errorCode")==0){
                                            parent = new ArrayList<>();
                                            map = new HashMap<>();
                                            JSONArray jsonArrayParent=jsonObject.getJSONArray("areas");
                                            for(int i=0;i<jsonArrayParent.length();i++){
                                                JSONObject tempParent= jsonArrayParent.getJSONObject(i);
                                                Area tempArea=new Area();
                                                tempArea.setAreaId(tempParent.getString("areaId"));
                                                tempArea.setAreaName(tempParent.getString("areaName"));
                                                tempArea.setIsParent(1);
                                                parent.add(tempArea);
                                                List<Area> child = new ArrayList<>();
                                                JSONArray jsonArrayChild=tempParent.getJSONArray("areas");
                                                for(int j=0;j<jsonArrayChild.length();j++){
                                                    JSONObject tempChild= jsonArrayChild.getJSONObject(j);
                                                    Area tempAreaChild=new Area();
                                                    tempAreaChild.setAreaId(tempChild.getString("areaId"));
                                                    tempAreaChild.setAreaName(tempChild.getString("areaName"));
                                                    tempAreaChild.setIsParent(0);
                                                    child.add(tempAreaChild);
                                                }
                                                map.put(tempParent.getString("areaName"),child);
                                            }
                                        }
                                        areaCondition.setItemsData2(parent,map,electricDevPresenter );
                                        areaCondition.showPopWindow();
                                        areaCondition.setClickable(true);
                                        areaCondition.closeLoading();
//                                        mvpPresenter.getPlaceTypeId(userID, privilege + "", 2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error","error");
                        }
                    });
                    mQueue.add(stringRequest);
                    areaCondition.setClickable(false);
                    areaCondition.showLoading();
                }
                break;
            case R.id.shop_type_condition://商铺类型下拉列表。。
                if (shopTypeCondition.ifShow()) {
                    shopTypeCondition.closePopWindow();
                } else {
                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 1);
                    shopTypeCondition.setClickable(false);
                    shopTypeCondition.showLoading();
                }
                break;
            case R.id.search_fire://查询按钮
                if (!Utils.isNetworkAvailable(getActivity())) {
                    return;
                }
                if (shopTypeCondition.ifShow()) {
                    shopTypeCondition.closePopWindow();
                }
                if (areaCondition.ifShow()) {
                    areaCondition.closePopWindow();
                }
                if ((mShopType != null && mShopType.getPlaceTypeId() != null) || (mArea != null && mArea.getAreaId() != null)) {
                    lin1.setVisibility(View.GONE);
                    searchFire.setVisibility(View.GONE);
                    add_fire.setVisibility(View.VISIBLE);
                    areaCondition.searchClose();
                    shopTypeCondition.searchClose();
                    visibility = false;
                    if (mArea != null && mArea.getAreaId() != null) {
                        if(mArea.getIsParent()==1){
                            parentId= mArea.getAreaId();//@@9.1
                            areaId="";
                        }else{
                            areaId = mArea.getAreaId();
                            parentId="";
                        }
                    } else {
                        areaId = "";
                        parentId="";
                    }
                    if (mShopType != null && mShopType.getPlaceTypeId() != null) {
                        shopTypeId = mShopType.getPlaceTypeId();
                    } else {
                        shopTypeId = "";
                    }

                    mvpPresenter.getNeedElectricInfo(userID, privilege + "",parentId, areaId,"", shopTypeId,"3",this);
//                    mvpPresenter.getSmokeSummary(userID,privilege+"",parentId,areaId,shopTypeId,"3",this);
                    mShopType = null;
                    mArea = null;
                } else {
                    lin1.setVisibility(View.GONE);
                    return;
                }
                break;
            case R.id.add_dev_fire:
                Intent intent=new Intent(mContext, AddDevActivity.class);
                startActivity(intent);
                break;
        }
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
//        smokeTotal.setVisibility(View.VISIBLE);//@@9.5
        list = new ArrayList<>();
        page = "1";
        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,false,DevFragment.this);
//        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","3",DevFragment.this);//@@9.5
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
//        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipereFreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
//                ((ElectricDevActivity)getActivity()).refreshView();
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
                if(electricFragmentAdapter==null){
                    return;
                }
                int count = electricFragmentAdapter.getItemCount();
                int itemCount = lastVisibleItem+1;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemCount == count) {
                    if(loadMoreCount>=20){
                        page = Integer.parseInt(page) + 1 + "";
                        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,true,DevFragment.this);
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
        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,true,DevFragment.this);
//        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","3",DevFragment.this);
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
                }else if(data.getDeviceType()==8){
                    intent = new Intent(mContext, ElectricBigActivity.class);
                }else{
                    intent = new Intent(mContext, ElectricActivity.class);
                }
                intent.putExtra("data",data);
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
        if (type == 1) {
            shopTypeCondition.setItemsData((ArrayList<Object>) shopTypes, electricDevPresenter);
            shopTypeCondition.showPopWindow();
            shopTypeCondition.setClickable(true);
            shopTypeCondition.closeLoading();
        } else {
            areaCondition.setItemsData((ArrayList<Object>) shopTypes, electricDevPresenter);
            areaCondition.showPopWindow();
            areaCondition.setClickable(true);
            areaCondition.closeLoading();
        }
    }

    @Override
    public void getAreaTypeFail(String msg, int type) {
        T.showShort(mContext, msg);
        if (type == 1) {
            shopTypeCondition.setClickable(true);
            shopTypeCondition.closeLoading();
        } else {
            areaCondition.setClickable(true);
            areaCondition.closeLoading();
        }
    }

    @Override
    public void unSubscribe(String type) {
    }

    @Override
    public void getChoiceArea(Area area) {
        mArea = area;
        if (mArea != null && mArea.getAreaId() != null) {
            add_fire.setVisibility(View.GONE);
            searchFire.setVisibility(View.VISIBLE);
        }
        if (mArea.getAreaId() == null && mShopType == null) {
            add_fire.setVisibility(View.VISIBLE);
            searchFire.setVisibility(View.GONE);
        } else if (mArea.getAreaId() == null && mShopType != null && mShopType.getPlaceTypeId() == null) {
            add_fire.setVisibility(View.VISIBLE);
            searchFire.setVisibility(View.GONE);
        }
    }

    @Override
    public void getChoiceShop(ShopType shopType) {
        mShopType = shopType;
        if (mShopType != null && mShopType.getPlaceTypeId() != null) {
            add_fire.setVisibility(View.GONE);
            searchFire.setVisibility(View.VISIBLE);
        }
        if (mShopType.getPlaceTypeId() == null && mArea == null) {
            add_fire.setVisibility(View.VISIBLE);
            searchFire.setVisibility(View.GONE);
        } else if (mShopType.getPlaceTypeId() == null && mArea != null && mArea.getAreaId() == null) {
            add_fire.setVisibility(View.VISIBLE);
            searchFire.setVisibility(View.GONE);
        }
    }

    @Override
    public void getSmokeSummary(SmokeSummary smokeSummary) {
//        totalNum.setText(smokeSummary.getAllSmokeNumber()+"");
//        onlineNum.setText(smokeSummary.getOnlineSmokeNumber()+"");
//        offlineNum.setText(smokeSummary.getLossSmokeNumber()+"");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }
}