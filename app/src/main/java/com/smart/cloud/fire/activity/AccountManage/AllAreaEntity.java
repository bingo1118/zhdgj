package com.smart.cloud.fire.activity.AccountManage;

import com.smart.cloud.fire.global.Area;

import java.util.List;


public class AllAreaEntity {
	
	private String error="";
    private int errorCode;
    private List<Area> list;
    
    
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
	public List<Area> getList() {
		return list;
	}
	public void setList(List<Area> list) {
		this.list = list;
	}

}
