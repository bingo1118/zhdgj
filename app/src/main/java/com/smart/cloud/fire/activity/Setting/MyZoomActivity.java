
package com.smart.cloud.fire.activity.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.p2p.core.update.UpdateManager;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.login.SplashActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;

import java.io.File;
import java.util.List;

import fire.cloud.smart.com.smartcloudfire.R;

public class MyZoomActivity extends AppCompatActivity {

    Context mContext;
    private AlertDialog dialog_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zoom);
        mContext=this;

        regFilter();
    }

    /**
     * 添加广播接收器件。。
     */
    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Constants.Action.ACTION_UPDATE");
        filter.addAction("Constants.Action.ACTION_UPDATE_NO");
        mContext.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            //已是最新版本。。
            if (intent.getAction().equals("Constants.Action.ACTION_UPDATE_NO")) {
                View view = LayoutInflater.from(mContext).inflate(
                        R.layout.dialog_update, null);
                TextView title = (TextView) view.findViewById(R.id.title_text);
                TextView content = (TextView) view
                        .findViewById(R.id.content_text);
                TextView button2 = (TextView) view
                        .findViewById(R.id.button2_text);
                ImageView minddle_image = (ImageView) view
                        .findViewById(R.id.minddle_image);
                RelativeLayout cancel_rela_dialog = (RelativeLayout) view
                        .findViewById(R.id.cancel_rela_dialog);
                title.setText("更新消息");
                content.setBackgroundColor(getResources().getColor(R.color.update_message)); // 设置背景色
                content.getBackground().setAlpha(255); // 设置填充透明度 范围：0-255
                content.setText("已是最新版本！");
                minddle_image.setVisibility(View.GONE);
                cancel_rela_dialog.setVisibility(View.GONE);
                button2.setText("确定");
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                dialog_update = builder.create();
                dialog_update.show();
                dialog_update.setContentView(view);
                button2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        if (null != dialog_update) {
                            dialog_update.cancel();
                        }
                    }
                });
            }
            //有新版本。。
            if (intent.getAction().equals("Constants.Action.ACTION_UPDATE")) {
                if (null != dialog_update && dialog_update.isShowing()) {
                    return;
                }
                View view = LayoutInflater.from(mContext).inflate(
                        R.layout.dialog_update, null);
                TextView title = (TextView) view.findViewById(R.id.title_text);
                WebView content = (WebView) view
                        .findViewById(R.id.content_text);
                TextView button1 = (TextView) view
                        .findViewById(R.id.button1_text);
                TextView button2 = (TextView) view
                        .findViewById(R.id.button2_text);

                title.setText("更新");
                content.setBackgroundColor(Color.WHITE); // 设置背景色
                content.getBackground().setAlpha(100); // 设置填充透明度 范围：0-255
                String data = intent.getStringExtra("message");
                final String downloadPath = intent.getStringExtra("url");
                content.loadDataWithBaseURL(null, data, "text/html", "utf-8",
                        null);
                button1.setText("立即更新");
                button2.setText("下次再说");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialog_update) {
                            dialog_update.dismiss();
                            dialog_update = null;
                        }
                        if (UpdateManager.getInstance().getIsDowning()) {
                            return;
                        }
                        MyApp.app.showDownNotification(
                                UpdateManager.HANDLE_MSG_DOWNING, 0);
                        new Thread() {
                            public void run() {
                                UpdateManager.getInstance().downloadApk(handler,
                                        ConstantValues.Update.SAVE_PATH,
                                        ConstantValues.Update.FILE_NAME, downloadPath);
                            }
                        }.start();
                    }
                });
                final String ignoreVersion=intent.getStringExtra("ignoreVersion");//@@7.12
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialog_update) {
                            dialog_update.cancel();
                            SharedPreferencesManager.getInstance().putData(context,"ignoreVersion",ignoreVersion);//@@7.12
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                dialog_update = builder.create();
                dialog_update.show();
                dialog_update.setContentView(view);
                FrameLayout.LayoutParams layout = (FrameLayout.LayoutParams) view
                        .getLayoutParams();
                layout.width = (int) mContext.getResources().getDimension(
                        R.dimen.update_dialog_width);
                view.setLayoutParams(layout);
                dialog_update.setCanceledOnTouchOutside(false);
                Window window = dialog_update.getWindow();
                window.setWindowAnimations(R.style.dialog_normal);
            }
        }
    };
    Handler handler = new Handler() {
        long last_time;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int value = msg.arg1;
            switch (msg.what) {
                case UpdateManager.HANDLE_MSG_DOWNING:
                    if ((System.currentTimeMillis() - last_time) > 1000) {
                        MyApp.app.showDownNotification(
                                UpdateManager.HANDLE_MSG_DOWNING, value);
                        last_time = System.currentTimeMillis();
                    }
                    break;
                case UpdateManager.HANDLE_MSG_DOWN_SUCCESS:
//                    MyApp.app.hideDownNotification();
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    File file = new File(Environment.getExternalStorageDirectory()
//                            + "/" + ConstantValues.Update.SAVE_PATH + "/"
//                            + ConstantValues.Update.FILE_NAME);
//                    if (!file.exists()) {
//                        return;
//                    }
//                    Uri uri=Uri.fromFile(file);
//                    intent.setDataAndType(uri,
//                            ConstantValues.Update.INSTALL_APK);
//                    mContext.startActivity(intent);


                    MyApp.app.hideDownNotification();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(Environment.getExternalStorageDirectory()
                            + "/" + ConstantValues.Update.SAVE_PATH + "/"
                            + ConstantValues.Update.FILE_NAME);
                    if (!file.exists()) {
                        return;
                    }
                    if(Build.VERSION.SDK_INT>24){
                        Uri uri= FileProvider.getUriForFile(mContext,mContext.getApplicationContext().getPackageName()+".fileprovider",file);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.setDataAndType(uri,
                                ConstantValues.Update.INSTALL_APK);
                        List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            mContext.grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }else{
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(file),
                                ConstantValues.Update.INSTALL_APK);
                    }
                    startActivity(intent);
//                    update();
                    
                    break;
                case UpdateManager.HANDLE_MSG_DOWN_FAULT:
                    break;
            }
        }
    };


