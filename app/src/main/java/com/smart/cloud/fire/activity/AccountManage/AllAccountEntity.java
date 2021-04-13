package com.smart.cloud.fire.activity.AccountManage;

import java.util.List;


public class AllAccountEntity {

	private String error="";
    private int errorCode;
    private List<AccountEntity> list;
    
	public List<AccountEntity> getList() {
		return list;
	}
	public void setList(List<AccountEntity> list) {
		this.list = list;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
