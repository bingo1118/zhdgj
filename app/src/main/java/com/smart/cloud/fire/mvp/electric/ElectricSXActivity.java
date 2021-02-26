package com.smart.cloud.fire.mvp.electric;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.adapter.ElectricActivityAdapterTest;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.ElectricDXDetailEntity;
import com.smart.cloud.fire.global.ElectricDetailEntity;
import com.smart.cloud.fire.global.ElectricValue;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.ElectrTimerTask.ElectrTimerTaskActivity;
import com.smart.cloud.fire.mvp.LineChart.ElectricChartActivity;
import com.smart.cloud.fire.mvp.LineChart.PowerChartActivity;
import com.smart.cloud.fire.mvp.electricChangeHistory.ElectricChangeHistoryActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TimePickerDialog;
import com.smart.cloud.fire.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

public class ElectricSXActivity extends MvpActivity<ElectricPresenter> implements ElectricView,TimePickerDialog.TimePickerDialogInterface {

    @Bind(R.id.swipe_fresh_layout)
    ImageView swipeFreshLayout;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    private ElectricPresenter electricPresenter;
    private ElectricActivityAdapterTest electricActivityAdapter;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private String electricMac;
    private String userID;
    private int privilege;
    Electric electricData;

    private ElectricDXDetailEntity mModel;


    @Bind(R.id.dev_id)
    TextView dev_id;
    @Bind(R.id.dev_ccid)
    TextView dev_ccid;
    @Bind(R.id.dev_areaid)
    TextView dev_areaid;
    @Bind(R.id.dev_address)
    TextView dev_address;
    @Bind(R.id.dev_place)
    TextView dev_place;

    @Bind(R.id.more)
    TextView more;//@@菜单

    @Bind(R.id.dy_a)
    TextView dy_a;
    @Bind(R.id.dy_b)
    TextView dy_b;
    @Bind(R.id.dy_c)
    TextView dy_c;
    @Bind(R.id.dl_a)
    TextView dl_a;
    @Bind(R.id.dl_b)
    TextView dl_b;
    @Bind(R.id.dl_c)
    TextView dl_c;
    @Bind(R.id.ldl_a)
    TextView ldl_a;
    @Bind(R.id.wd_a)
    TextView wd_a;
    @Bind(R.id.wd_b)
    TextView wd_b;
    @Bind(R.id.wd_c)
    TextView wd_c;
    @Bind(R.id.wd_n)
    TextView wd_n;
    @Bind(R.id.sydl_a)
    TextView sydl_a;
    @Bind(R.id.tzdl_a)
    TextView tzdl_a;
    @Bind(R.id.dianliang_a)
    TextView dianliang_a;
    @Bind(R.id.price_value)
    TextView price_value;
    @Bind(R.id.gl_a)
    TextView gl_a;
    @Bind(R.id.gl_b)
    TextView gl_b;
    @Bind(R.id.gl_c)
    TextView gl_c;
    @Bind(R.id.glys_a)
    TextView glys_a;
    @Bind(R.id.glys_b)
    TextView glys_b;
    @Bind(R.id.glys_c)
    TextView glys_c;
    @Bind(R.id.pl_a)
    TextView pl_a;

    @Bind(R.id.yuzhi_dy_a)
    TextView yuzhi_dy_a;
    @Bind(R.id.yuzhi_dy_b)
    TextView yuzhi_dy_b;
    @Bind(R.id.yuzhi_dy_c)
    TextView yuzhi_dy_c;

    @Bind(R.id.yuzhi_dl_a)
    TextView yuzhi_dl_a;
    @Bind(R.id.yuzhi_dl_b)
    TextView yuzhi_dl_b;
    @Bind(R.id.yuzhi_dl_c)
    TextView yuzhi_dl_c;

    @Bind(R.id.yuzhi_ldl)
    TextView yuzhi_ldl;