//    Intent intent = new Intent(Intent.ACTION_VIEW);
//    public static final int INSTALL_PACKAGES_REQUESTCODE = 1;
//    private AlertDialog mAlertDialog;
//    public static AndroidOInstallPermissionListener sListener;
//
//    private void update() {
//        // 兼容Android 8.0
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            //先获取是否有安装未知来源应用的权限
//            boolean haveInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
//            if (!haveInstallPermission) {//没有权限
//                // 弹窗，并去设置页面授权
//                final AndroidOInstallPermissionListener listener = new AndroidOInstallPermissionListener() {
//                    @Override
//                    public void permissionSuccess() {
//                        installApk(mContext, intent);
//                    }
//
//                    @Override
//                    public void permissionFail() {
//                        T.showShort(mContext, "授权失败，无法安装应用");
//                    }
//                };
//
//                sListener = listener;
//                showDialog();
//
//
//            } else {
//                installApk(mContext, intent);
//            }
//        } else {
//            installApk(mContext, intent);
//        }
//    }
//
//    private void installApk(Context mContext, Intent intent) {
//        intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        MyApp.app.hideDownNotification();
//        File file = new File(Environment.getExternalStorageDirectory()
//                + "/" + ConstantValues.Update.SAVE_PATH + "/"
//                + ConstantValues.Update.FILE_NAME);
//        if (!file.exists()) {
//            return;
//        }
////        intent.setDataAndType(Uri.fromFile(file),
////                ConstantValues.Update.INSTALL_APK);
//
//        if(Build.VERSION.SDK_INT>24){
//            Uri uri= FileProvider.getUriForFile(mContext,"fire.cloud.smart.com.smartcloudfire_zhdgj.fileprovider",file);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.setDataAndType(uri,
//                    ConstantValues.Update.INSTALL_APK);
//        }else{
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.fromFile(file),
//                    ConstantValues.Update.INSTALL_APK);
//        }
//
//        startActivity(intent);
//    }
//
//    public interface AndroidOInstallPermissionListener {
//        void permissionSuccess();
//
//        void permissionFail();
//    }
//
//    private void showDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.app_name);
//        builder.setMessage("为了正常升级 xxx APP，请点击设置按钮，允许安装未知来源应用，本功能只限用于 xxx APP版本升级");
//        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                startInstallPermissionSettingActivity();
//                mAlertDialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (sListener != null) {
//                    sListener.permissionFail();
//                }
//                mAlertDialog.dismiss();
//                finish();
//            }
//        });
//        mAlertDialog = builder.create();
//        mAlertDialog.show();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void startInstallPermissionSettingActivity() {
//        //注意这个是8.0新API
//            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, 1);
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            // 授权成功
//            if (sListener != null) {
//                sListener.permissionSuccess();
//            }
//        } else {
//            // 授权失败
//            if (sListener != null) {
//                sListener.permissionFail();
//            }
//        }
//        finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        sListener = null;
//    }


}
