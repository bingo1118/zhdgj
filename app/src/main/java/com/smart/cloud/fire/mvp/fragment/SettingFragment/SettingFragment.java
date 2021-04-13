package com.smart.cloud.fire.mvp.fragment.SettingFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.cloud.fire.activity.AccountManage.AccountManageActivity;
import com.smart.cloud.fire.activity.AddNFC.AddNFCMacActivity;
import com.smart.cloud.fire.activity.Setting.MyZoomActivity;
import com.smart.cloud.fire.activity.UploadNFCInfo.UploadNFCInfoActivity;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.camera.AddCameraFirstActivity;
import com.smart.cloud.fire.ui.AboutActivity;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;

/**
 * Created by Administrator on 2016/9/21.
 */
public class SettingFragment extends MvpFragment<SettingFragmentPresenter> implements SettingFragmentView {
    @Bind(R.id.setting_user_id)
    TextView settingUserId;
    @Bind(R.id.setting_help_rela)
    RelativeLayout settingHelpRela;
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.setting_camera_relative)
    RelativeLayout settingCameraRelative;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container,
                false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        init();
    }

    private void init() {
        String userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTPASS_NUMBER);
        String username = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        int peroid=SharedPreferencesManager.getInstance().getIntData(mContext,"NFC_period");//@@10.24
        settingUserId.setText(username);
        String state = MyApp.app.getPushState();

        int privilege = MyApp.app.getPrivilege();
        if (privilege == 3) {
//            settingHelpRela.setVisibility(View.VISIBLE);//显示添加摄像机。。
//            settingCameraRelative.setVisibility(View.VISIBLE);//显示绑定摄像机。。
        }
    }

    @OnClick({R.id.app_update, R.id.setting_help_about, R.id.setting_help_rela, R.id.setting_help_exit,
            R.id.setting_camera_relative,R.id.setting_account_manage})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.setting_account_manage:
                intent = new Intent(mContext, AccountManageActivity.class);
                startActivity(intent);
                break;
            case R.id.app_update:
                mvpPresenter.checkUpdate(mContext);
//                getActivity().finish();//@@7.13
                break;
            case R.id.setting_help_about:
                intent = new Intent(mContext, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_help_rela:
                intent = new Intent(mContext, AddCameraFirstActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_help_exit:
                Intent in = new Intent();
                in.setAction("APP_EXIT");
                in.setPackage("fire.cloud.smart.com.smartcloudfire_zhdgj");//@@7.13只传当前应用
                mContext.sendBroadcast(in);
                if(getActivity() instanceof MyZoomActivity){
                    getActivity().finish();//@@7.17
                }
                break;
            case R.id.setting_camera_relative:
                mvpPresenter.bindDialog(mContext);
                break;
            default:
                break;
        }
    }

    @Override
    protected SettingFragmentPresenter createPresenter() {
        SettingFragmentPresenter mMapFragmentPresenter = new SettingFragmentPresenter(SettingFragment.this);
        return mMapFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "settingFragment";
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    public void bindResult(String msg) {
        T.showShort(mContext, msg);
    }
}
