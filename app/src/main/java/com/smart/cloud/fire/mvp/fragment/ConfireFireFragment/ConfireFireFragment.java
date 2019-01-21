package com.smart.cloud.fire.mvp.fragment.ConfireFireFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.jakewharton.rxbinding.view.RxView;
import com.obsessive.zbar.CaptureActivity;
import com.smart.cloud.fire.GetLocationActivity;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Camera;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.T;
import com.smart.cloud.fire.view.XCDropDownListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/9/21.
 */
public class ConfireFireFragment extends MvpFragment<ConfireFireFragmentPresenter> implements ConfireFireFragmentView {
    @Bind(R.id.add_repeater_mac)
    EditText addRepeaterMac;//集中器。。
    @Bind(R.id.add_fire_mac)
    EditText addFireMac;//探测器。。
    @Bind(R.id.add_fire_name)
    EditText addFireName;//设备名称。。
    @Bind(R.id.add_fire_lat)
    EditText addFireLat;//经度。。
    @Bind(R.id.add_fire_lon)
    EditText addFireLon;//纬度。。
    @Bind(R.id.add_fire_address)
    EditText addFireAddress;//设备地址。。
    @Bind(R.id.add_fire_man)
    EditText addFireMan;//负责人姓名。。
    @Bind(R.id.add_fire_man_phone)
    EditText addFireManPhone;//负责人电话。。
    @Bind(R.id.add_fire_man_two)
    EditText addFireManTwo;//负责人2.。
    @Bind(R.id.add_fire_man_phone_two)
    EditText addFireManPhoneTwo;//负责人电话2.。
    @Bind(R.id.scan_repeater_ma)
    ImageView scanRepeaterMa;
    @Bind(R.id.scan_er_wei_ma)
    ImageView scanErWeiMa;
    @Bind(R.id.location_image)
    ImageView locationImage;
    @Bind(R.id.add_fire_zjq)
    XCDropDownListView addFireZjq;//选择区域。。
    @Bind(R.id.add_fire_type)
    XCDropDownListView addFireType;//选择类型。。
    @Bind(R.id.add_fire_dev_btn)
    RelativeLayout addFireDevBtn;//添加设备按钮。。
    @Bind(R.id.mProgressBar)
    ProgressBar mProgressBar;//加载进度。。
    @Bind(R.id.add_camera_name)
    EditText addCameraName;
    @Bind(R.id.add_camera_relative)
    RelativeLayout addCameraRelative;
    private Context mContext;
    private int scanType = 0;//0表示扫描中继器，1表示扫描烟感
    private int privilege;
    private String userID;
    private ShopType mShopType;
    private Area mArea;
    private String areaId = "";
    private String shopTypeId = "";
    private String camera = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_fire, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        init();
    }

    private void init() {
        addFireMac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mvpPresenter.getOneSmoke(userID, privilege + "", addFireMac.getText().toString());//@@5.5如果添加过该烟感则显示出原来的信息
                }
            }
        });//@@10.18
        addCameraRelative.setVisibility(View.VISIBLE);
        addFireZjq.setEditTextHint("区域");
        addFireType.setEditTextHint("类型");
        RxView.clicks(addFireDevBtn).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                addFire();
            }
        });
    }

    /**
     * 添加设备，提交设备信息。。
     */
    private void addFire() {
        if (mShopType != null) {
            shopTypeId = mShopType.getPlaceTypeId();
        }
        if (mArea != null) {
            areaId = mArea.getAreaId();
        }
        String longitude = addFireLon.getText().toString().trim();
        String latitude = addFireLat.getText().toString().trim();
        String smokeName = addFireName.getText().toString().trim();
        String smokeMac = addFireMac.getText().toString().trim();
        String address = addFireAddress.getText().toString().trim();
        String placeAddress = "";
        String principal1 = addFireMan.getText().toString().trim();
        String principal2 = addFireManTwo.getText().toString().trim();
        String principal1Phone = addFireManPhone.getText().toString().trim();
        String principal2Phone = addFireManPhoneTwo.getText().toString().trim();
        String repeater = addRepeaterMac.getText().toString().trim();
        camera = addCameraName.getText().toString().trim();
        mvpPresenter.addSmoke(userID, privilege + "", smokeName, smokeMac, address, longitude,
                latitude, placeAddress, shopTypeId, principal1, principal1Phone, principal2,
                principal2Phone, areaId, repeater, camera);
    }

    @Override
    protected ConfireFireFragmentPresenter createPresenter() {
        ConfireFireFragmentPresenter mConfireFireFragmentPresenter = new ConfireFireFragmentPresenter(ConfireFireFragment.this);
        return mConfireFireFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "ConfireFireFragment";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (addFireZjq.ifShow()) {
            addFireZjq.closePopWindow();
        }
        if (addFireType.ifShow()) {
            addFireType.closePopWindow();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        mvpPresenter.stopLocation();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        mvpPresenter.initLocation();
        super.onStart();
    }

    @OnClick({R.id.scan_repeater_ma, R.id.scan_er_wei_ma, R.id.location_image, R.id.add_fire_zjq, R.id.add_fire_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_repeater_ma:
                scanType = 0;
                Intent scanRepeater = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(scanRepeater, 0);
                break;
            case R.id.scan_er_wei_ma:
                scanType = 1;
                Intent openCameraIntent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
            case R.id.location_image:
                mvpPresenter.startLocation();
                Intent intent=new Intent(mContext, GetLocationActivity.class);
                startActivityForResult(intent,1);//@@6.20
                break;
            case R.id.add_fire_zjq:
                if (addFireZjq.ifShow()) {
                    addFireZjq.closePopWindow();
                } else {
                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 2);
                    addFireZjq.setClickable(false);
                    addFireZjq.showLoading();
                }
                break;
            case R.id.add_fire_type:
                if (addFireType.ifShow()) {
                    addFireType.closePopWindow();
                } else {
                    mvpPresenter.getPlaceTypeId(userID, privilege + "", 1);
                    addFireType.setClickable(false);
                    addFireType.showLoading();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getLocationData(BDLocation location) {
        addFireLon.setText(location.getLongitude() + "");
        addFireAddress.setText(location.getAddrStr());
        addFireLat.setText(location.getLatitude() + "");
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
    public void getDataFail(String msg) {
        T.showShort(mContext, msg);
    }

    @Override
    public void getDataSuccess(Smoke smoke) {
        addFireLon.setText(smoke.getLongitude() + "");
        addFireLat.setText(smoke.getLatitude() + "");
        addFireAddress.setText(smoke.getAddress());
        addFireName.setText(smoke.getName());
        addFireMan.setText(smoke.getPrincipal1());
        addFireManPhone.setText(smoke.getPrincipal1Phone());
        addFireManTwo.setText(smoke.getPrincipal2());
        addFireManPhoneTwo.setText(smoke.getPrincipal2Phone());
        addFireZjq.setEditTextData(smoke.getAreaName());
        addFireType.setEditTextData(smoke.getPlaceType());//@@10.18
        areaId=smoke.getAreaId()+"";
        shopTypeId=smoke.getPlaceTypeId();//@@10.18
        Camera mCamera = smoke.getCamera();
        if (mCamera != null) {
            addCameraName.setText(mCamera.getCameraId());
        }
        addRepeaterMac.setText(smoke.getRepeater().trim());
    }

    @Override
    public void getShopType(ArrayList<Object> shopTypes) {
        addFireType.setItemsData(shopTypes,mvpPresenter);
        addFireType.showPopWindow();
        addFireType.setClickable(true);
        addFireType.closeLoading();
    }

    @Override
    public void getShopTypeFail(String msg) {
        T.showShort(mContext, msg);
        addFireType.setClickable(true);
        addFireType.closeLoading();
    }

    @Override
    public void getAreaType(ArrayList<Object> shopTypes) {
        addFireZjq.setItemsData(shopTypes,mvpPresenter);
        addFireZjq.showPopWindow();
        addFireZjq.setClickable(true);
        addFireZjq.closeLoading();
    }

    @Override
    public void getAreaTypeFail(String msg) {
        T.showShort(mContext, msg);
        addFireZjq.setClickable(true);
        addFireZjq.closeLoading();
    }

    @Override
    public void addSmokeResult(String msg, int errorCode) {
        T.showShort(mContext, msg);
        if (errorCode == 0) {
            mShopType = null;
            mArea = null;
            clearText();
            areaId = "";
            shopTypeId = "";
            camera = "";
            addFireMac.setText("");
            addFireZjq.addFinish();
            addFireType.addFinish();
        }
    }

    @Override
    public void getChoiceArea(Area area) {
        mArea = area;
    }

    @Override
    public void getChoiceShop(ShopType shopType) {
        mShopType = shopType;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    if (scanType == 0) {
                        addRepeaterMac.setText(scanResult);
                    } else {
                        if(scanResult.contains("-")){
                            scanResult=scanResult.substring(scanResult.lastIndexOf("=")+1);
                        }//@@12.26三江nb-iot烟感
                        addFireMac.setText(scanResult);
                        clearText();
                        mvpPresenter.getOneSmoke(userID, privilege + "", scanResult);//@@5.5如果添加过该烟感则显示出原来的信息
                    }
                }
                break;
            case 1://@@6.20
                if (resultCode == getActivity().RESULT_OK) {
                    Bundle bundle=data.getBundleExtra("data");
                    addFireLat.setText(String.format("%.8f",bundle.getDouble("lat")));
                    addFireLon.setText(String.format("%.8f",bundle.getDouble("lon")));
                    addFireAddress.setText(bundle.getString("address"));
                }
                break;
        }

    }

    /**
     * 清空其他编辑框内容。。
     */
    private void clearText() {
        addFireLon.setText("");
        addFireLat.setText("");
        addFireAddress.setText("");
        addFireName.setText("");
        addFireMan.setText("");
        addFireManPhone.setText("");
        addFireManTwo.setText("");
        addFireManPhoneTwo.setText("");
        addFireZjq.setEditTextData("");
        addFireType.setEditTextData("");
        addCameraName.setText("");
    }
}
