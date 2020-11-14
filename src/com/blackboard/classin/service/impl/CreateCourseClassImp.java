package com.blackboard.classin.service.impl;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.controller.NewClassinCourseClassController;
import com.blackboard.classin.entity.BBCourseClassinCourseInfo;
import com.blackboard.classin.entity.BbCourseClassinCourse;
import com.blackboard.classin.entity.SystemRegistry;
import com.blackboard.classin.entity.UserPhone;
import com.blackboard.classin.mapper.BbCourseClassinCourseMapper;
import com.blackboard.classin.mapper.ClassinCourseClassMapper;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import com.blackboard.classin.mapper.UserPhoneMapper;
import com.blackboard.classin.service.CreateCourseClassService;
import com.blackboard.classin.service.LabelService;
import com.blackboard.classin.util.HttpClient;
import com.blackboard.classin.util.SystemUtil;
import com.blackboard.classin.util.TimeStampUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CreateCourseClassImp implements CreateCourseClassService {

    private Logger log = Logger.getLogger(CreateCourseClassImp.class);

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private SystemRegistryMapper systemRegistryMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private LabelService labelService;

    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Override
    public void createCourseClass(HttpServletRequest request, HttpServletResponse response, String className, String classType, String startDate, String startTime,
                                  String hour, String minute, String teacher, String assistantTeacher,
                                  String bbCourseId, String isLive, String isRecord, String isReplay,
                                  String startTimeStamp) throws PersistenceException, IOException {
        Properties properties = new Properties();
        String errorMsg = "";
        String url = "";
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        String[] teacherInfo = teacher.split(" ");
        String assistantTeacherUID = "";
        String[] assistantTeacherInfo = null;
        String assistantTeacherPhone = "";
        if (!assistantTeacher.equals("请选择助教教师")) {
            assistantTeacherInfo = assistantTeacher.split(" ");
            assistantTeacherPhone = assistantTeacherInfo[1];
            assistantTeacherUID = userPhoneMapper.findByPhone(assistantTeacherPhone).getClassinUid();   //firstuid1111
            if (assistantTeacherUID == null) {
                SystemUtil.getUid(userPhoneMapper.findByPhone(assistantTeacherPhone), systemRegistryMapper, userPhoneMapper);
                assistantTeacherUID = userPhoneMapper.findByPhone(assistantTeacherPhone).getClassinUid();
            }
        }
        String teacherPhone = teacherInfo[1];
        String teacherUID = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();    //seconduid1111
        if (teacherUID == null) {
            SystemUtil.getUid(userPhoneMapper.findByPhone(teacherPhone), systemRegistryMapper, userPhoneMapper);
            teacherUID = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();
        }
        String createCoureseURL = systemRegistryMapper.getURLByKey("classin_addcourse_url");
        String addClassURL = systemRegistryMapper.getURLByKey("classin_addcourseclass_url");
        long currentCreateClassTime = System.currentTimeMillis() / 1000;
        String sID = "SID=" + Constants.SID;
        String safeKey = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String timeStamp = "timeStamp=" + currentCreateClassTime;
        String classInCourseId = "";
        String params = "";
        int status = 0;
        //创建课程
        Course bbCourse = SystemUtil.getCourseById(bbCourseId);
        //bb课程Id
        String bbId = bbCourse.getCourseId();
        bbSession.setGlobalKey("bbCourseId", bbId);
        String courseName = "courseName=" + bbCourse.getTitle();
        BbCourseClassinCourse course = bbCourseClassinCourseMapper.findByCourseId(bbCourse.getCourseId());
        if (course == null) {
            status = 1;
            params = sID + "&" + safeKey + "&" + timeStamp + "&" + courseName;
            String resultCourse = HttpClient.doPost(createCoureseURL, params);
            Map<String, Object> classInCourseMap = new HashMap<String, Object>();

            ObjectMapper objectMapper = new ObjectMapper();
            if (resultCourse != null && !resultCourse.equals("")) {
                classInCourseMap = objectMapper.readValue(resultCourse, Map.class);
                //解析返回的数据
                Map<String, Object> errorInfo = (Map<String, Object>) classInCourseMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                bbSession.setGlobalKey("courseErrno", errno);
                bbSession.setGlobalKey("courseError", error);
                if ("1".equals(errno)) {
                    //创建classin课程成功，获取classin_course_id
                    classInCourseId = classInCourseMap.get("data").toString();
                    bbSession.setGlobalKey("classinCourseId", classInCourseId);
                    Map<String, String> paramMap = new HashMap<String, String>();
                    String courseId = bbCourse.getCourseId();
                    BBCourseClassinCourseInfo courseInfo = new BBCourseClassinCourseInfo();
                    courseInfo.setBbCourseId(courseId);
                    courseInfo.setClassinCourseId(classInCourseId);
                    bbCourseClassinCourseMapper.createClassinCourseOnBbCourse(courseInfo);
                } else {
                    //创建课程课程失败
                    errorMsg = "创建课程失败";
                    url = "/classin/createClassinClass";
                    properties.put("errorMsg", errorMsg);
                    properties.put("url", url);
                }
            }
        }
        if (status == 0) {
            classInCourseId = course.getClassinCourseId();
        }
        String courseId = "courseId=" + classInCourseId;
        String courseClassName = className;
        className = "className=" + className;
        long endTimeStamp = new Integer(hour) * 60 * 60 + new Integer(minute) * 60 + Long.valueOf(startTimeStamp);
        String beginTime = "beginTime=" + startTimeStamp;
        String endTime = "endTime=" + endTimeStamp;
        teacherUID = "teacherUid=" + teacherUID;
        assistantTeacherUID = "assistantUid=" + assistantTeacherUID;
        String live = "live=0";
        String replay = "replay=0";
        String record = "record=0";
        if (isLive.equals("true")) {
            live = "live=1";
            record = "record=1";
        }
        if (isRecord.equals("true")) {
            record = "record=1";
        }

        if (isReplay.equals("true")) {
            replay = "replay=1";
            record = "record=1";
        }
        //创建课节
        String teacherAccount = "teacherAccount=" + bbSession.getGlobalKey("telephone");
        for (int i = 0; i <= 2; i++) {


            if (assistantTeacher.equals("请选择助教教师")) {
                if (isLive.equals("false") && isRecord.equals("false") && isReplay.equals("false")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime;
                } else if (isLive.equals("true") && isReplay.equals("false")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + live;
                } else if (isLive.equals("false") && isReplay.equals("true")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + replay;
                } else if (isLive.equals("true") && isReplay.equals("true")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + replay + "&" + live;
                } else if (isLive.equals("false") && isRecord.equals("true") && isReplay.equals("false")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record;
                }
            } else {
                if (isLive.equals("false") && isRecord.equals("false") && isReplay.equals("false")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + assistantTeacherUID;
                } else if (isLive.equals("true") && isReplay.equals("false")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + live + "&" + assistantTeacherUID;
                } else if (isLive.equals("false") && isReplay.equals("true")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + replay + "&" + assistantTeacherUID;
                } else if (isLive.equals("true") && isReplay.equals("true")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + replay + "&" + live + "&" + assistantTeacherUID;
                } else if (isLive.equals("false") && isRecord.equals("true") && isReplay.equals("false")) {
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherUID + "&" + courseId + "&" + className +
                            "&" + beginTime + "&" + endTime + "&" + record + "&" + assistantTeacherUID;
                }
            }
            String resultClass = HttpClient.doPost(addClassURL, params);
            Map<String, Object> classInClassMap = new HashMap<String, Object>();
            ObjectMapper objectMapper1 = new ObjectMapper();
            if (resultClass != null && !resultClass.equals("")) {
                classInClassMap = objectMapper1.readValue(resultClass, Map.class);
                Map<String, Object> errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                bbSession.setGlobalKey("classErrno", errno);
                bbSession.setGlobalKey("classError", error);
                //创建classin课节成功
                if ("1".equals(errno)) {
                    Instant parse = Instant.ofEpochSecond(Long.valueOf(startTimeStamp));
                    ZonedDateTime zonedDateTime = parse.atZone(ZoneId.systemDefault());
                    startDate = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    startTime = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    //设置课节ID classinId
                    String classinCourseClassId = classInClassMap.get("data").toString();
                    bbSession.setGlobalKey("classinCourseId", classInCourseId);
                    bbSession.setGlobalKey("classinClassId", classinCourseClassId);
                    Map<String, Object> classInclassMap = new HashMap<String, Object>();
                    Map<String, Object> moreData = (Map<String, Object>) classInclassMap.get("more_data");
                    moreData = (Map<String, Object>) classInClassMap.get("more_data");
                    String liveURL = moreData.get("live_url").toString();
                    String liveInfo = moreData.get("live_info").toString();
                    UserPhone userPhone = userPhoneMapper.findByPhone(teacherPhone);
                    String userName = userPhone.getUserId();
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("className", courseClassName);
                    paramMap.put("userName", userName);
                    // paramMap.put("liveInfo", liveInfo + "");
                    paramMap.put("dtCreated", TimeStampUtil.getCurrentTime());
                    ///   ClassinCourseClass classinCourseClass = new ClassinCourseClass();
                    //  CustomClassinClassInfo customClassinClassInfo = new CustomClassinClassInfo();
                    if (assistantTeacher.equals("请选择助教教师")) {
                        //    classinCourseClass.setAssistantTeacherName("无助教教师");
                        paramMap.put("assistantName", null);
                        paramMap.put("assistantPhone", null);
                    } else {
                        //   classinCourseClass.setAssistantTeacherName(assistantTeacherInfo[0]);
                        paramMap.put("assistantName", assistantTeacherInfo[0]);
                        //   classinCourseClass.setAssistantPhone(assistantTeacherInfo[1]);
                        paramMap.put("assistantPhone", assistantTeacherInfo[1]);
                    }
                    //  classinCourseClass.setClassType(classType);
                    paramMap.put("classType", classType);
                    //   classinCourseClass.setClassinClassId(classinCourseClassId);
                    paramMap.put("classinClassId", classinCourseClassId);
                    //    classinCourseClass.setClassinCourseId(classInCourseId);
                    paramMap.put("classinCourseId", classInCourseId);
                    //    classinCourseClass.setClassTotalTime(hour + "小时" + minute + "分钟");
                    paramMap.put("classTotalTime", hour + "小时" + minute + "分钟");
                    //   classinCourseClass.setStartTime(startTime);
                    paramMap.put("startTime", startTime);
                    paramMap.put("startTimeStamp", startTimeStamp);
                    //    classinCourseClass.setStartDate(startDate);
                    paramMap.put("startDate", startDate);
                    //    classinCourseClass.setTeacherPhone(teacherInfo[1]);
                    paramMap.put("teacherPhone", teacherInfo[1]);
                    //    classinCourseClass.setTimeStamp(new Long(endTimeStamp).intValue());
                    paramMap.put("endTimeStamp", new Long(endTimeStamp).intValue());
                    //    classinCourseClass.setTeacherName(teacherInfo[0]);
                    paramMap.put("teacherName", teacherInfo[0]);
                    //    classinCourseClass.setLiveURL(liveURL);
                    paramMap.put("liveURL", liveURL);
                    paramMap.put("bbCourseId", bbId);
                    if (isLive.equals("true")) {
                        //        classinCourseClass.setLive(1);
                        paramMap.put("live", 1);
                        //        classinCourseClass.setRecord(1);
                        paramMap.put("record", 1);
                    }
                    if (isRecord.equals("true")) {
                        //       classinCourseClass.setRecord(1);
                        paramMap.put("record", 1);
                    }
                    if (isReplay.equals("true")) {
                        //       classinCourseClass.setRecord(1);
                        paramMap.put("record", 1);
                        paramMap.put("replay", 1);
                    }
                    paramMap.put("expireStatus", "1");
                    paramMap.put("deleteStatus", "N");
                    paramMap.put("studentsTotal", 0);
                    Map<String, String> labelMap = labelService.getLabel(classType);
                    String labelId = null;
                    String labelName = null;
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
                        jsonObject.put("classId", classinCourseClassId);
                        labelArray.add(labelId);
                        jsonObject.put("classLabelId", labelArray);
                        jsonArray.add(jsonObject);
                    } else {
                        paramMap.put("labelId", null);
                        paramMap.put("labelName", null);
                    }
                    classinCourseClassMapper.save(paramMap);

                    if (labelId != null) {
                        String classList = "classList=" + jsonArray;
                        String classin_label_url = systemRegistryMapper.getURLByKey("classin_label_url");
                        params = sID + "&" + timeStamp + "&" + safeKey + "&" + courseId + "&" + classList;
                        resultClass = HttpClient.doPost(classin_label_url, params);
                        objectMapper1 = new ObjectMapper();
                        if (resultClass != null && !resultClass.equals("")) {
                            classInClassMap = objectMapper1.readValue(resultClass, Map.class);
                            errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                            errno = errorInfo.get("errno").toString();
                            error = errorInfo.get("error").toString();
                            //创建classin课节成功
                            if (!"1".equals(errno)) {
                                errorMsg = error;
                                url = "/classin/tips";
                                properties.put("errorMsg", errorMsg);
                                properties.put("url", url);
                            }
                        } else {
                            errorMsg = "classIn服务器未收到请求信息，请重试";
                            url = "/classin/tips";
                            properties.put("errorMsg", errorMsg);
                            properties.put("url", url);
                        }
                    }
                    url = "redirect:/classinCourseClass/goBack.do?course_id=" + bbCourseId;
                    properties.put("url", url);
                } else if (errno.equals("318") || errno.equals("136")) {
                    if (errno.equals("318")) {
                        teacherAccount = "teacherAccount=" + assistantTeacherPhone;

                    } else {
                        teacherAccount = "teacherAccount=" + teacherPhone;
                    }

                    User currentUser = SystemUtil.getCurrentUser();
                    String teacherName = "teacherName=" + currentUser.getFamilyName() + currentUser.getMiddleName() + currentUser.getGivenName();
                    params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherAccount + "&" + teacherName;
                    String addTeacherURL = systemRegistryMapper.getURLByKey("classin_addteacher_url");
                    resultClass = HttpClient.doPost(addTeacherURL, params);
                    if (resultClass != null && !resultClass.equals("")) {
                        classInClassMap = objectMapper1.readValue(resultClass, Map.class);
                        errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                        errno = errorInfo.get("errno").toString();
                        error = errorInfo.get("error").toString();
                        if (!errno.equals("1") && !errno.equals("133")) {
                            errorMsg = error;
                            url = "/classin/tips";
                            properties.put("errorMsg", errorMsg);
                            properties.put("url", url);
                        } else {
                            continue;
                        }
                    } else {
                        errorMsg = "classIn服务器未收到请求信息，请重试";
                        url = "/classin/tips";
                        properties.put("errorMsg", errorMsg);
                        properties.put("url", url);
                    }

                } else {
                    //其他错误代码
                    errorMsg = error;
                    url = "/classin/tips";
                    properties.put("errorMsg", errorMsg);
                    properties.put("url", url);
                }
            } else {//未获取到classin返回的信息
                errorMsg = "classIn服务器未收到请求信息，请重试";
                url = "/classin/tips";
                properties.put("errorMsg", errorMsg);
                properties.put("url", url);
            }
        }
    }

    @Override
    public Properties batcheCreateCourseClass(HttpServletRequest request, HttpServletResponse response, String className, String classType, String startDate, String startTime,
                                              String hour, String minute, String teacher, String assistantTeacher,
                                              String bbCourseId, String isLive, String isRecord, String isReplay,
                                              String startTimeStamp, String classAmount, String days, String currentDay, String classNameSuffix) throws PersistenceException, IOException {
        Properties properties = new Properties();
        String errorMsg = "";
        String url = "";
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        String[] teacherInfo = teacher.split(" ");
        String assistantTeacherUID = "";
        String[] assistantTeacherInfo = null;
        String assistantTeacherPhone = "";
        if (!assistantTeacher.equals("请选择助教教师")) {
            assistantTeacherInfo = assistantTeacher.split(" ");
            assistantTeacherPhone = assistantTeacherInfo[1];
            assistantTeacherUID = userPhoneMapper.findByPhone(assistantTeacherPhone).getClassinUid();   //firstuid1111
            if (assistantTeacherUID == null) {
                SystemUtil.getUid(userPhoneMapper.findByPhone(assistantTeacherPhone), systemRegistryMapper, userPhoneMapper);
                assistantTeacherUID = userPhoneMapper.findByPhone(assistantTeacherPhone).getClassinUid();
            }
        }
        String teacherPhone = teacherInfo[1];
        String teacherUID = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();    //seconduid1111
        if (teacherUID == null) {
            SystemUtil.getUid(userPhoneMapper.findByPhone(teacherPhone), systemRegistryMapper, userPhoneMapper);
            teacherUID = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();
        }
        String createCoureseURL = systemRegistryMapper.getURLByKey("classin_addcourse_url");
        String addClassURL = systemRegistryMapper.getURLByKey("classin_addcourseclassmultiple_url");
        long currentCreateClassTime = System.currentTimeMillis() / 1000;
        String sID = "SID=" + Constants.SID;
        String safeKey = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String timeStamp = "timeStamp=" + currentCreateClassTime;
        String classInCourseId = "";
        String params = "";
        int status = 0;
        //创建课程
        Course bbCourse = SystemUtil.getCourseById(bbCourseId);
        //bb课程Id
        String bbId = bbCourse.getCourseId();
        bbSession.setGlobalKey("bbCourseId", bbId);
        String courseName = "courseName=" + bbCourse.getTitle();
        BbCourseClassinCourse course = bbCourseClassinCourseMapper.findByCourseId(bbCourse.getCourseId());
        if (course == null) {
            status = 1;
            params = sID + "&" + safeKey + "&" + timeStamp + "&" + courseName;
            String resultCourse = HttpClient.doPost(createCoureseURL, params);
            Map<String, Object> classInCourseMap = new HashMap<String, Object>();

            ObjectMapper objectMapper = new ObjectMapper();
            if (resultCourse != null && !resultCourse.equals("")) {
                classInCourseMap = objectMapper.readValue(resultCourse, Map.class);
                //解析返回的数据
                Map<String, Object> errorInfo = (Map<String, Object>) classInCourseMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                bbSession.setGlobalKey("courseErrno", errno);
                bbSession.setGlobalKey("courseError", error);
                if ("1".equals(errno)) {
                    //创建classin课程成功，获取classin_course_id
                    classInCourseId = classInCourseMap.get("data").toString();
                    bbSession.setGlobalKey("classinCourseId", classInCourseId);
                    Map<String, String> paramMap = new HashMap<String, String>();
                    String courseId = bbCourse.getCourseId();
                    BBCourseClassinCourseInfo courseInfo = new BBCourseClassinCourseInfo();
                    courseInfo.setBbCourseId(courseId);
                    courseInfo.setClassinCourseId(classInCourseId);
                    bbCourseClassinCourseMapper.createClassinCourseOnBbCourse(courseInfo);
                } else {
                    //创建课程课程失败
                    errorMsg = "创建课程失败";
                    url = "/classin/createClassinClass";
                    properties.put("errorMsg", errorMsg);
                    properties.put("url", url);
                }
            }
        }
        if (status == 0) {
            classInCourseId = course.getClassinCourseId();
        }
        Integer classes = Integer.valueOf(classAmount);
        String courseId = "courseId=" + classInCourseId;
        String courseClassName = className;
        String[] courseClassNames = new String[classes];
        Integer minSuffix = Integer.valueOf(classNameSuffix);
        int maxSuffix = Integer.valueOf(classAmount) + minSuffix;
        int j = -1;
        int index = className.lastIndexOf("_");
        String subName = className.substring(0, index + 1);
        for (int i = minSuffix; i < maxSuffix; i++) {
            j++;
            courseClassNames[j] = subName + i;
        }

        String[] weekDays = days.split(",");
        String[] startTimeStamps = new String[classes];
        String[] endTimeStamps = new String[classes];
        String[] firstStartTimeStamps = new String[weekDays.length];
        String[] firstEndTimeStamps = new String[weekDays.length];
        int[] diffDays = new int[classes];
        //课节数与周课节数的余数
        int remainder = classes % weekDays.length;
        //需要新建课节周数
        int weeks = classes / weekDays.length;
        if (remainder > 0) {
            weeks = 1 + weeks;
        }
        //System.out.println("weeks"+weeks);
        int classDays = weekDays.length;
        int flag = -1;
        String date = TimeStampUtil.timeStampToDate(startTimeStamp + "");
        LocalDate localDate = LocalDate.parse(date);
        int value = localDate.getDayOfWeek().getValue();
        for (int i = 0; i < weekDays.length; i++) {
            Integer weekDay = value;
            Integer selectedWeekDay = Integer.valueOf(weekDays[i]);
            if (weekDay <= selectedWeekDay) {
                int differDay = selectedWeekDay - weekDay;
                if (i < classes) {
                    diffDays[i] = differDay;
                }
                long beginTimeStamp = Long.valueOf(startTimeStamp) + differDay * 24 * 60 * 60;
                long endTimeStamp = new Integer(hour) * 60 * 60 + new Integer(minute) * 60 + beginTimeStamp;
                firstStartTimeStamps[i] = beginTimeStamp + "";
                firstEndTimeStamps[i] = endTimeStamp + "";
//                if (i < classes) {
//                    startTimeStamps[i] = beginTimeStamp + "";
//                    endTimeStamps[i] = endTimeStamp + "";
//                }
            }
            if (weekDay > selectedWeekDay) {
                int differDay = 7 - weekDay + selectedWeekDay;
                if (i < classes) {
                    diffDays[i] = differDay;
                }
//                diffDays[i] = differDay;
                long beginTimeStamp = Long.valueOf(startTimeStamp) + differDay * 24 * 60 * 60;
                long endTimeStamp = new Integer(hour) * 60 * 60 + new Integer(minute) * 60 + beginTimeStamp;
//                startTimeStamps[i] = beginTimeStamp + "";
                firstStartTimeStamps[i] = beginTimeStamp + "";
//                endTimeStamps[i] = endTimeStamp + "";
                firstEndTimeStamps[i] = endTimeStamp + "";
            }
        }

        Arrays.sort(firstEndTimeStamps);
        Arrays.sort(firstStartTimeStamps);


        if (weekDays.length > classes) {
            for (int i = 0; i < classes; i++) {
                startTimeStamps[i] = firstStartTimeStamps[i];
                endTimeStamps[i] = firstEndTimeStamps[i];
            }
        }else {
            for (int i = 0; i < weekDays.length; i++) {
                startTimeStamps[i] = firstStartTimeStamps[i];
                endTimeStamps[i] = firstEndTimeStamps[i];
            }
        }




        if (weeks >= 2)

        {
            for (int i = 2; i <= weeks; i++) {
                for (int m = 0; m < firstStartTimeStamps.length; m++) {
                    if (i == weeks) {
                        if (remainder != 0 && m >= remainder) {
                            break;
                        }
                    }
                    flag++;
                    if (firstEndTimeStamps.length + flag >= endTimeStamps.length) {
                        break;
                    }
                    startTimeStamps[firstEndTimeStamps.length + flag] = Long.valueOf(firstStartTimeStamps[m]) + (i - 1) * 7 * 24 * 60 * 60 + "";
                    endTimeStamps[firstEndTimeStamps.length + flag] = Long.valueOf(firstEndTimeStamps[m]) + (i - 1) * 7 * 24 * 60 * 60 + "";
                }
            }
        }

//        for (int i = 0; i < firstStartTimeStamps.length; i++){
//            log.info("***************ttttt*>>>>>>>>>>*******"+ firstStartTimeStamps[i]);
//        }


        className = "className=" + className;
        String beginTime = "beginTime=" + startTimeStamp;
        String endTime = "endTime=" + 123;
        String live = "live=0";
        String replay = "replay=0";
        String record = "record=0";
        if (isLive.equals("true"))

        {
            live = "live=1";
            record = "record=1";
        }
        if (isRecord.equals("true"))

        {
            record = "record=1";
        }

        if (isReplay.equals("true"))

        {
            replay = "replay=1";
            record = "record=1";
        }


        Arrays.sort(endTimeStamps);
        Arrays.sort(startTimeStamps);

        for (String str : firstStartTimeStamps){
            log.info("**********firstStartTime*********"+str);
        }

        for (String str : startTimeStamps){
            log.info("+++++++++++++startTime++++++++++++++"+str);
        }

        JSONArray classJsonArray = new JSONArray();
        for (int i = 0; i < classes; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("className", courseClassNames[i]);
            jsonObject.put("beginTime", startTimeStamps[i]);
            jsonObject.put("endTime", endTimeStamps[i]);
            jsonObject.put("teacherUid", teacherUID);
            if (isLive.equals("true")) {
                jsonObject.put("live", 1);
                jsonObject.put("record", 1);
            }
            if (isRecord.equals("true")) {
                jsonObject.put("record", 1);
            }

            if (isReplay.equals("true")) {
                jsonObject.put("replay", 1);
                jsonObject.put("record", 1);
            }
            if (!assistantTeacher.equals("请选择助教教师")) {
                jsonObject.put("assistantUid", assistantTeacherUID);

            }
            classJsonArray.add(jsonObject);
        }

        params = sID + "&" + safeKey + "&" + timeStamp + "&" + courseId + "&" + "classJson=" + classJsonArray.toJSONString();
        String teacherAccount = "teacherAccount=" + bbSession.getGlobalKey("telephone");
        //创建课节
        for (int i = 0; i <= 2; i++) {
            String resultClass = HttpClient.doPost(addClassURL, params);
            Map<String, Object> classInClassMap = new HashMap<String, Object>();
            ObjectMapper objectMapper1 = new ObjectMapper();
            if (resultClass != null && !resultClass.equals("")) {
                JSONObject result = (JSONObject) JSONObject.parse(resultClass);
                JSONArray dataArray = (JSONArray) result.get("data");
                int count = -1;
                for (Object object : dataArray) {
                    count++;
                    JSONObject resultObject = (JSONObject) object;
                    String errno = (Integer) resultObject.get("errno") + "";
                    Map<String, Object> errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                    String error = (String) resultObject.get("error");
                    bbSession.setGlobalKey("classErrno", errno);
                    bbSession.setGlobalKey("classError", error);
                    //创建classin课节成功
                    if ("1".equals(errno)) {
                        Instant parse = Instant.ofEpochSecond(Long.valueOf(startTimeStamp));
                        ZonedDateTime zonedDateTime = parse.atZone(ZoneId.systemDefault());
                        startDate = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        startTime = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                        String liveURL = "";
                        String liveInfo = "";
                        UserPhone userPhone = userPhoneMapper.findByPhone(teacherPhone);
                        String userName = userPhone.getUserId();
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        JSONObject moreData = (JSONObject) resultObject.get("more_data");
                        liveURL = (String) moreData.get("live_url");
                        String classId = (Integer) resultObject.get("data") + "";
                        courseClassName = (String) resultObject.get("className");
                        paramMap.put("classinClassId", classId);
                        paramMap.put("className", courseClassName);
                        paramMap.put("userName", userName);
                        // paramMap.put("liveInfo", liveInfo + "");
                        paramMap.put("dtCreated", TimeStampUtil.getCurrentTime());
                        ///   ClassinCourseClass classinCourseClass = new ClassinCourseClass();
                        //  CustomClassinClassInfo customClassinClassInfo = new CustomClassinClassInfo();
                        if (assistantTeacher.equals("请选择助教教师")) {
                            //    classinCourseClass.setAssistantTeacherName("无助教教师");
                            paramMap.put("assistantName", null);
                            paramMap.put("assistantPhone", null);
                        } else {
                            //   classinCourseClass.setAssistantTeacherName(assistantTeacherInfo[0]);
                            paramMap.put("assistantName", assistantTeacherInfo[0]);
                            //   classinCourseClass.setAssistantPhone(assistantTeacherInfo[1]);
                            paramMap.put("assistantPhone", assistantTeacherInfo[1]);
                        }
                        //  classinCourseClass.setClassType(classType);
                        paramMap.put("classType", classType);
                        //   classinCourseClass.setClassinClassId(classinCourseClassId);
                        //    classinCourseClass.setClassinCourseId(classInCourseId);
                        paramMap.put("classinCourseId", classInCourseId);
                        //    classinCourseClass.setClassTotalTime(hour + "小时" + minute + "分钟");
                        paramMap.put("classTotalTime", hour + "小时" + minute + "分钟");
                        //   classinCourseClass.setStartTime(startTime);
                        paramMap.put("startTime", startTime);
                        paramMap.put("startTimeStamp", startTimeStamps[count]);
                        //    classinCourseClass.setStartDate(startDate);
                        paramMap.put("startDate", TimeStampUtil.timeStampToDate(startTimeStamps[count]));
                        //    classinCourseClass.setTeacherPhone(teacherInfo[1]);
                        paramMap.put("teacherPhone", teacherInfo[1]);
                        //    classinCourseClass.setTimeStamp(new Long(endTimeStamp).intValue());
                        paramMap.put("endTimeStamp", endTimeStamps[count]);
                        //    classinCourseClass.setTeacherName(teacherInfo[0]);
                        paramMap.put("teacherName", teacherInfo[0]);
                        //    classinCourseClass.setLiveURL(liveURL);
                        paramMap.put("liveURL", liveURL);
                        paramMap.put("bbCourseId", bbId);
                        if (isLive.equals("true")) {
                            //        classinCourseClass.setLive(1);
                            paramMap.put("live", 1);
                            //        classinCourseClass.setRecord(1);
                            paramMap.put("record", 1);
                        }
                        if (isRecord.equals("true")) {
                            //       classinCourseClass.setRecord(1);
                            paramMap.put("record", 1);
                        }
                        if (isReplay.equals("true")) {
                            //       classinCourseClass.setRecord(1);
                            paramMap.put("record", 1);
                            paramMap.put("replay", 1);
                        }
                        paramMap.put("expireStatus", "0");
                        paramMap.put("deleteStatus", "N");
                        paramMap.put("studentsTotal", 0);
                        Map<String, String> labelMap = labelService.getLabel(classType);
                        String labelId = null;
                        String labelName = null;
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
                            jsonObject.put("classId", "");
                            labelArray.add(labelId);
                            jsonObject.put("classLabelId", labelArray);
                            jsonArray.add(jsonObject);
                        } else {
                            paramMap.put("labelId", null);
                            paramMap.put("labelName", null);
                        }
                        classinCourseClassMapper.save(paramMap);

                        if (labelId != null) {
                            String classList = "classList=" + jsonArray;
                            String classin_label_url = systemRegistryMapper.getURLByKey("classin_label_url");
                            params = sID + "&" + timeStamp + "&" + safeKey + "&" + courseId + "&" + classList;
                            resultClass = HttpClient.doPost(classin_label_url, params);
                            objectMapper1 = new ObjectMapper();
                            if (resultClass != null && !resultClass.equals("")) {
                                classInClassMap = objectMapper1.readValue(resultClass, Map.class);
                                errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                                errno = errorInfo.get("errno").toString();
                                error = errorInfo.get("error").toString();
                                //创建classin课节成功
                                if (!"1".equals(errno)) {
                                    errorMsg = error;
                                    url = "/classin/tips";
                                    properties.put("errorMsg", errorMsg);
                                    properties.put("url", url);
                                }
                            } else {
                                errorMsg = "classIn服务器未收到请求信息，请重试";
                                url = "/classin/tips";
                                properties.put("errorMsg", errorMsg);
                                properties.put("url", url);
                            }
                        }
                        url = "redirect:/classinCourseClass/goBack.do?course_id=" + bbCourseId;
                        properties.put("url", url);
                    } else if (errno.equals("318") || errno.equals("136")) {
                        if (errno.equals("318")) {
                            teacherAccount = "teacherAccount=" + assistantTeacherPhone;

                        } else {
                            teacherAccount = "teacherAccount=" + teacherPhone;
                        }

                        User currentUser = SystemUtil.getCurrentUser();
                        String teacherName = "teacherName=" + currentUser.getFamilyName() + currentUser.getMiddleName() + currentUser.getGivenName();
                        params = sID + "&" + safeKey + "&" + timeStamp + "&" + teacherAccount + "&" + teacherName;
                        String addTeacherURL = systemRegistryMapper.getURLByKey("classin_addteacher_url");
                        resultClass = HttpClient.doPost(addTeacherURL, params);
                        if (resultClass != null && !resultClass.equals("")) {
                            classInClassMap = objectMapper1.readValue(resultClass, Map.class);
                            errorInfo = (Map<String, Object>) classInClassMap.get("error_info");
                            errno = errorInfo.get("errno").toString();
                            error = errorInfo.get("error").toString();
                            if (!errno.equals("1") && !errno.equals("133")) {
                                errorMsg = error;
                                url = "/classin/tips";
                                properties.put("errorMsg", errorMsg);
                                properties.put("url", url);
                            } else {
                                continue;
                            }
                        } else {
                            errorMsg = "classIn服务器未收到请求信息，请重试";
                            url = "/classin/tips";
                            properties.put("errorMsg", errorMsg);
                            properties.put("url", url);
                        }

                    } else {
                        //其他错误代码
                        errorMsg = error;
                        url = "/classin/tips";
                        properties.put("errorMsg", errorMsg);
                        properties.put("url", url);
                    }
                }
                if (properties.get("errorMsg") == null) {
                    break;
                }
            } else {//未获取到classin返回的信息
                errorMsg = "classIn服务器未收到请求信息，请重试";
                url = "/classin/tips";
                properties.put("errorMsg", errorMsg);
                properties.put("url", url);
            }
        }
        return properties;
    }
}