    @Bind(R.id.yuzhi_wd_a)
    TextView yuzhi_wd_a;
    @Bind(R.id.yuzhi_wd_b)
    TextView yuzhi_wd_b;
    @Bind(R.id.yuzhi_wd_c)
    TextView yuzhi_wd_c;
    @Bind(R.id.yuzhi_wd_n)
    TextView yuzhi_wd_n;

    @Bind(R.id.dy_his_a)
    ImageButton dy_his_a;
    @Bind(R.id.dy_his_b)
    ImageButton dy_his_b;
    @Bind(R.id.dy_his_c)
    ImageButton dy_his_c;

    @Bind(R.id.dl_his_a)
    ImageButton dl_his_a;
    @Bind(R.id.dl_his_b)
    ImageButton dl_his_b;
    @Bind(R.id.dl_his_c)
    ImageButton dl_his_c;

    @Bind(R.id.ldl_his)
    ImageButton ldl_his;

    @Bind(R.id.wd_his_a)
    ImageButton wd_his_a;
    @Bind(R.id.wd_his_b)
    ImageButton wd_his_b;
    @Bind(R.id.wd_his_c)
    ImageButton wd_his_c;
    @Bind(R.id.wd_his_n)
    ImageButton wd_his_n;
    @Bind(R.id.dianliang_his)
    ImageButton dianliang_his;

    @Bind(R.id.setting_dev_img)
    ImageView setting_dev_img;
    @Bind(R.id.share_dev_img)
    ImageView share_dev_img;
    @Bind(R.id.timer_img)
    ImageView timer_img;

    @Bind(R.id.line_wendu)
    LinearLayout line_wendu;




    int devType=1;


    private String repeatMac;
    private int timetype=1;
    private String time;
    TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_sx);
        mContext=this;
        electricMac = getIntent().getExtras().getString("ElectricMac");
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        devType = getIntent().getExtras().getInt("devType");
        repeatMac = getIntent().getExtras().getString("repeatMac");
        ButterKnife.bind(this);
        refreshListView();
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        setting_dev_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yuzhi_set_dx();
            }
        });
        timer_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_time();
            }
        });
        share_dev_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.share_dev,(ViewGroup) findViewById(R.id.rela));
                final AlertDialog.Builder builder=new AlertDialog.Builder(mContext).setView(layout);
                final AlertDialog dialog =builder.create();
                final EditText userid_edit=(EditText)layout.findViewById(R.id.userid_edit);

                Button commit=(Button)(Button)layout.findViewById(R.id.commit);
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userid=userid_edit.getText().toString();
                        if(userid.length()==0){
                            T.showShort(mContext,"输入不可为空");
                        }else{
                            electricPresenter.shareDev(userid,electricMac,mContext,dialog);
                        }
                    }
                });
                dialog.show();
            }
        });
        electricPresenter.getOneElectricDXInfo(userID,privilege+"",electricMac,devType,false);
        electricData= (Electric) getIntent().getExtras().getSerializable("data");
        dev_id.setText("SN码:"+electricData.getMac());
        dev_areaid.setText("区域:"+electricData.getAreaName());
        dev_address.setText("地址:"+electricData.getAddress());
        dev_place.setText("分组:"+electricData.getPlaceType());
