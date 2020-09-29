package com.blackboard.classin.service.impl;

import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.controller.ClassinCourseClassController;
import com.blackboard.classin.entity.ClassinCourseClass;
import com.blackboard.classin.entity.UserPhone;
import com.blackboard.classin.mapper.BbCourseClassinCourseMapper;
import com.blackboard.classin.mapper.ClassinCourseClassMapper;
import com.blackboard.classin.mapper.UserPhoneMapper;
import com.blackboard.classin.service.CheckinRelationDataService;
import com.blackboard.classin.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class CheckinRelationDataImp implements CheckinRelationDataService {

    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Override
    public void handleClassSituationData(String datas) throws PersistenceException, IOException {
        JSONObject totalJson = JSONObject.parseObject(datas);
        int courseID = (int) totalJson.get("CourseID");
        int classID = (int) totalJson.get("ClassID");
        ClassinCourseClass classInfo = classinCourseClassMapper.findByClassId(classID+"");
        String courseId = classInfo.getClassinCourseId();
        String className = classInfo.getClassName();
        long closeTime = (int) totalJson.get("CloseTime");
        long startTime = (int) totalJson.get("StartTime")-1200;
        String teacherBBId = classInfo.getUserName();
        User user = SystemUtil.getUserByUserId(teacherBBId);
        String teacherName = user.getFamilyName() + user.getMiddleName() + user.getGivenName();
        String teacherPhone = classInfo.getTeacherPhone();
        JSONObject dataJson = (JSONObject)totalJson.get("Data");;
        UserPhone userPhone = userPhoneMapper.findByPhone(teacherPhone);
        String teacherUid = userPhone.getClassinUid();
        JSONObject inOutEndJson = (JSONObject)dataJson.get("inoutEnd");
        String checkin="";
        String late="";
        String back="";
        JSONObject inOutUIDJson = (JSONObject)inOutEndJson.get(teacherUid);
        String total_time = (String) inOutUIDJson.get("Total");
        if (!total_time.equals("0")){
            checkin = "出勤";
        }else {
            checkin = "缺勤";
        }

        JSONArray details = (JSONArray) inOutUIDJson.get("Details");
        JSONObject inData = (JSONObject) details.get(0);
        JSONObject outData = (JSONObject) details.get(details.size()-1);
        long firstInTime = (int)inData.get("Time");
        long endOutTime = (int)outData.get("Time");
        if (firstInTime > startTime){
            late = "迟到";
        }
        if (endOutTime < closeTime){
            back="早退";
        }

        String classType = classInfo.getClassType();
        //应出勤学生数
        String studentTotal="";
        if (classType.equals("课表课")){
            studentTotal = classInfo.getStudentTotal();
        } else{
            String bbCourseId = bbCourseClassinCourseMapper.findByClassinCourseId(courseID + "");
            studentTotal = SystemUtil.getStudents(bbCourseId)+"";
        }

        Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
        //出勤学生数
        int checkinStudent = users.size();

        //学生迟到总数
        int laterTotal=0;

        //学生早退总数
        int leaveEarly=0;
        Iterator<Map.Entry<String, Object>> iterator = users.iterator();
        while (iterator.hasNext()){
            String uid = iterator.next().getKey();
            if (uid.equals(teacherUid)){
                continue;
            } else {
                JSONObject inOutJson = (JSONObject)inOutEndJson.get(uid) ;
                JSONArray inOutArray = (JSONArray) inOutJson.get("Details");
                JSONObject inObject = (JSONObject) inOutArray.get(0);
                long inTime = Long.valueOf(inObject.get("Time").toString());
                if (inTime > startTime){
                    laterTotal++;
                }
                JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
                long outTime = Long.valueOf(outObject.get("Time").toString());
                if (outTime < closeTime){
                    leaveEarly++;
                }
            }
        }

        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
        Properties properties = new Properties();
        properties.load(resource);
        String textFile = properties.getProperty("textFile");
        String av = properties.getProperty("av");

        JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
        //文本课件使用时长
        int textCoursewareTime=0;
        //文本课件使用数量
        int textCoursewareCount=0;
        //音视频课件使用时长
        int avCoursewareTime=0;
        //音视频课件使用数量
        int avCoursewareCount=0;

        LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
        LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
        JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
        for (Object file:coursewareArray){
            JSONObject fileJson = (JSONObject) file;
            String fileName = (String) fileJson.get("FileName");
            String format = fileName.substring(fileName.indexOf(".")+1);
            int endTime = (int) fileJson.get("EndTime");
            int beginTime = (int) fileJson.get("StartTime");
            int duration = endTime - beginTime;
            if (av.contains(format)) {
                avCoursewareTime+=duration;
                avFileNames.add(format);
            }else if (textFile.contains(format)){
                textFileNames.add(format);
                textCoursewareTime+=duration;
            }
        }

        avCoursewareCount = avFileNames.size();
        textCoursewareCount = textFileNames.size();


        JSONObject muteJson = (JSONObject) totalJson.get("muteEnd");
        JSONObject muteAllJson = (JSONObject) muteJson.get("MuteAll");
        //全体静音次数
        int allMuteCount = (int)muteAllJson.get("Count");
        //全体静音总时长
        int allMuteTotalTime = (int)muteAllJson.get("Total");

        JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
        //移出学生人数
        int tickOutPeoples = tickOutJson.size();

        //移出学生次数
        int tickOutCount=0;
        Iterator<Map.Entry<String, Object>> tickoutIterator = tickOutJson.entrySet().iterator();
        while (tickoutIterator.hasNext()){
            String uid = tickoutIterator.next().getKey();
            JSONArray uidJsonArray = (JSONArray) tickOutJson.get(uid);
            tickOutCount += uidJsonArray.size();
        }

        JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
        //奖励人数
        int awardPeoples = awardJson.size();
        //奖励次数
        int awardCount=0;
        Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
        while (awardIterator.hasNext()){
            String uid = awardIterator.next().getKey();
            JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
            int total = (int)uidJsonArray.get("Total");
            awardCount += total;
        }

        JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
        //举手人数
        int handsupPeoples = handsupJson.size();
        //举手次数
        int handsupCount=0;
        Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
        while (handsupIterator.hasNext()){
            String uid = handsupIterator.next().getKey();
            JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
            int total = (int)uidJsonArray.get("Total");
            handsupCount += total;
        }

        JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
        //授权人数
        int authorizePeoples = authorizeJson.size();
        //授权次数
        int authorizeCount=0;
        //授权时长
        int authorizeTime=0;
        Iterator<Map.Entry<String, Object>> authorizeIterator = authorizeJson.entrySet().iterator();
        while (authorizeIterator.hasNext()){
            String uid = authorizeIterator.next().getKey();
            JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
            int total = (int)uidJsonArray.get("Count");
            int totalTime = (int)uidJsonArray.get("Total");
            authorizeTime += totalTime;
            authorizeCount += total;
        }

        JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
        //屏幕共享时长
        int screenshareTime=0;
        //屏幕共享次数
        int screenshareCount=0;
        screenshareTime= (int)screenshareJson.get("Total");
        screenshareCount = (int)screenshareJson.get("Count");

        JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
        //定时器次数
        int timerCount=0;
        //计时器次数
        int computeTimerCount=0;
        timerCount= (int)timerJson.get("Count");
        computeTimerCount = (int)timerJson.get("Timing_Count");


        JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
        //骰子次数
        int diceCount=0;
        diceCount= (int)diceJson.get("Count");

        JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
        //抢答器次数
        int responderCount=0;
        responderCount= (int)responderJson.get("Count");

        JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
        //答题器次数
        int answerCount=0;
        answerCount= (int)answerJson.get("Count");
        double averageAccuracy=0;
        averageAccuracy= new Double(answerJson.get("AverageAccuracy")+"");

        JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");

        //小黑板使用次数
        int smallboardCount=0;
        smallboardCount= (int)smallboardJson.get("Count");
        //小黑帮使用时长
        int totalTime=0;
        totalTime= (int)smallboardJson.get("Total");
    }

    @Override
    public void handleStudentDetailData(String datas) throws IOException, PersistenceException {
        JSONObject totalJson = JSONObject.parseObject(datas);
        int courseID = (int) totalJson.get("CourseID");
        int classID = (int) totalJson.get("ClassID");
        ClassinCourseClass classInfo = classinCourseClassMapper.findByClassId(classID+"");
        String courseId = classInfo.getClassinCourseId();
        String className = classInfo.getClassName();
        long closeTime = (int) totalJson.get("CloseTime");
        long startTime = (int) totalJson.get("StartTime")-1200;
        JSONObject dataJson = (JSONObject)totalJson.get("Data");
        JSONObject inOutEndJson = (JSONObject)dataJson.get("inoutEnd");
        Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
        Iterator<Map.Entry<String, Object>> iteratorInOut = users.iterator();
        while (iteratorInOut.hasNext()){
            String uid = iteratorInOut.next().getKey();
            JSONObject studentJson = (JSONObject) inOutEndJson.get(uid);
            int identity = (int)studentJson.get("Identity");
            if (identity != 3) {
                UserPhone userInfo = userPhoneMapper.findByClassinUid(uid);
                String studentBBId = userInfo.getUserId();
                String studentPhone = userInfo.getPhone();
                User user = SystemUtil.getUserByUserId(studentBBId);
                String studentName = user.getFamilyName() + user.getMiddleName() + user.getGivenName();
            }

        }

//        String checkin="";
//        String late="";
//        String back="";
//        JSONObject inOutUIDJson = (JSONObject)inOutEndJson.get(teacherUid);
//        String total_time = (String) inOutUIDJson.get("Total");
//        if (!total_time.equals("0")){
//            checkin = "出勤";
//        }else {
//            checkin = "缺勤";
//        }
//
//        JSONArray details = (JSONArray) inOutUIDJson.get("Details");
//        JSONObject inData = (JSONObject) details.get(0);
//        JSONObject outData = (JSONObject) details.get(details.size()-1);
//        long firstInTime = (int)inData.get("Time");
//        long endOutTime = (int)outData.get("Time");
//        if (firstInTime > startTime){
//            late = "迟到";
//        }
//        if (endOutTime < closeTime){
//            back="早退";
//        }
//
//        String classType = classInfo.getClassType();
//        //应出勤学生数
//        String studentTotal="";
//        if (classType.equals("课表课")){
//            studentTotal = classInfo.getStudentTotal();
//        } else{
//            String bbCourseId = bbCourseClassinCourseMapper.findByClassinCourseId(courseID + "");
//            studentTotal = SystemUtil.getStudents(bbCourseId)+"";
//        }
//
//        Set<Map.Entry<String, Object>> users = users;
//        //出勤学生数
//        int checkinStudent = users.size();
//
//        //学生迟到总数
//        int laterTotal=0;
//
//        //学生早退总数
//        int leaveEarly=0;
//        Iterator<Map.Entry<String, Object>> iterator = users.iterator();
//        while (iterator.hasNext()){
//            String uid = iterator.next().getKey();
//            if (uid.equals(teacherUid)){
//                continue;
//            } else {
//                JSONObject inOutJson = (JSONObject)inOutEndJson.get(uid) ;
//                JSONArray inOutArray = (JSONArray) inOutJson.get("Details");
//                JSONObject inObject = (JSONObject) inOutArray.get(0);
//                long inTime = Long.valueOf(inObject.get("Time").toString());
//                if (inTime > startTime){
//                    laterTotal++;
//                }
//                JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
//                long outTime = Long.valueOf(outObject.get("Time").toString());
//                if (outTime < closeTime){
//                    leaveEarly++;
//                }
//            }
//        }
//
//        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
//        Properties properties = new Properties();
//        properties.load(resource);
//        String textFile = properties.getProperty("textFile");
//        String av = properties.getProperty("av");
//
//        JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
//        //文本课件使用时长
//        int textCoursewareTime=0;
//        //文本课件使用数量
//        int textCoursewareCount=0;
//        //音视频课件使用时长
//        int avCoursewareTime=0;
//        //音视频课件使用数量
//        int avCoursewareCount=0;
//
//        LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
//        LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
//        JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
//        for (Object file:coursewareArray){
//            JSONObject fileJson = (JSONObject) file;
//            String fileName = (String) fileJson.get("FileName");
//            String format = fileName.substring(fileName.indexOf(".")+1);
//            int endTime = (int) fileJson.get("EndTime");
//            int beginTime = (int) fileJson.get("StartTime");
//            int duration = endTime - beginTime;
//            if (av.contains(format)) {
//                avCoursewareTime+=duration;
//                avFileNames.add(format);
//            }else if (textFile.contains(format)){
//                textFileNames.add(format);
//                textCoursewareTime+=duration;
//            }
//        }
//
//        avCoursewareCount = avFileNames.size();
//        textCoursewareCount = textFileNames.size();
//
//
//        JSONObject muteJson = (JSONObject) totalJson.get("muteEnd");
//        JSONObject muteAllJson = (JSONObject) muteJson.get("MuteAll");
//        //全体静音次数
//        int allMuteCount = (int)muteAllJson.get("Count");
//        //全体静音总时长
//        int allMuteTotalTime = (int)muteAllJson.get("Total");
//
//        JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
//        //移出学生人数
//        int tickOutPeoples = tickOutJson.size();
//
//        //移出学生次数
//        int tickOutCount=0;
//        Iterator<Map.Entry<String, Object>> tickoutIterator = tickOutJson.entrySet().iterator();
//        while (tickoutIterator.hasNext()){
//            String uid = tickoutIterator.next().getKey();
//            JSONArray uidJsonArray = (JSONArray) tickOutJson.get(uid);
//            tickOutCount += uidJsonArray.size();
//        }
//
//        JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
//        //奖励人数
//        int awardPeoples = awardJson.size();
//        //奖励次数
//        int awardCount=0;
//        Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
//        while (awardIterator.hasNext()){
//            String uid = awardIterator.next().getKey();
//            JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
//            int total = (int)uidJsonArray.get("Total");
//            awardCount += total;
//        }
//
//        JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
//        //举手人数
//        int handsupPeoples = handsupJson.size();
//        //举手次数
//        int handsupCount=0;
//        Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
//        while (handsupIterator.hasNext()){
//            String uid = handsupIterator.next().getKey();
//            JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
//            int total = (int)uidJsonArray.get("Total");
//            handsupCount += total;
//        }
//
//        JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
//        //授权人数
//        int authorizePeoples = authorizeJson.size();
//        //授权次数
//        int authorizeCount=0;
//        //授权时长
//        int authorizeTime=0;
//        Iterator<Map.Entry<String, Object>> authorizeIterator = authorizeJson.entrySet().iterator();
//        while (authorizeIterator.hasNext()){
//            String uid = authorizeIterator.next().getKey();
//            JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
//            int total = (int)uidJsonArray.get("Count");
//            int totalTime = (int)uidJsonArray.get("Total");
//            authorizeTime += totalTime;
//            authorizeCount += total;
//        }
//
//        JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
//        //屏幕共享时长
//        int screenshareTime=0;
//        //屏幕共享次数
//        int screenshareCount=0;
//        screenshareTime= (int)screenshareJson.get("Total");
//        screenshareCount = (int)screenshareJson.get("Count");
//
//        JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
//        //定时器次数
//        int timerCount=0;
//        //计时器次数
//        int computeTimerCount=0;
//        timerCount= (int)timerJson.get("Count");
//        computeTimerCount = (int)timerJson.get("Timing_Count");
//
//
//        JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
//        //骰子次数
//        int diceCount=0;
//        diceCount= (int)diceJson.get("Count");
//
//        JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
//        //抢答器次数
//        int responderCount=0;
//        responderCount= (int)responderJson.get("Count");
//
//        JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
//        //答题器次数
//        int answerCount=0;
//        answerCount= (int)answerJson.get("Count");
//        double averageAccuracy=0;
//        averageAccuracy= new Double(answerJson.get("AverageAccuracy")+"");
//
//        JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");
//
//        //小黑板使用次数
//        int smallboardCount=0;
//        smallboardCount= (int)smallboardJson.get("Count");
//        //小黑帮使用时长
//        int totalTime=0;
//        totalTime= (int)smallboardJson.get("Total");
    }
}
