package com.smart.cloud.fire.mvp.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jakewharton.rxbinding.view.RxView;
import com.mob.MobSDK;
import com.smart.cloud.fire.base.ui.MvpActivity;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.mvp.login.LoginActivity;
import com.smart.cloud.fire.mvp.login.SplashActivity;
import com.smart.cloud.fire.mvp.register.presenter.RegisterPresenter;
import com.smart.cloud.fire.mvp.register.view.RegisterView;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/9/19.
 */
public class RegisterPhoneActivity extends MvpActivity<RegisterPresenter> implements RegisterView {
    private Context mContext;
    @Bind(R.id.register_user)
    EditText register_user;
    @Bind(R.id.register_pwd)
    EditText register_pwd;
    @Bind(R.id.register_comfire_pwd)
    EditText register_comfire_pwd;
    @Bind(R.id.register_code)
    EditText register_code;
    @Bind(R.id.register_get_code)
    Button register_get_code;
    @Bind(R.id.register_btn_phone)
    Button register_btn_phone;
    @Bind(R.id.register_old_user_tv)
    TextView register_old_user_tv;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    private String phoneNO;
    private int isforget=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        mContext=this;
        doAction();
        MobSDK.init(this);

        isforget=getIntent().getIntExtra("isforget",0);
        if(isforget==1){
            register_pwd.setHint("新密码");
            register_comfire_pwd.setHint("再次输入新密码");
            register_btn_phone.setText("重设密码");
        }
    }

    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                    register_get_code.setClickable(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i=60;i>0;i--){
                                    final int finalI = i;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            register_get_code.setText(finalI +"秒后重新获取");
                                        }
                                    });
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        register_get_code.setText("重新获取");
                                        register_get_code.setClickable(true);
                                    }
                                });
                            }
                        }).start();
                } else{
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                T.showShort(mContext,des);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // 用完回调要注销，否则会造成泄露
                SMSSDK.unregisterEventHandler(this);
            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
//        SMSSDK.getVoiceVerifyCode(country,phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
//                        Intent intent = new Intent(MainActivity.this,Main2Activity.class);
//                        startActivity(intent);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(mContext,"验证成功",Toast.LENGTH_SHORT).show();
                                addUser(register_user.getText().toString().trim(),register_pwd.getText().toString().trim(),mContext,isforget);
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,"语音验证发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,"验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        Log.i("test","test");
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    Log.i("ssss",throwable.toString());
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,des,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    private void doAction() {
        //获取验证码
        RxView.clicks(register_get_code).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        phoneNO = register_user.getText().toString().trim();
                        if(TextUtils.isEmpty(phoneNO)){
                            T.showShort(mContext,"手机号不能为空");
                        }else{
                            sendCode("86",phoneNO);
                        }
//                        mvpPresenter.getMesageCode(phoneNO);
                    }
                });
        RxView.clicks(register_btn_phone).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm.isActive()){
                            imm.hideSoftInputFromWindow(register_btn_phone.getWindowToken(),0);//隐藏输入软键盘@@4.28
                        }
                        String phoneNO = register_user.getText().toString().trim();
                        String pwd = register_pwd.getText().toString().trim();
                        String rePwd = register_comfire_pwd.getText().toString().trim();
                        String code = register_code.getText().toString().trim();
                        if(phoneNO.length()==0){
                            T.showShort(mContext,"请输入的手机号");
                            return;
                        };
                        if(pwd.length()==0){
                            T.showShort(mContext,"请输入密码");
                            return;
                        };
                        if(rePwd.length()==0){
                            T.showShort(mContext,"请再次输入密码");
                            return;
                        };
                        if(!Utils.isNumOrEng(pwd)){
                            T.showShort(mContext,"密码只能由字母和数字组成");
                            return;
                        }
                        if(!rePwd.equals(pwd)){
                            T.showShort(mContext,"两次密码不一致");
                            return;
                        };
                        submitCode("86",phoneNO,code);
//                        mvpPresenter.register(phoneNO,pwd,rePwd,code,mContext);
//                        addUser(phoneNO,pwd,mContext);
                    }
                });
        RxView.clicks(register_old_user_tv).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //跳转到登录界面
                        Intent intent1 = new Intent(mContext,LoginActivity.class);
                        startActivity(intent1);
                        finish();
                        //一个参数是第一个activity进入时的动画，另外一个参数则是第二个activity退出时的动画。。
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    }
                });
    }

    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter(this);
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
    public void register() {
        if(isforget==1){
            T.showLong(mContext,"设置成功,请重新登录");
            Intent login = new Intent(mContext, LoginActivity.class);
            startActivity(login);
            finish();
        }else{
            T.showShort(mContext,"注册成功,正在登陆");
            Intent login = new Intent(mContext, SplashActivity.class);
            startActivity(login);
            finish();
        }
        SMSSDK.unregisterAllEventHandler();//@@注销短信验证处理
    }

    @Override
    public void getMesageSuccess() {
        T.showShort(mContext,"获取验证码成功");
    }

    private void addUser(final String phoneNo, final String pwd, final Context mContext,int isforget) {
        final RequestQueue mQueue = Volley.newRequestQueue(mContext);
        String url= ConstantValues.SERVER_IP_NEW+"AddUserAction?userId="+phoneNo+"&pwd="+pwd+"&isforget="+isforget;
        final StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.getInt("errorCode")==0){
//                                SharedPreferencesManager.getInstance().putData(mContext,
//                                        SharedPreferencesManager.SP_FILE_GWELL,
//                                        SharedPreferencesManager.KEY_RECENTPASS,
//                                        pwd);
//                                SharedPreferencesManager.getInstance().putData(mContext,
//                                        SharedPreferencesManager.SP_FILE_GWELL,
//                                        SharedPreferencesManager.KEY_RECENTNAME,
//                                        phoneNo);
                                register();//注册成功后跳转到欢迎页
                            }else if(jsonObject.getInt("errorCode")==1){
                                T.showShort(mContext,"账号已存在");
                            }else{
                                T.showShort(mContext,"注册错误");
                            }
                        } catch (JSONException e) {
                            T.showShort(mContext,"注册错误");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                T.showShort(mContext,"注册错误");
                Log.e("error","error");
            }
        });
        mQueue.add(stringRequest);
    }
}
