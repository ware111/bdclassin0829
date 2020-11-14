package com.blackboard.classin.entity;

public class StudentDetail {
    private String courseId;
    private String className;
    private String classId;
    private long closeTime;
    private long startTime;
    private String studentBBId;
    private String studentName;
    private String studentPhone;
    private String studentUid;
    //学生实际上课时长
    private String studentInClassTime;
    //老师实际上课时长
    private String teacherInClassTime;
    private String checkin="";
    private String late="";
    private String back="";
    private int speakingDuration;
    private int upStageTimes;
    private int upStageDuration;
    private int downStageTimes;
    private int downStageDuration;
    //移出次数
    private int tickOutCount;
    //奖励次数
    private int awardCount;
    //举手次数
    private int handsupCount;
    //授权次数
    private int authorizeCount;
    //授权时长
    private int authorizeTime;
    //抢答器次数
    private int responderCount;
    //强中次数
    private int responderSelectedCount;
    //答题器次数
    private int answerCount;
    //回答正确次数
    private int answerCorrectTimes;
    //摄像头打开时长
    private int cameraDuration;
    private String json;
    private int identity;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherInClassTime() {
        return teacherInClassTime;
    }

    public void setTeacherInClassTime(String teacherInClassTime) {
        this.teacherInClassTime = teacherInClassTime;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getStudentBBId() {
        return studentBBId;
    }

    public void setStudentBBId(String studentBBId) {
        this.studentBBId = studentBBId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentInClassTime() {
        return studentInClassTime;
    }

    public void setStudentInClassTime(String studentInClassTime) {
        this.studentInClassTime = studentInClassTime;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public int getSpeakingDuration() {
        return speakingDuration;
    }

    public void setSpeakingDuration(int speakingDuration) {
        this.speakingDuration = speakingDuration;
    }

    public int getUpStageTimes() {
        return upStageTimes;
    }

    public void setUpStageTimes(int upStageTimes) {
        this.upStageTimes = upStageTimes;
    }

    public int getUpStageDuration() {
        return upStageDuration;
    }

    public void setUpStageDuration(int upStageDuration) {
        this.upStageDuration = upStageDuration;
    }

    public int getDownStageTimes() {
        return downStageTimes;
    }

    public void setDownStageTimes(int downStageTimes) {
        this.downStageTimes = downStageTimes;
    }

    public int getDownStageDuration() {
        return downStageDuration;
    }

    public void setDownStageDuration(int downStageDuration) {
        this.downStageDuration = downStageDuration;
    }

    public int getTickOutCount() {
        return tickOutCount;
    }

    public void setTickOutCount(int tickOutCount) {
        this.tickOutCount = tickOutCount;
    }

    public int getAwardCount() {
        return awardCount;
    }

    public void setAwardCount(int awardCount) {
        this.awardCount = awardCount;
    }

    public int getHandsupCount() {
        return handsupCount;
    }

    public void setHandsupCount(int handsupCount) {
        this.handsupCount = handsupCount;
    }

    public int getAuthorizeCount() {
        return authorizeCount;
    }

    public void setAuthorizeCount(int authorizeCount) {
        this.authorizeCount = authorizeCount;
    }

    public int getAuthorizeTime() {
        return authorizeTime;
    }

    public void setAuthorizeTime(int authorizeTime) {
        this.authorizeTime = authorizeTime;
    }

    public int getResponderCount() {
        return responderCount;
    }

    public void setResponderCount(int responderCount) {
        this.responderCount = responderCount;
    }

    public int getResponderSelectedCount() {
        return responderSelectedCount;
    }

    public void setResponderSelectedCount(int responderSelectedCount) {
        this.responderSelectedCount = responderSelectedCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getAnswerCorrectTimes() {
        return answerCorrectTimes;
    }

    public void setAnswerCorrectTimes(int answerCorrectTimes) {
        this.answerCorrectTimes = answerCorrectTimes;
    }

    public int getCameraDuration() {
        return cameraDuration;
    }

    public void setCameraDuration(int cameraDuration) {
        this.cameraDuration = cameraDuration;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
