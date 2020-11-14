package com.blackboard.classin.service.impl;

import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.controller.ClassinCourseClassController;
import com.blackboard.classin.entity.*;
import com.blackboard.classin.mapper.*;
import com.blackboard.classin.service.CheckinRelationDataService;
import com.blackboard.classin.util.SearchUtil;
import com.blackboard.classin.util.SystemUtil;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CheckinRelationDataImp implements CheckinRelationDataService {

    private Logger logger = Logger.getLogger(CheckinRelationDataImp.class);

    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private ClassConditionMapper classConditionMapper;

    @Autowired
    private StudentDetailMapper studentDetailMapper;

    @Override
    public void handleClassSituationData(String datas) throws PersistenceException, IOException {
//        JSONObject totalJson = JSONObject.parseObject(datas);
//        CourseClassConditionData classConditionData = new CourseClassConditionData();
//        JSONObject jsonObject = new JSONObject();
//        int courseID = (int) totalJson.get("CourseID");
//        classConditionData.setCourseId(courseID + "");
//        int classID = (int) totalJson.get("ClassID");
//        classConditionData.setClassId(classID + "");
//        ClassinCourseClass classInfo = classinCourseClassMapper.findByClassId(classID + "");
//        String bbCourseId = bbCourseClassinCourseMapper.findByClassinCourseId(courseID + "");
//        if (bbCourseId != null) {
//            if (classInfo != null) {
//                String className = classInfo.getClassName();
//                classConditionData.setClassName(className);
//                long closeTime = (int) totalJson.get("CloseTime") - 1200;
//                long startTime = (int) totalJson.get("StartTime");
//                classConditionData.setStartTime(startTime);
//                classConditionData.setCloseTime(closeTime);
//                String teacherBBId = classInfo.getUserName();
//                classConditionData.setTeacherBBId(teacherBBId);
//                User user = SystemUtil.getUserByUserId(teacherBBId);
//                String teacherName = user.getFamilyName() + user.getMiddleName() + user.getGivenName();
//                classConditionData.setTeacherName(teacherName);
//                String teacherPhone = classInfo.getTeacherPhone();
//                String assistantUid = "";
//                String assistantPhone = "";
//
//                String assistantName = classInfo.getAssistantName();
//                if (assistantName == null) {
//                    assistantName = "无";
//                }
//                classConditionData.setTeacherPhone(teacherPhone);
//                classConditionData.setAssistantPhone(assistantPhone);
//                classConditionData.setAssistantName(assistantName);
//
//                JSONObject dataJson = (JSONObject) totalJson.get("Data");
//                String teacherUid = "";
//
//                JSONObject inOutEndJson = (JSONObject) dataJson.get("inoutEnd");
//                String checkin = "";
//                String late = "";
//                String back = "";
//                JSONObject inOutUIDJson = (JSONObject) inOutEndJson.get(teacherUid);
//                int checkinTotalTime = (int) inOutUIDJson.get("Total");
//                if (checkinTotalTime > 0) {
//                    checkin = "出勤";
//                } else {
//                    checkin = "缺勤";
//                }
//                classConditionData.setCheckin(checkin);
//                JSONArray details = (JSONArray) inOutUIDJson.get("Details");
//                JSONObject inData = (JSONObject) details.get(0);
//                JSONObject outData = (JSONObject) details.get(details.size() - 1);
//                long firstInTime = (int) inData.get("Time");
//                long endOutTime = (int) outData.get("Time");
//
//                //实际上课时长
//                long teacherClassTime = checkinTotalTime;
//                classConditionData.setTeacheInClassTime(teacherClassTime / 60 + "");
//                if (firstInTime > startTime) {
//                    late = "迟到";
//                    classConditionData.setLate("迟到");
//                }
//                if (endOutTime < closeTime) {
//                    back = "早退";
//                    classConditionData.setBack("早退");
//                }
//
//                String classType = classInfo.getClassType();
//                //应出勤学生数
//                String studentTotal = "";
//                if (classType.equals("课表课")) {
//                    studentTotal = classInfo.getStudentTotal();
//                    if (studentTotal.equals("0")) {
//                        studentTotal = SystemUtil.getStudents(bbCourseId) + "";
//                    }
//                } else {
//                    studentTotal = SystemUtil.getStudents(bbCourseId) + "";
//                }
//                classConditionData.setStudentTotal(studentTotal);
//
//                Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
//                //出勤学生数
//                int checkinStudent = 0;
//                //学生迟到总数
//                int laterTotal = 0;
//
//                //学生早退总数
//                int leaveEarly = 0;
//                Iterator<Map.Entry<String, Object>> iterator = users.iterator();
//                while (iterator.hasNext()) {
//                    String uid = iterator.next().getKey();
//                    JSONObject studentUid = (JSONObject) inOutEndJson.get(uid);
//                    int identity = (int) studentUid.get("Identity");
//                    if (identity == 3 || identity == 4 || identity == 193 || identity == 194) {
//                        if (identity == 4) {
//                            assistantUid = uid;
//                        }
//                        continue;
//                    } else {
//                        checkinStudent++;
//                        JSONObject inOutJson = (JSONObject) inOutEndJson.get(uid);
//                        JSONArray inOutArray = (JSONArray) inOutJson.get("Details");
//                        JSONObject inObject = (JSONObject) inOutArray.get(0);
//                        long inTime = Long.valueOf(inObject.get("Time").toString());
//                        if (inTime > startTime) {
//                            laterTotal++;
//                        }
//                        JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
//                        long outTime = Long.valueOf(outObject.get("Time").toString());
//                        if (outTime < closeTime) {
//                            leaveEarly++;
//                        }
//                    }
//                }
//                classConditionData.setCheckinStudent(checkinStudent);
//                classConditionData.setLaterTotal(laterTotal);
//                classConditionData.setLeaveEarly(leaveEarly);
//
//                InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
//                Properties properties = new Properties();
//                properties.load(resource);
//                String textFile = properties.getProperty("textFile");
//                String av = properties.getProperty("av");
//                JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
//                //文本课件使用时长
//                int textCoursewareTime = 0;
//                //文本课件使用数量
//                int textCoursewareCount = 0;
//                //音视频课件使用时长
//                int avCoursewareTime = 0;
//                //音视频课件使用数量
//                int avCoursewareCount = 0;
//                LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
//                LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
//                if (coursewareJson != null) {
//                    JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
//                    if (coursewareArray != null) {
//                        for (Object file : coursewareArray) {
//                            JSONObject fileJson = (JSONObject) file;
//                            String fileName = (String) fileJson.get("FileName");
//                            String format = fileName.substring(fileName.indexOf(".") + 1);
//                            int endTime = (int) fileJson.get("EndTime");
//                            int beginTime = (int) fileJson.get("StartTime");
//                            int duration = endTime - beginTime;
//                            if (av.contains(format)) {
//                                avCoursewareCount++;
//                                avCoursewareTime += duration;
//                                avFileNames.add(avCoursewareCount + "");
//                            } else if (textFile.contains(format)) {
//                                textCoursewareCount++;
//                                textFileNames.add(textCoursewareCount + "");
//                                textCoursewareTime += duration;
//                            }
//                        }
//
//                        avCoursewareCount = avFileNames.size();
//                        textCoursewareCount = textFileNames.size();
//
//                        jsonObject.put("textFiles", textCoursewareCount);
//                        jsonObject.put("textFileTotalDuration", textCoursewareTime);
//                        jsonObject.put("audioVideoFiles", avCoursewareCount);
//                        jsonObject.put("audioVideoTotalDuration", avCoursewareTime);
//                    }
//                } else {
//                    jsonObject.put("textFiles", 0);
//                    jsonObject.put("textFileTotalDuration", 0);
//                    jsonObject.put("audioVideoFiles", 0);
//                    jsonObject.put("audioVideoTotalDuration", 0);
//                }
//                JSONObject muteJson = (JSONObject) dataJson.get("muteEnd");
//                int allMuteCount = 0;
//                int allMuteTotalTime = 0;
//                if (muteJson != null) {
//                    JSONObject muteAllJson = (JSONObject) muteJson.get("MuteAll");
//                    if (muteAllJson != null) {
//                        //全体静音次数
//                        if (muteAllJson.get("Count") != null) {
//                            allMuteCount = (int) muteAllJson.get("Count");
//                        }
//
//                        if (muteAllJson.get("Total") != null) {
//                            //全体静音总时长
//                            allMuteTotalTime = (int) muteAllJson.get("Total");
//                        }
//                    }
//                }
//                jsonObject.put("muteAllTimes", allMuteCount);
//                jsonObject.put("muteAllTotallDuration", allMuteTotalTime);
//                //移出学生人数
//                int tickOutPeoples = 0;
//                JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
//                if (tickOutJson != null) {
//                    tickOutPeoples = tickOutJson.size();
//                }
//                jsonObject.put("removeStudents", tickOutPeoples);
//                //移出学生次数
//                int tickOutCount = 0;
//                if (tickOutJson != null) {
//                    Iterator<Map.Entry<String, Object>> tickoutIterator = tickOutJson.entrySet().iterator();
//                    while (tickoutIterator.hasNext()) {
//                        String uid = tickoutIterator.next().getKey();
//                        JSONArray uidJsonArray = (JSONArray) tickOutJson.get(uid);
//                        tickOutCount += uidJsonArray.size();
//                    }
//                }
//                jsonObject.put("removeStudentTimes", tickOutCount);
//
//                //奖励人数
//                int awardPeoples = 0;
//                JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
//                if (awardJson != null) {
//                    awardPeoples = awardJson.size();
//                }
//                classConditionData.setAwardPeoples(awardPeoples);
//                //奖励次数
//                int awardCount = 0;
//                if (awardJson != null) {
//                    Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
//                    while (awardIterator.hasNext()) {
//                        String uid = awardIterator.next().getKey();
//                        JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
//                        int total = (int) uidJsonArray.get("Total");
//                        awardCount += total;
//                    }
//                }
//                classConditionData.setAwardCount(awardCount);
//
//                //举手人数
//                int handsupPeoples = 0;
//                JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
//                if (handsupJson != null) {
//                    handsupPeoples = handsupJson.size();
//                }
//                classConditionData.setHandsupPeoples(handsupPeoples);
//                //举手次数
//                int handsupCount = 0;
//                if (handsupJson != null) {
//                    Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
//                    while (handsupIterator.hasNext()) {
//                        String uid = handsupIterator.next().getKey();
//                        JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
//                        int total = (int) uidJsonArray.get("Total");
//                        handsupCount += total;
//                    }
//                }
//                classConditionData.setHandsupCount(handsupCount);
//
//                //授权人数
//                int authorizePeoples = 0;
//                JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
////            if (authorizeJson != null) {
////                authorizeJson.remove(teacherUid);
////                //授权人数
////                authorizePeoples = authorizeJson.size();
////            }
//                //授权次数
//                int authorizeCount = 0;
//                //授权时长
//                int authorizeTime = 0;
//                if (authorizeJson != null) {
//                    Iterator<Map.Entry<String, Object>> authorizeIterator = authorizeJson.entrySet().iterator();
//                    while (authorizeIterator.hasNext()) {
//                        String uid = authorizeIterator.next().getKey();
//                        if (!assistantUid.equals("")) {
//                            if (!uid.equals(teacherUid) && !uid.equals(assistantUid)) {
//                                JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
//                                int total = (int) uidJsonArray.get("Count");
//                                int totalTime = (int) uidJsonArray.get("Total");
//                                authorizeTime += totalTime;
//                                authorizeCount += total;
//                                if (total > 0) {
//                                    authorizePeoples++;
//                                }
//                            }
//                        } else {
//                            if (!uid.equals(teacherUid)) {
//                                if (!uid.equals(teacherUid) && !uid.equals(assistantUid)) {
//                                    JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
//                                    int total = (int) uidJsonArray.get("Count");
//                                    int totalTime = (int) uidJsonArray.get("Total");
//                                    authorizeTime += totalTime;
//                                    authorizeCount += total;
//                                    if (total > 0) {
//                                        authorizePeoples++;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                classConditionData.setAuthorizePeoples(authorizePeoples);
//                classConditionData.setAuthorizeCount(authorizeCount);
//                jsonObject.put("authorizeTotalDuration", (double) (authorizeTime));
//                JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
//                //屏幕共享时长
//                int screenshareTime = 0;
//                //屏幕共享次数
//                int screenshareCount = 0;
//                if (screenshareJson != null) {
//                    screenshareTime = (int) screenshareJson.get("Total");
//                    screenshareCount = (int) screenshareJson.get("Count");
//                }
//                jsonObject.put("deskShareTimes", screenshareCount);
//                jsonObject.put("deskShareTotalDuration", screenshareTime);
//
//                JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
//                //定时器次数
//                int timerCount = 0;
//                //计时器次数
//                int computeTimerCount = 0;
//                if (timerJson != null) {
//                    timerCount = (int) timerJson.get("Count");
//                    computeTimerCount = (int) timerJson.get("Timing_Count");
//                }
//                jsonObject.put("countDownTimes", timerCount);
//
//                JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
//                //骰子次数
//                int diceCount = 0;
//                if (diceJson != null) {
//                    diceCount = (int) diceJson.get("Count");
//                }
//                jsonObject.put("diceTimes", diceCount);
//
//                JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
//
//                //抢答器次数
//                int responderCount = 0;
//                if (responderJson != null) {
//                    responderCount = (int) responderJson.get("Count");
//                }
//                jsonObject.put("responderTimes", responderCount);
//
//                JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
//                //答题器次数
//                int answerCount = 0;
//                //答题器平均正确率
//                BigDecimal averageAccuracy = new BigDecimal(0);
//                if (answerJson != null) {
//                    answerCount = (int) answerJson.get("Count");
//                    averageAccuracy = (BigDecimal) answerJson.get("AverageAccuracy");
//                }
//                classConditionData.setAnswerCount(answerCount);
//                classConditionData.setAverageAccuracy(averageAccuracy + "");
//
//                JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");
//
//                //小黑板使用次数
//                int smallboardCount = 0;
//                //小黑板使用时长
//                int totalTime = 0;
//                if (smallboardJson != null) {
//                    smallboardCount = (int) smallboardJson.get("Count");
//                    totalTime = (int) smallboardJson.get("Total");
//                }
//                jsonObject.put("blackboardTimes", smallboardCount);
//                jsonObject.put("blackboardTotalDuration", totalTime);
//                String json = jsonObject.toJSONString();
//                classConditionData.setJson(json);
//            } else {
//                classConditionData.setClassName("无");
//                long closeTime = (int) totalJson.get("CloseTime") - 1200;
//                long startTime = (int) totalJson.get("StartTime");
//                classConditionData.setStartTime(startTime);
//                classConditionData.setCloseTime(closeTime);
//                classConditionData.setTeacherBBId("无");
//                classConditionData.setTeacherName("无");
//                classConditionData.setTeacherPhone("无");
//                classConditionData.setAssistantPhone("无");
//                classConditionData.setAssistantName("无");
//                JSONObject dataJson = (JSONObject) totalJson.get("Data");
//                JSONObject inOutEndJson = (JSONObject) dataJson.get("inoutEnd");
//                String checkin = "";
//                String late = "";
//                String back = "";
//                classConditionData.setCheckin("无");
//
//                //实际上课时长
//                classConditionData.setTeacheInClassTime("0");
//                classConditionData.setLate("无");
//                classConditionData.setBack("无");
//                String studentTotal = SystemUtil.getStudents(bbCourseId) + "";
//                classConditionData.setStudentTotal(studentTotal);
//                logger.info("****************studentTotal******************" + studentTotal);
//                Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
//                //出勤学生数
//                int checkinStudent = 0;
//                //学生迟到总数
//                int laterTotal = 0;
//
//                //学生早退总数
//                int leaveEarly = 0;
//                String teacherUid = "";
//                String assistantUid = "";
//                Iterator<Map.Entry<String, Object>> iterator = users.iterator();
//                while (iterator.hasNext()) {
//                    String uid = iterator.next().getKey();
//                    JSONObject studentUid = (JSONObject) inOutEndJson.get(uid);
//                    int identity = (int) studentUid.get("Identity");
//                    if (identity == 3 || identity == 4 || identity == 193 || identity == 194) {
//                        if (identity == 3) {
//                            teacherUid = uid;
//                        }
//                        if (identity == 4) {
//                            assistantUid = uid;
//                            logger.info("***************assistantUid*****************" + assistantUid);
//                        }
//                        continue;
//                    } else {
//                        checkinStudent++;
//                        JSONObject inOutJson = (JSONObject) inOutEndJson.get(uid);
//                        JSONArray inOutArray = (JSONArray) inOutJson.get("Details");
//                        JSONObject inObject = (JSONObject) inOutArray.get(0);
//                        long inTime = Long.valueOf(inObject.get("Time").toString());
//                        if (inTime > startTime) {
//                            laterTotal++;
//                        }
//                        JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
//                        long outTime = Long.valueOf(outObject.get("Time").toString());
//                        if (outTime < closeTime) {
//                            leaveEarly++;
//                        }
//                    }
//                }
//                classConditionData.setCheckinStudent(checkinStudent);
//                classConditionData.setLaterTotal(laterTotal);
//                classConditionData.setLeaveEarly(leaveEarly);
//
//                InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
//                Properties properties = new Properties();
//                properties.load(resource);
//                String textFile = properties.getProperty("textFile");
//                String av = properties.getProperty("av");
//                JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
//                //文本课件使用时长
//                int textCoursewareTime = 0;
//                //文本课件使用数量
//                int textCoursewareCount = 0;
//                //音视频课件使用时长
//                int avCoursewareTime = 0;
//                //音视频课件使用数量
//                int avCoursewareCount = 0;
//                LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
//                LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
//                if (coursewareJson != null) {
//                    JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
//                    if (coursewareArray != null) {
//                        for (Object file : coursewareArray) {
//                            JSONObject fileJson = (JSONObject) file;
//                            String fileName = (String) fileJson.get("FileName");
//                            String format = fileName.substring(fileName.indexOf(".") + 1);
//                            int endTime = (int) fileJson.get("EndTime");
//                            int beginTime = (int) fileJson.get("StartTime");
//                            int duration = endTime - beginTime;
//                            if (av.contains(format)) {
//                                avCoursewareTime += duration;
//                                avFileNames.add(fileName);
//                            } else if (textFile.contains(format)) {
//                                textFileNames.add(fileName);
//                                textCoursewareTime += duration;
//                            }
//                        }
//
//                        avCoursewareCount = avFileNames.size();
//                        textCoursewareCount = textFileNames.size();
//
//                        jsonObject.put("textFiles", textCoursewareCount);
//                        jsonObject.put("textFileTotalDuration", textCoursewareTime);
//                        jsonObject.put("audioVideoFiles", avCoursewareCount);
//                        jsonObject.put("audioVideoTotalDuration", avCoursewareTime);
//                    }
//                } else {
//                    jsonObject.put("textFiles", 0);
//                    jsonObject.put("textFileTotalDuration", 0);
//                    jsonObject.put("audioVideoFiles", 0);
//                    jsonObject.put("audioVideoTotalDuration", 0);
//                }
//                JSONObject muteJson = (JSONObject) dataJson.get("muteEnd");
//                int allMuteCount = 0;
//                int allMuteTotalTime = 0;
//                if (muteJson != null) {
//                    JSONObject muteAllJson = (JSONObject) muteJson.get("MuteAll");
//                    if (muteAllJson != null) {
//                        //全体静音次数
//                        if (muteAllJson.get("Count") != null) {
//                            allMuteCount = (int) muteAllJson.get("Count");
//                        }
//
//                        if (muteAllJson.get("Total") != null) {
//                            //全体静音总时长
//                            allMuteTotalTime = (int) muteAllJson.get("Total");
//                        }
//                    }
//                }
//                jsonObject.put("muteAllTimes", allMuteCount);
//                jsonObject.put("muteAllTotallDuration", allMuteTotalTime);
//                //移出学生人数
//                int tickOutPeoples = 0;
//                JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
//                if (tickOutJson != null) {
//                    tickOutPeoples = tickOutJson.size();
//                }
//                jsonObject.put("removeStudents", tickOutPeoples);
//                //移出学生次数
//                int tickOutCount = 0;
//                if (tickOutJson != null) {
//                    Iterator<Map.Entry<String, Object>> tickoutIterator = tickOutJson.entrySet().iterator();
//                    while (tickoutIterator.hasNext()) {
//                        String uid = tickoutIterator.next().getKey();
//                        JSONArray uidJsonArray = (JSONArray) tickOutJson.get(uid);
//                        tickOutCount += uidJsonArray.size();
//                    }
//                }
//                jsonObject.put("removeStudentTimes", tickOutCount);
//
//                //奖励人数
//                int awardPeoples = 0;
//                JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
//                if (awardJson != null) {
//                    awardPeoples = awardJson.size();
//                }
//                classConditionData.setAwardPeoples(awardPeoples);
//                //奖励次数
//                int awardCount = 0;
//                if (awardJson != null) {
//                    Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
//                    while (awardIterator.hasNext()) {
//                        String uid = awardIterator.next().getKey();
//                        JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
//                        int total = (int) uidJsonArray.get("Total");
//                        awardCount += total;
//                    }
//                }
//                classConditionData.setAwardCount(awardCount);
//
//                //举手人数
//                int handsupPeoples = 0;
//                JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
//                if (handsupJson != null) {
//                    handsupPeoples = handsupJson.size();
//                }
//                classConditionData.setHandsupPeoples(handsupPeoples);
//                //举手次数
//                int handsupCount = 0;
//                if (handsupJson != null) {
//                    Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
//                    while (handsupIterator.hasNext()) {
//                        String uid = handsupIterator.next().getKey();
//                        JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
//                        int total = (int) uidJsonArray.get("Total");
//                        handsupCount += total;
//                    }
//                }
//                classConditionData.setHandsupCount(handsupCount);
//
//                //授权人数
//                int authorizePeoples = 0;
//                JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
////            if (authorizeJson != null) {
////                authorizeJson.remove(teacherUid);
////                authorizeJson.remove(assistantUid);
////                //授权人数
////                authorizePeoples = authorizeJson.size();
////            }
//
//                //授权次数
//                int authorizeCount = 0;
//                //授权时长
//                int authorizeTime = 0;
//                if (authorizeJson != null) {
//                    Iterator<Map.Entry<String, Object>> authorizeIterator = authorizeJson.entrySet().iterator();
//                    while (authorizeIterator.hasNext()) {
//                        String uid = authorizeIterator.next().getKey();
//                        if (!assistantUid.equals("")) {
//                            if (!uid.equals(teacherUid) && !uid.equals(assistantUid)) {
//                                JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
//                                int total = (int) uidJsonArray.get("Count");
//                                int totalTime = (int) uidJsonArray.get("Total");
//                                authorizeTime += totalTime;
//                                authorizeCount += total;
//                                authorizePeoples++;
//                            }
//                        } else {
//                            if (!uid.equals(teacherUid)) {
//                                JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
//                                int total = (int) uidJsonArray.get("Count");
//                                int totalTime = (int) uidJsonArray.get("Total");
//                                authorizeTime += totalTime;
//                                authorizeCount += total;
//                                authorizePeoples++;
//                            }
//                        }
//                    }
//                }
//                classConditionData.setAuthorizePeoples(authorizePeoples);
//                classConditionData.setAuthorizeCount(authorizeCount);
//                jsonObject.put("authorizeTotalDuration", (double) (authorizeTime / 60));
//                JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
//                //屏幕共享时长
//                int screenshareTime = 0;
//                //屏幕共享次数
//                int screenshareCount = 0;
//                if (screenshareJson != null) {
//                    screenshareTime = (int) screenshareJson.get("Total");
//                    screenshareCount = (int) screenshareJson.get("Count");
//                }
//                jsonObject.put("deskShareTimes", screenshareCount);
//                jsonObject.put("deskShareTotalDuration", screenshareTime);
//
//                JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
//                //定时器次数
//                int timerCount = 0;
//                //计时器次数
//                int computeTimerCount = 0;
//                if (timerJson != null) {
//                    timerCount = (int) timerJson.get("Count");
//                    computeTimerCount = (int) timerJson.get("Timing_Count");
//                }
//                jsonObject.put("countDownTimes", timerCount);
//
//                JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
//                //骰子次数
//                int diceCount = 0;
//                if (diceJson != null) {
//                    diceCount = (int) diceJson.get("Count");
//                }
//                jsonObject.put("diceTimes", diceCount);
//
//                JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
//
//                //抢答器次数
//                int responderCount = 0;
//                if (responderJson != null) {
//                    responderCount = (int) responderJson.get("Count");
//                }
//                jsonObject.put("responderTimes", responderCount);
//
//                JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
//                //答题器次数
//                int answerCount = 0;
//                //答题器平均正确率
//                BigDecimal averageAccuracy = new BigDecimal(0);
//                if (answerJson != null) {
//                    answerCount = (int) answerJson.get("Count");
//                    averageAccuracy = (BigDecimal) answerJson.get("AverageAccuracy");
//                }
//                classConditionData.setAnswerCount(answerCount);
//                classConditionData.setAverageAccuracy(averageAccuracy + "");
//
//                JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");
//
//                //小黑板使用次数
//                int smallboardCount = 0;
//                //小黑板使用时长
//                int totalTime = 0;
//                if (smallboardJson != null) {
//                    smallboardCount = (int) smallboardJson.get("Count");
//                    totalTime = (int) smallboardJson.get("Total");
//                }
//                jsonObject.put("blackboardTimes", smallboardCount);
//                jsonObject.put("blackboardTotalDuration", totalTime);
//                String json = jsonObject.toJSONString();
//                classConditionData.setJson(json);
//            }
//            classConditionMapper.saveClassCondition(classConditionData);
//        }
    }

    @Override
    public void handleStudentDetailData(String datas) throws IOException, PersistenceException {
        JSONObject totalJson = JSONObject.parseObject(datas);
        int courseID = (int) totalJson.get("CourseID");
        String bbCourseId = bbCourseClassinCourseMapper.findByClassinCourseId(courseID + "");
        if (bbCourseId != null) {
            int classID = (int) totalJson.get("ClassID");
            ClassinCourseClass classInfo = classinCourseClassMapper.findByClassId(classID + "");
            String teacherUid = "";
            String assistantUid = "";
            String className = "";
            String teacherPhone = "";
            String assistantPhone = "";
            String teacherName = "";
            String assistantName = "";
            String teacherBBid = "";
            ArrayList<String> teacherUids = new ArrayList<>();
            ArrayList<String> assistantUids = new ArrayList<>();
            HashMap<String, Integer> teacherMap = new HashMap<>();
            HashMap<String, Integer> assistantMap = new HashMap<>();
            JSONObject userJson;
            JSONObject studentJson = null;
            String checkin = "";
            String late = "";
            String back = "";
            int checkinTotalTime;
            long teacherInTime = 0;
            int assitantInTime = 0;
            //出勤学生数
            int checkinStudent = 0;
            //学生迟到总数
            int laterTotal = 0;
            //学生早退总数
            int leaveEarly = 0;
            //奖励人数
            int awardPeoples = 0;
            //奖励次数
            int totalAwardCount = 0;
            //举手人数
            int handsupPeoples = 0;
            //举手次数
            int totalHandsupCount = 0;
            //移出次数
            int totalTickOutCount = 0;
            //授权总人数
            int totalAuthorizePeoples = 0;
            //授权总次数
            int totalAuthorizeCount = 0;
            //授权总时长
            int totalAuthorizeTime = 0;
            //学生上课时长
            long studentClassTime = 0;
            CourseClassConditionData classConditionData = new CourseClassConditionData();
            className = classInfo.getClassName();
            String classType = classInfo.getClassType();
            long closeTime = (int) totalJson.get("CloseTime") - 1200;
            long startTime = (int) totalJson.get("StartTime");
            JSONObject dataJson = (JSONObject) totalJson.get("Data");
            JSONObject inOutEndJson = (JSONObject) dataJson.get("inoutEnd");
            Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
            Iterator<Map.Entry<String, Object>> iteratorInOut = users.iterator();
            HashMap<String, Integer> answerSum = new HashMap<>();
            HashMap<String, Integer> rightSum = new HashMap<>();
            ArrayList<StudentDetail> studentDetails = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            JSONObject classJsonObject = new JSONObject();
            //应出勤学生数
            String studentTotal = "";
            int totalResponderCount = 0;
            if (classType.equals("课表课")) {
                studentTotal = classInfo.getStudentTotal();
                if (studentTotal.equals("0")) {
                    studentTotal = SystemUtil.getStudents(bbCourseId) + "";
                }
            } else {
                studentTotal = SystemUtil.getStudents(bbCourseId) + "";
            }
            classConditionData.setStudentTotal(studentTotal);
            //移出学生人数
            int tickOutPeoples = 0;
            JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
            if (tickOutJson != null) {
                tickOutPeoples = tickOutJson.size();
            }
            JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
            classConditionData.setClassName(className);
            classConditionData.setCourseId(courseID + "");
            classConditionData.setClassId(classID + "");
            classConditionData.setStartTime(startTime);
            classConditionData.setCloseTime(closeTime);
            classConditionData.setClassType(classType);
            classJsonObject.put("removeStudents", tickOutPeoples);
            int j = 0;
            while (iteratorInOut.hasNext()) {
                StudentDetail studentDetail = new StudentDetail();
                studentDetail.setCourseId(courseID + "");
                studentDetail.setClassName(className);
                studentDetail.setClassId(classID + "");
                studentDetail.setStartTime(startTime);
                studentDetail.setCloseTime(closeTime);
                String uid = iteratorInOut.next().getKey();
                userJson = (JSONObject) inOutEndJson.get(uid);
                int identity = (int) userJson.get("Identity");
                UserPhone userInfo = userPhoneMapper.findByClassinUid(uid);
                if (identity == 1 || identity == 2) {
                    if (userInfo != null) {
                        studentJson = userJson;
                        String userName = userInfo.getUserId();
                        names.add(userName);
                        String studentUid = uid;
                        String studentBBId = userInfo.getUserId();
                        studentDetail.setStudentBBId(studentBBId);
                        studentDetail.setTeacherInClassTime(teacherInTime / 60 + "");
                        studentDetail.setIdentity(identity);
                        studentDetail.setStudentUid(studentUid);
                        String studentPhone = userInfo.getPhone();
                        studentDetail.setStudentPhone(studentPhone);
                        User user = SystemUtil.getUserByUserId(studentBBId);
                        String studentName = user.getFamilyName() + user.getMiddleName() + user.getGivenName();
                        studentDetail.setStudentName(studentName);
                    } else {
                        studentDetail.setStudentPhone("无");
                        studentDetail.setStudentName("无");
                        studentDetail.setStudentBBId("无");
                        studentDetail.setIdentity(identity);
                        studentDetail.setStudentUid(uid);
                        studentDetail.setTeacherInClassTime(teacherInTime / 60 + "");
                    }
                } else if (identity == 3 || identity == 4 || identity == 193 || identity == 194) {
                    if (identity == 3) {
                        JSONArray inOutArray = (JSONArray) userJson.get("Details");
                        JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size()-1);
                        int outTime = Integer.valueOf(outObject.get("Time").toString());
                        teacherMap.put(uid, outTime);
                        JSONObject teacherJson = userJson;
                        teacherInTime = (int) teacherJson.get("Total");
                        checkinTotalTime = (int) teacherJson.get("Total");
                        if (checkinTotalTime > 0) {
                            checkin = "出勤";
                        } else {
                            checkin = "缺勤";
                        }
                        checkinTotalTime++;
                        teacherUids.add(uid);
                        classConditionData.setCheckin(checkin);
                    }

                    if (identity == 4) {
                        JSONArray inOutArray = (JSONArray) userJson.get("Details");
                        JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size()-1);
                        int outTime = Integer.valueOf(outObject.get("Time").toString());
                        assistantMap.put(uid, outTime);
                        JSONObject assistantJson = (JSONObject) inOutEndJson.get(uid);
                        assitantInTime = (int) assistantJson.get("Total");
                        assitantInTime++;
                        assistantUids.add(uid);
                    }
                    continue;
                }
                checkinStudent++;
                JSONArray inOutArray = (JSONArray) userJson.get("Details");
                JSONObject inObject = (JSONObject) inOutArray.get(0);
                long inTime = Long.valueOf(inObject.get("Time").toString());
                if (inTime > startTime) {
                    laterTotal++;
                }
                JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
                long outTime = Long.valueOf(outObject.get("Time").toString());
                if (outTime < closeTime) {
                    leaveEarly++;
                }

                int total_time = (int) userJson.get("Total");
                if (total_time != 0) {
                    checkin = "出勤";
                } else {
                    checkin = "缺勤";
                }
                studentDetail.setCheckin(checkin);
                JSONArray details = (JSONArray) userJson.get("Details");
                JSONObject inData = (JSONObject) details.get(0);
                JSONObject outData = (JSONObject) details.get(details.size() - 1);
                long firstInTime = (int) inData.get("Time");
                long endOutTime = (int) outData.get("Time");
                //学生实际上课时长
                studentClassTime = (int) userJson.get("Total");
                studentDetail.setStudentInClassTime(studentClassTime + "");
                if (firstInTime > startTime) {
                    late = "迟到";
                    studentDetail.setLate(late);
                }
                if (endOutTime < closeTime) {
                    back = "早退";
                    studentDetail.setBack(back);
                }

                if (studentDetail.getBack() == null) {
                    studentDetail.setBack("未早退");
                }
                if (studentDetail.getLate() == null) {
                    studentDetail.setLate("未迟到");
                }


                JSONObject jsonObject = new JSONObject();
                JSONObject muteJson = (JSONObject) dataJson.get("muteEnd");
                int talkTotalTime = 0;
                if (muteJson != null) {
                    JSONObject speakerJson = (JSONObject) muteJson.get("Persons");
                    if (speakerJson != null) {
                        JSONObject talkJson = (JSONObject) speakerJson.get(uid);
                        if (talkJson != null) {
                            //发言时长
                            talkTotalTime = (int) talkJson.get("Total");
                        }
                    }
                }
                jsonObject.put("speakingDuration", talkTotalTime);
                JSONObject stageJson = (JSONObject) dataJson.get("stageEnd");
                JSONObject stageUidJson = (JSONObject) stageJson.get(uid);
                int downCount = 0;
                //上台次数
                int upCount = 0;
                //下台时长
                int downTotal = 0;
                //上台时长
                int upTotal = 0;
                if (stageUidJson != null) {
                    //下台次数
                    downCount = (int) stageUidJson.get("DownCount");
                    //上台次数
                    upCount = (int) stageUidJson.get("UpCount");
                    //下台时长
                    downTotal = (int) stageUidJson.get("DownTotal");
                    //上台时长
                    upTotal = (int) stageUidJson.get("UpTotal");
                }
                jsonObject.put("upStageTimes", upCount);
                jsonObject.put("upStateDuration", upTotal);
                jsonObject.put("downStageTimes", downCount);
                jsonObject.put("downStageDuration", downTotal);

                //移出次数
                int tickOutCount = 0;
                if (tickOutJson != null) {
                    JSONArray tickoutDetailJson = (JSONArray) tickOutJson.get(uid);
                    if (tickoutDetailJson != null) {
                        tickOutCount = tickoutDetailJson.size();
                        totalTickOutCount += tickOutCount;
                    }
                }
                jsonObject.put("removeTimes", tickOutCount);
                JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
                //奖励次数
                int awardCount = 0;
                if (awardJson != null) {
                    JSONObject awardDetailJson = (JSONObject) awardJson.get(uid);
                    if (awardDetailJson != null) {
                        awardCount = (int) awardDetailJson.get("Total");
                    }
                }
                studentDetail.setAwardCount(awardCount);

                //举手次数
                int handsupCount = 0;
                JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
                if (handsupJson != null) {
                    JSONObject handsupDetailJson = (JSONObject) handsupJson.get(uid);
                    if (handsupDetailJson != null) {
                        handsupCount = (int) handsupDetailJson.get("Total");
                    }
                }
                studentDetail.setHandsupCount(handsupCount);

                //授权次数
                int authorizeCount = 0;
                //授权时长
                int authorizeTime = 0;
                if (authorizeJson != null) {
                    JSONObject authorizeDetailJson = (JSONObject) authorizeJson.get(uid);
                    if (authorizeDetailJson != null) {
                        authorizeCount = (int) authorizeDetailJson.get("Count");
                        authorizeTime = (int) authorizeDetailJson.get("Total");
                        totalAuthorizeCount += authorizeCount;
                        totalAuthorizeTime += authorizeTime;
                        if (authorizeCount > 0) {
                            totalAuthorizePeoples++;
                        }
                    }
                }
                studentDetail.setAuthorizeCount(authorizeCount);
                jsonObject.put("authorizeTotalDuration", authorizeTime);

                JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
                int responderUseCount = 0;
                if (responderJson != null) {
                    responderUseCount = (int) responderJson.get("Count");
                }
                jsonObject.put("responderUseTimes", responderUseCount);
                //抢答次数
                int responderCount = 0;
                //使用抢答器抢中次数
                int responderSurse = 0;
                if (responderJson != null) {
                    JSONObject responderPersonsJson = (JSONObject) responderJson.get("Persons");
                    JSONObject responderDetailJson = (JSONObject) responderPersonsJson.get(uid);
                    if (responderDetailJson != null) {
                        responderCount = (int) responderDetailJson.get("Count");
                        responderSurse = (int) responderDetailJson.get("SCount");
                    }
                }
                jsonObject.put("responderTimes", responderCount);
                jsonObject.put("responderAnswerTimes", responderSurse);

                JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
                if (answerJson != null) {
                    JSONArray answerDetailArray = (JSONArray) answerJson.get("Answers");
                    if (answerDetailArray != null) {
                        for (Object o : answerDetailArray) {
                            JSONObject anserDetailJson = (JSONObject) o;
                            String correctItems = (String) anserDetailJson.get("CorrectItems");
                            JSONObject studentAnswerJson = (JSONObject) anserDetailJson.get(uid);
                            if (studentAnswerJson != null) {
                                String selectedItem = (String) studentAnswerJson.get("SelectedItem");
                                if (correctItems.equals(selectedItem)) {
                                    if (rightSum.get(uid) != null) {
                                        Integer rightCount = rightSum.get(uid);
                                        rightSum.put(uid, ++rightCount);
                                    } else {
                                        rightSum.put(uid, 1);
                                    }
                                } else {
                                    if (rightSum.get(uid) == null) {
                                        rightSum.put(uid, 0);
                                    }
                                }
                                if (answerSum.get(uid) != null) {
                                    Integer anserCount = answerSum.get(uid);
                                    answerSum.put(uid, ++anserCount);
                                } else {
                                    answerSum.put(uid, 1);
                                }
                            }
                        }
                    }
                }
                //摄像头打开时长
                int cameraTotalTime = 0;
                JSONObject equipmentsJson = (JSONObject) dataJson.get("equipmentsEnd");
                if (equipmentsJson != null) {
                    JSONObject equipmentsDetailJson = (JSONObject) equipmentsJson.get(uid);
                    if (equipmentsDetailJson != null) {
                        JSONObject cameraJson = (JSONObject) equipmentsDetailJson.get("Camera");
                        cameraTotalTime = (int) cameraJson.get("Total");
                    }
                }
                jsonObject.put("cameraDuration", cameraTotalTime);
                String json = jsonObject.toJSONString();
                studentDetail.setJson(json);
                if (studentDetail.getStudentUid() != null) {
                    studentDetails.add(studentDetail);
                }
            }

            JSONObject teacherJson;
            String firstTeacherUid = teacherUids.get(0);
            teacherJson = (JSONObject) inOutEndJson.get(firstTeacherUid);
            JSONArray details = (JSONArray) teacherJson.get("Details");
            JSONObject inData = (JSONObject) details.get(0);


            long firstInTime = (int) inData.get("Time");
            if (firstInTime > startTime) {
                late = "迟到";
                classConditionData.setLate(late);
            }
            Map<String, Integer> sortTeacherMap = SystemUtil.sortMapByValue(teacherMap);
            if (teacherUids.size() > 1) {
                Set<String> uidSet = sortTeacherMap.keySet();
                int size = uidSet.size();
                Object[] uids = uidSet.toArray();
                String lastTeacherUid = (String)uids[size-1];
                teacherJson = (JSONObject) inOutEndJson.get(lastTeacherUid);
                details = (JSONArray) teacherJson.get("Details");
                JSONObject outData = (JSONObject) details.get(details.size() - 1);
                long endOutTime = (int) outData.get("Time");
                if (endOutTime < closeTime) {
                    back = "早退";
                    classConditionData.setBack(back);
                }
                UserPhone teacherUserPhone = userPhoneMapper.findByClassinUid(lastTeacherUid);
                if (teacherUserPhone != null) {
                    teacherPhone = teacherUserPhone.getPhone();
                    teacherBBid = teacherUserPhone.getUserId();
                } else {
                    teacherPhone = "无";
                    teacherBBid = "无";
                }

                if (classInfo != null) {
                    teacherName = classInfo.getTeacherName();
                } else {
                    teacherName = "无";
                }
            } else {
                teacherJson = (JSONObject) inOutEndJson.get(teacherUids.get(0));
                details = (JSONArray) teacherJson.get("Details");
                UserPhone teacherUserPhone = userPhoneMapper.findByClassinUid(teacherUids.get(0));
                if (teacherUserPhone != null) {
                    teacherPhone = teacherUserPhone.getPhone();
                    teacherBBid = teacherUserPhone.getUserId();
                } else {
                    teacherPhone = "无";
                    teacherBBid = "无";
                }

                if (classInfo != null) {
                    teacherName = classInfo.getTeacherName();
                } else {
                    teacherName = "无";
                }
                JSONObject outData = (JSONObject) details.get(details.size() - 1);
                long endOutTime = (int) outData.get("Time");
                if (endOutTime < closeTime) {
                    back = "早退";
                    classConditionData.setBack(back);
                }
            }

            if (classConditionData.getBack() == null) {
                classConditionData.setBack("未早退");
            }

            if (classConditionData.getLate() == null) {
                classConditionData.setLate("未迟到");
            }


            Map<String, Integer> sortAssistantMap = SystemUtil.sortMapByValue(assistantMap);

            if (assistantUids.size() > 1) {
                Set<String> uidSet = sortAssistantMap.keySet();
                int size = uidSet.size();
                Object[] uids = uidSet.toArray();
                String lastAssistantUid = (String)uids[size-1];
                UserPhone assistantUserPhone = userPhoneMapper.findByClassinUid(lastAssistantUid);
                if (assistantUserPhone != null) {
                    assistantPhone = assistantUserPhone.getPhone();
                } else {
                    assistantPhone = "无";
                }

                if (classInfo != null) {
                    assistantName = classInfo.getAssistantName();
                } else {
                    assistantName = "无";
                }

            } else {
                if (assistantUids.size() == 1) {
                    UserPhone assistantUserPhone = userPhoneMapper.findByClassinUid(assistantUids.get(0));
                    if (assistantUserPhone != null) {
                        assistantPhone = assistantUserPhone.getPhone();
                    } else {
                        assistantPhone = "无";
                    }

                    if (classInfo != null) {
                        assistantName = classInfo.getAssistantName();
                    } else {
                        assistantName = "无";
                    }
                } else {
                    assistantPhone = "无";
                    assistantName = "无";
                }
            }

            JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
            if (awardJson != null) {
                awardPeoples = awardJson.size();
            }
            if (awardJson != null) {
                Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
                while (awardIterator.hasNext()) {
                    String uid = awardIterator.next().getKey();
                    JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
                    int total = (int) uidJsonArray.get("Total");
                    totalAwardCount += total;
                }
            }

            JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
            if (handsupJson != null) {
                handsupPeoples = handsupJson.size();
            }

            if (handsupJson != null) {
                Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
                while (handsupIterator.hasNext()) {
                    String uid = handsupIterator.next().getKey();
                    JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
                    int total = (int) uidJsonArray.get("Total");
                    totalHandsupCount += total;
                }
            }

            JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
            int answerCount = 0;
            //答题器平均正确率
            BigDecimal averageAccuracy = new BigDecimal(0);
            if (answerJson != null) {
                answerCount = (int) answerJson.get("Count");
                averageAccuracy = (BigDecimal) answerJson.get("AverageAccuracy");
            }

            classConditionData.setAuthorizePeoples(totalAuthorizePeoples);
            classConditionData.setAuthorizeCount(totalAuthorizeCount);
            classConditionData.setAnswerCount(answerCount);
            classConditionData.setAverageAccuracy(averageAccuracy + "");
            classConditionData.setHandsupPeoples(handsupPeoples);
            classConditionData.setHandsupCount(totalHandsupCount);
            classConditionData.setAwardPeoples(awardPeoples);
            classConditionData.setAwardCount(totalAwardCount);
            classConditionData.setAssistantPhone(assistantPhone);
            if (assistantName != null && assistantName.equals("")) {
                assistantName = "无";
            }
            if (assistantPhone.equals("")) {
                assistantPhone = "无";
            }
            if (teacherBBid.equals("")) {
                teacherBBid = "无";
            }
            if (teacherPhone.equals("")) {
                teacherPhone = "无";
            }
            if (teacherName.equals("")) {
                teacherName = "无";
            }

            classConditionData.setAssistantName(assistantName);
            classConditionData.setAssistantPhone(assistantPhone);
            classConditionData.setTeacherPhone(teacherPhone);
            classConditionData.setTeacherBBId(teacherBBid);
            classConditionData.setTeacherName(teacherName);
            classConditionData.setTeacheInClassTime(String.format("%.2f", (double) teacherInTime / 60));
            classConditionData.setAssistantInClassTime(assitantInTime + "");
            classConditionData.setCheckinStudent(checkinStudent);
            classConditionData.setLaterTotal(laterTotal);
            classConditionData.setLeaveEarly(leaveEarly);


            JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
            if (responderJson != null) {
                totalResponderCount = (int) responderJson.get("Count");
            }
            classJsonObject.put("responderTimes", totalResponderCount);

            InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
            Properties properties = new Properties();
            properties.load(resource);
            String textFile = properties.getProperty("textFile");
            String av = properties.getProperty("av");
            JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
            //文本课件使用时长
            int textCoursewareTime = 0;
            //文本课件使用数量
            int textCoursewareCount = 0;
            //音视频课件使用时长
            int avCoursewareTime = 0;
            //音视频课件使用数量
            int avCoursewareCount = 0;
            LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
            LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
            if (coursewareJson != null) {
                JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
                if (coursewareArray != null) {
                    for (Object file : coursewareArray) {
                        JSONObject fileJson = (JSONObject) file;
                        String fileName = (String) fileJson.get("FileName");
                        String format = fileName.substring(fileName.lastIndexOf(".") + 1);
                        int endTime = (int) fileJson.get("EndTime");
                        int beginTime = (int) fileJson.get("StartTime");
                        int duration = endTime - beginTime;
                        if (av.contains(format)) {
                            avCoursewareCount++;
                            avCoursewareTime += duration;
                            avFileNames.add(avCoursewareCount + "");
                        } else if (textFile.contains(format)) {
                            textCoursewareCount++;
                            textFileNames.add(textCoursewareCount + "");
                            textCoursewareTime += duration;
                        }
                    }

                    avCoursewareCount = avFileNames.size();
                    textCoursewareCount = textFileNames.size();
                }
            }

            JSONObject muteJson = (JSONObject) dataJson.get("muteEnd");
            int allMuteCount = 0;
            int allMuteTotalTime = 0;
            if (muteJson != null) {
                JSONObject muteAllJson = (JSONObject) muteJson.get("MuteAll");
                if (muteAllJson != null) {
                    //全体静音次数
                    if (muteAllJson.get("Count") != null) {
                        allMuteCount = (int) muteAllJson.get("Count");
                    }

                    if (muteAllJson.get("Total") != null) {
                        //全体静音总时长
                        allMuteTotalTime = (int) muteAllJson.get("Total");
                    }
                }
            }


            JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
            //屏幕共享时长
            int screenshareTime = 0;
            //屏幕共享次数
            int screenshareCount = 0;
            if (screenshareJson != null) {
                screenshareTime = (int) screenshareJson.get("Total");
                screenshareCount = (int) screenshareJson.get("Count");
            }

            JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
            //定时器次数
            int timerCount = 0;
            //计时器次数
            int computeTimerCount = 0;
            if (timerJson != null) {
                timerCount = (int) timerJson.get("Count");
                computeTimerCount = (int) timerJson.get("Timing_Count");
            }

            JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
            //骰子次数
            int diceCount = 0;
            if (diceJson != null) {
                diceCount = (int) diceJson.get("Count");
            }

            JSONObject totalResponderJson = (JSONObject) dataJson.get("responderEnd");

            //抢答器次数
            int responderCount = 0;
            if (totalResponderJson != null) {
                responderCount = (int) totalResponderJson.get("Count");
            }

            JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");

            //小黑板使用次数
            int smallboardCount = 0;
            //小黑板使用时长
            int totalTime = 0;
            if (smallboardJson != null) {
                smallboardCount = (int) smallboardJson.get("Count");
                totalTime = (int) smallboardJson.get("Total");
            }

            classJsonObject.put("blackboardTimes", smallboardCount);
            classJsonObject.put("blackboardTotalDuration", totalTime);
            classJsonObject.put("responderTimes", responderCount);
            classJsonObject.put("diceTimes", diceCount);
            classJsonObject.put("countDownTimes", timerCount);
            classJsonObject.put("deskShareTimes", screenshareCount);
            classJsonObject.put("deskShareTotalDuration", screenshareTime);
            classJsonObject.put("muteAllTimes", allMuteCount);
            classJsonObject.put("muteAllTotallDuration", allMuteTotalTime);
            classJsonObject.put("removeStudents", tickOutPeoples);
            classJsonObject.put("authorizeTotalDuration", (double) (totalAuthorizeTime));
            classJsonObject.put("removeStudentTimes", totalTickOutCount);
            classJsonObject.put("textFiles", textCoursewareCount);
            classJsonObject.put("textFileTotalDuration", textCoursewareTime);
            classJsonObject.put("audioVideoFiles", avCoursewareCount);
            classJsonObject.put("audioVideoTotalDuration", avCoursewareTime);

            classConditionData.setJson(classJsonObject.toJSONString());
            classConditionMapper.saveClassCondition(classConditionData);

            List<String> studentNames = SystemUtil.getBBStudentName(bbCourseId);
//            Collections.sort(studentNames);
//            for (String userName : studentNames) {
//                if (!names.contains(userName)){
//                int i = Collections.binarySearch(studentNames, userName);
//                if(i<0) {
            List<String> studentList = SearchUtil.getDifferListByMap(names, studentNames);
            for (String userName : studentList) {
                StudentDetail studentDetail = new StudentDetail();
                studentDetail.setStudentPhone("无");
                studentDetail.setStudentUid("无");
                studentDetail.setStudentInClassTime("0");
                studentDetail.setCourseId(courseID + "");
                studentDetail.setClassId(classID + "");
                studentDetail.setClassName(className);
                studentDetail.setCloseTime(closeTime);
                studentDetail.setStartTime(startTime);
                User user = SystemUtil.getUserByUserId(userName);
                studentDetail.setStudentBBId(userName);
                if (user != null) {
                    studentDetail.setStudentName(user.getFamilyName() + user.getMiddleName() + user.getGivenName());
                }
                studentDetail.setIdentity(1);
                studentDetail.setCheckin("缺勤");
                studentDetail.setStudentInClassTime("0");
                studentDetail.setTeacherInClassTime(teacherInTime + "");
                studentDetails.add(studentDetail);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cameraDuration", 0);
                jsonObject.put("downStageTimes", 0);
                jsonObject.put("downStageDuration", 0);
                jsonObject.put("responderTimes", 0);
                jsonObject.put("removeTimes", 0);
                jsonObject.put("responderAnswerTimes", 0);
                jsonObject.put("speakingDuration", 0);
                jsonObject.put("upStageTimes", 0);
                jsonObject.put("authorizeTotalDuration", 0);
                jsonObject.put("upStateDuration", 0);
                jsonObject.put("responderUseTimes", 0);

                String json = jsonObject.toJSONString();
                studentDetail.setJson(json);
            }

            for (StudentDetail studentDetail : studentDetails) {
                studentDetail.setTeacherInClassTime(teacherInTime + "");
                if (!answerSum.isEmpty() && !studentDetail.getCheckin().equals("缺勤")) {
                    answerSum.forEach((key, value) -> {
                        if (studentDetail.getStudentUid().equals(key)) {
                            studentDetail.setAnswerCount(value);
                        }
                    });
                }
                if (!rightSum.isEmpty() && !studentDetail.getCheckin().equals("缺勤")) {
                    rightSum.forEach((key, value) -> {
                        if (studentDetail.getStudentUid().equals(key)) {
                            studentDetail.setAnswerCorrectTimes(value);
                        }
                    });
                }

                studentDetailMapper.saveStudentDetail(studentDetail);
            }
        }
    }
}
