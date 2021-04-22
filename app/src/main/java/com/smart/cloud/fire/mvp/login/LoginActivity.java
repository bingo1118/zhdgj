package com.smart.cloud.fire.mvp.login;

/**
 * Created by Administrator on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.p2p.core.update.UpdateManager;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MainThread;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.login.model.LoginModel;
import com.smart.cloud.fire.mvp.login.presenter.LoginPresenter;
import com.smart.cloud.fire.mvp.login.view.LoginView;
import com.smart.cloud.fire.mvp.main.Main6Activity;
import com.smart.cloud.fire.mvp.register.RegisterPhoneActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.functions.Action1;

public class LoginActivity extends MvpActivity<LoginPresenter> implements LoginView{
    private Context mContext;
    @Bind(R.id.login_user)
    EditText login_user;
    @Bind(R.id.login_pwd)
    EditText login_pwd;
    @Bind(R.id.login_rela2)
    Button login_rela2;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.login_new_register)
    Button login_new_register;
    @Bind(R.id.login_forget_pwd)
    TextView login_forget_pwd;
    @Bind(R.id.isread_chaekbox)
    CheckBox isread_chaekbox;
    private  String userId;
    private String pwd;

    private AlertDialog dialog_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mContext=this;
        initView();
        regFilter();//@@7.12
    }

    private void initView() {
        String remenber_account=SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.LOGIN_REMENBER_ACCOUNT);
        String remenber_pwd=SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.LOGIN_REMENBER_PWD);
        if(remenber_account!=null&&remenber_account.length()>0){
            login_user.setText(remenber_account);
            login_pwd.setText(remenber_pwd);
            isread_chaekbox.setChecked(true);
        }

        //登陆按钮事件。。
        RxView.clicks(login_rela2).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        userId = login_user.getText().toString().trim();
                        pwd = login_pwd.getText().toString().trim();
                        if(userId.length()==0){
                            T.show(mContext,"账号不能为空",Toast.LENGTH_SHORT);
                            return;
                        }
                        if(pwd.length()==0){
                            T.show(mContext,"密码不能为空",Toast.LENGTH_SHORT);
                            return;
                        }
//                        mvpPresenter.loginYooSee(userId,pwd,mContext,1);
                        mvpPresenter.loginYooSee("13622215085","123456",mContext,1);
                        showLoading();
                        String userCID = SharedPreferencesManager.getInstance().getData(mContext,SharedPreferencesManager.SP_FILE_GWELL,"CID");//@@6.26获取cid
                        mvpPresenter.loginServer2(userId,pwd,userCID);//@@7.12 取消几位本地服务器登陆
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm.isActive()){
                            imm.hideSoftInputFromWindow(login_rela2.getWindowToken(),0);//隐藏输入软键盘@@4.28
                        }
                    }
                });
        RxView.clicks(login_new_register).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(mContext, RegisterPhoneActivity.class);
                        intent.putExtra("isforget",0);
                        startActivity(intent);
//                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    }
                });
        RxView.clicks(login_forget_pwd).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        Uri uri = Uri.parse(ConstantValues.FORGET_PASSWORD_URL);
//                        Intent open_web = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(open_web);
                        Intent intent = new Intent(mContext, RegisterPhoneActivity.class);
                        intent.putExtra("isforget",1);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void getDataSuccess(LoginModel model) {
        Intent intent = new Intent(mContext, Main6Activity.class);
        startActivity(intent);
        if(isread_chaekbox.isChecked()){
            SharedPreferencesManager.getInstance().putData(mContext,
                    SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.LOGIN_REMENBER_ACCOUNT, userId);
            SharedPreferencesManager.getInstance().putData(mContext,
                    SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.LOGIN_REMENBER_PWD, pwd);
        }else{
            SharedPreferencesManager.getInstance().putData(mContext,
                    SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.LOGIN_REMENBER_ACCOUNT, "");
            SharedPreferencesManager.getInstance().putData(mContext,
                    SharedPreferencesManager.SP_FILE_GWELL,
                    SharedPreferencesManager.LOGIN_REMENBER_PWD, "");
        }
        finish();
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
    public void autoLogin(String userId, String pwd) {
    }

    @Override
    public void autoLoginFail() {
    }

    @Override
    public void bindAlias() {
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this);
    }

    //@@7.12
    class MyTast extends AsyncTask<Context, Integer, Integer> {

        @Override
        protected Integer doInBackground(Context... params) {
            // TODO Auto-generated method stub\
            Context context = params[0];
            long ll = -2;
            int result = new MainThread(context).checkUpdate(ll);
            return result;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
        }
    }
    private void regFilter() {//@@7.12
        IntentFilter filter = new IntentFilter();
        filter.addAction("Constants.Action.ACTION_UPDATE");
        mContext.registerReceiver(mReceiver, filter);
        new MyTast().execute(mContext);//@@5.31
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {//@@7.12

        @Override
        public void onReceive(final Context context, Intent intent) {
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
    Handler handler = new Handler() {//@@7.12
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
//                    intent.setDataAndType(Uri.fromFile(file),
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
                    break;
                case UpdateManager.HANDLE_MSG_DOWN_FAULT:
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}

