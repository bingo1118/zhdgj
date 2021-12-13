package com.smart.cloud.fire.activity.Info;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smart.cloud.fire.base.ui.BaseActivity;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.ui.CallManagerDialogActivity;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.utils.Utils;
import com.smart.cloud.fire.view.NormalDialog;

import butterknife.Bind;
import fire.cloud.smart.com.smartcloudfire.R;

public class DeviceInfoActivity extends BaseActivity {

    @Bind(R.id.sn_tv)
    TextView sn_tv;
    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.address_tv)
    TextView address_tv;
    @Bind(R.id.area_tv)
    TextView area_tv;
    @Bind(R.id.team_tv)
    TextView team_tv;
    @Bind(R.id.man_tv)
    TextView man_tv;
    @Bind(R.id.phone_tv)
    TextView phone_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        final Electric info=(Electric) getIntent().getSerializableExtra("info");
        sn_tv.setText(info.getMac());
        name_tv.setText(info.getName());
        address_tv.setText(info.getAddress());
        area_tv.setText(info.getAreaName());
        team_tv.setText(info.getPlaceType());
        man_tv.setText(info.getPrincipal1());
        phone_tv.setText(info.getPrincipal1Phone());
        phone_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum=info.getPrincipal1Phone();
                if(Utils.isPhoneNumber(phoneNum)){
                    NormalDialog mNormalDialog = new NormalDialog(mActivity, "是否需要拨打电话：", phoneNum,
                            "是", "否");
                    mNormalDialog.showNormalDialog();//拨号过程封装在NormalDialog中。。
                }else{
                    T.showShort(mActivity, "电话号码不合法");
                }
            }
        });
    }
}
