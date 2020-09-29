package com.blackboard.classin.entity;

import blackboard.data.user.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.util.SystemUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClassCondition {
    private String courseId;
    private String className;
    private long closeTim;
    private long startTime;
    private String teacherBBId;
    private String teacherName;
    private String teacherPhone;
    private String teacheInClass;
    private String checkin="";
    private String late="";
    private String back="";
    //应出勤学生数
    private String studentTotal="";
    //出勤学生数
    private int checkinStudent;
    //学生迟到总数
    private int laterTotal;
    //学生早退总数
    private int leaveEarly;
    //文本课件使用时长
    private int textCoursewareTime;
    //文本课件使用数量
    private int textCoursewareCount;
    //音视频课件使用时长
    private int avCoursewareTime;
    //音视频课件使用数量
    private int avCoursewareCount;
    //全体静言次数
    private int allMuteCount;
    //全体静言总时长
    private int allMuteTotalTime ;
    //移出学生人数
    private int tickOutPeoples;
    //移出学生次数
    private int tickOutCount;
    //奖励人数
    private int awardPeoples;
    //奖励次数
    private int awardCount;
    //举手人数
    private int handsupPeoples;
    //举手次数
    private int handsupCount;
    //授权人数
    private int authorizePeoples;
    //授权次数
    private int authorizeCount;
    //授权时长
    private int authorizeTime;
    //屏幕共享时长
    private int screenshareTime;
    //屏幕共享次数
    private int screenshareCount;
    //定时器次数
    private int timerCount;
    //计时器次数
    private int computeTimerCount;
    //骰子次数
    private int diceCount;
    //抢答器次数
    private int responderCount;
    //答题器次数
    private int answerCount;
    //答题器平均正确率
    private double averageAccuracy;
    //小黑板使用次数
    private int smallboardCount;
    //小黑帮使用时长
    private int totalTime;

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

    public long getCloseTim() {
        return closeTim;
    }

    public void setCloseTim(long closeTim) {
        this.closeTim = closeTim;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTeacherBBId() {
        return teacherBBId;
    }

    public void setTeacherBBId(String teacherBBId) {
        this.teacherBBId = teacherBBId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public String getTeacheInClass() {
        return teacheInClass;
    }

    public void setTeacheInClass(String teacheInClass) {
        this.teacheInClass = teacheInClass;
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

    public String getStudentTotal() {
        return studentTotal;
    }

    public void setStudentTotal(String studentTotal) {
        this.studentTotal = studentTotal;
    }

    public int getCheckinStudent() {
        return checkinStudent;
    }

    public void setCheckinStudent(int checkinStudent) {
        this.checkinStudent = checkinStudent;
    }

    public int getLaterTotal() {
        return laterTotal;
    }

    public void setLaterTotal(int laterTotal) {
        this.laterTotal = laterTotal;
    }

    public int getLeaveEarly() {
        return leaveEarly;
    }

    public void setLeaveEarly(int leaveEarly) {
        this.leaveEarly = leaveEarly;
    }

    public int getTextCoursewareTime() {
        return textCoursewareTime;
    }

    public void setTextCoursewareTime(int textCoursewareTime) {
        this.textCoursewareTime = textCoursewareTime;
    }

    public int getTextCoursewareCount() {
        return textCoursewareCount;
    }

    public void setTextCoursewareCount(int textCoursewareCount) {
        this.textCoursewareCount = textCoursewareCount;
    }

    public int getAvCoursewareTime() {
        return avCoursewareTime;
    }

    public void setAvCoursewareTime(int avCoursewareTime) {
        this.avCoursewareTime = avCoursewareTime;
    }

    public int getAvCoursewareCount() {
        return avCoursewareCount;
    }

    public void setAvCoursewareCount(int avCoursewareCount) {
        this.avCoursewareCount = avCoursewareCount;
    }

    public int getAllMuteCount() {
        return allMuteCount;
    }

    public void setAllMuteCount(int allMuteCount) {
        this.allMuteCount = allMuteCount;
    }

    public int getAllMuteTotalTime() {
        return allMuteTotalTime;
    }

    public void setAllMuteTotalTime(int allMuteTotalTime) {
        this.allMuteTotalTime = allMuteTotalTime;
    }

    public int getTickOutPeoples() {
        return tickOutPeoples;
    }

    public void setTickOutPeoples(int tickOutPeoples) {
        this.tickOutPeoples = tickOutPeoples;
    }

    public int getTickOutCount() {
        return tickOutCount;
    }

    public void setTickOutCount(int tickOutCount) {
        this.tickOutCount = tickOutCount;
    }

    public int getAwardPeoples() {
        return awardPeoples;
    }

    public void setAwardPeoples(int awardPeoples) {
        this.awardPeoples = awardPeoples;
    }

    public int getAwardCount() {
        return awardCount;
    }

    public void setAwardCount(int awardCount) {
        this.awardCount = awardCount;
    }

    public int getHandsupPeoples() {
        return handsupPeoples;
    }

    public void setHandsupPeoples(int handsupPeoples) {
        this.handsupPeoples = handsupPeoples;
    }

    public int getHandsupCount() {
        return handsupCount;
    }

    public void setHandsupCount(int handsupCount) {
        this.handsupCount = handsupCount;
    }

    public int getAuthorizePeoples() {
        return authorizePeoples;
    }

    public void setAuthorizePeoples(int authorizePeoples) {
        this.authorizePeoples = authorizePeoples;
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

    public int getScreenshareTime() {
        return screenshareTime;
    }

    public void setScreenshareTime(int screenshareTime) {
        this.screenshareTime = screenshareTime;
    }

    public int getScreenshareCount() {
        return screenshareCount;
    }

    public void setScreenshareCount(int screenshareCount) {
        this.screenshareCount = screenshareCount;
    }

    public int getTimerCount() {
        return timerCount;
    }

    public void setTimerCount(int timerCount) {
        this.timerCount = timerCount;
    }

    public int getComputeTimerCount() {
        return computeTimerCount;
    }

    public void setComputeTimerCount(int computeTimerCount) {
        this.computeTimerCount = computeTimerCount;
    }

    public int getDiceCount() {
        return diceCount;
    }

    public void setDiceCount(int diceCount) {
        this.diceCount = diceCount;
    }

    public int getResponderCount() {
        return responderCount;
    }

    public void setResponderCount(int responderCount) {
        this.responderCount = responderCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public int getSmallboardCount() {
        return smallboardCount;
    }

    public void setSmallboardCount(int smallboardCount) {
        this.smallboardCount = smallboardCount;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
}
