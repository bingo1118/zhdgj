package com.smart.cloud.fire.mvp.electric;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.adapter.DateNumericAdapter;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TimePickerDialog;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.OnWheelScrollListener;
import com.smart.cloud.fire.view.WheelView;
import com.smart.cloud.fire.view.XCDropDownListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class AutoTimeSettingActivity extends Activity{


    @Bind(R.id.ontime_edit)
    TextView ontime_edit;
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//添加设备按钮。。
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//加载进度。。
    @Bind(R.id.swich)
    Switch swich;
    @Bind(R.id.swich_cycle)
    Switch swich_cycle;
    @Bind(R.id.week1)
    CheckBox week1;
    @Bind(R.id.week2)
    CheckBox week2;
    @Bind(R.id.week3)
    CheckBox week3;
    @Bind(R.id.week4)
    CheckBox week4;
    @Bind(R.id.week5)
    CheckBox week5;
    @Bind(R.id.week6)
    CheckBox week6;
    @Bind(R.id.week7)
    CheckBox week7;
    @Bind(R.id.cycle_line)
    LinearLayout cycle_line;

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

    private Context mContext;

    TimePickerDialog mTimePickerDialog;
    int type=1;
    int state=1;
    String cycle;
    private String mac;

    private static final int DATE_DIALOG_ID = 1;
    private static final int SHOW_DATAPICK = 0;
    private int mYear;
    private int mMonth;
    private int mDay;

    String getDate;

    private boolean wheelScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_time_setting);

        ButterKnife.bind(this);
        mContext=this;

        mac=getIntent().getStringExtra("mac");
        initWheel();

        swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    state=1;
                } else {
                    state=2;
                }
            }
        });

        swich_cycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    cycle_line.setVisibility(View.VISIBLE);
                } else {
                    cycle_line.setVisibility(View.GONE);
                }
            }
        });
        addFireDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state==1&&(ontime_edit.getText().length()==0)){
                    T.showShort(mContext,"请输入时间");
                    return;
                }
                if(swich_cycle.isChecked()){
                    cycle="0";
                }else{
                    if(week1.isChecked()){
                        cycle+="1";
                    }
                    if(week2.isChecked()){
                        cycle+="2";
                    }
                    if(week3.isChecked()){
                        cycle+="3";
                    }
                    if(week4.isChecked()){
                        cycle+="4";
                    }
                    if(week5.isChecked()){
                        cycle+="5";
                    }
                    if(week6.isChecked()){
                        cycle+="6";
                    }
                    if(week7.isChecked()){
                        cycle+="7";
                    }
                }
                String url= ConstantValues.SERVER_IP_NEW+"addElectrTimer?mac="+mac
                        +"&state="+state
                        +"&time="+ontime_edit.getText()+":00"
                        +"&cycle="+cycle;
                VolleyHelper helper=VolleyHelper.getInstance(mContext);
                RequestQueue mQueue = helper.getRequestQueue();
//                            RequestQueue mQueue = Volley.newRequestQueue(context);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        T.showShort(mContext,"设置成功");
                                    }else{
                                        T.showShort(mContext,"设置失败");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"网络错误");
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
            }
        });

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

    private void initWheelView(WheelView wv, DateNumericAdapter dateNumericAdapter, int type) {
        wv.setViewAdapter(dateNumericAdapter);
        wv.setCurrentItem(type);
        wv.addScrollingListener(scrolledListener);
        wv.setCyclic(true);
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

        ontime_edit.setText(sb.toString());

    }
}


