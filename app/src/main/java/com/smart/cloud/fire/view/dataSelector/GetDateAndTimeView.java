package com.smart.cloud.fire.view.dataSelector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;


import com.smart.cloud.fire.view.dateSelector.DateTimePicker;
import com.smart.cloud.fire.view.dateSelector.LineConfig;

import fire.cloud.smart.com.smartcloudfire.R;

public class GetDateAndTimeView extends LinearLayout {

    public TextView editText;
    private ImageView imageView;
    private PopupWindow popupWindow = null;
    private Context mContext;
    private ImageView clear_choice;

    TimePicker time_picker;
    DatePicker date_pick;

    enum Status {
        INIT,
        CHECKED,
        CLEARED
    }

    Status state = Status.INIT;

    String checkedTime = "";

    public String getCheckedTime() {
        return checkedTime;
    }


    public GetDateAndTimeView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public GetDateAndTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public GetDateAndTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView(context);
    }


    public void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.data_selector_view, this, true);
        editText = (TextView) findViewById(R.id.text);
        imageView = (ImageView) findViewById(R.id.btn);
        editText.setHint("请选择");
        clear_choice = (ImageView) findViewById(R.id.clear_choice);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopWindow();
                DateTimePicker picker = new DateTimePicker((Activity) mContext, DateTimePicker.HOUR_24);
                picker.setActionButtonTop(true);
                picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
//                picker.setDateRangeStart(2017, 1, 1);
//                picker.setDateRangeEnd(2025, 11, 11);
//                picker.setSelectedItem(2018,6,16,0,0);
//                picker.setTimeRangeStart(9, 0);
//                picker.setTimeRangeEnd(20, 30);
                picker.setCanLinkage(false);
                picker.setTitleText("请选择");
                picker.setStepMinute(1);
                picker.setWeightEnable(true);
                picker.setCanceledOnTouchOutside(true);
                LineConfig config = new LineConfig();
                config.setColor(Color.BLUE);//线颜色
                config.setAlpha(120);//线透明度
                config.setVisible(true);//线不显示 默认显示
                picker.setLineConfig(config);
                picker.setOuterLabelEnable(true);
//        picker.setLabel(null,null,null,null,null);
                picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
                    @Override
                    public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
//                        ToastUtils.showShort(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                        changeStatus(Status.CHECKED, year + "-" + month + "-" + day + " " + hour + ":" + minute);
                    }
                });
                picker.show();
            }
        });
        clear_choice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(Status.CLEARED, "");
            }
        });
    }

    public void setHint(String s) {
        editText.setHint(s);
    }

    /**
     * 打开下拉列表弹窗
     */
    public void showPopWindow() {
        // 加载popupWindow的布局文件
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.date_time_popwin, null, false);
        time_picker = (TimePicker) contentView.findViewById(R.id.time_picker);
        date_pick = (DatePicker) contentView.findViewById(R.id.date_pick);
        Button commit_btn = (Button) contentView.findViewById(R.id.commit_btn);
        commit_btn.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                int year = date_pick.getYear();
                int month = date_pick.getMonth() + 1;
                int day = date_pick.getDayOfMonth();
                int hour = time_picker.getHour();
                int minute = time_picker.getMinute();
                changeStatus(Status.CHECKED, year + "-" + (month < 10 ? "0" + month : month) + "-"
                        + (day < 10 ? "0" + day : day) + " " +
                        (hour < 10 ? "0" + hour : hour) + ":" + time_picker.getMinute());
            }
        });

        popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 关闭下拉列表弹窗
     */
    public void closePopWindow() {
        if (popupWindow != null)
            popupWindow.dismiss();
        popupWindow = null;
    }


    public void changeStatus(Status status, String model) {
        state = status;
        checkedTime = model;
        switch (status) {
            case INIT:
                clear_choice.setVisibility(GONE);
                imageView.setVisibility(GONE);
                editText.setText("");
                break;
            case CHECKED:
                clear_choice.setVisibility(VISIBLE);
                imageView.setVisibility(GONE);
                editText.setText(model);
                closePopWindow();
                break;
            case CLEARED:
                clear_choice.setVisibility(GONE);
                imageView.setVisibility(VISIBLE);
                editText.setText("");
                break;
        }
    }

    ;
}

