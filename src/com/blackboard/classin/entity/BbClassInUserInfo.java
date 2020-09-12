package com.blackboard.classin.entity;

import java.sql.Timestamp;

public class BbClassInUserInfo {
	private Integer pk1;
	private String bbUserId;
	private String classinUid;
	private String telephone;
	private Timestamp dtcreated;
	public Integer getPk1() {
		return pk1;
	}
	public void setPk1(Integer pk1) {
		this.pk1 = pk1;
	}
	public String getBbUserId() {
		return bbUserId;
	}
	public void setBbUserId(String bbUserId) {
		this.bbUserId = bbUserId;
	}
	public String getClassinUid() {
		return classinUid;
	}
	public void setClassinUid(String classinUid) {
		this.classinUid = classinUid;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public Timestamp getDtcreated() {
		return dtcreated;
	}
	public void setDtcreated(Timestamp dtcreated) {
		this.dtcreated = dtcreated;
	}
	
	
}
