package com.smart.cloud.fire.mvp.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.igexin.sdk.PushManager;
import com.p2p.core.P2PHandler;
import com.p2p.core.update.UpdateManager;
import com.smart.cloud.fire.activity.AddDev.ChioceDevTypeActivity;
import com.smart.cloud.fire.activity.AlarmHistory.AlarmHistoryActivity;
import com.smart.cloud.fire.activity.AllSmoke.AllSmokeActivity;
import com.smart.cloud.fire.activity.Camera.CameraDevActivity;
import com.smart.cloud.fire.activity.Electric.ElectricDevActivity;
import com.smart.cloud.fire.activity.Host.HostActivity;
import com.smart.cloud.fire.activity.NFCDev.NFCDevActivity;
import com.smart.cloud.fire.activity.Setting.MyZoomActivity;
import com.smart.cloud.fire.activity.SecurityDev.SecurityDevActivity;
import com.smart.cloud.fire.activity.WiredDev.WiredDevActivity;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MainService;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.login.SplashActivity;
import com.smart.cloud.fire.mvp.main.presenter.MainPresenter;
import com.smart.cloud.fire.mvp.main.view.MainView;
import com.smart.cloud.fire.service.RemoteService;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.yoosee.P2PListener;
import com.smart.cloud.fire.yoosee.SettingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/9/21.
 */
public class MainActivity extends MvpActivity<MainPresenter> implements MainView {

    private Context mContext;
    private AlertDialog dialog_update;
    @Bind(R.id.home_alarm_light)
    ImageView home_alarm_light;
    @Bind(R.id.my_image)
    ImageView my_image;
    @Bind(R.id.sxcs_btn)
    RelativeLayout sxcs_btn;
    @Bind(R.id.tjsb_btn)
    RelativeLayout tjsb_btn;
    @Bind(R.id.dqfh_btn)
    RelativeLayout dqfh_btn;
    @Bind(R.id.spjk_btn)
    RelativeLayout spjk_btn;
    @Bind(R.id.zddw_btn)
    RelativeLayout zddw_btn;
    @Bind(R.id.xfwl_btn)
    RelativeLayout xfwl_btn;
    @Bind(R.id.nfc_btn)
    RelativeLayout nfc_btn;
    @Bind(R.id.zjgl_btn)
    RelativeLayout zjgl_btn;
    @Bind(R.id.home_alarm_lin)
    LinearLayout home_alarm_lin;
    @Bind(R.id.home_alarm_info_text)
    TextView home_alarm_info_text;

