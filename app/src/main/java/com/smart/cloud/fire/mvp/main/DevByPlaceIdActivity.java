package com.smart.cloud.fire.mvp.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.smart.cloud.fire.base.ui.MvpActivity;
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
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

public class DevByPlaceIdActivity extends MvpActivity<ElectricDevPresenter> implements ElectricDevView {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipere_fresh_layout)
    SwipeRefreshLayout swipereFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.title_tv)
    TextView title_tv;
    @Bind(R.id.quick_change_all_tv)
    TextView quick_change_all_tv;
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

    private String place_id;
    private String place_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_by_place_id);

        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        refreshListView();

//        smokeTotal.setVisibility(View.VISIBLE);//@@9.5
        place_id=getIntent().getStringExtra("ID");
        place_name=getIntent().getStringExtra("NAME");
        title_tv.setText(place_name);
        list = new ArrayList<>();
        page = "1";
        quick_change_all_tv.setVisibility(View.VISIBLE);
        quick_change_all_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuickChangeAllDialog();
            }
        });
//        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,false,this);
        mvpPresenter.getNeedElectricInfo(userID, privilege + "",parentId, areaId,"", place_id,"3",this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","3",this);//@@9.5

    }

    int changeAllChoice;
    private void showQuickChangeAllDialog(){
        final String[] items = { "全部合闸","全部分闸" };
        changeAllChoice = 0;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle("一键操作");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeAllChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changepower(changeAllChoice);
                    }
                });
        singleChoiceDialog.show();
    }

    public void  changepower(final int eleState){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        if(eleState==1){
            builder.setMessage("是否执行分闸命令？");
        }else{
            builder.setMessage("是否执行合闸命令？");
        }
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userID = SharedPreferencesManager.getInstance().getData(mContext,
                        SharedPreferencesManager.SP_FILE_GWELL,
                        SharedPreferencesManager.KEY_RECENTNAME);
                RequestQueue mQueue = Volley.newRequestQueue(mContext);
                String url="";
                if(eleState==0){
                    url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control_dealAll?placeId="+place_id+"&devCmd=12&userid="+userID;
                }else{
                    url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control_dealAll?placeId="+place_id+"&devCmd=13&userid="+userID;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("提示");
                dialog1.setMessage("设置中，请稍候");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
//                Toast.makeText(mContext,"设置中，请稍候",Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject=new JSONObject(response);
                                    Toast.makeText(mContext,jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog1.dismiss();
                        Toast.makeText(mContext,"设置超时",Toast.LENGTH_SHORT).show();
                    }
                });
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(stringRequest);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected ElectricDevPresenter createPresenter() {
        electricDevPresenter=new ElectricDevPresenter(this);
        return electricDevPresenter;
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
                int count = electricFragmentAdapter.getItemCount();
                int itemCount = lastVisibleItem+1;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemCount == count) {
                    if(loadMoreCount>=20){
                        page = Integer.parseInt(page) + 1 + "";
//                        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,true, (ElectricDevView) mContext);
                        mvpPresenter.getNeedElectricInfo(userID, privilege + "",parentId, areaId,"", place_id,"3",(ElectricDevView) mContext);
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
//        mvpPresenter.getAllElectricInfo(userID, privilege + "", page,"3",list,1,true,this);
        mvpPresenter.getNeedElectricInfo(userID, privilege + "",parentId, areaId,"", place_id,"3", this);
        mvpPresenter.getSmokeSummary(userID,privilege+"","","","","3",this);

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

    }

    @Override
    public void getAreaTypeFail(String msg, int type) {
        T.showShort(mContext, msg);
    }

    @Override
    public void unSubscribe(String type) {
    }

    @Override
    public void getChoiceArea(Area area) {
        mArea = area;
    }

    @Override
    public void getChoiceShop(ShopType shopType) {
        mShopType = shopType;
    }

    @Override
    public void getSmokeSummary(SmokeSummary smokeSummary) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ButterKnife.unbind(this);
    }
}
