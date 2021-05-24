package com.smart.cloud.fire.activity.AlarmHistory;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smart.cloud.fire.adapter.DateNumericAdapter;
import com.smart.cloud.fire.adapter.RefreshRecyclerAdapter;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.fragment.CollectFragment.AlarmMessageModel;
import com.smart.cloud.fire.mvp.fragment.CollectFragment.CollectFragmentPresenter;
import com.smart.cloud.fire.mvp.fragment.CollectFragment.CollectFragmentView;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.AreaChooceListView;
import com.smart.cloud.fire.view.OnWheelScrollListener;
import com.smart.cloud.fire.view.WheelView;
import com.smart.cloud.fire.view.XCDropDownListViewFire;
import com.smart.cloud.fire.view.dataSelector.GetDateAndTimeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2016/9/21.
 */
public class OneDeviceAlarmHistoryActivity extends MvpActivity<CollectFragmentPresenter> implements CollectFragmentView, View.OnFocusChangeListener {

    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.add_fire)
    ImageView addFire;

    @Bind(R.id.demo_recycler)
    RecyclerView demoRecycler;
    @Bind(R.id.demo_swiperefreshlayout)
    SwipeRefreshLayout demoSwiperefreshlayout;

    private String userID;
    private int privilege;
    private String page;
    private Context mContext;
    private CollectFragmentPresenter collectFragmentPresenter;
    private boolean research = false;
    private List<AlarmMessageModel> messageModelList;
    private int loadMoreCount;
    boolean isDpShow = false;
    private boolean wheelScrolled = false;
    private int selected_Date;
    private static final int START_TIME = 0;
    private static final int END_TIME = 1;
    private ShopType mShopType;
    private Area mArea;
    private LinearLayoutManager linearLayoutManager;
    private RefreshRecyclerAdapter adapter;
    private int lastVisibleItem;
    private PopupWindow popupWindow = null;

    //startStr, endStr, areaId, placeTypeId
    private String startStr;
    private String endStr;

    List<Area> parent = null;//@@9.11
    Map<String, List<Area>> map = null;//@@9.11
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_device_alarm_history);

        mContext = this;
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        mac=getIntent().getStringExtra("mac");
        page = "1";
        mvpPresenter.getOneDeviceAlarm(userID, privilege + "", mac,page, "", "");
        init();
    }

    private void init() {
        //设置刷新时动画的颜色，可以设置4个
        demoSwiperefreshlayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        demoSwiperefreshlayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        demoSwiperefreshlayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager=new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        demoRecycler.setLayoutManager(linearLayoutManager);

        demoSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                research = false;
                page = "1";
                startStr="";
                endStr="";
                mvpPresenter.getOneDeviceAlarm(userID, privilege + "", mac,page, startStr, endStr);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        addFire.setVisibility(VISIBLE);
        addFire.setImageResource(R.drawable.search);

        demoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(adapter==null){
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
//                    if (loadMoreCount >= 20 && research == false) {
                    if (loadMoreCount >= 20 ) {//@@7.12
                        page = Integer.parseInt(page) + 1 + "";
                        mvpPresenter.getOneDeviceAlarm(userID, privilege + "", mac,page, startStr, endStr);
                        mProgressBar.setVisibility(View.GONE);
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

    @OnClick({R.id.add_fire})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fire:
                showPopWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 打开下拉列表弹窗
     */
    public void showPopWindow() {
        // 加载popupWindow的布局文件
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.alarm_time_popwin, null, false);
        final GetDateAndTimeView start_time_selector=contentView.findViewById(R.id.start_time_selector);
        final GetDateAndTimeView end_time_selector=contentView.findViewById(R.id.end_time_selector);
        Button commit_btn=(Button)contentView.findViewById(R.id.search_btn);
        Button cancel_btn=(Button)contentView.findViewById(R.id.date_cancel);
        start_time_selector.setHint("起始时间");
        end_time_selector.setHint("终止时间");

        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isNetworkAvailable(mActivity)) {
                    return;
                }
                startStr = start_time_selector.getCheckedTime();
                endStr = end_time_selector.getCheckedTime();
                if (startStr.length() == 0 && endStr.length() == 0) {
                    T.showShort(mContext, "请选择查询条件");
                    return;
                }
                if (startStr.length() > 0 && endStr.length() == 0) {
                    T.showShort(mContext, "结束时间不能为空");
                    return;
                }
                if (endStr.length() > 0 && startStr.length() == 0) {
                    T.showShort(mContext, "开始时间都不能为空");
                    return;
                }

                if (startStr.length() > 0 && endStr.length() > 0) {
                    startStr = startStr + ":00";
                    endStr = endStr + ":00";
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        long startLong = df.parse(startStr).getTime();
                        long endLong = df.parse(endStr).getTime();
                        if (startLong > endLong) {
                            T.showShort(mContext, "开始时间不能大于结束时间");
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                page = "1";//@@9.11
                mvpPresenter.getOneDeviceAlarm(userID, privilege + "", mac,page, startStr, endStr);
                popupWindow.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected CollectFragmentPresenter createPresenter() {
        collectFragmentPresenter = new CollectFragmentPresenter(this);
        return collectFragmentPresenter;
    }



    @Override
    public void getDataSuccess(List<AlarmMessageModel> alarmMessageModels) {
        int pageInt = Integer.parseInt(page);
        if (messageModelList != null && messageModelList.size() >= 20 && pageInt > 1) {
            loadMoreCount=alarmMessageModels.size();
            messageModelList.addAll(alarmMessageModels);
            adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
        } else {
            messageModelList = new ArrayList<>();
            loadMoreCount=alarmMessageModels.size();
            messageModelList.addAll(alarmMessageModels);
            adapter = new RefreshRecyclerAdapter(mActivity, messageModelList, collectFragmentPresenter, userID, privilege + "");
            demoRecycler.setAdapter(adapter);
            demoSwiperefreshlayout.setRefreshing(false);
            adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
        }
    }

    @Override
    public void getDataFail(String msg) {
        demoSwiperefreshlayout.setRefreshing(false);
        if(adapter!=null){
            adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
        }
        T.showShort(mContext,"无数据");
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void dealAlarmMsgSuccess(List<AlarmMessageModel> alarmMessageModels) {
        messageModelList.clear();
        messageModelList.addAll(alarmMessageModels);
        loadMoreCount=alarmMessageModels.size();//@@7.13
        adapter = new RefreshRecyclerAdapter(mActivity, messageModelList, collectFragmentPresenter, userID, privilege + "");
        demoRecycler.setAdapter(adapter);
        adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
    }

    @Override
    public void updateAlarmMsgSuccess(int index) {
        adapter.setList(index);
    }

    @Override
    public void getShopType(ArrayList<Object> shopTypes) {

    }

    @Override
    public void getShopTypeFail(String msg) {

    }

    @Override
    public void getAreaType(ArrayList<Object> shopTypes) {

    }

    @Override
    public void getAreaTypeFail(String msg) {

    }

    @Override
    public void getDataByCondition(List<AlarmMessageModel> alarmMessageModels) {

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
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
