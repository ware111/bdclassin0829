package com.blackboard.classin.service.impl;

import blackboard.data.course.Course;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.*;
import com.blackboard.classin.mapper.*;
import com.blackboard.classin.service.IBbCourseClassinCourse;
import com.blackboard.classin.service.LabelService;
import com.blackboard.classin.service.TimerTaskService;
import com.blackboard.classin.task.ScheduledTask;
import com.blackboard.classin.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimerTaskImp implements TimerTaskService {

    private Logger log = Logger.getLogger(TimerTaskImp.class);


    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Autowired
    private SystemRegistryMapper systemRegistryMapper;

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private ClassScheduleDataMapper classScheduleDataMapper;

    @Autowired
    private IBbCourseClassinCourse classinCourseServuce;

    @Autowired
    private LabelService labelService;

    @Override
    public void scheduleTask() throws MessagingException, InterruptedException, IOException {
        String wrongDataPath = System.getProperty("user.dir");
        File file = new File(wrongDataPath);
        File[] files = file.listFiles();
        ///创建成功后的课节再次重试计数
        for (File temp : files) {
            String name = temp.getName();
            if (name.contains("CreateClassResult")) {
                temp.delete();
            }
        }
        String fileName = System.getProperty("user.dir") + "/CreateClassResult" + System.currentTimeMillis() + ".txt";
        String emailMsg = "";
        HashMap<String, String> paraMap = new HashMap<>();
        List<ClassBean> list = null;
        String data = HttpClient.doGet("https://coursehqy.pku.edu.cn/v2/schedule/get-course-list?search_time=today");
        String scheduleTime = TimeStampUtil.getTodayTime();
        if (!data.equals("timeout")) {
            ClassScheduleBean classScheduleBean = JSONObject.parseObject(data, ClassScheduleBean.class);
            if (classScheduleBean != null) {
                list = classScheduleBean.getList();
                if (list != null) {
                    int k = -1;
                    String sID = "SID=" + Constants.SID;
                    String classinUid = "";
                    while (k < list.size()) {
                        k++;
                        if (k >= list.size()){
                            break;
                        }
                        String result = classScheduleDataMapper.getCreateClassStatus(list.get(k).getSub_id());
                        if (result != null && result.equals("success")){
                            continue;
                        }
                        classScheduleDataMapper.deleteFailureDataBySubId(list.get(k).getSub_id());
                        Course bb_course = SystemUtil.getCourseByCourseId(list.get(k).getKcwybm());
                        if (bb_course != null) {
                            UserPhone userInfo = userPhoneMapper.findPhoneByUserId(list.get(k).getTeacher());
                            if (userInfo != null) {
                                String teacherPhone = userInfo.getPhone();
                                classinUid = userInfo.getClassinUid();
                                if (classinUid == null) {
                                    long currentCreateClassTime = System.currentTimeMillis() / 1000;
                                    String parma1 = "SID=" + Constants.SID;
                                    String parma2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
                                    String parma3 = "timeStamp=" + currentCreateClassTime;
                                    String param_nickname = "nickname=" + list.get(k).getTeacher_name();
                                    String param_pwd = "password=" + "password";
                                    String param_telephone = "telephone=" + userInfo.getPhone();

                                    StringBuilder strsBuilder = new StringBuilder();
                                    strsBuilder.append(parma1).append("&").append(parma2).append("&").append(parma3).append("&").append(param_telephone)
                                            .append("&").append(param_nickname).append("&").append(param_pwd).append("&");

                                    String classin_register_url = systemRegistryMapper.getURLByKey("classin_register_url");
                                    String resultRegisterMapStr = HttpClient.doPost(classin_register_url, strsBuilder.toString());
                                    log.info("resultRegisterMap is >>>" + resultRegisterMapStr);
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    Map<String, Object> resultRegisterMap = new HashMap<String, Object>();
                                    if (resultRegisterMapStr != null && !"".equals(resultRegisterMapStr)) {
                                        resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
                                        //解析返回的数据
                                        Map<String, Object> errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
                                        String errno = errorInfo.get("errno").toString();
                                        String error = errorInfo.get("error").toString();
                                        if ("1".equals(errno) || "135".equals(errno)) {
                                            classinUid = resultRegisterMap.get("data").toString();
                                            userInfo.setClassinUid(classinUid);
                                            userPhoneMapper.updatePhone(userInfo);
                                        } else {
                                            log.info(">>>>>>>>>>>>>>>>>>>classin get uid fail");
                                            paraMap.put("content", list.get(k).getSub_id());
                                            paraMap.put("result", "fail");
                                            paraMap.put("reason", error);
                                            paraMap.put("courseId", list.get(k).getKcwybm());
                                            paraMap.put("yearDate", scheduleTime);
                                            paraMap.put("subId", list.get(k).getSub_id());
                                            classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                        }
                                    } else {
                                        log.info(">>>>>>>>>>>>>>>>>>>classin get uid URL do not return data");
                                        paraMap.put("content", list.get(k).getSub_id());
                                        paraMap.put("result", "fail");
                                        paraMap.put("reason", "没有发送收到获取uid请求后，classin后台返回的数据");
                                        paraMap.put("courseId", list.get(k).getKcwybm());
                                        paraMap.put("yearDate", scheduleTime);
                                        paraMap.put("subId", list.get(k).getSub_id());
                                        classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                    }
                                }
                                String addClassURL = systemRegistryMapper.getURLByKey("classin_addcourseclass_url");
                                String classInCourseId = "";
                                String params = "";
                                int status = 0;
                                //创建课程
                                Course bbCourse = SystemUtil.getCourseByCourseId(list.get(k).getKcwybm());
                                String bbId = list.get(k).getKcwybm();
                                long currentCreateClassTime = System.currentTimeMillis() / 1000;
                                BbCourseClassinCourse classinCourse = bbCourseClassinCourseMapper.findByCourseId(list.get(k).getKcwybm());
                                String safeKey = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
                                String timeStamp = "timeStamp=" + currentCreateClassTime;
                                if (classinCourse == null) {
                                    String courseName = "courseName=" + bbCourse.getTitle();
                                    String createCoureseURL = systemRegistryMapper.getURLByKey("classin_addcourse_url");
                                    status = 1;
                                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + courseName;
                                    String resultCourse = HttpClient.doPost(createCoureseURL, params);
                                    Map<String, Object> classInCourseMap = new HashMap<String, Object>();

                                    ObjectMapper objectMapper = new ObjectMapper();
                                    if (resultCourse != null && !resultCourse.equals("")) {
                                        try {
                                            classInCourseMap = objectMapper.readValue(resultCourse, Map.class);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //解析返回的数据
                                        Map<String, Object> errorInfo = (Map<String, Object>) classInCourseMap.get("error_info");
                                        String errno = errorInfo.get("errno").toString();
                                        String error = errorInfo.get("error").toString();
                                        if ("1".equals(errno)) {
                                            //创建classin课程成功，获取classin_course_id
                                            classInCourseId = classInCourseMap.get("data").toString();
                                            String courseId = bbCourse.getCourseId();
                                            BBCourseClassinCourseInfo courseInfo = new BBCourseClassinCourseInfo();
                                            courseInfo.setBbCourseId(list.get(k).getKcwybm());
                                            courseInfo.setClassinCourseId(classInCourseId);
                                            bbCourseClassinCourseMapper.createClassinCourseOnBbCourse(courseInfo);
                                        } else {
                                            log.info(">>>>>>>>>>>>>>>>>>>classin create course error"+errno);
                                            paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                            paraMap.put("result", "fail");
                                            paraMap.put("reason", error);
                                            paraMap.put("courseId", list.get(k).getKcwybm());
                                            paraMap.put("yearDate", TimeStampUtil.getYearDateTime(list.get(k).getCourse_begin()));
                                            paraMap.put("subId", list.get(k).getSub_id());
                                            classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                            continue;
                                        }
                                    } else{
                                        log.info(">>>>>>>>>>>>>>>>>>>classin create course URL do not return data");
                                        paraMap.put("content", list.get(k).getSub_id());
                                        paraMap.put("result", "fail");
                                        paraMap.put("reason", "没有收到创建课程后，classin后台返回的数据");
                                        paraMap.put("courseId", list.get(k).getKcwybm());
                                        paraMap.put("yearDate", scheduleTime);
                                        paraMap.put("subId", list.get(k).getSub_id());
                                        classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                        continue;
                                    }
                                }
                                if (status == 0) {
                                    classInCourseId = classinCourse.getClassinCourseId();
                                }
                                //创建课节
                                String courseNum = classinCourseClassMapper.findByBBCourseId(bbId);

                                String courseId = "courseId=" + classInCourseId;
//                                User user = SystemUtil.getUserByUserId(bean.getTeacher());
                                String courseClassName = bbCourse.getCourseId() + "_" + bbCourse.getTitle() + "_" + TimeStampUtil.getMonthDay();
                                String className = "className=" + courseClassName+"_"+TimeStampUtil.getCurrentTime();
                                long startTimeStamp = TimeStampUtil.getTimeStamp(list.get(k).getCourse_begin());
                                long endTimeStamp = TimeStampUtil.getTimeStamp(list.get(k).getCourse_over());
                                String beginTime = "beginTime=" + startTimeStamp;
                                String endTime = "endTime=" + endTimeStamp;
                                String teacherUID = "teacherUid=" + classinUid;
                                String live = "live=1";
                                String replay = "replay=1";
                                String record = "record=1";
                                String courseUniqueIdentity="courseUniqueIdentity="+TimeStampUtil.todayTimeStampToTime()+list.get(k).getSub_id();
                                params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                                        "&" + beginTime + "&" + endTime + "&" + record + "&" + replay + "&" + live + "&" + courseUniqueIdentity;

//                                        Thread.sleep(1000);
                                String resultClass = HttpClient.classSchedulePost(addClassURL, params);
                                Map<String, Object> classInClassMap = new HashMap<String, Object>();
                                ObjectMapper objectMapper1 = new ObjectMapper();
                                if (resultClass != null && !resultClass.equals("") && !resultClass.equals("timeout")) {
                                    try {
                                        classInClassMap = objectMapper1.readValue(resultClass, Map.class);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Map<String, Object> errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                                    String errno = errorInfo.get("errno").toString();
                                    String error = errorInfo.get("error").toString();

                                    if ("1".equals(errno)) {
                                        String classinCourseClassId = classInClassMap.get("data").toString();
                                        Map<String, Object> classInclassMap = new HashMap<String, Object>();
                                        Map<String, Object> moreData = (Map<String, Object>) classInclassMap.get("more_data");
                                        moreData = (Map<String, Object>) classInClassMap.get("more_data");
                                        String liveURL = moreData.get("live_url").toString();
//                                        log.info(">>>>>>>>>>>>>>>>>>回放回放" + liveURL);
//                                        String liveInfo = moreData.get("live_info").toString();
                                        String userName = userInfo.getUserId();
                                        Map<String, Object> paramMap = new HashMap<String, Object>();
                                        paramMap.put("className", courseClassName);
                                        paramMap.put("userName", userName);
                                        paramMap.put("dtCreated", TimeStampUtil.getCurrentTime());
                                        paramMap.put("assistantName", " ");
                                        paramMap.put("assistantPhone", " ");
                                        paramMap.put("classType", "课表课");
                                        paramMap.put("classinClassId", classinCourseClassId);
                                        paramMap.put("classinCourseId", classInCourseId);
                                        long totalTime = TimeStampUtil.getTimeStamp(list.get(k).getCourse_over()) - TimeStampUtil.getTimeStamp(list.get(k).getCourse_begin());
                                        long hour = totalTime / 60;
                                        long minute = totalTime % 60;
                                        paramMap.put("classTotalTime", hour + "小时" + minute + "分钟");
                                        paramMap.put("startTime", list.get(k).getCourse_begin().split(" ")[1]);
                                        paramMap.put("startTimeStamp", startTimeStamp);
                                        paramMap.put("startDate", list.get(k).getCourse_begin().split(" ")[0]);
                                        paramMap.put("teacherPhone", teacherPhone);
                                        paramMap.put("endTimeStamp", endTimeStamp);
                                        paramMap.put("teacherName", list.get(k).getTeacher_name());
                                        paramMap.put("liveURL", liveURL);
                                        paramMap.put("bbCourseId", bbId);
                                        paramMap.put("live", 1);
                                        paramMap.put("record", 1);
                                        paramMap.put("replay", 1);
                                        paramMap.put("expireStatus", "1");
                                        paramMap.put("deleteStatus", "N");
                                        paraMap.put("studentsTotal", list.get(k).getStudent_total());
                                        Map<String, String> labelMap = labelService.getLabel("课表课");
                                        String labelId=null;
                                        String labelName=null;
                                        JSONArray jsonArray = new JSONArray();
                                        JSONArray labelArray = new JSONArray();
                                        JSONObject jsonObject = new JSONObject();
                                        if (labelMap != null) {
                                            labelId = labelMap.get("LABEL_ID");
                                            labelName = labelMap.get("LABEL_NAME");
                                            JSONArray classLableIds = new JSONArray();
                                            classLableIds.add(labelId);
                                            paramMap.put("labelId", labelId);
                                            paramMap.put("labelName", labelName);
                                            jsonObject.put("classId",classinCourseClassId);
                                            labelArray.add(labelId);
                                            jsonObject.put("classLabelId",labelArray);
                                            jsonArray.add(jsonObject);
                                        }else {
                                            paramMap.put("labelId", null);
                                            paramMap.put("labelName", null);
                                        }
                                        classinCourseClassMapper.save(paramMap);

                                        if (labelId != null) {
                                            String classList = "classList="+jsonArray;
                                            String classin_label_url = systemRegistryMapper.getURLByKey("classin_label_url");
                                            params = sID + "&" + timeStamp + "&" + safeKey + "&" + courseId + "&" + classList;
                                            HttpClient.doPost(classin_label_url, params);
                                        }
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                        hashMap.put("result", "success");
                                        hashMap.put("reason", "正常执行");
                                        hashMap.put("courseId", list.get(k).getKcwybm());
                                        hashMap.put("yearDate", TimeStampUtil.getYearDateTime(list.get(k).getCourse_begin()));
                                        hashMap.put("subId", list.get(k).getSub_id());
                                        classScheduleDataMapper.saveHandledScheduleData(hashMap);


                                    } else if (errno.equals("136")) {
                                        String teacherAccount = "teacherAccount=" + teacherPhone;
                                        String teacherIdentity = "teacherName=" + list.get(k).getTeacher_name();
                                        params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherAccount + "&" + teacherIdentity;
                                        String addTeacherURL = systemRegistryMapper.getURLByKey("classin_addteacher_url");
                                        resultClass = HttpClient.doPost(addTeacherURL, params);
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        if (resultClass != null && !resultClass.equals("")) {
                                            Map resultHashMap = objectMapper.readValue(resultClass, Map.class);
                                            resultHashMap = resultHashMap;
                                            errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
                                            errno = errorInfo.get("errno").toString();
                                            error = errorInfo.get("error").toString();
                                            if (!errno.equals("1")) {
                                                paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                                paraMap.put("result", "fail");
                                                paraMap.put("reason", error);
                                                paraMap.put("courseId", list.get(k).getKcwybm());
                                                paraMap.put("yearDate", TimeStampUtil.getYearDateTime(list.get(k).getCourse_begin()));
                                                paraMap.put("subId", list.get(k).getSub_id());
                                                classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                            } else {
                                                k--;
                                                continue;
                                            }
                                        } else {
                                            log.info(">>>>>>>>>>>>>>>>>>>classin addTeacher URL do not return data");
                                            paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                            paraMap.put("result", "fail");
                                            paraMap.put("reason", "没有收到添加老师后，classin后台返回的数据");
                                            paraMap.put("courseId", list.get(k).getKcwybm());
                                            paraMap.put("yearDate", scheduleTime);
                                            paraMap.put("subId", list.get(k).getSub_id());
                                            classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                        }
                                    }  else{
                                        if (!errno.equals("398")) {
                                            log.info(">>>>>>>>>>>>>>>>>>>classin error number" + errno);
                                            paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                            paraMap.put("result", "fail");
                                            paraMap.put("reason", error);
                                            paraMap.put("courseId", list.get(k).getKcwybm());
                                            paraMap.put("yearDate", scheduleTime);
                                            paraMap.put("subId", list.get(k).getSub_id());
                                            classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                        }
                                    }
                                } else if (resultClass == null || resultClass.equals("")) {
                                    log.info(">>>>>>>>>>>>>>>>>>>classin create class URL do not return data");
                                    paraMap.put("content", list.get(k).getSub_id());
                                    paraMap.put("result", "fail");
                                    paraMap.put("reason", "没有收到创建课节后，classin后台返回的数据");
                                    paraMap.put("courseId", list.get(k).getKcwybm());
                                    paraMap.put("yearDate", scheduleTime);
                                    paraMap.put("subId", list.get(k).getSub_id());
                                    classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                } else if (resultClass.equals("timeout")) {
                                    log.info(">>>>>>>>>>>>>>>>>>>classintimeout");
                                    paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                    paraMap.put("result", "fail");
                                    paraMap.put("reason", "classintimeout");
                                    paraMap.put("courseId", list.get(k).getKcwybm());
                                    paraMap.put("yearDate", scheduleTime);
                                    paraMap.put("subId", list.get(k).getSub_id());
                                    classScheduleDataMapper.saveHandledScheduleData(paraMap);
                                }
                            } else {
                                log.info(">>>>>>>>>>>>>>>>>>>user don not register");
                                paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                                paraMap.put("result", "fail");
                                paraMap.put("reason", "此用户没有注册classin账号");
                                paraMap.put("courseId", list.get(k).getKcwybm());
                                paraMap.put("yearDate", scheduleTime);
                                paraMap.put("subId", list.get(k).getSub_id());
                                classScheduleDataMapper.saveHandledScheduleData(paraMap);
                            }
                        } else {
                            log.info(">>>>>>>>>>>>>>>>>>>bbcourse is null");
//                            scheduleTime = TimeStampUtil.getTodayTime();
                            paraMap.put("content", JSONObject.toJSON(list.get(k)).toString());
                            paraMap.put("result", "fail");
                            paraMap.put("reason", "bb平台没有课程编号为"+list.get(k).getKcwybm()+"的课程");
                            paraMap.put("courseId", list.get(k).getKcwybm());
                            paraMap.put("yearDate", scheduleTime);
                            paraMap.put("subId", list.get(k).getSub_id());
                            classScheduleDataMapper.saveHandledScheduleData(paraMap);
                        }
                    }
                } else {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>课表数据为空");
//                    scheduleTime = TimeStampUtil.getTodayTime();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("reason", "course table is empty");
                    hashMap.put("yearDate", scheduleTime);
                    classScheduleDataMapper.deleteFailureDataByReason(hashMap);
                    paraMap.put("content", "无数据");
                    paraMap.put("result", "fail");
                    paraMap.put("reason", "course table is empty");
                    paraMap.put("courseId", "课程ID为空");
                    paraMap.put("yearDate", scheduleTime);
                    paraMap.put("subId", "无");
                    classScheduleDataMapper.saveHandledScheduleData(paraMap);
                }
            }
        } else {
//            scheduleTime = TimeStampUtil.getTodayTime();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("reason", "get course data timeout");
            hashMap.put("yearDate", scheduleTime);
            classScheduleDataMapper.deleteFailureDataByReason(hashMap);
            paraMap.put("content", "无数据");
            paraMap.put("result", "fail");
            paraMap.put("reason", "get course data timeout");
            paraMap.put("courseId", "课程ID为空");
            paraMap.put("yearDate", scheduleTime);
            paraMap.put("subId", "无");
            classScheduleDataMapper.saveHandledScheduleData(paraMap);
        }
        if (data.equals("timeout")) {
            emailMsg = "fail课表数据：所有数据   " + "失败原因：课表数据接口超时";
            SendEmailUtil.sendMail(GetMailAddressUtil.getMailAddress(), emailMsg, fileName);
        } else {
            if (list == null) {
                emailMsg = "fail课表数据：所有数据   " + "失败原因：无课表数据";
                SendEmailUtil.sendMail(GetMailAddressUtil.getMailAddress(), emailMsg, fileName);
            } else {
                WriteFileUtil.writeFile(classScheduleDataMapper, fileName,Constants.SID);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("result", "fail");
                hashMap.put("yearDate", scheduleTime);
                List<Map<String, String>> failureDataList = classScheduleDataMapper.getDataByReason(hashMap);
                int failureData = failureDataList.size();
                int totalData = list.size();
                int suceessData = totalData - failureData;
                emailMsg = "创建课节成功数据：" + suceessData + "   创建课节失败数据：" + failureData + "    课节总数据：" + totalData;
                String[] addresses = GetMailAddressUtil.getMailAddress().split(",");
                for (String address : addresses) {
                    SendEmailUtil.sendMail(address, emailMsg, fileName);
                }
            }
        }
    }
}
