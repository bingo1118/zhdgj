package com.smart.cloud.fire.mvp.fragment.CollectFragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.AreaChooceListView;
import com.smart.cloud.fire.view.OnWheelScrollListener;
import com.smart.cloud.fire.view.WheelView;
import com.smart.cloud.fire.view.XCDropDownListViewFire;

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

/**
 * Created by Administrator on 2016/9/21.
 */
public class CollectFragment extends MvpFragment<CollectFragmentPresenter> implements CollectFragmentView, View.OnFocusChangeListener {
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.add_fire)
    ImageView addFire;
    @Bind(R.id.start_time)
    EditText startTime;
    @Bind(R.id.end_time)
    EditText endTime;
    @Bind(R.id.area_type_choice)
    AreaChooceListView areaTypeChoice;//@@9.11
    @Bind(R.id.shang_pu_type_choice)
    XCDropDownListViewFire shangPuTypeChoice;
    @Bind(R.id.date_pick)
    LinearLayout datePick;
    @Bind(R.id.textY)
    TextView textY;
    @Bind(R.id.textM)
    TextView textM;
    @Bind(R.id.textD)
    TextView textD;
    @Bind(R.id.textH)
    TextView textH;
    @Bind(R.id.textMi)
    TextView textMi;
    @Bind(R.id.date_year)
    WheelView dateYear;
    @Bind(R.id.date_month)
    WheelView dateMonth;
    @Bind(R.id.date_day)
    WheelView dateDay;
    @Bind(R.id.date_hour)
    WheelView dateHour;
    @Bind(R.id.date_minute)
    WheelView dateMinute;
    @Bind(R.id.delete_start_time_rela)
    RelativeLayout deleteStartTimeRela;
    @Bind(R.id.delete_end_time_rela)
    RelativeLayout deleteEndTimeRela;
    @Bind(R.id.type_lin)
    LinearLayout typeLin;
    @Bind(R.id.demo_recycler)
    RecyclerView demoRecycler;
    @Bind(R.id.demo_swiperefreshlayout)
    SwipeRefreshLayout demoSwiperefreshlayout;
    @Bind(R.id.layout_cNumber)
    RelativeLayout layoutCNumber;
    @Bind(R.id.layout_cNumber2)
    RelativeLayout layoutCNumber2;
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

    //startStr, endStr, areaId, placeTypeId
    private int type=1;//@@是否是按条件查询 1 查询所有 2 条件查询
    private String startStr;
    private String endStr;
    private String areaId;
    private String placeTypeId;
    private String parentId;

    List<Area> parent = null;//@@9.11
    Map<String, List<Area>> map = null;//@@9.11

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_fire, container,
                false);
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
        page = "1";
        mvpPresenter.getAllAlarm(userID, privilege + "", page, 1, "", "", "", "","");
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
                mvpPresenter.getAllAlarm(userID, privilege + "", page, 1, "", "", "", "","");
                type=1;//@@7.12
                mProgressBar.setVisibility(View.GONE);
            }
        });
        if (privilege == 1) {
            typeLin.setVisibility(View.GONE);
        }
        addFire.setVisibility(View.VISIBLE);
        addFire.setImageResource(R.drawable.search);
        startTime.setOnFocusChangeListener(this);
        endTime.setOnFocusChangeListener(this);
        startTime.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);
        shangPuTypeChoice.setEditTextHint("类型");
        areaTypeChoice.setEditTextHint("区域");
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
                        if(type==2){
                            mvpPresenter.getAllAlarm(userID, privilege + "", page, 2,startStr, endStr, areaId, placeTypeId,parentId);
                        }else{
                            mvpPresenter.getAllAlarm(userID, privilege + "", page, 1, "", "", "", "","");
                        }//@@7.12 区分是否是条件查询 1 查询全部 2 条件查询
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

    @OnClick({R.id.add_fire, R.id.date_cancel, R.id.delete_start_time_rela, R.id.delete_end_time_rela, R.id.area_type_choice, R.id.shang_pu_type_choice, R.id.search_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                if (!Utils.isNetworkAvailable(getActivity())) {
                    return;
                }
                if (shangPuTypeChoice != null) {
                    if (shangPuTypeChoice.ifShow()) {
                        shangPuTypeChoice.closePopWindow();
                    }
                }
                if (areaTypeChoice != null) {
                    if (areaTypeChoice.ifShow()) {
                        areaTypeChoice.closePopWindow();
                    }
                }
                String startStr = startTime.getText().toString().trim();
                String endStr = endTime.getText().toString().trim();
                String areaStr = areaTypeChoice.getTv().trim();
                String typeStr = shangPuTypeChoice.getTv().trim();
                if (areaStr.length() == 0 && typeStr.length() == 0 && startStr.length() == 0 && endStr.length() == 0) {
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
                String placeTypeId = "";
                if (mShopType != null) {
                    placeTypeId = mShopType.getPlaceTypeId();
                    if (placeTypeId == null) {
                        placeTypeId = "";
                    }
                }
                String areaId = "";
                String parentId="";//@@9.11
//                if (mArea != null) {
//                    areaId = mArea.getAreaId();
//                    if (areaId == null) {
//                        areaId = "";
//                    }
//                }

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
                }

                this.research=false;//@@8.10再次查询
                page = "1";//@@9.11
                mvpPresenter.getAllAlarm(userID, privilege + "", page, 2, startStr, endStr, areaId, placeTypeId,parentId);
                this.startStr=startStr;
                this.endStr=endStr;
                this.areaId=areaId;
                this.parentId=parentId;
                this.placeTypeId=placeTypeId;
                this.type=2;//@@7.12保存查询条件


                hideDatePick();
                mArea = null;
                mShopType = null;
                areaTypeChoice.addFinish();
                shangPuTypeChoice.addFinish();
                areaTypeChoice.setEditText("");
                shangPuTypeChoice.setEditText("");
                page = "1";
                break;
            case R.id.add_fire:
                startTime.requestFocus();
                if (!isDpShow) {
                    showDatePick();
                    shangPuTypeChoice.setEditText("");
                    areaTypeChoice.setEditText("");
                }else{
                    if (areaTypeChoice.ifShow()) {
                        areaTypeChoice.closePopWindow();
                    }//@@5.5关闭下拉选项
                    if (shangPuTypeChoice.ifShow()) {
                        shangPuTypeChoice.closePopWindow();
                    }//@@5.5关闭下拉选项
                    hideDatePick();//@@5.5
                }
                break;
            case R.id.date_cancel:
                hideDatePick();
                if (shangPuTypeChoice != null) {
                    if (shangPuTypeChoice.ifShow()) {
                        shangPuTypeChoice.closePopWindow();
                    }
                }
                if (areaTypeChoice != null) {
                    if (areaTypeChoice.ifShow()) {
                        areaTypeChoice.closePopWindow();
                    }
                }
                break;
            case R.id.delete_start_time_rela:
                startTime.setText("");
                deleteStartTimeRela.setVisibility(View.GONE);
                break;
            case R.id.delete_end_time_rela:
                endTime.setText("");
                deleteEndTimeRela.setVisibility(View.GONE);
                break;
            case R.id.area_type_choice:
                if (areaTypeChoice.ifShow()) {
                    areaTypeChoice.closePopWindow();
                } else {
                    RequestQueue mQueue = Volley.newRequestQueue(mContext);
                    String url= ConstantValues.SERVER_IP_NEW+"getAreaInfo?userId="+userID+"&privilege="+privilege;
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
                                        areaTypeChoice.setItemsData2(parent,map, collectFragmentPresenter);
                                        areaTypeChoice.showPopWindow();
                                        areaTypeChoice.setClickable(true);
                                        areaTypeChoice.closeLoading();
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
                    areaTypeChoice.setClickable(false);
                    areaTypeChoice.showLoading();
                }
                break;
            case R.id.shang_pu_type_choice:
                if (shangPuTypeChoice.ifShow()) {
                    shangPuTypeChoice.closePopWindow();
                } else {
                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 1);
                    shangPuTypeChoice.setClickable(false);
                    shangPuTypeChoice.showLoading();
                }
                break;
            default:
                break;
        }
    }

    public void showDatePick() {
        isDpShow = true;
        datePick.setVisibility(RelativeLayout.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(mContext,
                R.anim.slide_in_bottom);
        datePick.startAnimation(anim);
        initWheel();
    }

    public void hideDatePick() {
        isDpShow = false;
        Animation anim = AnimationUtils.loadAnimation(mContext,
                R.anim.slide_out_top);
        datePick.startAnimation(anim);
        datePick.setVisibility(RelativeLayout.GONE);
    }

    private void initWheel() {
        Calendar calendar = Calendar.getInstance();

        int curYear = calendar.get(Calendar.YEAR) - 2010;
        initWheelView(dateYear, new DateNumericAdapter(mContext, 2010, 2036), curYear);

        int curMonth = calendar.get(Calendar.MONTH);
        initWheelView(dateMonth, new DateNumericAdapter(mContext, 1, 12), curMonth);

        int curDay = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        initWheelView(dateDay, new DateNumericAdapter(mContext, 1, 31), curDay);

        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        initWheelView(dateHour, new DateNumericAdapter(mContext, 0, 23), curHour);

        int curMinute = calendar.get(Calendar.MINUTE);
        initWheelView(dateMinute, new DateNumericAdapter(mContext, 0, 59), curMinute);
    }

    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
            updateStatus();
            updateSearchEdit();
        }

        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus();
            updateSearchEdit();
        }
    };

    private void initWheelView(WheelView wv, DateNumericAdapter dateNumericAdapter, int type) {
        wv.setViewAdapter(dateNumericAdapter);
        wv.setCurrentItem(type);
        wv.addScrollingListener(scrolledListener);
        wv.setCyclic(true);
    }

    private void updateStatus() {
        int year = dateYear.getCurrentItem() + 2010;
        int month = dateMonth.getCurrentItem() + 1;

        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12) {
            dateDay.setViewAdapter(new DateNumericAdapter(mContext, 1, 31));
        } else if (month == 2) {

            boolean isLeapYear = false;
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    isLeapYear = true;
                } else {
                    isLeapYear = false;
                }
            } else {
                if (year % 4 == 0) {
                    isLeapYear = true;
                } else {
                    isLeapYear = false;
                }
            }
            if (isLeapYear) {
                if (dateDay.getCurrentItem() > 28) {
                    dateDay.scroll(30, 2000);
                }
                dateDay.setViewAdapter(new DateNumericAdapter(mContext, 1, 29));
            } else {
                if (dateDay.getCurrentItem() > 27) {
                    dateDay.scroll(30, 2000);
                }
                dateDay.setViewAdapter(new DateNumericAdapter(mContext, 1, 28));
            }

        } else {
            if (dateDay.getCurrentItem() > 29) {
                dateDay.scroll(30, 2000);
            }
            dateDay.setViewAdapter(new DateNumericAdapter(mContext, 1, 30));
        }
    }

    public void updateSearchEdit() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int year = dateYear.getCurrentItem() + 2010;
        int month = dateMonth.getCurrentItem() + 1;
        int day = dateDay.getCurrentItem() + 1;
        int hour = dateHour.getCurrentItem();
        int minute = dateMinute.getCurrentItem();
        StringBuilder sb = new StringBuilder();
        sb.append(year + "-");

        if (month < 10) {
            sb.append("0" + month + "-");
        } else {
            sb.append(month + "-");
        }

        if (day < 10) {
            sb.append("0" + day + " ");
        } else {
            sb.append(day + " ");
        }

        if (hour < 10) {
            sb.append("0" + hour + ":");
        } else {
            sb.append(hour + ":");
        }

        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append("" + minute);
        }

        if (selected_Date == START_TIME) {
            startTime.setText(sb.toString());
            deleteStartTimeRela.setVisibility(View.VISIBLE);
        } else {
            endTime.setText(sb.toString());
            deleteEndTimeRela.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected CollectFragmentPresenter createPresenter() {
        collectFragmentPresenter = new CollectFragmentPresenter(CollectFragment.this);
        return collectFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "CollectFragment";
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
            adapter = new RefreshRecyclerAdapter(getActivity(), messageModelList, collectFragmentPresenter, userID, privilege + "");
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

    @Override
    public void dealAlarmMsgSuccess(List<AlarmMessageModel> alarmMessageModels) {
        messageModelList.clear();
        messageModelList.addAll(alarmMessageModels);
        loadMoreCount=alarmMessageModels.size();//@@7.13
        adapter = new RefreshRecyclerAdapter(getActivity(), messageModelList, collectFragmentPresenter, userID, privilege + "");
        demoRecycler.setAdapter(adapter);
        adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
    }

    @Override
    public void updateAlarmMsgSuccess(int index) {
        adapter.setList(index);
    }

    @Override
    public void getShopType(ArrayList<Object> shopTypes) {
        shangPuTypeChoice.setItemsData(shopTypes,mvpPresenter);
        shangPuTypeChoice.showPopWindow();
        shangPuTypeChoice.setClickable(true);
        shangPuTypeChoice.closeLoading();
    }

    @Override
    public void getShopTypeFail(String msg) {
        T.showShort(mContext, msg);
        shangPuTypeChoice.setClickable(true);
        shangPuTypeChoice.closeLoading();
    }

    @Override
    public void getAreaType(ArrayList<Object> shopTypes) {
        areaTypeChoice.setItemsData(shopTypes,mvpPresenter);
        areaTypeChoice.showPopWindow();
        areaTypeChoice.setClickable(true);
        areaTypeChoice.closeLoading();
    }

    @Override
    public void getAreaTypeFail(String msg) {
        T.showShort(mContext, msg);
        areaTypeChoice.setClickable(true);
        areaTypeChoice.closeLoading();
    }

    @Override
    public void getDataByCondition(List<AlarmMessageModel> alarmMessageModels) {
        if(!research){
            research = true;
            messageModelList.clear();
        }//@@7.13
        int pageInt = Integer.parseInt(page);
        if (messageModelList != null && messageModelList.size() >= 20 && pageInt > 1) {
            loadMoreCount=alarmMessageModels.size();
            messageModelList.addAll(alarmMessageModels);
            adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
        } else {
            loadMoreCount=alarmMessageModels.size();
            messageModelList.addAll(alarmMessageModels);
            adapter = new RefreshRecyclerAdapter(getActivity(), messageModelList, collectFragmentPresenter, userID, privilege + "");//@@9.11
            demoRecycler.setAdapter(adapter);//@@9.11
//            adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
        }//@@7.13 添加条件查询分页

//        messageModelList.clear();
//        messageModelList.addAll(alarmMessageModels);
//        adapter = new RefreshRecyclerAdapter(getActivity(), messageModelList, collectFragmentPresenter, userID, privilege + "");
//        demoRecycler.setAdapter(adapter);
//        adapter.changeMoreStatus(RefreshRecyclerAdapter.NO_DATA);
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        isDpShow = false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.start_time:
                selected_Date = START_TIME;
                startTime.setTextColor(getResources().getColor(R.color.login_btn));
                startTime.setHintTextColor(getResources().getColor(R.color.hint_color_text));
                endTime.setTextColor(Color.BLACK);
                endTime.setHintTextColor(Color.BLACK);
                break;
            case R.id.end_time:
                selected_Date = END_TIME;
                startTime.setTextColor(Color.BLACK);
                startTime.setHintTextColor(Color.BLACK);
                endTime.setTextColor(getResources().getColor(R.color.login_btn));
                endTime.setHintTextColor(getResources().getColor(R.color.hint_color_text));
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (areaTypeChoice.ifShow()) {
            areaTypeChoice.closePopWindow();
        }//@@5.5关闭下拉选项
        if (shangPuTypeChoice.ifShow()) {
            shangPuTypeChoice.closePopWindow();
        }//@@5.5关闭下拉选项
        hideDatePick();//@@5.5
    }
}
