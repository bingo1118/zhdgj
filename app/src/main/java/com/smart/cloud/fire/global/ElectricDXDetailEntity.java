package com.smart.cloud.fire.global;

import java.io.Serializable;

public class ElectricDXDetailEntity implements Serializable {
    private String mac="";

    private String voltage="";//电压
    private String voltageB="";//电压B
    private String voltageC="";//电压C

    private String current="";//电流
    private String currentB="";//电流B
    private String currentC="";//电流C

    private String leakage="";//漏电流

    private String remainingBattery="";//剩余电量
    private String totalBattery="";//总用电量
    private String overdraft="";//透支电量

    private String versionCode="";//版本号
    private String ccid="";
    private String signal="";//信号

    private String power="";//功率
    private String powerB="";//功率B
    private String powerC="";//功率C

    private String powerFactor="";//功率因数
    private String powerFactorB="";//功率因数B
    private String powerFactorC="";//功率因数C

    private String frequency="";//频率
    private String workMode="";//工作模式

    private String tempFire="";//火线温度
    private String tempFireB="";//火线温度B
    private String tempFireC="";//火线温度C


    private String tempZero="";//零线温度
    private String relayState="";//继电器状态
    private String autoMode="";//手动自动模式

    private String requestCode="";//下发命令
    private String respondCode="";//下发命令回复

    private String threshold33enable="";//超电压使能
    private String threshold33="";//过压阈值
    private String threshold34enable="";//低电压使能
    private String threshold34="";//欠压阈值
    private String threshold35enable="";//超负载使能
    private String threshold35="";//电流阈值
    private String threshold36enable="";//漏电流使能
    private String threshold36="";//漏电流阈值
    private String threshold37enable="";//超温度使能
    private String threshold37="";//温度阈值
    private String actionDelay="";//动作时延
    private String openSum="";//重合闸次数
    private String payMode="";//付费模式
    private String price="";//电价

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

    public String getThreshold33() {
        return threshold33;
    }
    public void setThreshold33(String threshold33) {
        this.threshold33 = threshold33;
    }
    public String getThreshold34() {
        return threshold34;
    }
    public void setThreshold34(String threshold34) {
        this.threshold34 = threshold34;
    }
    public String getThreshold35() {
        return threshold35;
    }
    public void setThreshold35(String threshold35) {
        this.threshold35 = threshold35;
    }
    public String getThreshold36() {
        return threshold36;
    }
    public void setThreshold36(String threshold36) {
        this.threshold36 = threshold36;
    }
    public String getThreshold37() {
        return threshold37;
    }
    public void setThreshold37(String threshold37) {
        this.threshold37 = threshold37;
    }

    public String getVoltageB() {
        return voltageB;
    }
    public void setVoltageB(String voltageB) {
        this.voltageB = voltageB;
    }
    public String getCurrentB() {
        return currentB;
    }
    public void setCurrentB(String currentB) {
        this.currentB = currentB;
    }
    public String getVoltageC() {
        return voltageC;
    }
    public void setVoltageC(String voltageC) {
        this.voltageC = voltageC;
    }
    public String getCurrentC() {
        return currentC;
    }
    public void setCurrentC(String currentC) {
        this.currentC = currentC;
    }
    public String getPowerB() {
        return powerB;
    }
    public void setPowerB(String powerB) {
        this.powerB = powerB;
    }
    public String getPowerC() {
        return powerC;
    }
    public void setPowerC(String powerC) {
        this.powerC = powerC;
    }
    public String getPowerFactorB() {
        return powerFactorB;
    }
    public void setPowerFactorB(String powerFactorB) {
        this.powerFactorB = powerFactorB;
    }
    public String getPowerFactorC() {
        return powerFactorC;
    }
    public void setPowerFactorC(String powerFactorC) {
        this.powerFactorC = powerFactorC;
    }
    public String getTempFireB() {
        return tempFireB;
    }
    public void setTempFireB(String tempFireB) {
        this.tempFireB = tempFireB;
    }
    public String getTempFireC() {
        return tempFireC;
    }
    public void setTempFireC(String tempFireC) {
        this.tempFireC = tempFireC;
    }

    public String getThreshold33enable() {
        return threshold33enable;
    }
    public void setThreshold33enable(String threshold33enable) {
        this.threshold33enable = threshold33enable;
    }
    public String getThreshold34enable() {
        return threshold34enable;
    }
    public void setThreshold34enable(String threshold34enable) {
        this.threshold34enable = threshold34enable;
    }
    public String getThreshold35enable() {
        return threshold35enable;
    }
    public void setThreshold35enable(String threshold35enable) {
        this.threshold35enable = threshold35enable;
    }
    public String getThreshold36enable() {
        return threshold36enable;
    }
    public void setThreshold36enable(String threshold36enable) {
        this.threshold36enable = threshold36enable;
    }
    public String getThreshold37enable() {
        return threshold37enable;
    }
    public void setThreshold37enable(String threshold37enable) {
        this.threshold37enable = threshold37enable;
    }
    public String getActionDelay() {
        return actionDelay;
    }
    public void setActionDelay(String actionDelay) {
        this.actionDelay = actionDelay;
    }
    public String getOpenSum() {
        return openSum;
    }
    public void setOpenSum(String openSum) {
        this.openSum = openSum;
    }
    public String getPayMode() {
        return payMode;
    }
    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