//        getYuzhi(electricMac);

        electricPresenter.getOneElectricDXyuzhi(electricMac);
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_electr, popupMenu.getMenu());
        // menu的item点击事件
        if(devType!=52&&devType!=53&&devType!=75&&devType!=77){
            MenuItem item=popupMenu.getMenu().findItem(R.id.yuzhi_set);
            item.setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.move:
                        getPlaces();
                        break;
                    case R.id.change_history:
                        Intent intent=new Intent(mContext, ElectricChangeHistoryActivity.class);
                        intent.putExtra("mac",electricMac);
                        startActivity(intent);
                        break;
                    case R.id.auto_time:
                        auto_time();
                        break;
                    case R.id.yuzhi_set:
                        yuzhi_set_dx();
                        break;
                    case R.id.add_battery:
                        if(mModel.getPayMode().equals("0")){
                            addBattery();
                        }else{
                            T.showShort(mContext,"仅预付费模式支持");
                        }

                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }

    private void auto_time() {
        Intent intent6=new Intent(mContext,ElectrTimerTaskActivity.class);
        intent6.putExtra("mac",electricMac);
        intent6.putExtra("devType",devType+"");
        startActivity(intent6);
    }


    //莱源第三方单相阈值设置
    private void yuzhi_set_dx() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.eletr_threshold_setting_dx,(ViewGroup) findViewById(R.id.rela));
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext).setView(layout);
        final AlertDialog dialog =builder.create();
        final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
        high_value.setText(mModel.getThreshold33());
        final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
        low_value.setText(mModel.getThreshold34());
        final EditText overcurrentvalue=(EditText)layout.findViewById(R.id.overcurrentvalue);
        overcurrentvalue.setText(mModel.getThreshold35());
        final EditText Leakage_value=(EditText)layout.findViewById(R.id.Leakage_value);
        Leakage_value.setText(mModel.getThreshold36());
        final EditText temp_value=(EditText)layout.findViewById(R.id.temp_value);
        temp_value.setText(mModel.getThreshold37());

        final Switch high_value_enable=(Switch) layout.findViewById(R.id.high_value_enable);
        high_value_enable.setChecked(mModel.getThreshold33enable().equals("1"));
        final Switch low_value_enable=(Switch) layout.findViewById(R.id.low_value_enable);
        low_value_enable.setChecked(mModel.getThreshold34enable().equals("1"));
        final Switch overcurrentvalue_enable=(Switch) layout.findViewById(R.id.overcurrentvalue_enable);
        overcurrentvalue_enable.setChecked(mModel.getThreshold35enable().equals("1"));
        final Switch Leakage_value_enable=(Switch) layout.findViewById(R.id.Leakage_value_enable);
        Leakage_value_enable.setChecked(mModel.getThreshold36enable().equals("1"));
        final Switch temp_value_name=(Switch) layout.findViewById(R.id.temp_value_enable);
        temp_value_name.setChecked(mModel.getThreshold37enable().equals("1"));

        final EditText action_delay_value=(EditText)layout.findViewById(R.id.action_delay_value);
        action_delay_value.setText(mModel.getActionDelay());
        final EditText open_sum_value=(EditText)layout.findViewById(R.id.open_sum_value);
        open_sum_value.setText(mModel.getOpenSum());
        final EditText price_value=(EditText)layout.findViewById(R.id.price_value);

        final RadioGroup auto_mode_value=(RadioGroup)layout.findViewById(R.id.auto_mode_value);
        final RadioButton shoudong=(RadioButton)layout.findViewById(R.id.shoudong);
        final RadioButton zidong=(RadioButton)layout.findViewById(R.id.zidong);
        if(mModel.getAutoMode().equals("0")){
            shoudong.setChecked(true);
        }else{
            zidong.setChecked(true);
        }

        final RadioButton xian=(RadioButton)layout.findViewById(R.id.xian);
        final RadioButton hou=(RadioButton)layout.findViewById(R.id.hou);
        if(mModel.getPayMode().equals("0")){
            xian.setChecked(true);
        }else{
            hou.setChecked(true);
        }

        Button commit=(Button)(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{
                    int high=(int)Float.parseFloat(high_value.getText().toString());
                    int low=(int)Float.parseFloat(low_value.getText().toString());
                    float value45=Float.parseFloat(overcurrentvalue.getText().toString());
                    int value46=(int)Float.parseFloat(Leakage_value.getText().toString());
                    int value47=(int)Float.parseFloat(temp_value.getText().toString());

                    if(low<130||low>220){
                        T.showShort(mContext,"欠压阈值设置范围为130-220V");
                        return;
                    }
                    if(high<220||high>280){
                        T.showShort(mContext,"过压阈值设置范围为220-280V");
                        return;
                    }
                    if(value45<1||value45>100){
                        T.showShort(mContext,"过流阈值设置范围为1-100A");
                        return;
                    }
                    if(value46<5||value46>1000){
                        T.showShort(mContext,"漏电流阈值设置范围为5-1000mA");
                        return;
                    }
                    if(value47<20||value47>200){
                        T.showShort(mContext,"漏电流阈值设置范围为20-200mA");
                        return;
                    }
                    if(low>high){
                        T.showShort(mContext,"欠压阈值不能高于过压阈值");
                        return;
                    }

                    url= ConstantValues.SERVER_IP_NEW+"ackThresholdSX?threshold43="+high
                            +"&threshold44="+low
                            +"&threshold45="+value45
                            +"&threshold46="+value46
                            +"&threshold47="+value47
                            +"&threshold43enable="+(high_value_enable.isChecked()?"1":"0")
                            +"&threshold44enable="+(low_value_enable.isChecked()?"1":"0")
                            +"&threshold45enable="+(overcurrentvalue_enable.isChecked()?"1":"0")
                            +"&threshold46enable="+(Leakage_value_enable.isChecked()?"1":"0")
                            +"&threshold47enable="+(temp_value_name.isChecked()?"1":"0")
                            +"&actionDelay="+action_delay_value.getText().toString()
                            +"&openSum="+open_sum_value.getText().toString()
                            +"&autoMode="+(zidong.isChecked()?"1":"0")
                            +"&payMode="+(hou.isChecked()?"1":"0")
                            +"&price="+price_value.getText().toString()
                            +"&mac="+electricMac+"&userId="+userID;

                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"输入数据不完全或有误");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("提示");
                dialog1.setMessage("设置中，请稍候");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
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
                                        electricPresenter.getOneElectricDXInfo(userID,privilege+"",electricMac, devType, false);
                                    }else{
                                        T.showShort(mContext,"设置失败");
                                    }
