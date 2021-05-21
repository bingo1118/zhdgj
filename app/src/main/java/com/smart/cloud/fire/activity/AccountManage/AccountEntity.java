package com.smart.cloud.fire.activity.AccountManage;

import java.io.Serializable;

public class AccountEntity implements Serializable{
	
	private String userName;
	private String userId;
	private int privilege;
	private int grade;
	private String p_userId;
	private int istxt;
	private int cut_electr;
	private int add_electr;

	private int gradeOfArea;//区域里的等级  1管理员 2普通用户
	
	public int getIstxt() {
		return istxt;
	}
	public void setIstxt(int istxt) {
		this.istxt = istxt;
	}
	public int getCut_electr() {
		return cut_electr;
	}
	public void setCut_electr(int cut_electr) {
		this.cut_electr = cut_electr;
	}
	public int getAdd_electr() {
		return add_electr;
	}
	public void setAdd_electr(int add_electr) {
		this.add_electr = add_electr;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getPrivilege() {
		return privilege;
	}
	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public String getP_userId() {
		return p_userId;
	}
	public void setP_userId(String p_userId) {
		this.p_userId = p_userId;
	}

	public int getGradeOfArea() {
		return gradeOfArea;
	}

	public void setGradeOfArea(int gradeOfArea) {
		this.gradeOfArea = gradeOfArea;
	}
}
