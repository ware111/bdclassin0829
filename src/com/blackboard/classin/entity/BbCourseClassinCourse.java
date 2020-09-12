package com.blackboard.classin.entity;

public class BbCourseClassinCourse {
	
	private String bbCourseId;
	private String classinCourseId;
	
	public BbCourseClassinCourse() {};
	public BbCourseClassinCourse(String bbCourseId, String classinCourseId) {
		super();
		this.bbCourseId = bbCourseId;
		this.classinCourseId = classinCourseId;
	}
	public String getBbCourseId() {
		return bbCourseId;
	}
	public void setBbCourseId(String bbCourseId) {
		this.bbCourseId = bbCourseId;
	}
	public String getClassinCourseId() {
		return classinCourseId;
	}
	public void setClassinCourseId(String classinCourseId) {
		this.classinCourseId = classinCourseId;
	}
	
}