//                                    getYuzhi(electricMac);
                                    electricPresenter.getOneElectricDXyuzhi(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"网络错误");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void refreshListView() {
        //设置刷新时动画的颜色，可以设置4个

        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        swipeFreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastestData();
//                electricPresenter.getOneElectricDXInfo(userID,privilege+"",electricMac, devType, true);
//                getYuzhi(electricMac);
                electricPresenter.getOneElectricDXyuzhi(electricMac);
            }
        });
    }

    private void getLastestData() {
        showLoading();
        String url=ConstantValues.SERVER_IP_NEW+"ackDataRefreshSX?mac="+electricMac+"&userId="+userID;
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                T.showShort(mContext,"刷新成功");
                                electricPresenter.getOneElectricDXInfo(userID,privilege+"",electricMac, devType, false);
                            }else{
                                T.showShort(mContext,"刷新失败");
                            }
                            hideLoading();
//                            getYuzhi(electricMac);
                            electricPresenter.getOneElectricDXyuzhi(electricMac);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoading();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"网络错误");
                hideLoading();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    @Override
    protected ElectricPresenter createPresenter() {
        electricPresenter = new ElectricPresenter(this);
        return electricPresenter;
    }

    @Override
    public void getDataSuccess(List<ElectricValue.ElectricValueBean> smokeList) {

    }

    @Override
    public void getDataDXSuccess(ElectricDXDetailEntity entity) {
        setDataToView(entity);
    }

    private void setDataToView(ElectricDXDetailEntity entity) {
        dev_ccid.setText("CCID:"+entity.getCcid());

        dy_a.setText(Float.parseFloat(entity.getVoltage())+"");
        dy_b.setText(Float.parseFloat(entity.getVoltageB())+"");
        dy_c.setText(Float.parseFloat(entity.getVoltageC())+"");
        dy_his_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",6);
                intent.putExtra("electricNum",1);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        dy_his_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",6);
                intent.putExtra("electricNum",2);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        dy_his_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",6);
                intent.putExtra("electricNum",3);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });

        dl_a.setText(Float.parseFloat(entity.getCurrent())+"");
        dl_b.setText(Float.parseFloat(entity.getCurrentB())+"");
        dl_c.setText(Float.parseFloat(entity.getCurrentC())+"");
        dl_his_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",7);
                intent.putExtra("electricNum",1);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        dl_his_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",7);
                intent.putExtra("electricNum",2);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        dl_his_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",7);
                intent.putExtra("electricNum",3);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });

        ldl_a.setText(Float.parseFloat(entity.getLeakage())+"");
        ldl_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",8);
                intent.putExtra("electricNum",1);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });

        wd_a.setText(Float.parseFloat(entity.getTempFire())+"");
        wd_b.setText(Float.parseFloat(entity.getTempFireB())+"");
        wd_c.setText(Float.parseFloat(entity.getTempFireC())+"");
        wd_n.setText(Float.parseFloat(entity.getTempZero())+"");
        wd_his_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",9);
                intent.putExtra("electricNum",1);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        wd_his_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",9);
                intent.putExtra("electricNum",2);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        wd_his_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",9);
                intent.putExtra("electricNum",3);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        wd_his_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ElectricChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",9);
                intent.putExtra("electricNum",4);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        dianliang_a.setText(entity.getTotalBattery());
        dianliang_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PowerChartActivity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",9);
                intent.putExtra("devType",devType+"");
                startActivity(intent);
            }
        });
        sydl_a.setText(entity.getRemainingBattery());
        tzdl_a.setText(entity.getOverdraft());
        float price=0;
        try {
            price=Float.parseFloat(entity.getTotalBattery()) * Float.parseFloat(entity.getPrice());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            price_value.setText(price+"");
        }
        gl_a.setText(entity.getPower());
        gl_b.setText(entity.getPowerB());
        gl_c.setText(entity.getPowerC());
        glys_a.setText(entity.getPowerFactor());
        glys_b.setText(entity.getPowerFactorB());
        glys_c.setText(entity.getPowerFactorC());
        pl_a.setText(entity.getFrequency());
    }

    @Override
    public void getDataFail(String msg) {
        T.showShort(mContext,msg);
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
    public void getDataDXyuzhiSuccess(ElectricDXDetailEntity model) {
        mModel=model;
        yuzhi_dy_a.setText("阈值:"+mModel.getThreshold33()+"—"+mModel.getThreshold34());
        yuzhi_dy_b.setText("阈值:"+mModel.getThreshold33()+"—"+mModel.getThreshold34());
        yuzhi_dy_c.setText("阈值:"+mModel.getThreshold33()+"—"+mModel.getThreshold34());
        yuzhi_dl_a.setText("阈值:"+mModel.getThreshold35());
        yuzhi_dl_b.setText("阈值:"+mModel.getThreshold35());
        yuzhi_dl_c.setText("阈值:"+mModel.getThreshold35());
        yuzhi_ldl.setText("阈值:"+mModel.getThreshold36());
        yuzhi_wd_a.setText("阈值:"+mModel.getThreshold37());
        yuzhi_wd_b.setText("阈值:"+mModel.getThreshold37());
        yuzhi_wd_c.setText("阈值:"+mModel.getThreshold37());
        yuzhi_wd_n.setText("阈值:"+mModel.getThreshold37());
    }

    public void getYuzhi1(String mac){
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        String url=ConstantValues.SERVER_IP_NEW+"getElectrDXThreshold?mac="+mac;
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
//                                yuzhi43=response.getString("value43");
//                                yuzhi44=response.getString("value44");
//                                yuzhi45=response.getString("value45");
//                                yuzhi46=response.getString("value46");
//                                yuzhi47=response.getString("value47");
//                                yuzhi_qy.setText(yuzhi44);
//                                yuzhi_gy.setText(yuzhi43);
//                                yuzhi_dl.setText(yuzhi45);
//                                yuzhi_ldl.setText(yuzhi46);
//                                yuzhi_wd.setText(yuzhi47);
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

    @Override
    public void positiveListener() {
        String url=ConstantValues.SERVER_IP_NEW+"setElectrAutoTime?mac="+electricMac
                +"&type="+timetype
                +"&time="+mTimePickerDialog.getHour()+":"+mTimePickerDialog.getMinute();
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
                                electricPresenter.getOneElectricDXInfo(userID,privilege+"",electricMac, devType, false);
                            }else{
                                T.showShort(mContext,"设置失败");
                            }
//                            getYuzhi(electricMac);
                            electricPresenter.getOneElectricDXyuzhi(electricMac);
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

    @Override
    public void negativeListener() {

    }

    //莱源电量充值
    private void addBattery() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.add_battery_setting,(ViewGroup) findViewById(R.id.rela));
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext).setView(layout);
        final AlertDialog dialog =builder.create();
        final EditText value_et=(EditText)layout.findViewById(R.id.value_et);


        Button commit=(Button)(Button)layout.findViewById(R.id.commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                try{
                    int value=(int)Float.parseFloat(value_et.getText().toString());
                    url= ConstantValues.SERVER_IP_NEW+"ackBatterySX?battery="+value
                            +"&mac="+electricMac+"&userId="+userID;

                }catch(Exception e){
                    e.printStackTrace();
                    T.showShort(mContext,"输入数据不完全或有误");
                    return;
                }
                final ProgressDialog dialog1 = new ProgressDialog(mContext);
                dialog1.setTitle("提示");
                dialog1.setMessage("设置中，请稍候");
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
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
                                        T.showShort(mContext,response.getString("error"));
                                    }
                                    electricPresenter.getOneElectricDXyuzhi(electricMac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog1.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        T.showShort(mContext,"网络错误");
                        dialog1.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                mQueue.add(jsonObjectRequest);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getPlaces() {
        // 建立数据
        String url= ConstantValues.SERVER_IP_NEW+"getPlaceTypeId?userId="+userID+"&privilege="+privilege;

        VolleyHelper helper=VolleyHelper.getInstance(MyApp.app);
        final RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("errorCode")==0){
                                List<ShopType>  placeTypeList=new ArrayList<>();
                                JSONArray array=response.getJSONArray("placeType");
                                for(int i=0;i<array.length();i++){
                                    JSONObject o= array.getJSONObject(i);
                                    ShopType temp=new ShopType();
                                    temp.setPlaceTypeId(o.getString("placeTypeId"));
                                    temp.setPlaceTypeName(o.getString("placeTypeName"));
                                    placeTypeList.add(temp);
                                }
                                String[] mItems =new String[placeTypeList.size()];
                                for (int i=0;i<placeTypeList.size();i++) {
                                    mItems[i]=placeTypeList.get(i).getPlaceTypeName();
                                };
                                final List<ShopType>  placeTypeListTemp=placeTypeList;
                                AlertDialog.Builder customizeDialog =
                                        new AlertDialog.Builder(mContext);
                                final View dialogView = LayoutInflater.from(mContext)
                                        .inflate(R.layout.dialog_moveplace,null);
                                final Spinner spinner =(Spinner) dialogView.findViewById(R.id.place_spinner);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item ,mItems);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);

                                customizeDialog.setTitle("移动分组至:");
                                customizeDialog.setView(dialogView);
                                customizeDialog.setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                movePlace(electricMac,placeTypeListTemp.get(spinner.getSelectedItemPosition()).getPlaceTypeId()+"",placeTypeListTemp.get(spinner.getSelectedItemPosition()).getPlaceTypeName()+"");
                                            }
                                        });
                                customizeDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(jsonObjectRequest);
    }

    private void movePlace(String mac, String placeId, final String placeName) {
        String url= ConstantValues.SERVER_IP_NEW+"movePlace?userId="+userID
                +"&privilege="+privilege
                +"&mac="+mac
                +"&placeId="+placeId;

        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        final RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("errorCode")==0){
                                T.showShort(mContext,"移动成功");
                                dev_place.setText("分组:"+placeName);
                            }
                        } catch (JSONException e) {
                            T.showShort(mContext,"移动失败");
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
