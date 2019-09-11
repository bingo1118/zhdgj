package com.smart.cloud.fire.mvp.fragment.ConfireFireFragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.service.LocationService;
import com.smart.cloud.fire.utils.Utils;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/9/21.
 */
public class ConfireFireFragmentPresenter extends BasePresenter<ConfireFireFragmentView> {
    private LocationService locationService;
    public ConfireFireFragmentPresenter(ConfireFireFragmentView view) {
        attachView(view);
    }

    public void initLocation(){
        locationService = MyApp.app.locationService;
        locationService.registerListener(mListener);
    }

    public void startLocation(){
        locationService.start();
        mvpView.showLoading();
    }

    public void stopLocation(){
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                int result = location.getLocType();
                switch (result){
                    case 61:
                        mvpView.getLocationData(location);
                        break;
                    case 161:
                        mvpView.getLocationData(location);
                        break;
                }
                locationService.stop();
                mvpView.hideLoading();
            }
        }
    };

    public void getOneSmoke(String userId,String privilege,String smokeMac){
        mvpView.showLoading();
        String macStr = (String) smokeMac.subSequence(0, 1);
        switch (macStr){
            case "T":
                smokeMac = smokeMac.replace("T","");
                break;
            case "R":
                smokeMac = smokeMac.replace("R","");
                smokeMac = smokeMac.replace("N","");
                break;
            case "Q":
                smokeMac = smokeMac.replace("Q","");
                smokeMac = smokeMac.replace("S","");
                smokeMac = smokeMac.replace("L","");
                smokeMac = smokeMac.replace("N","");
                smokeMac = smokeMac.replace("G","");
                break;
            case "G":
                smokeMac = smokeMac.replace("G","");
                break;
            case "S":
                smokeMac = smokeMac.replace("S","");
                break;
            case "J":
                smokeMac = smokeMac.replace("J","");
                break;
            case "W"://@@5.5水压
                smokeMac = smokeMac.replace("W","");
                smokeMac = smokeMac.replace("A","");
                smokeMac = smokeMac.replace("B","");
                smokeMac = smokeMac.replace("C","");
                smokeMac = smokeMac.replace("L","");
                break;
            case "N"://@@NB烟感
                smokeMac = smokeMac.replace("N","");
                smokeMac = smokeMac.replace("R","");
                smokeMac = smokeMac.replace("O","");
                smokeMac = smokeMac.replace("Z","");
                smokeMac = smokeMac.replace("H","");
                smokeMac = smokeMac.replace("I","");
                smokeMac = smokeMac.replace("Q","");
                break;
            case "L"://@@5.13红外
                smokeMac = smokeMac.replace("L","");
                break;
            case "M"://@@5.13门磁
                smokeMac = smokeMac.replace("M","");
                break;
            case "H"://@@5.13空气探测器
                smokeMac = smokeMac.replace("H","");
                break;
            case "Y"://@@8.1水禁
                smokeMac = smokeMac.replace("Y","");
                break;
            case "P"://@@10.31喷淋
                smokeMac = smokeMac.replace("P","");
                break;
            case "K"://@@无线输入输出模块
                smokeMac = smokeMac.replace("K","");
                break;
            case "Z"://@@无线输入输出模块
                smokeMac = smokeMac.replace("Z","");
                break;
//            case "C"://@@创安
//                smokeMac = smokeMac.replace("C","");
//                break;
        }
        if(smokeMac!=null&&smokeMac.length()>0){
            Observable mObservable = apiStores1.getOneSmoke(userId,smokeMac,privilege);
            addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<ConfireFireModel>() {
                @Override
                public void onSuccess(ConfireFireModel model) {
                    int errorResult = model.getErrorCode();
                    if(errorResult==0){
                        mvpView.getDataSuccess(model.getSmoke());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    mvpView.getDataFail("网络错误");
                }

                @Override
                public void onCompleted() {
                    mvpView.hideLoading();
                }
            }));
        }else {//@@10.18
            mvpView.hideLoading();
        }
    }

    //type:1表示查询商铺类型，2表示查询区域类型
    public void getPlaceTypeId(String userId, String privilege, final int type){
        Observable mObservable = null;
        if(type==1){
            mObservable= apiStores1.getPlaceTypeId(userId,privilege,"").map(new Func1<HttpError,ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(HttpError o) {
                    return o.getPlaceType();
                }
            });
        }else{
            mObservable= apiStores1.getAreaId(userId,privilege,"").map(new Func1<HttpAreaResult,ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(HttpAreaResult o) {
                    return o.getSmoke();
                }
            });
        }
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<ArrayList<Object>>() {
            @Override
            public void onSuccess(ArrayList<Object> model) {
                if(type==1){
                    if(model!=null&&model.size()>0){
                        mvpView.getShopType(model);
                    }else{
                        mvpView.getShopTypeFail("无数据");
                    }
                }else{
                    if(model!=null&&model.size()>0){
                        mvpView.getAreaType(model);
                    }else{
                        mvpView.getAreaTypeFail("无数据");
                    }
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                mvpView.getDataFail("网络错误");
            }
            @Override
            public void onCompleted() {
            }
        }));
    }

    public void addSmoke(String userID,String privilege,String smokeName,String smokeMac,String address,String longitude,
                         String latitude,String placeAddress,String placeTypeId,String principal1,String principal1Phone,String principal2,
                         String principal2Phone,String areaId,String repeater,String camera){
        int electrState=0;//@@8.26电气开关
        if(longitude.length()==0||latitude.length()==0){
            mvpView.addSmokeResult("请获取经纬度",1);
            return;
        }
        if(smokeName.length()==0||smokeName.length()==0){
            mvpView.addSmokeResult("请填写名称",1);
            return;
        }
        if(smokeMac.length()==0){
            mvpView.addSmokeResult("请填写探测器MAC",1);
            return;
        }
        if(areaId==null||areaId.length()==0){
            mvpView.addSmokeResult("请填选择区域",1);
            return;
        }
        //@@
        if(placeTypeId==null||placeTypeId.length()==0){
            mvpView.addSmokeResult("请填选择类型",1);
            return;
        }
        String deviceType="1";//烟感。。

        String macStr = (String) smokeMac.subSequence(0, 1);
        if(smokeMac.length()==15){
//            deviceType="14";//GPS
            deviceType="41";//海曼NB
        }else if (smokeMac.length()==6){
            deviceType="70";//恒星水压
        }else if (smokeMac.length()==7){
            if (macStr.equals("W")){//@@9.29 区分NB
                deviceType="69";//@@恒星水位
            }
            smokeMac = smokeMac.substring(1, smokeMac.length());
        }else if (smokeMac.length()==12){
            deviceType="51";//创安
        }else if(smokeMac.length()==16||smokeMac.length()==18){
            switch(macStr){
                case "A":
                    smokeMac = smokeMac.substring(1, smokeMac.length());//三江无线传输设备
                    deviceType="119";
                    break;
                case "W":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("W")){
                        deviceType="19";//@@水位2018.01.02
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("A")){
                        deviceType="124";//@@拓普水位2018.01.30
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("B")){
                        deviceType="125";//@@拓普水压2018.01.30
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else{
                        deviceType="10";//@@水压
                    }
                    smokeMac = smokeMac.replace("W","");//水压设备
                    break;
                case "Z":
                    smokeMac = smokeMac.substring(1, smokeMac.length());//嘉德烟感
                    deviceType="55";
                    break;
                default:
                    deviceType="21";//loraOne烟感
                    break;
            }
        }else if(smokeMac.equals(repeater)){
            deviceType="126";//海湾主机
        }else if(smokeMac.contains("-")){
            deviceType="31";//三江nb烟感
        }else{
            switch (macStr){
                case "R":
                    if ((smokeMac.charAt(smokeMac.length()-1)+"").equals("R")){//@@9.29 区分NB
                        deviceType="16";//@@NB燃气
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        deviceType="22";
                    }else{
                        deviceType="2";//@@燃气
                    }
                    smokeMac = smokeMac.replace("R","");//燃气
                    smokeMac = smokeMac.replace("N","");//燃气
                    break;
                case "Q":
                    deviceType="5";
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Q")){
                        electrState=1;
                    }//@@8.26
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("S")){
                        electrState=3;
                    }//@@2018.01.18 三相设备
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("K")){
                        electrState=4;
                    }//@@2018.01.18 三相设备
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("L")){
                        electrState=1;
                        deviceType="52";
                    }//@@2018.05.15 Lara电气设备
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        electrState=1;
                        deviceType="53";
                    }//@@2018.05.15 Lara电气设备
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("G")){
                        electrState=0;
                        deviceType="59";
                    }//@@NB北秦电气设备
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Y")){
                        electrState=1;
                        deviceType="77";
                    }//@@NB北秦电气设备
                    smokeMac = smokeMac.replace("Q","");//电气火灾
                    smokeMac = smokeMac.replace("S","");//电气火灾
                    smokeMac = smokeMac.replace("L","");//电气火灾
                    smokeMac = smokeMac.replace("N","");//电气火灾
                    smokeMac = smokeMac.replace("G","");//电气火灾
                    smokeMac = smokeMac.replace("Y","");//电气火灾
                    smokeMac = smokeMac.replace("K","");//电气火灾
                    break;
                case "T":
                    smokeMac = smokeMac.replace("T","");//温湿度设备
                    deviceType="25";
                    break;
                case "A":
                    smokeMac = smokeMac.substring(1, smokeMac.length());
                    deviceType="119";
                    break;
                case "G":
                    smokeMac = smokeMac.replace("G","");//声光报警器 6
                    deviceType="7";
                    break;
                case "K":
                    smokeMac = smokeMac.replace("K","");//@@无线输出输入模块2018.01.24
                    deviceType="20";
                    break;
                case "S":
                    smokeMac = smokeMac.replace("S","");//手动报警，显示 7
                    deviceType="8";
                    break;
                case "J":
                    smokeMac = smokeMac.replace("J","");//三江设备
                    deviceType="9";
                    break;
                case "W":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("W")){
                        deviceType="19";//@@水位2018.01.02
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("C")){
                        deviceType="42";//@@NB水压
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("L")){
                        deviceType="43";//@@Lara水压
                        smokeMac =smokeMac.substring(0,smokeMac.length()-1);
                    }else{
                        deviceType="10";//@@水压
                    }
                    smokeMac = smokeMac.replace("W","");//水压设备
                    smokeMac = smokeMac.replace("L","");//水压设备
                    break;
                case "L":
                    smokeMac = smokeMac.replace("L","");//红外设备
                    deviceType="11";
                    break;
                case "M":
                    smokeMac = smokeMac.replace("M","");//门磁设备
                    deviceType="12";
                    break;
                case "N":
                    if((smokeMac.charAt(smokeMac.length()-1)+"").equals("N")){
                        deviceType="56";//@@NB-iot烟感
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("O")){
                        deviceType="57";//@@onet烟感
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("R")){
                        deviceType="45";//@@海曼气感
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Z")){
                        deviceType="58";//@@嘉德移动烟感
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("H")){
                        deviceType="35";//@@电弧 电信
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("I")){
                        deviceType="36";//@@嘉德移动烟感
                    }else if((smokeMac.charAt(smokeMac.length()-1)+"").equals("Q")){
                        deviceType="75";//
                    }else{
                        deviceType="41";
                    }
                    smokeMac = smokeMac.replace("N","");//NB烟感设备
                    smokeMac = smokeMac.replace("O","");
                    smokeMac = smokeMac.replace("R","");
                    smokeMac = smokeMac.replace("Z","");
                    smokeMac = smokeMac.replace("H","");
                    smokeMac = smokeMac.replace("I","");
                    smokeMac = smokeMac.replace("Q","");
                    break;
                case "H":
                    smokeMac = smokeMac.replace("H","");//空气探测器
                    deviceType="13";
                    break;
                case "Y":
                    smokeMac = smokeMac.replace("Y","");//水禁
                    deviceType="15";
                    break;
                case "P":
                    smokeMac = smokeMac.replace("P","");//10.31喷淋
                    deviceType="18";
                    electrState=2;//@@11.01 1开2关
                    break;
