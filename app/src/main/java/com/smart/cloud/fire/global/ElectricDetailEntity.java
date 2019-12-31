package com.smart.cloud.fire.global;

public class ElectricDetailEntity {
    private String mac="";
    private String voltage="";//电压
    private String current="";//电流
    private String leakage="";//漏电流

    private String remainingBattery="";//剩余电量
    private String totalBattery="";//总用电量
    private String overdraft="";//透支电量

    private String versionCode="";//版本号
    private String ccid="";
    private String signal="";//信号
    private String power="";//功率
    private String powerFactor="";//功率因数

    private String frequency="";//频率
    private String workMode="";//工作模式
    private String tempFire="";//火线温度；
    private String tempZero="";//零线温度
    private String relayState="";//继电器状态
    private String autoMode="";//手动自动模式

    private String requestCode="";//下发命令
    private String respondCode="";//下发命令回复

    private String threshold1="";//电压阈值
    private String threshold2="";//电流阈值
    private String threshold3="";//漏电流阈值
    private String threshold4="";//温度阈值
    private String error="获取失败";
    private int errorCode=2;



    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getVoltage() {
        return voltage;
    }
    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }
    public String getCurrent() {
        return current;
    }
    public void setCurrent(String current) {
        this.current = current;
    }
    public String getLeakage() {
        return leakage;
    }
    public void setLeakage(String leakage) {
        this.leakage = leakage;
    }
    public String getRemainingBattery() {
        return remainingBattery;
    }
    public void setRemainingBattery(String remainingBattery) {
        this.remainingBattery = remainingBattery;
    }
    public String getTotalBattery() {
        return totalBattery;
    }
    public void setTotalBattery(String totalBattery) {
        this.totalBattery = totalBattery;
    }
    public String getOverdraft() {
        return overdraft;
    }
    public void setOverdraft(String overdraft) {
        this.overdraft = overdraft;
    }
    public String getVersionCode() {
        return versionCode;
    }
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
    public String getCcid() {
        return ccid;
    }
    public void setCcid(String ccid) {
        this.ccid = ccid;
    }
    public String getSignal() {
        return signal;
    }
    public void setSignal(String signal) {
        this.signal = signal;
    }
    public String getPower() {
        return power;
    }
    public void setPower(String power) {
        this.power = power;
    }
    public String getPowerFactor() {
        return powerFactor;
    }
    public void setPowerFactor(String powerFactor) {
        this.powerFactor = powerFactor;
    }
    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public String getWorkMode() {
        return workMode;
    }
    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }
    public String getTempFire() {
        return tempFire;
    }
    public void setTempFire(String tempFire) {
        this.tempFire = tempFire;
    }
    public String getTempZero() {
        return tempZero;
    }
    public void setTempZero(String tempZero) {
        this.tempZero = tempZero;
    }
    public String getRelayState() {
        return relayState;
    }
    public void setRelayState(String relayState) {
        this.relayState = relayState;
    }
    public String getAutoMode() {
        return autoMode;
    }
    public void setAutoMode(String autoMode) {
        this.autoMode = autoMode;
    }
    public String getRespondCode() {
        return respondCode;
    }
    public void setRespondCode(String respondCode) {
        this.respondCode = respondCode;
    }
    public String getRequestCode() {
        return requestCode;
    }
    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }
    public String getThreshold1() {
        return threshold1;
    }
    public void setThreshold1(String threshold) {
        this.threshold1 = threshold;
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
    public String getThreshold2() {
        return threshold2;
    }
    public void setThreshold2(String threshold2) {
        this.threshold2 = threshold2;
    }
    public String getThreshold3() {
        return threshold3;
    }
    public void setThreshold3(String threshold3) {
        this.threshold3 = threshold3;
    }
    public String getThreshold4() {
        return threshold4;
    }
    public void setThreshold4(String threshold4) {
        this.threshold4 = threshold4;
    }
}
