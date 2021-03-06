package com.smart.cloud.fire.global;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.RemoteViews;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.mob.MobSDK;
import com.p2p.core.update.UpdateManager;
import com.smart.cloud.fire.activity.AccountManage.AccountEntity;
import com.smart.cloud.fire.adapter.SelectDevPlaceTypeAdapter;
import com.smart.cloud.fire.service.LocationService;
import com.smart.cloud.fire.ui.ForwardDownActivity;
import com.smart.cloud.fire.utils.CrashHandler;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.VolleyHelper;
import com.squareup.leakcanary.LeakCanary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/7/28.
 */
public class MyApp extends Application {
    public static MyApp app;
    public String pushState;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    public static final int NOTIFICATION_DOWN_ID = 0x53256562;
    private RemoteViews cur_down_view;
    private static int privilege=-1;
    public static String userid;
    public LocationService locationService;
    public Vibrator mVibrator;
    public static AccountEntity entity;



    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        entity=new AccountEntity();
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(this);
        //启动集错程序
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //检查内存是否泄漏初始化，正式版应该关闭
        LeakCanary.install(this);
        MobSDK.init(this);
        //启动个推接收推送信息。。
        PushManager.getInstance().initialize(this.getApplicationContext(), com.smart.cloud.fire.geTuiPush.DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), com.smart.cloud.fire.geTuiPush.DemoIntentService.class);
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public  void setPrivilege(int privilege){
        this.privilege = privilege;
        userid=SharedPreferencesManager.getInstance().getData(MyApp.app,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
    }

    public static int getPrivilege(){
        //return privilege;
        if(privilege==-1){
            return SharedPreferencesManager.getInstance().getIntData(MyApp.app,
                    SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.KEY_RECENT_PRIVILEGE);
        }else{
            return privilege;
        }//@@5.5防止突然网络错误问题
    }

    public static String getUserID(){
        if(userid==""){
            return SharedPreferencesManager.getInstance().getData(MyApp.app,
                    SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.KEY_RECENTNAME);
        }else{
            return userid;
        }//@@5.5防止突然网络错误问题
    }


    public String getPushState() {
        return pushState;
    }

    public void setPushState(String pushState) {
        this.pushState = pushState;
    }

    /**
     * 创建下载图标
     */
    @SuppressWarnings("deprecation")
    public  void showDownNotification(int state,int value) {
        boolean isShowNotify = SharedPreferencesManager.getInstance().getIsShowNotify(this);
        if(isShowNotify){

            mNotificationManager = getNotificationManager();
            mNotification = new Notification();
            long when = System.currentTimeMillis();
            mNotification = new Notification(
                    R.mipmap.ic_launcher,
                    this.getResources().getString(R.string.app_name),
                    when);
            // 放置在"正在运行"栏目中
            mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;

            RemoteViews contentView = new RemoteViews(getPackageName(),
                    R.layout.notify_down_bar);
            cur_down_view = contentView;
            contentView.setImageViewResource(R.id.icon,
                    R.mipmap.ic_launcher);

            Intent intent = new Intent(this,ForwardDownActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            switch(state){
                case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
                    cur_down_view.setTextViewText(R.id.down_complete_text, this.getResources().getString(R.string.down_complete_click));
                    cur_down_view.setTextViewText(R.id.progress_value,"100%");
                    contentView.setProgressBar(R.id.progress_bar, 100, 100, false);
                    intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_SUCCESS);
                    break;
                case UpdateManager.HANDLE_MSG_DOWNING:
                    cur_down_view.setTextViewText(R.id.down_complete_text, this.getResources().getString(R.string.down_londing_click));
                    cur_down_view.setTextViewText(R.id.progress_value,value+"%");
                    contentView.setProgressBar(R.id.progress_bar, 100, value, false);
                    intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWNING);
                    break;
                case UpdateManager.HANDLE_MSG_DOWN_FAULT:
                    cur_down_view.setTextViewText(R.id.down_complete_text, this.getResources().getString(R.string.down_fault_click));
                    cur_down_view.setTextViewText(R.id.progress_value,value+"%");
                    contentView.setProgressBar(R.id.progress_bar, 100, value, false);
                    intent.putExtra("state", UpdateManager.HANDLE_MSG_DOWN_FAULT);
                    break;
            }
            mNotification.contentView = contentView;
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotification.contentIntent = contentIntent;

            mNotificationManager.notify(NOTIFICATION_DOWN_ID,
                    mNotification);
        }
    }

    public void hideDownNotification(){
        mNotificationManager = getNotificationManager();
        mNotificationManager.cancel(NOTIFICATION_DOWN_ID);

    }

}