//                case "C"://@@创安
//                    smokeMac = smokeMac.substring(1, smokeMac.length());
//                    deviceType="51";
//                    break;
            }
            if(smokeMac.length()<8){
                mvpView.addSmokeResult("设备MAC号长度不正确",1);
                return;
            }//@@11.06限制MAC长度
            if(!Utils.isNumOrEng(smokeMac)){
                mvpView.addSmokeResult("设备MAC仅能含有数字或字母",1);
                return;
            }
        }



        mvpView.showLoading();
        Observable mObservable =null;

        mObservable = apiStores1.addSmoke(userID,smokeName,privilege,smokeMac,address,
                longitude,latitude,placeAddress,placeTypeId,principal1,principal1Phone,principal2,
                principal2Phone,areaId,repeater,camera,deviceType,electrState+"");


        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<ConfireFireModel>() {
            @Override
            public void onSuccess(ConfireFireModel model) {
                int result = model.getErrorCode();
                String error=model.getError();//@@6.15
                if(result==0){
                    mvpView.addSmokeResult("添加成功",0);
                }else{
//                    mvpView.addSmokeResult("添加失败",1);
                    mvpView.addSmokeResult(error,1);//@@6.15
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                mvpView.addSmokeResult("添加失败",1);
            }

            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }
    @Override
    public void getArea(Area area) {
        mvpView.getChoiceArea(area);
    }

    @Override
    public void getShop(ShopType shopType) {
        mvpView.getChoiceShop(shopType);
    }

}
