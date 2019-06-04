package com.smart.cloud.fire.mvp.electric;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.cloud.fire.adapter.ElectricActivityAdapterTest;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.ElectricValue;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.LineChart.ElectricChartActivity;
import com.smart.cloud.fire.mvp.LineChart.LineChart01Activity;
import com.smart.cloud.fire.mvp.LineChart.LineChartActivity;
import com.smart.cloud.fire.mvp.electricChangeHistory.ElectricChangeHistoryActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.TimePickerDialog;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.smart.cloud.fire.view.DialChart05View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ElectricActivity extends MvpActivity<ElectricPresenter> implements ElectricView,TimePickerDialog.TimePickerDialogInterface {

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


    @Bind(R.id.dev_id)
    TextView dev_id;
    @Bind(R.id.dev_areaid)
    TextView dev_areaid;
    @Bind(R.id.dev_address)
    TextView dev_address;

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
    @Bind(R.id.yuzhi_gy)
    TextView yuzhi_gy;
    @Bind(R.id.yuzhi_qy)
    TextView yuzhi_qy;
    @Bind(R.id.yuzhi_dl)
    TextView yuzhi_dl;
    @Bind(R.id.yuzhi_ldl)
    TextView yuzhi_ldl;
    @Bind(R.id.yuzhi_wd)
    TextView yuzhi_wd;

    @Bind(R.id.dy_his)
    ImageButton dy_his;
    @Bind(R.id.dl_his)
    ImageButton dl_his;
    @Bind(R.id.ldl_his)
    ImageButton ldl_his;
    @Bind(R.id.wd_his)
    ImageButton wd_his;




    int devType=1;
    private String yuzhi43="";
    private String yuzhi44="";
    private String yuzhi45="";
    private String yuzhi46="";


    private String repeatMac;
    private int timetype=1;
    private String time;
    TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric);
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
        electricPresenter.getOneElectricInfo(userID,privilege+"",electricMac,false);
        electricData= (Electric) getIntent().getExtras().getSerializable("data");
        dev_id.setText("SN码:"+electricData.getMac());
        dev_areaid.setText("区域:"+electricData.getAreaName());
        dev_address.setText("地址:"+electricData.getAddress());
        if(devType==52||devType==53||devType==75){
            getYuzhi(electricMac);
        }
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
                    case R.id.change_history:
                        Intent intent=new Intent(mContext, ElectricChangeHistoryActivity.class);
                        intent.putExtra("mac",electricMac);
                        startActivity(intent);
                        break;
                    case R.id.auto_time:
                        Intent intent6=new Intent(mContext,AutoTimeSettingActivity.class);
                        intent6.putExtra("mac",electricMac);
                        startActivity(intent6);
                        break;
                    case R.id.yuzhi_set:
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.electr_threshold_setting,(ViewGroup) findViewById(R.id.rela));
                        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext).setView(layout);
                        final AlertDialog dialog =builder.create();
                        final EditText high_value=(EditText)layout.findViewById(R.id.high_value);
                        high_value.setText(yuzhi43);
                        final EditText low_value=(EditText)layout.findViewById(R.id.low_value);
                        low_value.setText(yuzhi44);
                        final EditText overcurrentvalue=(EditText)layout.findViewById(R.id.overcurrentvalue);
                        overcurrentvalue.setText(yuzhi45);
                        final EditText Leakage_value=(EditText)layout.findViewById(R.id.Leakage_value);
                        Leakage_value.setText(yuzhi46);
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
                                    if(devType==75||devType==77){
                                        if(low<100||low>200){
                                            T.showShort(mContext,"欠压阈值设置范围为100-200V");
                                            return;
                                        }
                                        if(high<230||high>320){
                                            T.showShort(mContext,"过压阈值设置范围为230-320V");
                                            return;
                                        }
                                        if(value45<4||value45>250){
                                            T.showShort(mContext,"过流阈值设置范围为4-250A");
                                            return;
                                        }
                                        if(value46<30||value46>1000){
                                            T.showShort(mContext,"漏电流阈值设置范围为30-1000mA");
                                            return;
                                        }
                                        if(low>high){
                                            T.showShort(mContext,"欠压阈值不能高于过压阈值");
                                            return;
                                        }
                                    }else{
                                        if(low<145||low>220){
                                            T.showShort(mContext,"欠压阈值设置范围为145-220V");
                                            return;
                                        }
                                        if(high<220||high>280){
                                            T.showShort(mContext,"过压阈值设置范围为220-280V");
                                            return;
                                        }
                                        if(value45<1||value45>63){
                                            T.showShort(mContext,"过流阈值设置范围为1-63A");
                                            return;
                                        }
                                        if(value46<10||value46>90){
                                            T.showShort(mContext,"漏电流阈值设置范围为10-90mA");
                                            return;
                                        }
                                        if(low>high){
                                            T.showShort(mContext,"欠压阈值不能高于过压阈值");
                                            return;
                                        }
                                    }
                                    if(devType==52){
                                        url= ConstantValues.SERVER_IP_NEW+"ackControlCvls?Overvoltage="+high_value.getText().toString()
                                                +"&Undervoltage="+low_value.getText().toString()
                                                +"&Overcurrent="+value45
                                                +"&Leakage="+value46
                                                +"&repeaterMac="+repeatMac+"&smokeMac="+electricMac+"&userId="+userID;
                                    }else if(devType==53){
                                        url= ConstantValues.SERVER_IP_NEW+"EasyIot_Uool_control?Overvoltage="+high_value.getText().toString()
                                                +"&Undervoltage="+low_value.getText().toString()
                                                +"&Overcurrent="+value45
                                                +"&Leakage="+value46
                                                +"&appId=1&devSerial="+electricMac+"&userId="+userID;
                                    }else if(devType==75||devType==77){
                                        url= ConstantValues.SERVER_IP_NEW+"Telegraphy_Uool_control?Overvoltage="+high_value.getText().toString()
                                                +"&Undervoltage="+low_value.getText().toString()
                                                +"&Overcurrent="+value45
                                                +"&Leakage="+value46
                                                +"&deviceType="+devType+"&devCmd=14&imei="+electricMac+"&userid="+userID;
                                    }else{
                                        Toast.makeText(getApplicationContext(),"该设备不支持阈值设置", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
//                                            Toast.makeText(getApplicationContext(),"设置中，请稍后", Toast.LENGTH_SHORT).show();
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
                                                        electricPresenter.getOneElectricInfo(userID,privilege+"",electricMac,false);
                                                    }else{
                                                        T.showShort(mContext,"设置失败");
                                                    }
                                                    getYuzhi(electricMac);
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
                        break;
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

    private void refreshListView() {
        //设置刷新时动画的颜色，可以设置4个

        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        swipeFreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                electricPresenter.getOneElectricInfo(userID,privilege+"",electricMac,true);
                getYuzhi(electricMac);
            }
        });
    }

    @Override
    protected ElectricPresenter createPresenter() {
        electricPresenter = new ElectricPresenter(this);
        return electricPresenter;
    }

    @Override
    public void getDataSuccess(List<ElectricValue.ElectricValueBean> smokeList) {
        if(smokeList.size()==0){
            Toast.makeText(mContext,"无数据",Toast.LENGTH_SHORT).show();
        }//@@7.7
        electricActivityAdapter = new ElectricActivityAdapterTest(mContext, smokeList, electricPresenter);
        setDataToView(smokeList);
        electricActivityAdapter.setOnItemClickListener(new ElectricActivityAdapterTest.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view, ElectricValue.ElectricValueBean data){
                Intent intent = new Intent(mContext, LineChart01Activity.class);
                intent.putExtra("electricMac",electricMac);
                intent.putExtra("electricType",data.getElectricType());
                intent.putExtra("electricNum",data.getId());
                startActivity(intent);
            }
        });
    }

    private void setDataToView(List<ElectricValue.ElectricValueBean> smokeList) {
        for (final ElectricValue.ElectricValueBean bean:smokeList) {
            String value=bean.getValue();
            switch (bean.getElectricType()){
                case 6:
                    if(bean.getElectricThreshold().length()>0&&bean.getElectricThreshold().contains("\\\\")){
                        yuzhi_gy.setText(bean.getElectricThreshold().split("\\\\")[0]);
                        yuzhi_gy.setText(bean.getElectricThreshold().split("\\\\")[1]);
                    }
                    if(null!=value&&value.length()>0){
                        try {
                            switch (bean.getId()){
                                case 1:
                                    dy_a.setText(Float.parseFloat(value)+"");
                                    break;
                                case 2:
                                    dy_b.setText(Float.parseFloat(value)+"");
                                    break;
                                case 3:
                                    dy_c.setText(Float.parseFloat(value)+"");
                                    break;
                            }
//                            dv_dy.setAllData(max,Integer.parseInt(value),"电压","(单位:V)",bean.getElectricThreshold());
                            dy_his.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ElectricChartActivity.class);
                                    intent.putExtra("electricMac",electricMac);
                                    intent.putExtra("electricType",bean.getElectricType());
                                    intent.putExtra("electricNum",bean.getId());
                                    startActivity(intent);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case 7:
                    if(bean.getElectricThreshold()!=null&&bean.getElectricThreshold().length()>0){
                        yuzhi_dl.setText(bean.getElectricThreshold());
                    }
                    try {
                        if(null!=value&&value.length()>0){
                            switch (bean.getId()){
                                case 1:
                                    dl_a.setText(Float.parseFloat(value)+"");
                                    break;
                                case 2:
                                    dl_b.setText(Float.parseFloat(value)+"");
                                    break;
                                case 3:
                                    dl_c.setText(Float.parseFloat(value)+"");
                                    break;
                            }
//                            max=Float.parseFloat(value)<300?300:560;
//                            dv_dl.setAllData(max,(int)Float.parseFloat(value),"电流","(单位:A)",bean.getElectricThreshold());
                            dl_his.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ElectricChartActivity.class);
                                    intent.putExtra("electricMac",electricMac);
                                    intent.putExtra("electricType",bean.getElectricType());
                                    intent.putExtra("electricNum",bean.getId());
                                    startActivity(intent);
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 8:
                    if(bean.getElectricThreshold()!=null&&bean.getElectricThreshold().length()>0){
                        yuzhi_ldl.setText(bean.getElectricThreshold());
                    }
                    try {
                        if(null!=value&&value.length()>0){
                            ldl_a.setText(Float.parseFloat(value)+"");

//                            dv_ldl.setAllData(max,Integer.parseInt(value),"漏电流","(单位:mA)",bean.getElectricThreshold());
                            ldl_his.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ElectricChartActivity.class);
                                    intent.putExtra("electricMac",electricMac);
                                    intent.putExtra("electricType",bean.getElectricType());
                                    intent.putExtra("electricNum",bean.getId());
                                    startActivity(intent);
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 9:
                    if(bean.getElectricThreshold()!=null&&bean.getElectricThreshold().length()>0){
                        yuzhi_wd.setText(bean.getElectricThreshold());
                    }
                    try {
                        if(null!=value&&value.length()>0){
                            switch (bean.getId()){
                                case 1:
                                    wd_a.setText(Float.parseFloat(value)+"");
                                    break;
                                case 2:
                                    wd_b.setText(Float.parseFloat(value)+"");
                                    break;
                                case 3:
                                    wd_c.setText(Float.parseFloat(value)+"");
                                    break;
                                case 4:
                                    wd_n.setText(Float.parseFloat(value)+"");
                                    break;
                            }
//                            max=Float.parseFloat(value)<300?300:560;
//                            dv_dl.setAllData(max,(int)Float.parseFloat(value),"电流","(单位:A)",bean.getElectricThreshold());
                            wd_his.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, ElectricChartActivity.class);
                                    intent.putExtra("electricMac",electricMac);
                                    intent.putExtra("electricType",bean.getElectricType());
                                    intent.putExtra("electricNum",bean.getId());
                                    startActivity(intent);
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
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

    public void getYuzhi(String mac){
        VolleyHelper helper=VolleyHelper.getInstance(mContext);
        String url=ConstantValues.SERVER_IP_NEW+"getElectrAlarmThreshold?mac="+mac;
        RequestQueue mQueue = helper.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errorCode=response.getInt("errorCode");
                            if(errorCode==0){
                                yuzhi43=response.getString("value43");
                                yuzhi44=response.getString("value44");
                                yuzhi45=response.getString("value45");
                                yuzhi46=response.getString("value46");
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
                                electricPresenter.getOneElectricInfo(userID,privilege+"",electricMac,false);
                            }else{
                                T.showShort(mContext,"设置失败");
                            }
                            getYuzhi(electricMac);
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
}
