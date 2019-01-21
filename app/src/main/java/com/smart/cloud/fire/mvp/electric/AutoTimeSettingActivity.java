package com.smart.cloud.fire.mvp.electric;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TimePickerDialog;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.XCDropDownListView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class AutoTimeSettingActivity extends Activity implements TimePickerDialog.TimePickerDialogInterface{


    @Bind(R.id.ontime_edit)
    EditText ontime_edit;//负责人姓名。。
    @Bind(R.id.offtime_edit)
    EditText offtime_edit;//负责人2.。
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//添加设备按钮。。
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//加载进度。。
    @Bind(R.id.swich)
    Switch swich;
    private Context mContext;

    TimePickerDialog mTimePickerDialog;
    int type=1;
    int state=1;
    private String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_time_setting);

        ButterKnife.bind(this);
        mContext=this;

        mac=getIntent().getStringExtra("mac");
        getAutoTime(mac);

        ontime_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerDialog = new TimePickerDialog(mContext);
                mTimePickerDialog.showTimePickerDialog();
                type=1;
                mac=getIntent().getStringExtra("mac");
            }
        });
        offtime_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerDialog = new TimePickerDialog(mContext);
                mTimePickerDialog.showTimePickerDialog();
                type=2;
            }
        });
        swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    state=1;
                } else {
                    state=0;
                }
            }
        });
        addFireDevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state==1&&(ontime_edit.getText().length()==0||offtime_edit.getText().length()==0)){
                    T.showShort(mContext,"请输入时间");
                    return;
                }
                String url= ConstantValues.SERVER_IP_NEW+"setElectrAutoOnAndOffTime?mac="+mac
                        +"&state="+state
                        +"&ontime="+ontime_edit.getText()
                        +"&offtime="+offtime_edit.getText();
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

    @Override
    public void positiveListener() {
        if(type==1){
            ontime_edit.setText(mTimePickerDialog.getHour()+":"+mTimePickerDialog.getMinute());
        }else{
            offtime_edit.setText(mTimePickerDialog.getHour()+":"+mTimePickerDialog.getMinute());
        }
    }

    @Override
    public void negativeListener() {

    }

    public void getAutoTime(String mac){
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        String url=ConstantValues.SERVER_IP_NEW+"getElectrAutoTime?mac="+mac;
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                ontime_edit.setText(response.getString("ontime"));
                                offtime_edit.setText(response.getString("offtime"));
                                switch (response.getString("state")){
                                    case "0":
                                        swich.setChecked(false);
                                        break;
                                    case "1":
                                        swich.setChecked(true);
                                        break;
                                }
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
}
