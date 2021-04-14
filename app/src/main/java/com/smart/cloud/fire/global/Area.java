package com.smart.cloud.fire.global;

import com.smart.cloud.fire.view.dataSelector.BingoViewModel;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/22.
 */
public class Area implements Serializable,BingoViewModel{
    private String areaId;
    private String areaName;
    private String p_areaId;
    private String p_areaName;
    private int isParent;//@@9.1

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getIsParent() {
        return isParent;
    }

    public void setIsParent(int isParent) {
        this.isParent = isParent;
    }

    @Override
    public String getModelId() {
        return areaId;
    }

    @Override
    public String getModelName() {
        return areaName;
    }

    public String getP_areaId() {
        return p_areaId;
    }

    public void setP_areaId(String p_areaId) {
        this.p_areaId = p_areaId;
    }

    public String getP_areaName() {
        return p_areaName;
    }

    public void setP_areaName(String p_areaName) {
        this.p_areaName = p_areaName;
    }
}
