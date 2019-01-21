package com.smart.cloud.fire.mvp.fragment.MapFragment;

import com.smart.cloud.fire.activity.AddNFC.NFCDeviceType;
import com.smart.cloud.fire.activity.NFCDev.NFCRecordBean;
import com.smart.cloud.fire.global.ElectricInfo;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.electricChangeHistory.HistoryBean;
import com.smart.cloud.fire.mvp.fragment.CollectFragment.AlarmMessageModel;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.WiredDevFragment.WiredSmoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.WiredDevFragment.WiredSmokeHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class HttpError<T> {

    /**
     * error : 获取烟感成功）
     * errorCode : 0
     */

    private String error;
    private int errorCode;
    private  String state;
    private List<Smoke> smoke;
    private List<Camera> camera;
    private ArrayList<ShopType> placeType;
    private List<AlarmMessageModel> Alarm;
    private List<ElectricInfo> Electric;
    private List<WiredSmoke> faultment;//@@6.30
    private List<WiredSmokeHistory> alarm;
    private List<HistoryBean> eleList;//@@8.28

    private ArrayList<NFCDeviceType> deviceType;//@@8.16 NFC设备类型
    private ArrayList<NFCRecordBean> nfcList;//@@8.16 NFC设备

    public List<WiredSmokeHistory> getalarm() {
        return alarm;
    }

    public void setalarm(List<WiredSmokeHistory> alarm) {
        this.alarm = alarm;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<Smoke> getSmoke() {
        return smoke;
    }

    public void setSmoke(List<Smoke> smoke) {
        this.smoke = smoke;
    }

    public List<Camera> getCamera() {
        return camera;
    }

    public void setCamera(List<Camera> camera) {
        this.camera = camera;
    }

    public ArrayList<ShopType> getPlaceType() {
        return placeType;
    }

    public void setPlaceType(ArrayList<ShopType> placeType) {
        this.placeType = placeType;
    }

    public List<AlarmMessageModel> getAlarm() {
        return Alarm;
    }

    public void setAlarm(List<AlarmMessageModel> alarm) {
        Alarm = alarm;
    }

    public List<ElectricInfo> getElectric() {
        return Electric;
    }

    public void setElectric(List<ElectricInfo> electric) {
        Electric = electric;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public List<WiredSmoke> getFaultment() {
        return faultment;
    }

    public void setFaultment(List<WiredSmoke> faultment) {
        this.faultment = faultment;
    }


    public ArrayList<NFCDeviceType> getDeviceType() {
        return deviceType;
    }

    public void setDeviceTypes(ArrayList<NFCDeviceType> deviceType) {
        this.deviceType = deviceType;
    }

    public ArrayList<NFCRecordBean> getNfcList() {
        return nfcList;
    }

    public void setNfcList(ArrayList<NFCRecordBean> nfcList) {
        this.nfcList = nfcList;
    }

    public List<HistoryBean> getEleList() {
        return eleList;
    }

    public void setEleList(List<HistoryBean> eleList) {
        this.eleList = eleList;
    }
}
