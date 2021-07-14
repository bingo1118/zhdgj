package com.smart.cloud.fire.geTuiPush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.Alarm.AlarmActivity;
import com.smart.cloud.fire.mvp.Alarm.UserAlarmActivity;
import com.smart.cloud.fire.mvp.LineChart.LineChartActivity;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.pushmessage.DisposeAlarm;
import com.smart.cloud.fire.pushmessage.GetUserAlarm;
import com.smart.cloud.fire.pushmessage.PushAlarmMsg;
import com.smart.cloud.fire.pushmessage.PushWiredSmokeAlarmMsg;
import com.smart.cloud.fire.retrofit.ApiStores;
import com.smart.cloud.fire.retrofit.AppClient;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/12/2.
 */
public class DemoIntentService extends GTIntentService {
    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String cid) {
        String userID = SharedPreferencesManager.getInstance().getData(context,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        SharedPreferencesManager.getInstance().putData(context, SharedPreferencesManager.SP_FILE_GWELL,
                "CID", cid);//@@5.27存储个推cid
        PushManager.getInstance().bindAlias(this.getApplicationContext(), userID);
        goToServer(cid, userID);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        String msg = new String(gtTransmitMessage.getPayload());
        boolean showDateChange = false;
        try {
            JSONObject dataJson = new JSONObject(msg);

            int deviceType = dataJson.getInt("deviceType");
            switch (deviceType) {
                case 224:
                case 221://@@6.30有线烟感
                    JSONObject WiredJson = dataJson.getJSONObject("masterFault");
                    String wiredMessage = "发生" + WiredJson.getString("faultType");
                    PushWiredSmokeAlarmMsg mPushAlarmMsg2 = jsJson2(dataJson.getJSONObject("masterFault"));
                    Random random2 = new Random();
//                        showDownNotification(context,wiredMessage,mPushAlarmMsg2,random2.nextInt(),AlarmActivity.class);
                    Intent wiredIntent = new Intent(context, AlarmActivity.class);
                    wiredIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    wiredIntent.putExtra("mPushAlarmMsg", mPushAlarmMsg2);
                    wiredIntent.putExtra("alarmMsg", wiredMessage);
                    wiredIntent.putExtra("isWiredAlarmMsg", 1);//@@6.30
                    context.startActivity(wiredIntent);
                    break;
                case 1://烟感
                case 2://燃气
//                case 8://手报
                case 10://水压@@4.28
                case 11://红外
                case 12://门磁
                case 15://水禁
                case 16://NB燃气
                case 18://喷淋
                case 19://水位
                case 21://LoraOne烟感
                case 22://南京平台燃气
                case 25://@@温湿度传感器
                case 31://三江iot烟感
                case 35://NB电弧
                case 36://联通NB电弧
                case 41://NB烟感
                case 42://@@NB水压2018.02.23
                case 43:
                case 45://海曼NB气感
                case 51://@@创安燃气
                case 55:
                case 56://NBiot烟感
                case 57://onet烟感
                case 69://恒星水位
                case 70://恒星水压
                case 111://@@小主机，终端
                case 119://联动烟感
                case 124://@@外接水位
                case 125://@@外接水压
                case 131://lora标签
                    String message = null;
                    int alarmType = dataJson.getInt("alarmType");
                    switch (deviceType) {
                        case 131:
                            message = "发生探测器故障";
                            break;
                        case 36:
                        case 35:
                            if (alarmType == 53) {
                                message = "发生报警";
                            } else if (alarmType == 36) {
                                message = "发生485故障";
                            } else if (alarmType == 54) {
                                message = "发生探测器故障";
                            }
                            break;
                        case 111:
                            message = "主机处于备电状态";
                            break;
                        case 25:
                            if (alarmType == 307) {
                                message = "发生低温报警";
                            } else if (alarmType == 308) {
                                message = "发生高温报警";
                            } else if (alarmType == 407) {
                                message = "湿度过低";
                            } else if (alarmType == 408) {
                                message = "湿度过高";
                            } else if (alarmType == 193) {
                                message = "烟感电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 31:
                            if (alarmType == 202) {
                                message = "发生烟雾报警";
                            } else if (alarmType == 67) {
                                message = "发生自检报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 119:
                        case 41:
                        case 57:
                        case 56:
                        case 55:
                        case 1:
                            if (alarmType == 202) {
                                message = "发生烟雾报警";
                            } else if (alarmType == 103) {
                                message = "发生温度报警";
                            } else if (alarmType == 104) {
                                message = "发生温度报警恢复";
                            } else if (alarmType == 105) {
                                message = "发生烟雾低电量报警";
                            } else if (alarmType == 106) {
                                message = "发生烟雾低电量报警恢复";
                            } else if (alarmType == 107) {
                                message = "发生低电量报警";
                            } else if (alarmType == 108) {
                                message = "发生低电量报警恢复";
                            } else if (alarmType == 109) {
                                message = "发生烟雾故障报警";
                            } else if (alarmType == 110) {
                                message = "发生烟雾故障报警恢复";
                            } else if (alarmType == 111) {
                                message = "发生温湿度故障报警";
                            } else if (alarmType == 112) {
                                message = "发生温湿度故障报警恢复";
                            } else if (alarmType == 113) {
                                message = "发生手动报警";
                            } else if (alarmType == 67) {
                                message = "发生自检报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else if (alarmType == 14) {
                                message = "该设备已被拆除";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 124:
                        case 69:
                        case 19:
                            if (alarmType == 207) {
                                message = "发生低水位报警";
                            } else if (alarmType == 208) {
                                message = "发生高水位报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else if (alarmType == 136) {
                                message = "发生通信故障";
                            } else if (alarmType == 36) {
                                message = "发生故障";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 21:
                            if (alarmType == 202) {
                                message = "发生烟雾报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 18://@@10.31 喷淋
                            if (alarmType == 202 || alarmType == 66 || alarmType == 203) {
                                message = "发生报警";
                            } else if (alarmType == 201) {
                                message = "阀门已关闭";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 45://@@海曼气感
                            if (alarmType == 71) {
                                message = "发生轻度泄露";
                            } else if (alarmType == 72) {
                                message = "发生重度泄露";
                            } else if (alarmType == 73) {
                                message = "发生短路报警";
                            } else if (alarmType == 74) {
                                message = "发生开路报警";
                            } else if (alarmType == 75) {
                                message = "发生机械手故障";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else if (alarmType == 70) {
                                message = "发生报警恢复";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 51:
                        case 22:
                        case 16:
                        case 2:
                            message = "燃气发生泄漏";
                            break;
                        case 7:
                            message = "声光发出报警";
                            break;
                        case 8:
                            message = "手动报警";
                            break;
                        case 11:
                            if (alarmType == 202 || alarmType == 206) {
                                message = "发生报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 12:
                            if (alarmType == 202 || alarmType == 205) {
                                message = "发生报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 15://@@8.3
                            if (alarmType == 202 || alarmType == 221) {
                                message = "发生报警";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                        case 125:
                        case 70:
                        case 42:
                        case 43:
                        case 10://@@4.28
                            int alarmFamily = dataJson.getInt("alarmFamily");
                            if (alarmType == 218) {
                                message = "发生高水压报警 水压值：" + alarmFamily + "kpa";
                            } else if (alarmType == 209) {
                                message = "发生低水压报警 水压值：" + alarmFamily + "kpa";
                            } else if (alarmType == 217) {
                                message = "发生水压升高,水压值：" + alarmFamily + "kpa";
                                showDateChange = true;
                            } else if (alarmType == 210) {
                                message = "发生水压降低,水压值：" + alarmFamily + "kpa";
                                showDateChange = true;
                            } else if (alarmType == 136) {
                                message = "发生通信故障";
                            } else if (alarmType == 36) {
                                message = "发生故障";
                            } else if (alarmType == 193) {
                                message = "电量低，请更换电池";
                            } else {
                                message = "发生未知类型报警";
                            }
                            break;
                    }
                    if (showDateChange == true) {
                        PushAlarmMsg mPushAlarmMsg = jsJson(dataJson);
                        Random random1 = new Random();
                        showDataChangeNotification(context, message, mPushAlarmMsg, random1.nextInt(), LineChartActivity.class);
                    } else {
                        PushAlarmMsg mPushAlarmMsg = jsJson(dataJson);
                        Random random1 = new Random();
                        showDownNotification(context, message, mPushAlarmMsg, random1.nextInt(), AlarmActivity.class);
                        Intent intent1 = new Intent(context, AlarmActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtra("mPushAlarmMsg", mPushAlarmMsg);
                        intent1.putExtra("alarmMsg", message);
                        context.startActivity(intent1);
                    }
                    break;
                case 77:
                case 75:
                case 59:
                case 53:
                case 52:
                case 8:
                case 7://三相
                case 6://单相
                case 5://电气
                    PushAlarmMsg pushAlarmMsg1 = jsJson(dataJson);
                    int alarmFamily = pushAlarmMsg1.getAlarmFamily();
                    String alarmMsg = null;
                    switch (alarmFamily) {
                        case 60://电量低
                            int alarmType18 = pushAlarmMsg1.getAlarmType();
                            if (alarmType18 != 0) {
                                alarmMsg = "电气探测器电量即将耗尽";
                            } else {
                                alarmMsg = "电气探测器电量即将耗尽（测试）";
                            }
                            break;
                        case 43://电气报警
                            int alarmType1 = pushAlarmMsg1.getAlarmType();
                            if (alarmType1 != 0) {
                                alarmMsg = "电气探测器发出：过压报警";
                            } else {
                                alarmMsg = "电气探测器发出：过压报警（测试）";
                            }
                            break;
                        case 36:
                            int alarmType36 = pushAlarmMsg1.getAlarmType();
                            switch (alarmType36) {
                                case 1:
                                    alarmMsg = "电气探测器发出：漏电流故障报警";
                                    break;
                                case 2:
                                    alarmMsg = "电气探测器发出：温度故障报警";
                                    break;
                                case 3:
                                    alarmMsg = "电气探测器发出：供电中断报警";
                                    break;
                                case 4:
                                    alarmMsg = "电气探测器发出：错相报警";
                                    break;
                                case 5:
                                    alarmMsg = "电气探测器发出：缺相报警";
                                    break;
                                case 6:
                                    alarmMsg = "电气探测器发出：电弧故障报警";
                                    break;
                                case 7:
                                    alarmMsg = "电气探测器发出：负载故障报警";
                                    break;
                                case 8:
                                    alarmMsg = "电气探测器发出：短路故障报警";
                                    break;
                                case 9:
                                    alarmMsg = "电气探测器发出：断路故障报警";
                                    break;
                                case 10://@@6.28
                                    alarmMsg = "电气探测器发出：485通信故障";
                                    break;
                                case 0://@@6.28
                                    alarmMsg = "电气探测器发出：故障报警";
                                    break;
                                default:
                                    alarmMsg = "电气探测器发出：故障报警";
                                    break;
                            }
                            break;
                        case 45://电气报警
                            int alarmType2 = pushAlarmMsg1.getAlarmType();
                            if (alarmType2 != 0) {
                                alarmMsg = "电气探测器发出：过流报警";
                            } else {
                                alarmMsg = "电气探测器发出：过流报警（测试）";
                            }
                            break;
                        case 44://欠压报警
                            int alarmType3 = pushAlarmMsg1.getAlarmType();
                            if (alarmType3 != 0) {
                                alarmMsg = "电气探测器发出：欠压报警";
                            } else {
                                alarmMsg = "电气探测器发出：欠压报警（测试）";
                            }
                            break;
                        case 46://电气报警
                            int alarmType4 = pushAlarmMsg1.getAlarmType();
                            if (alarmType4 != 0) {
                                alarmMsg = "电气探测器发出：漏电报警";
                            } else {
                                alarmMsg = "电气探测器发出：漏电报警（测试）";
                            }
                            break;
                        case 47://电气报警
                            int alarmType5 = pushAlarmMsg1.getAlarmType();
                            if (alarmType5 != 0) {
                                alarmMsg = "电气探测器发出：温度报警";
                            } else {
                                alarmMsg = "电气探测器发出：温度报警（测试）";
                            }
                            break;
                        case 48://分闸报警@@6.28
                            int alarmType6 = pushAlarmMsg1.getAlarmType();
                            if (alarmType6 != 0) {
                                alarmMsg = "电气探测器发出：合闸报警";
                            } else {
                                alarmMsg = "电气探测器发出：合闸报警（测试）";
                            }
                            break;
                        case 143://@@电气报警（贵州电气报警）8.11
                            int alarmType11 = pushAlarmMsg1.getAlarmType();
                            if (alarmType11 != 0) {
                                alarmMsg = "电气探测器发出：过压报警（线路已断开）";
                            }
                            break;
                        case 145://电气报警
                            int alarmType12 = pushAlarmMsg1.getAlarmType();
                            if (alarmType12 != 0) {
                                alarmMsg = "电气探测器发出：过流报警（线路已断开）";
                            }
                            break;
                        case 144://欠压报警
                            int alarmType13 = pushAlarmMsg1.getAlarmType();
                            if (alarmType13 != 0) {
                                alarmMsg = "电气探测器发出：欠压报警（线路已断开）";
                            }
                            break;
                        case 146://电气报警
                            int alarmType14 = pushAlarmMsg1.getAlarmType();
                            if (alarmType14 != 0) {
                                alarmMsg = "电气探测器发出：漏电报警（线路已断开）";
                            }
                            break;
                        case 147://电气报警
                            int alarmType15 = pushAlarmMsg1.getAlarmType();
                            if (alarmType15 != 0) {
                                alarmMsg = "电气探测器发出：温度报警（线路已断开）";
                            }
                            break;
                        case 148://合闸报警
                            int alarmType16 = pushAlarmMsg1.getAlarmType();
                            if (alarmType16 != 0) {
                                alarmMsg = "电气探测器发出：合闸报警";
                            }
                            break;
                        case 49://短路报警
                            alarmMsg = "电气探测器发出：短路报警";
                            break;
                        case 50://过热报警
                            alarmMsg = "电气探测器发出：过热报警";
                            break;
                        case 51:
                            alarmMsg = "电气探测器发出：分闸报警";
                            break;
                        case 52:
                            alarmMsg = "电气探测器发出：断路报警";
                            break;
                        default:
                            alarmMsg = "电气探测器发出：无该报警类型（测试）";
                            break;
                    }
                    Random random = new Random();
                    showDownNotification(context, alarmMsg, pushAlarmMsg1, random.nextInt(), AlarmActivity.class);
                    Intent intent2 = new Intent(context, AlarmActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtra("mPushAlarmMsg", pushAlarmMsg1);
                    intent2.putExtra("alarmMsg", alarmMsg);
                    context.startActivity(intent2);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private PushAlarmMsg jsJson(JSONObject dataJson) throws JSONException {
        PushAlarmMsg mPushAlarmMsg = new PushAlarmMsg();
        mPushAlarmMsg.setAddress(dataJson.getString("address"));
        mPushAlarmMsg.setAlarmType(dataJson.getInt("alarmType"));
        mPushAlarmMsg.setAreaId(dataJson.getString("areaId"));
        mPushAlarmMsg.setLatitude(dataJson.getString("latitude"));
        mPushAlarmMsg.setLongitude(dataJson.getString("longitude"));
        mPushAlarmMsg.setName(dataJson.getString("name"));
        mPushAlarmMsg.setPlaceAddress(dataJson.getString("placeAddress"));
        mPushAlarmMsg.setIfDealAlarm(dataJson.getInt("ifDealAlarm"));
        mPushAlarmMsg.setPrincipal1(dataJson.getString("principal1"));
        mPushAlarmMsg.setPlaceType(dataJson.getString("placeType"));
        mPushAlarmMsg.setPrincipal1Phone(dataJson.getString("principal1Phone"));
        mPushAlarmMsg.setPrincipal2(dataJson.getString("principal2"));
        mPushAlarmMsg.setPrincipal2Phone(dataJson.getString("principal2Phone"));
        mPushAlarmMsg.setAlarmTime(dataJson.getString("alarmTime"));
        mPushAlarmMsg.setDeviceType(dataJson.getInt("deviceType"));
        mPushAlarmMsg.setAlarmFamily(dataJson.getInt("alarmFamily"));
        try {
            JSONObject jsonObject = dataJson.getJSONObject("camera");
            if (jsonObject != null) {
                PushAlarmMsg.CameraBean cameraBean = new PushAlarmMsg.CameraBean();
                cameraBean.setCameraId(jsonObject.getString("cameraId"));
                cameraBean.setCameraPwd(jsonObject.getString("cameraPwd"));
                mPushAlarmMsg.setCamera(cameraBean);
            }
        } catch (Exception e) {
        }
        mPushAlarmMsg.setMac(dataJson.getString("mac"));
        return mPushAlarmMsg;
    }

    //@@6.30有线烟感推送信息
    private PushWiredSmokeAlarmMsg jsJson2(JSONObject dataJson) throws JSONException {
        PushWiredSmokeAlarmMsg mPushAlarmMsg = new PushWiredSmokeAlarmMsg();
        mPushAlarmMsg.setFaultCode(dataJson.getString("faultCode"));
        mPushAlarmMsg.setFaultDevDesc(dataJson.getString("faultDevDesc"));
        mPushAlarmMsg.setFaultInfo(dataJson.getString("faultInfo"));
        mPushAlarmMsg.setFaultTime(dataJson.getString("faultTime"));
        mPushAlarmMsg.setFaultType(dataJson.getString("faultType"));
        mPushAlarmMsg.setRepeater(dataJson.getString("repeater"));
        return mPushAlarmMsg;
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
        if (b) {
            MyApp.app.setPushState("Online");
        } else {
            MyApp.app.setPushState("Offline");
        }
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
        System.out.print(gtCmdMessage);
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    private void goToServer(String cid, String userId) {
        ApiStores apiStores = AppClient.retrofit(ConstantValues.SERVER_PUSH).create(ApiStores.class);
        Observable observable = apiStores.bindAlias(userId, cid, "scfire");
        addSubscription(observable, new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                MyApp.app.setPushState(model.getState());
            }

            @Override
            public void onFailure(int code, String msg) {
            }

            @Override
            public void onCompleted() {
//                stopSelf();
            }
        }));
    }

    private void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    @SuppressWarnings("deprecation")
    private void showDownNotification(Context context, String message, Serializable mPushAlarmMsg, int id, Class clazz) {
        NotificationCompat.Builder m_builder = new NotificationCompat.Builder(context);
        m_builder.setContentTitle(message); // 主标题

        //从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //具体的通知内容

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // 将PNG图片转
        m_builder.setLargeIcon(icon);

        m_builder.setSmallIcon(R.mipmap.ic_launcher); //设置小图标
        m_builder.setWhen(System.currentTimeMillis());
        m_builder.setAutoCancel(true);
        if (clazz != null) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//设置提示音
            m_builder.setSound(uri);
            m_builder.setContentText("点击查看详情"); //设置主要内容
            //通知消息与Intent关联
            Intent it = new Intent(context, clazz);
            it.putExtra("mPushAlarmMsg", mPushAlarmMsg);
            it.putExtra("alarmMsg", message);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(context, id, it, PendingIntent.FLAG_CANCEL_CURRENT);
            m_builder.setContentIntent(contentIntent);
        }
        //执行通知
        nm.notify(id, m_builder.build());
    }

    //@@2018.02.24水压变化通知
    private void showDataChangeNotification(Context context, String message, Serializable mPushAlarmMsg, int id, Class clazz) {
        NotificationCompat.Builder m_builder = new NotificationCompat.Builder(context);
        m_builder.setContentTitle(((PushAlarmMsg) mPushAlarmMsg).getName() + message); // 主标题

        //从系统服务中获得通知管理器
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //具体的通知内容

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // 将PNG图片转
        m_builder.setLargeIcon(icon);

        m_builder.setSmallIcon(R.mipmap.ic_launcher); //设置小图标
        m_builder.setWhen(System.currentTimeMillis());
        m_builder.setAutoCancel(true);
        long[] vibrates = {0, 1000, 1000, 1000};
        m_builder.getNotification().vibrate = vibrates;
        if (clazz != null) {
            m_builder.setContentText("点击查看详情"); //设置主要内容
            //通知消息与Intent关联
            Intent it = new Intent(context, clazz);
            it.putExtra("electricMac", ((PushAlarmMsg) mPushAlarmMsg).getMac());
            it.putExtra("isWater", "1");//@@是否为水压
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(context, id, it, PendingIntent.FLAG_CANCEL_CURRENT);
            m_builder.setContentIntent(contentIntent);
        }
        //执行通知
        nm.notify(id, m_builder.build());
    }
}