    Timer getlastestAlarm;
    AnimationDrawable anim ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        regFilter();
        anim = (AnimationDrawable) home_alarm_light.getBackground();
        startService(new Intent(MainActivity.this, RemoteService.class));
        //启动个推接收推送信息。。
        PushManager.getInstance().initialize(this.getApplicationContext(), com.smart.cloud.fire.geTuiPush.DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), com.smart.cloud.fire.geTuiPush.DemoIntentService.class);
    }

    @OnClick({R.id.my_image,R.id.sxcs_btn,R.id.tjsb_btn,R.id.alarm_history_lin,R.id.dqfh_btn,R.id.spjk_btn,R.id.zddw_btn,
            R.id.xfwl_btn,R.id.nfc_btn,R.id.zjgl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_image:
                Intent intent = new Intent(mContext, MyZoomActivity.class);
                startActivity(intent);
                break;
            case R.id.sxcs_btn:
                Intent intent_sxcs = new Intent(mContext, AllSmokeActivity.class);
                startActivity(intent_sxcs);
                break;
            case R.id.tjsb_btn:
                Intent intent_tjsb = new Intent(mContext, ChioceDevTypeActivity.class);
                startActivity(intent_tjsb);
                break;
            case R.id.alarm_history_lin:
                Intent intent_history = new Intent(mContext, AlarmHistoryActivity.class);
                startActivity(intent_history);
                break;
            case R.id.dqfh_btn:
                Intent intent_dqfh = new Intent(mContext, ElectricDevActivity.class);
                startActivity(intent_dqfh);
                break;
            case R.id.spjk_btn:
                Intent intent_spjk = new Intent(mContext, CameraDevActivity.class);
                startActivity(intent_spjk);
                break;
            case R.id.zddw_btn:
                Intent intent_zddw = new Intent(mContext, WiredDevActivity.class);
                startActivity(intent_zddw);
                break;
            case R.id.xfwl_btn:
                Intent intent_xfwl = new Intent(mContext, SecurityDevActivity.class);
                startActivity(intent_xfwl);
                break;
            case R.id.nfc_btn:
                Intent intent_nfc = new Intent(mContext, NFCDevActivity.class);
                startActivity(intent_nfc);
                break;
            case R.id.zjgl_btn:
                Intent intent_zjgl= new Intent(mContext, HostActivity.class);
                startActivity(intent_zjgl);
                break;
            default:
                break;
        }
    }

    private void initView() {
        if(MyApp.app.getPrivilege()==1){//@@9.29 1级权限显示
            zddw_btn.setVisibility(View.GONE);
            dqfh_btn.setVisibility(View.GONE);
            xfwl_btn.setVisibility(View.GONE);
            spjk_btn.setVisibility(View.GONE);
            tjsb_btn.setVisibility(View.GONE);
            nfc_btn.setVisibility(View.GONE);
            zjgl_btn.setVisibility(View.GONE);
        }
        P2PHandler.getInstance().p2pInit(this,
                new P2PListener(),
                new SettingListener());
        connect();
        getlastestAlarm=new Timer();
        getlastestAlarm.schedule(new TimerTask() {
            @Override
            public void run() {
                String username = SharedPreferencesManager.getInstance().getData(mContext,
                        SharedPreferencesManager.SP_FILE_GWELL,
                        SharedPreferencesManager.KEY_RECENTNAME);
                int privilege = MyApp.app.getPrivilege();
                String url= ConstantValues.SERVER_IP_NEW+"getLastestAlarm?userId="+username+"&privilege="+privilege;
                RequestQueue mQueue = Volley.newRequestQueue(MyApp.app);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int errorCode=response.getInt("errorCode");
                                    if(errorCode==0){
                                        JSONObject lasteatalarm=response.getJSONObject("lasteatAlarm");
                                        if(lasteatalarm.getString("ifDealAlarm")=="0"){
                                            anim.start();
                                            home_alarm_info_text.setText(lasteatalarm.getString("address")
                                                +"\n"+lasteatalarm.getString("name")+"发生报警");
                                            home_alarm_lin.setBackgroundResource(R.drawable.corners_shape_top);
                                        }else{
                                            anim.stop();
                                            home_alarm_info_text.setText(lasteatalarm.getString("address")
                                                    +"\n"+lasteatalarm.getString("name")+"发生报警【已处理】");
                                            home_alarm_lin.setBackgroundResource(R.drawable.corners_shape_top_normal);
                                        }
                                    }else{
                                        anim.stop();
                                        home_alarm_info_text.setText("无最新报警信息");
                                        home_alarm_lin.setBackgroundResource(R.drawable.corners_shape_top_normal);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        anim.stop();
                        home_alarm_info_text.setText("未获取到数据");
                        home_alarm_lin.setBackgroundResource(R.drawable.corners_shape_top_normal);
                    }
                });
                mQueue.add(jsonObjectRequest);
            }
        },0,10000);
    }

    private void connect() {
        Intent service = new Intent(mContext, MainService.class);//检查更新版本服务。。
        startService(service);
    }

    /**
     * 添加广播接收器件。。
     */
    private void regFilter() {
        IntentFilter filter = new IntentFilter();
//        filter.addAction("Constants.Action.ACTION_UPDATE");
//        filter.addAction("Constants.Action.ACTION_UPDATE_NO");
        filter.addAction("APP_EXIT");
        mContext.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            //退出。。
            if (intent.getAction().equals("APP_EXIT")) {
                showDialog();
            }
        }
    };

    private void showDialog() {
        AlertDialog.Builder build=new AlertDialog.Builder(mContext);
        build.setTitle("提示").setMessage("确定退出当前账号？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferencesManager.getInstance().putData(mContext,
                                SharedPreferencesManager.SP_FILE_GWELL,
                                SharedPreferencesManager.KEY_RECENTPASS,
                                "");
                        SharedPreferencesManager.getInstance().removeData(mContext,
                                "LASTAREANAME");//@@11.13
                        SharedPreferencesManager.getInstance().removeData(mContext,
                                "LASTAREAID");//@@11.13
                        SharedPreferencesManager.getInstance().removeData(mContext,
                                "LASTAREAISPARENT");//@@11.13
                        PushManager.getInstance().stopService(getApplicationContext());
                        unbindAlias();
                        Intent in = new Intent(mContext, SplashActivity.class);
                        startActivity(in);
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            build.show();
    }

    /**
     * 个推解绑@@5.16
     */
    private void unbindAlias() {
        String userCID = SharedPreferencesManager.getInstance().getData(this,SharedPreferencesManager.SP_FILE_GWELL,"CID");//@@
        String username = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        String url= ConstantValues.SERVER_IP_NEW+"loginOut?userId="+username+"&alias="+username+"&cid="+userCID+"&appId=1";//@@5.27添加app编号
        RequestQueue mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        try {
//                            Toast.makeText(mContext,response.getString("error"),Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(jsonObjectRequest);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mvpPresenter.exitBy2Click(mContext);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void exitBy2Click(boolean isExit) {
        if (isExit) {
            //moveTaskToBack(false);
            moveTaskToBack(true);//@@5.31
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
        getlastestAlarm.cancel();
    }

    private void alarmInit() {
        //imageview动画设置。。
        final AnimationDrawable anim = (AnimationDrawable) home_alarm_light.getBackground();
        ViewTreeObserver.OnPreDrawListener opdl = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        };
        home_alarm_light.getViewTreeObserver().addOnPreDrawListener(opdl);
    }

}
