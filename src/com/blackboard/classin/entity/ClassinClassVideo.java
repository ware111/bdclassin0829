package com.blackboard.classin.entity;

public class ClassinClassVideo {
	private String classinCourseId;
	private String classinClassId;
	private String actionTime;
	private String sid;
	private String vTimestamp;
	private String vst;
	private String vet;
	private String cmd;
	private String vURL;
	private String vDuration;
	private String fileId;
	private int vSize;
	private String deleteStatus;
	private int vSequence;
	
	public int getvSequence() {
		return vSequence;
	}
	public void setvSequence(int vSequence) {
		this.vSequence = vSequence;
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
	public String getActionTime() {
		return actionTime;
	}
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getvTimestamp() {
		return vTimestamp;
	}
	public void setvTimestamp(String vTimestamp) {
		this.vTimestamp = vTimestamp;
	}
	public String getVst() {
		return vst;
	}
	public void setVst(String vst) {
		this.vst = vst;
	}
	public String getVet() {
		return vet;
	}
	public void setVet(String vet) {
		this.vet = vet;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getvURL() {
		return vURL;
	}
	public void setvURL(String vURL) {
		this.vURL = vURL;
	}
	public String getvDuration() {
		return vDuration;
	}
	public void setvDuration(String vDuration) {
		this.vDuration = vDuration;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public int getvSize() {
		return vSize;
	}
	public void setvSize(int vSize) {
		this.vSize = vSize;
	}
	public String getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
}
