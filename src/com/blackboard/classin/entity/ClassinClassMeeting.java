package com.blackboard.classin.entity;

public class ClassinClassMeeting {
	private String classinCourseId;
	private String classinClassId;
	private String dtCreated;
	private String teacherPhone;
	private String assistantPhone;
	private String expireStatus;
	private String liveURL;
	
	public String getLiveURL() {
		return liveURL;
	}
	public void setLiveURL(String liveURL) {
		this.liveURL = liveURL;
	}
	public String getClassinCourseId() {
		return classinCourseId;
	}
	public void setClassinCourseId(String classinCourseId) {
		this.classinCourseId = classinCourseId;
	}
	public String getClassinClassId() {
		return classinClassId;
	}
	public void setClassinClassId(String classinClassId) {
		this.classinClassId = classinClassId;
	}
	public String getDtCreated() {
		return dtCreated;
	}
	public void setDtCreated(String dtCreated) {
		this.dtCreated = dtCreated;
	}
	public String getTeacherPhone() {
		return teacherPhone;
	}
	public void setTeacherPhone(String teacherPhone) {
		this.teacherPhone = teacherPhone;
	}
	public String getAssistantPhone() {
		return assistantPhone;
	}
	public void setAssistantPhone(String assistantPhone) {
		this.assistantPhone = assistantPhone;
	}
	public String getExpireStatus() {
		return expireStatus;
	}
	public void setExpireStatus(String expireStatus) {
		this.expireStatus = expireStatus;
	}
}
