package com.blackboard.classin.controller;

import blackboard.base.BbList;
import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.*;
import com.blackboard.classin.mapper.*;
import com.blackboard.classin.service.IBbCourseClassinCourse;
import com.blackboard.classin.service.IClassinCourseClass;
import com.blackboard.classin.service.LabelService;
import com.blackboard.classin.service.TimerTaskService;
import com.blackboard.classin.task.ScheduledTask;
import com.blackboard.classin.util.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/classinCourseClass")
public class ClassinCourseClassController {

    private Logger log = Logger.getLogger(ClassinCourseClassController.class);

    @Autowired
    private IClassinCourseClass iClassinCourseClass;

    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Autowired
    private ClassinClassMeetingMapper classinClassMeetingMapper;

    @Autowired
    private SystemRegistryMapper systemRegistryMapper;

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private ClassScheduleDataMapper classScheduleDataMapper;

    @Autowired
    private TimerTaskService timerTaskService;

    @Autowired
    private LabelService labelService;


    @Autowired
    private IBbCourseClassinCourse iBbCourseClassinCourse;


//    @ResponseBody
//    @RequestMapping("test.do")
    public String test() throws IOException, PersistenceException {
        class MyThread extends Thread{
            @Override
            public void run() {
                try {
                    iBbCourseClassinCourse.deleteCourseStudentByPhoneAndUid();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        new MyThread().start();
//       SystemUtil.getStudentState("21");
//        SystemUtil.getStudentState("121");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data","success");
        return jsonObject.toJSONString();
    }

    /**
     * 网页端报错请求
     *
     * @author panhaiming
     * @date 20200915
     * */
    @RequestMapping("error.do")
    public String getErrorMsg(String error,Model model){
        model.addAttribute("error",error);
        return "/classin/tips";
    }


    /**
     * 获取课节页面所需的默认值
     *
     * @author panhaiming
     * @date 20200813
     */
    @ResponseBody
    @RequestMapping("/getDefaultValue.do")
    public String getDefautlValue(HttpServletRequest request, HttpServletResponse response, String course_id) throws
            PersistenceException, IOException {
        JSONObject jsonObject = new JSONObject();
        Course course = SystemUtil.getCourseById(course_id);

        String bbCourseId = course.getCourseId();
        String courseNum = classinCourseClassMapper.findByBBCourseId(bbCourseId);
        if (courseNum == null) {
            courseNum = "1";
        } else {
            courseNum = Integer.valueOf(courseNum) + 1 + "";
        }
        String courseTitle = course.getTitle();
        jsonObject.put("className", bbCourseId + "_" + courseTitle + "_" + courseNum);
        String[] split = course_id.split("_");
        String bb_course_id = split[1];
        List<BBUser> bbAssistantTeachers = SystemUtil.getBbAssistantTeachers(bb_course_id, userPhoneMapper);
        List<BBUser> bbTeachers = SystemUtil.getBbTeachers(bb_course_id, userPhoneMapper);
        User currentUser = SystemUtil.getCurrentUser();
        String name = currentUser.getUserName();
        UserPhone userInfo = userPhoneMapper.findPhoneByUserId(name);
        String teacherPhone = userInfo.getPhone();
        int i = 0;
        int j = 0;
        for (BBUser user : bbTeachers) {
            if (user.getPhone().indexOf(teacherPhone) != -1) {
                break;
            }
            i++;
        }
        if (i != bbTeachers.size()) {
            jsonObject.put("teacherNum", i + 1);
        } else {
            jsonObject.put("teacherNum", 0);
        }

        for (BBUser user : bbAssistantTeachers) {
            if (user.getPhone().indexOf("请绑定手机号") == -1) {
                break;
            }
            j++;
        }
        if (j != bbAssistantTeachers.size()) {
            jsonObject.put("assistantNum", j + 1);
        } else {
            jsonObject.put("assistantNum", 0);
        }
        return jsonObject.toJSONString();
    }

    /**
     * 将老师加为课节下学生
     *
     * @autho panhaming
     * @date 20200806
     */
    @ResponseBody
    @RequestMapping("/addClassStudent.do")
    public String addTeacherToCourse(HttpServletRequest request, HttpServletResponse response, Model
            model, String
                                             classId) throws
            JsonParseException, JsonMappingException, IOException, PersistenceException {
        JSONObject jsonObject = new JSONObject();
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        String classinUid = bbSession.getGlobalKey("classinUid");
        String userId = bbSession.getGlobalKey("userId");
        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param6 = "classId=" + classId;
        ClassinCourseClass classinCourse = classinCourseClassMapper.findByClassId(classId);
        String classInCourseId = classinCourse.getClassinCourseId();
        String param5 = "courseId=" + classInCourseId;
        //课程成添加学生/旁听
        String param4 = "identity=1";
        //String param_studentAccount = "studentAccount="+telephone;
        //手机号修改为uid
//        String param_studentAccount = "studentUid=" + classinUid;
        HashMap<String, Object> students = new HashMap<>();
        students.put("uid", classinUid);
        JSONObject stuJson = new JSONObject();
        stuJson.put("uid", classinUid);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(stuJson);
        String param7 = "studentJson=" + jsonArray;
        log.info("para7>>>>>>>>>>>>>>" + param7);
//        String param_studentName = "studentName=" + userId;
        String classin_addclassstudent_url = systemRegistryMapper.getURLByKey("classin_addclassstudent_url");
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5).append("&").append(param6).append("&").append(param7);
        ObjectMapper objectMapper = new ObjectMapper();
        String addClassStudentResultMapString = HttpClient.doPost(classin_addclassstudent_url, sBuilder.toString());
        log.info("addCourseStudentResultMapString is >>>" + addClassStudentResultMapString);
        Map<String, Object> addCourseStudentResultMap = new HashMap<String, Object>();

        if (addClassStudentResultMapString != null && !"".equals(addClassStudentResultMapString)) {
            addCourseStudentResultMap = objectMapper.readValue(addClassStudentResultMapString, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) addCourseStudentResultMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            //添加学生成功
            if ("1".equals(errno) || "166".equals(errno)) {
                jsonObject.put("errno", errno);
            } else {
                jsonObject.put("errno", errno);
                jsonObject.put("error", error);
            }
        } else {
            jsonObject.put("errno", "BB提示：");
            jsonObject.put("error", "系统错误，请重试");
        }
        return jsonObject.toJSONString();
    }

    /**
     * 将学生加入到课程下
     *
     * @autho panhaming
     * @date 20200806
     */
    @ResponseBody
    @RequestMapping("/addCourseStudent.do")
    public String addCourseStudent(HttpServletRequest request, HttpServletResponse response, Model
            model, String classId) throws
            JsonParseException, JsonMappingException, IOException, PersistenceException {
        JSONObject jsonObject = new JSONObject();
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        String classinUid = bbSession.getGlobalKey("classinUid");
        String userId = bbSession.getGlobalKey("userId");
        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        ClassinCourseClass classinCourse = classinCourseClassMapper.findByClassId(classId);
        String classInCourseId = classinCourse.getClassinCourseId();
        String param5 = "courseId=" + classInCourseId;
        //课程成添加学生/旁听
        String param4 = "identity=1";
        String param6 = "studentUid=" + classinUid;

        String studentName = "studentName=" + userId;
        String classin_addclassstudent_url = systemRegistryMapper.getURLByKey("classin_addcoursestudent_url");
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5).append("&").append(param6).append("&").append(studentName);
        for (int i = 0; i < 2; i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            String addClassStudentResultMapString = HttpClient.doPost(classin_addclassstudent_url, sBuilder.toString());
            log.info("addCourseStudentResultMapString is >>>" + addClassStudentResultMapString);
            Map<String, Object> addCourseStudentResultMap = new HashMap<String, Object>();

            if (addClassStudentResultMapString != null && !"".equals(addClassStudentResultMapString)) {
                addCourseStudentResultMap = objectMapper.readValue(addClassStudentResultMapString, Map.class);
                //解析返回的数据
                Map<String, Object> errorInfo = (Map<String, Object>) addCourseStudentResultMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                //添加学生成功
                if ("1".equals(errno) || "163".equals(errno)) {
                    jsonObject.put("errno", errno);
                    return jsonObject.toJSONString();
                } else if ("228".equals(errno)) {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>机构下无此学生");
                    String classin_addstudent_url = systemRegistryMapper.getURLByKey("classin_addstudent_url");
                    long currentCreateClassTime = System.currentTimeMillis() / 1000;
                    parma1 = "SID=" + Constants.SID;
                    String parma2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
                    String parma3 = "timeStamp=" + currentCreateClassTime;
                    String param_studentAccount = "studentAccount=" + telephone;
                    User currentUser = SystemUtil.getCurrentUser();
                    String param_studentName = "studentName=" + currentUser.getUserName();
                    StringBuilder strsBuilder = new StringBuilder();
                    strsBuilder.append(parma1).append("&").append(parma2).append("&").append(parma3).append("&").
                            append(param_studentAccount).append("&").append(param_studentName);
                    String resultRegisterMapStr = HttpClient.doPost(classin_addstudent_url, strsBuilder.toString());
                    Map resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
                    errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
                    errno = errorInfo.get("errno").toString();
                    error = errorInfo.get("error").toString();
                    if (!"1".equals(errno) && !"133".equals(errno)) {
                        model.addAttribute("source", "来自BB的提示信息");
                        model.addAttribute("error", error);
                        return "/classin/tips";
                    } else {
                        continue;
                    }

                } else {
                    jsonObject.put("errno", errno);
                    jsonObject.put("error", error);
                    return jsonObject.toJSONString();
                }
            } else {
                jsonObject.put("errno", "BB提示：");
                jsonObject.put("error", "系统错误，请重试");
                return jsonObject.toJSONString();
            }
        }
        return jsonObject.toJSONString();
    }

    /**
     * 将听课老师编辑为课节助教
     *
     * @author panhaiming
     * @date 20200811
     **/
    @ResponseBody
    @RequestMapping("/addClassAssitant.do")
    public String addClassAssitan(HttpServletRequest request, HttpServletResponse response, String classId,
                                  String listenClass) throws PersistenceException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        UserPhone userInfo = userPhoneMapper.findByPhone(telephone);
        String assistantName = "";
        String assistantPhone = "";
        assistantName = userInfo.getUserId();
        assistantPhone = telephone;
        //log.info("老师信息****************8"+teacherInfo);
        String teacherName = "";
        String teacherPhone = "";
        ClassinCourseClass classinCourse = classinCourseClassMapper.findByClassId(classId);
        teacherName = classinCourse.getTeacherName();
        teacherPhone = classinCourse.getTeacherPhone();
        log.info("addClassAssitant****************" + "电话" + assistantPhone + "姓名" + assistantName);
        ClassinCourseClass classinClass = classinCourseClassMapper.findByClassId(classId);
        String classinCourseId = classinClass.getClassinCourseId();
        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param5 = "courseId=" + classinCourseId;
        String param6 = "classId=" + classId;
        String param4 = "teacherUid=" + userPhoneMapper.findByPhone(teacherPhone).getClassinUid();
        String classinEditCourseClassURL = "";
        classinEditCourseClassURL = systemRegistryMapper.getURLByKey("classin_editCourseClass_url");
        StringBuilder stringBuilder = new StringBuilder();


        if (listenClass.equals("listen")) {
            String param7 = "assistantUid=" + userPhoneMapper.findByPhone(telephone).getClassinUid();
            stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                    .append("&").append(param4).append("&").append(param5).append("&").append(param7).append("&").append(param6);
        }
        String resultMap = HttpClient.doPost(classinEditCourseClassURL, stringBuilder.toString());
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>调用听课教师编辑课节信息");
        JSONObject jsonObject = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultHashMap = new HashMap<>();
        HashMap<String, Object> classMap = new HashMap<>();
        if (resultMap != null && !resultMap.equals("")) {
            resultHashMap = objectMapper.readValue(resultMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            //成功返回信息
            if ("1".equals(errno)) {
                Map<String, String> moreData = (Map<String, String>) resultHashMap.get("more_data");
                String liveURL = moreData.get("live_url");
                classMap.put("teacherName", teacherName);
                classMap.put("assistantName", assistantName);
                classMap.put("assistantPhone", assistantPhone);
                classMap.put("classId", classId);
                classMap.put("teacherPhone", teacherPhone);
                classinCourseClassMapper.editClassTeacher(classMap);
                jsonObject.put("errno", errno);
                return jsonObject.toJSONString();
            } else {
                jsonObject.put("errno", errno);
                jsonObject.put("error", error);
                return jsonObject.toJSONString();
            }
        } else {
            jsonObject.put("errno", "来自BB的报错信息");
            jsonObject.put("error", "系统错误");
            return jsonObject.toJSONString();
        }
    }

    /**
     * 更换课节中，授课教师与助教老师。
     *
     * @author panhaiming
     * @date 20200804
     */
    @RequestMapping("/editClassTeacher.do")
    public String editTeacher(HttpServletRequest request, HttpServletResponse response, String teacherInfo,
                              String classId, String assistantInfo, String course_id, String listenClass, String position, Model
                                      model) throws
            PersistenceException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String[] teacherUser = teacherInfo.split(",");
        String teacherPhone = teacherUser[1].trim();
        String teacherName = teacherUser[0].trim();
        UserPhone user = userPhoneMapper.findByPhone(teacherPhone);
        String userName = user.getUserId();
        ClassinCourseClass classinClass = classinCourseClassMapper.findByClassId(classId);
        String classinCourseId = classinClass.getClassinCourseId();
        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param5 = "courseId=" + classinCourseId;
        String param6 = "classId=" + classId;
        String teacherUid = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();     //thirduid1111
        if (teacherUid == null){
            SystemUtil.getUid(user,systemRegistryMapper,userPhoneMapper);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>teacherUid"+teacherUid);
            teacherUid = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();
        }
        String param4 = "teacherUid=" + teacherUid;
        String classinEditCourseClassURL = "";
        classinEditCourseClassURL = systemRegistryMapper.getURLByKey("classin_editCourseClass_url");
        StringBuilder stringBuilder = new StringBuilder();
        String assistantName = "";
        String assistantPhone = "";

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>assistantInfo"+assistantInfo);
        for (int i = 0; i <= 2; i++) {
            if (assistantInfo != null) {
                assistantName = assistantInfo.split(",")[0].trim();
                assistantPhone = assistantInfo.split(",")[1].trim();
                String assistantUid = userPhoneMapper.findByPhone(assistantPhone).getClassinUid();//fourthuid1111
                if (assistantUid == null){
                    SystemUtil.getUid(userPhoneMapper.findByPhone(assistantPhone),systemRegistryMapper,userPhoneMapper);
                    assistantUid = userPhoneMapper.findByPhone(assistantPhone).getClassinUid();
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>assistantUid"+assistantUid);
                }
                String param7 = "assistantUid="+assistantUid;
                stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                        .append("&").append(param4).append("&").append(param5).append("&").append(param7).append("&").append(param6);
            } else {
                stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                        .append("&").append(param4).append("&").append(param5).append("&").append(param6);
            }
            String resultMap = HttpClient.doPost(classinEditCourseClassURL, stringBuilder.toString());
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>调用编辑课节信息");
            JSONObject jsonObject = new JSONObject();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultHashMap = new HashMap<>();
            HashMap<String, Object> classMap = new HashMap<>();
            if (resultMap != null && !resultMap.equals("")) {
                resultHashMap = objectMapper.readValue(resultMap, Map.class);
                //解析返回的数据
                Map<String, Object> errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                //成功返回信息
                if ("1".equals(errno)) {
                    Map<String, String> moreData = (Map<String, String>) resultHashMap.get("more_data");
                    String liveURL = moreData.get("live_url");
                    classMap.put("liveURL", liveURL);
                    classMap.put("teacherName", teacherName);
                    classMap.put("classId", classId);
                    classMap.put("teacherPhone", teacherPhone);
                    classMap.put("userName", userName);
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>老师信息" + teacherName + "***********" + teacherPhone);
                    if (assistantInfo != null) {
                        classMap.put("assistantName", assistantName);
                        classMap.put("assistantPhone", assistantPhone);
                        log.info(">>>>>>>>>>>>>>>>>>>更新助教老师" + teacherPhone);
                        classinCourseClassMapper.editAssistantTeacher(classMap);
                    }

                    if (assistantInfo == null) {
                        log.info(">>>>>>>>>>>>>>>>>>>更新授课老师" + teacherPhone);
                        classinCourseClassMapper.editClassTeacher(classMap);
                    }

                    if (position != null) {
                        return "redirect:/classinCourseClass/getHomeClassList.do?course_id=" + course_id;
                    } else {
                        return "redirect:/classinCourseClass/getClassScheduleList.do?course_id=" + course_id;
                    }
                } else if ("325".equals(errno) || "321".equals(errno)) {
                    String identtity = "identity=1";
                    JSONArray jsonArray = new JSONArray();
                    if ("325".equals(errno)) {
                        jsonArray.add(teacherUid);
                    } else {
                        jsonArray.add(userPhoneMapper.findByPhone(assistantPhone).getClassinUid());
                    }
                    String studentUidJson = "studentUidJson=" + jsonArray;
                    StringBuilder stringBuilder1 = new StringBuilder();
                    stringBuilder1.append(parma1).append("&").append(param2).append("&").append(param3).append("&").
                            append(param5).append("&").append(identtity).append("&").append(studentUidJson).append("&").append(param6);
                    String deleteClassStudentURL = systemRegistryMapper.getURLByKey("classin_deleteclassstudent_url");
                    String result = HttpClient.doPost(deleteClassStudentURL, stringBuilder1.toString());
                    if (result != null && !result.equals("")) {
                        resultHashMap = objectMapper.readValue(result, Map.class);
                        //解析返回的数据
                        errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
                        errno = errorInfo.get("errno").toString();
                        error = errorInfo.get("error").toString();
                        if (errno.equals("1")) {
                            if (assistantInfo != null) {
                                assistantName = assistantInfo.split(",")[0].trim();
                                assistantPhone = assistantInfo.split(",")[1].trim();
                                String param7 = "assistantUid=" + userPhoneMapper.findByPhone(assistantPhone).getClassinUid();
                                stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                                        .append("&").append(param4).append("&").append(param5).append("&").append(param7).append("&").append(param6);
                            } else {
                                stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                                        .append("&").append(param4).append("&").append(param5).append("&").append(param6);
                            }

                            resultMap = HttpClient.doPost(classinEditCourseClassURL, stringBuilder.toString());
                            objectMapper = new ObjectMapper();
                            resultHashMap = new HashMap<>();
                            classMap = new HashMap<>();
                            if (resultMap != null && !resultMap.equals("")) {
                                resultHashMap = objectMapper.readValue(resultMap, Map.class);
                                //解析返回的数据
                                errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
                                errno = errorInfo.get("errno").toString();
                                error = errorInfo.get("error").toString();
                                //成功返回信息
                                if ("1".equals(errno)) {
                                    Map<String, String> moreData = (Map<String, String>) resultHashMap.get("more_data");
                                    String liveURL = moreData.get("live_url");
                                    classMap.put("liveURL", liveURL);
                                    classMap.put("teacherName", teacherName);
                                    classMap.put("classId", classId);
                                    classMap.put("teacherPhone", teacherPhone);
                                    classMap.put("userName", userName);
                                    if (assistantInfo != null) {
                                        classMap.put("assistantName", assistantName);
                                        classMap.put("assistantPhone", assistantPhone);
                                        classinCourseClassMapper.editAssistantTeacher(classMap);
                                    }

                                    if (assistantInfo == null) {
                                        classinCourseClassMapper.editClassTeacher(classMap);
                                    }

                                    if (position != null) {
                                        return "redirect:/classinCourseClass/getHomeClassList.do?course_id=" + course_id;
                                    } else {
                                        return "redirect:/classinCourseClass/getClassScheduleList.do?course_id=" + course_id;
                                    }
                                } else {
                                    model.addAttribute("source", "来自Classin的提示信息");
                                    model.addAttribute("error", error);
                                    return "/classin/tips";
                                }
                            } else {
                                model.addAttribute("source", "来自Classin的提示信息");
                                model.addAttribute("error", "bb发送请求失败");
                                return "/classin/tips";
                            }
                        }
                    } else {
                        model.addAttribute("source", "来自BB的提示信息");
                        model.addAttribute("error", error);
                        return "/classin/tips";
                    }
                } else if ("136".equals(errno) || "318".equals(errno)) {
                    String teacherAccount = "";
                    if (errno.equals("318")) {
                        teacherAccount = "teacherAccount=" + assistantPhone;

                    } else {
                        teacherAccount = "teacherAccount=" + teacherPhone;
                    }

                    User currentUser = SystemUtil.getCurrentUser();
                    String teacherIdentity = "teacherName=" + currentUser.getFamilyName() + currentUser.getMiddleName() + currentUser.getGivenName();
                    String params = parma1 + "&" + param2 + "&" + param3 + "&" + teacherAccount + "&" + teacherIdentity;
                    String addTeacherURL = systemRegistryMapper.getURLByKey("classin_addteacher_url");
                    String resultClass = HttpClient.doPost(addTeacherURL, params);
                    if (resultClass != null && !resultClass.equals("")) {
                        resultHashMap = objectMapper.readValue(resultClass, Map.class);
                        errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
                        errno = errorInfo.get("errno").toString();
                        error = errorInfo.get("error").toString();
                        if (!errno.equals("1")) {
                            model.addAttribute("error", error);
                            return "/classin/tips";
                        }
                    } else {
                        model.addAttribute("error", "classIn服务器未收到请求信息，请重试");
                        return "/classin/tips";
                    }
                    continue;
                } else {
                    model.addAttribute("source", "来自Classin的提示信息");
                    model.addAttribute("error", error);
                    return "/classin/tips";
                }
            } else {
                model.addAttribute("source", "来自Classin的提示信息");
                model.addAttribute("error", "bb发送请求失败");
                return "/classin/tips";
            }
        }
        return "";
    }

    /**
     * 删除非课表课节
     *
     * @author phm
     * @date 20200803
     */
    @RequestMapping("/deleteClass.do")
    public String deleteClass(HttpServletRequest request, HttpServletResponse response, String classId, String
            bbCourseId, Model model) throws PersistenceException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        Course course = SystemUtil.getCourseById(bbCourseId);
        String bbId = course.getCourseId();
        ClassinCourseClass classinCourse = classinCourseClassMapper.findByClassId(classId);
        String classinCourseId = classinCourse.getClassinCourseId();
        ;
        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param5 = "courseId=" + classinCourseId;
        String param6 = "classId=" + classId;
        String classinDeletecourseclassUrl = systemRegistryMapper.getURLByKey("classin_deletecourseclass_url");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append("&").append(param5).append("&").append(param6);
        String resultLoginMap = HttpClient.doPost(classinDeletecourseclassUrl, stringBuilder.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> classInCourseClassIdMap = new HashMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        if (resultLoginMap != null && !resultLoginMap.equals("")) {

            classInCourseClassIdMap = objectMapper.readValue(resultLoginMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();

            //成功返回信息
            if ("1".equals(errno)) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>" + classId + "++++++++++++++++++++++" + classinCourseId);
                classinCourseClassMapper.deleteClass(classId, classinCourseId);
                return "redirect:/classinCourseClass/getHomeClassList.do?course_id=" + bbCourseId;
            } else {
                model.addAttribute("errno", errno);
                model.addAttribute("error", error);
                return "/classin/tips";
            }
        } else {
            model.addAttribute("error", "网络错误，请重试");
            return "/classin/tips";
        }
    }

    /**
     * 进入教室
     *
     * @author phm
     * @date 20200802
     */
    @ResponseBody
    @RequestMapping("/turnIntoClassRoom.do")
    public String turnIntoClassRoom(HttpServletRequest request, HttpServletResponse response, Model model,
                                    String classId, String teacherInfo)
            throws PersistenceException, IOException {

        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String classinEntranceUrl = systemRegistryMapper.getClassinEntranceURL();
        String telephone = bbSession.getGlobalKey("telephone");
        ClassinCourseClass classInfo = classinCourseClassMapper.findByClassId(classId);
        String classinUid = "";
//        if (SystemUtil.isTeacher()){
//            classinUid = userPhoneMapper.findByPhone(teacherInfo.split(" ")[1]).getClassinUid();
//            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>访问者身份------老师");
//        }else {
//            classinUid = userPhoneMapper.findByPhone(telephone).getClassinUid();
//            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>访问者身份------学生");
//        }
        //判断教师是否为本节课老师，代码待补全
        classinUid = userPhoneMapper.findByPhone(telephone).getClassinUid();
        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param4 = "uid=" + classinUid;
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>uid" + classinUid);
        String param5 = "courseId=" + classInfo.getClassinCourseId();
        String param6 = "classId=" + classId;
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5).append("&").append(param6);
        String resultLoginMap = HttpClient.doPost(classinEntranceUrl, stringBuilder.toString());
        Map<String, Object> classInCourseClassIdMap = new HashMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        if (resultLoginMap != null && !resultLoginMap.equals("")) {
            classInCourseClassIdMap = objectMapper.readValue(resultLoginMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            //成功返回信息
            if ("1".equals(errno)) {
                String data = (String) classInCourseClassIdMap.get("data");
                String conditions = "";
                if (data != null && !data.equals("")) {
                    conditions = data.split("\\?")[1];
                }
                jsonObject.put("condition", conditions);
                return jsonObject.toJSONString();
            } else {
                jsonObject.put("source", "classin提示您----");
                jsonObject.put("errno", errno);
                jsonObject.put("error", error);
                jsonObject.put("condition", "error");
                return jsonObject.toJSONString();
            }
        } else {
            jsonObject.put("condition", "error");
            jsonObject.put("source", "BB提示您----");
            jsonObject.put("error", "获取进入教室url成功，如未跳转，请刷新页面后进行下一步操作~");
            return jsonObject.toJSONString();
        }
    }

    /**
     * 返回首页
     *
     * @autor panhaming
     * @date 20200730
     */
    @RequestMapping("/goBack.do")
    public String firstPag(HttpServletRequest request, HttpServletResponse response, String tips, Model
            model, String course_id) {
        model.addAttribute("tips", tips);
        return "redirect:/classinCourseClass/getHomeClassList.do?course_id=" + course_id;
    }

    /**
     * 获取课节状态
     *
     * @author panhaiming
     * @date 20200730
     */
    @ResponseBody
    @RequestMapping("/getClassStatus.do")
    public String getClassStatus(HttpServletRequest request, HttpServletResponse
            response, String course_id) {
        Course course = SystemUtil.getCourseById(course_id);
        String bbCourseId = course.getCourseId();
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("bbCourseId", bbCourseId);
        paraMap.put("currentTimeStamp", new Long(System.currentTimeMillis() / 1000).intValue());
        List<Map<String, Object>> classList = classinCourseClassMapper.getClassStatus(paraMap);
        boolean isTeacher = SystemUtil.isTeacher();
        Map<String, Object> map = new HashMap<>();
        map.put("isTeacher", isTeacher);
        int i = 0;
        String currentUserName = SystemUtil.getCurrentUser().getUserName();
        for (Map data : classList) {
            if (currentUserName.equals(data.get("USER_NAME"))) {
                i = 1;
                map.put("hasFinished", "no");
            }
        }

        if (i == 0) {
            map.put("hasFinished", "yes");
        }

        classList.add(map);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", classList);
        return jsonObject.toJSONString();
    }

    /**
     * 获取未结束的课程列表
     *
     * @Author panhaimang
     * @Date 20200730
     */
    @RequestMapping("/getClassScheduleList.do")
    public String getAvailableClass(HttpServletRequest request, HttpServletResponse response, String
            course_id, Model model) throws PersistenceException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        Course course = SystemUtil.getCourseById(course_id);
        String bbCourseId = course.getCourseId();
        long timeStamp = System.currentTimeMillis() / 1000;
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("bbCourseId", bbCourseId);
        paraMap.put("todayTimaStamp", new Long(timeStamp).intValue());
        List<Map<String, Object>> classList = classinCourseClassMapper.getClassList(paraMap);
        List<Map<String, Object>> newClassList = new ArrayList<>();
        int i = 1;
        for (Map<String, Object> map : classList) {
            map.put("id", i++);
            newClassList.add(map);
        }
        String[] split = course_id.split("_");
        String bb_course_id = split[1];
        List<BBUser> assistantTeachers = SystemUtil.getBbAssistantTeachers(bb_course_id, userPhoneMapper);
        List<BBUser> teachers = SystemUtil.getBbTeachers(bb_course_id, userPhoneMapper);
        boolean isTeacher = SystemUtil.isTeacher();
        boolean isAdministrator = SystemUtil.isAdministrator();
        String userName = SystemUtil.getCurrentUser().getUserName();
        model.addAttribute("userName",userName);
        model.addAttribute("isAdministrator",isAdministrator);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("classList", newClassList);
        model.addAttribute("assistantTeachers", assistantTeachers);
        model.addAttribute("teachers", teachers);
        model.addAttribute("currentUserTelephone", telephone);
        return "/classin/classScheduleList";
    }

    /**
     * 首页获取课节列表
     *
     * @author panhaiming
     * @date 20200805
     */
    @RequestMapping("/getHomeClassList.do")
    public String getHomeClassList(HttpServletRequest request, HttpServletResponse response, String
            course_id, Model model) throws PersistenceException, IOException {
        Course course = SystemUtil.getCourseById(course_id);
        String bbCourseId = course.getCourseId();
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        long timeStamp = System.currentTimeMillis() / 1000;
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("bbCourseId", bbCourseId);
        paraMap.put("todayTimaStamp", new Long(timeStamp).intValue());
        List<Map<String, Object>> classList = classinCourseClassMapper.getClassList(paraMap);
        List<Map<String, Object>> newClassList = new ArrayList<>();
        int i = 1;
        for (Map<String, Object> map : classList) {
            map.put("id", i++);
            newClassList.add(map);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + map.get("TEACHER_NAME") + map.get("TEACHER_PHONE"));
        }
        String[] split = course_id.split("_");
        String bb_course_id = split[1];
        List<BBUser> assistantTeachers = SystemUtil.getBbAssistantTeachers(bb_course_id, userPhoneMapper);
        List<BBUser> teachers = SystemUtil.getBbTeachers(bb_course_id, userPhoneMapper);
        boolean isTeacher = SystemUtil.isTeacher();
        boolean isAdministrator = SystemUtil.isAdministrator();
        String userName = SystemUtil.getCurrentUser().getUserName();
        model.addAttribute("userName",userName);
        model.addAttribute("isAdministrator",isAdministrator);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("classList", newClassList);
        model.addAttribute("assistantTeachers", assistantTeachers);
        model.addAttribute("teachers", teachers);
        model.addAttribute("currentUserTelephone", telephone);
        return "/classin/createClassinClass";
    }


    /**
     * 创建课程、课节、保存设置的课节信息
     */
    @RequestMapping("/store.do")
    public String createCourseAndClassAndSoreSetting(HttpServletRequest request, HttpServletResponse response,
                                                     String className, String classType, String startDate,
                                                     String startTime, String hour, String minute, String teacher,
                                                     String assistantTeacher, String bbCourseId, String isLive,
                                                     String isRecord, String isReplay,String startTimeStamp,
                                                     Model model) throws
            PersistenceException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        System.out.println(teacher + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String[] teacherInfo = teacher.split(" ");
        String assistantTeacherUID = "";
        String[] assistantTeacherInfo = null;
        String assistantTeacherPhone = "";
        if (!assistantTeacher.equals("请选择助教教师")) {
            assistantTeacherInfo = assistantTeacher.split(" ");
            assistantTeacherPhone = assistantTeacherInfo[1];
            assistantTeacherUID = userPhoneMapper.findByPhone(assistantTeacherPhone).getClassinUid();   //firstuid1111
            if (assistantTeacherUID == null){
                SystemUtil.getUid(userPhoneMapper.findByPhone(assistantTeacherPhone),systemRegistryMapper,userPhoneMapper);
                assistantTeacherUID = userPhoneMapper.findByPhone(assistantTeacherPhone).getClassinUid();
            }
        }
        String teacherPhone = teacherInfo[1];
        String teacherUID = userPhoneMapper.findByPhone(teacherPhone).getClassinUid();    //seconduid1111
        if (teacherUID == null){
            SystemUtil.getUid(userPhoneMapper.findByPhone(teacherPhone),systemRegistryMapper,userPhoneMapper);
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
        log.info("bbCourse>>>>>>>>>>>>>>>" + bbCourse);
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
                    model.addAttribute("tips", "创建课程失败");
                    return "/classin/createClassinClass";
                }
            }
        }
        if (status == 0) {
            classInCourseId = course.getClassinCourseId();
        }
        String courseId = "courseId=" + classInCourseId;
        String courseClassName = className;
        className = "className=" + className;
        //String tempStartTime = startDate + "  " + startTime;
        //long startTimeStamp = TimeStampUtil.getTimeStamp(tempStartTime);
        log.info(">>>>>>>>>>>>>>>课程ID>>>>>>>>>>>>>" + classInCourseId);
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
                    log.info(">>>>>>>>>>>>>>>>>>回放回放" + liveURL);
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
                        paramMap.put("assistantName", " ");
                        paramMap.put("assistantPhone", " ");
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
                    } else{
                        paramMap.put("labelId", null);
                        paramMap.put("labelName", null);
                    }
                    classinCourseClassMapper.save(paramMap);

                    if (labelId != null) {
                        String classList = "classList="+jsonArray;
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
                                model.addAttribute("error", error);
                                return "/classin/tips";
                            }
                        } else {
                            model.addAttribute("error", "classIn服务器未收到请求信息，请重试");
                            return "/classin/tips";
                        }
                    }
                    return "redirect:/classinCourseClass/goBack.do?course_id=" + bbCourseId;
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
                            model.addAttribute("error", error);
                            return "/classin/tips";
                        } else {
                            continue;
                        }
                    } else {
                        model.addAttribute("error", "classIn服务器未收到请求信息，请重试");
                        return "/classin/tips";
                    }

                } else {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>99999999999");
                    //其他错误代码
                    model.addAttribute("error", error);
                    return "/classin/tips";
                }
            } else {//未获取到classin返回的信息
                model.addAttribute("error", "classIn服务器未收到请求信息，请重试");
                return "/classin/tips";
            }
        }
        return "";
    }

    /**
     * 编辑课节信息
     *
     * @author panhaiming
     * @date 20200724
     */
    @RequestMapping(value = "/editClass.do")
    public String advancedSet(HttpServletRequest request, HttpServletResponse response, String course_id, Model
            model, String type) throws PersistenceException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String[] split = course_id.split("_");
        String bb_course_id = split[1];
        List<BBUser> teachers = SystemUtil.getBbTeachers(bb_course_id, userPhoneMapper);
        List<BBUser> assistantTeachers = SystemUtil.getBbAssistantTeachers(bb_course_id, userPhoneMapper);
        model.addAttribute("teachers", teachers);
        model.addAttribute("assistantTeachers", assistantTeachers);
        return "/classin/classEdit";
    }


    /**
     * 删除整个课节回放信息
     *
     * @param request
     * @param response
     * @param course_id
     * @param classinClassId
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @RequestMapping("/delete.do")
    public String deleteClassinClass(HttpServletRequest request, HttpServletResponse response,
                                     String course_id, String classinClassId,String classinCourseId, Model model)
            throws JsonParseException, JsonMappingException, IOException {
        log.info("deleteClassinClass: classinClassId=" + classinClassId);
        ObjectMapper objectMapper = new ObjectMapper();
        //删除classin端数据
        String deleteCourseClassURL = systemRegistryMapper.getURLByKey("classin_deletecourseclassvideo_url");

        long currentCreateClassTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String param3 = "timeStamp=" + currentCreateClassTime;
        String param4 = "classId=" + classinClassId;

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&");

        String resultLoginMap = HttpClient.doPost(deleteCourseClassURL, stringBuilder.toString());
        log.info("resultLoginMap >>>>" + resultLoginMap);
        Map<String, Object> classInCourseClassIdMap = new HashMap<String, Object>();

        if (resultLoginMap != null && !resultLoginMap.equals("")) {
            classInCourseClassIdMap = objectMapper.readValue(resultLoginMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();

            //classin成功删除
            if ("1".equals(errno) || "254".equals(errno) || "631".equals(errno) || "632".equals(errno)) {
                //BB删除
                classinCourseClassMapper.delete(classinClassId);
            } else {
                model.addAttribute("source", "来自Classin的提示信息:课节删除提示");
                model.addAttribute("errno", errno);
                model.addAttribute("error", error);
                model.addAttribute("type", null);
                return "/classin/tips";
            }

        }
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("bbCourseId",classinCourseId);
        paraMap.put("currentTimeStamp",System.currentTimeMillis()/1000+"");
        List<ClassinCourseClass> classinCourseClassList = classinCourseClassMapper.getReplayList(paraMap);
//		if(classinCourseClassList != null && classinCourseClassList.size() != 0) {
//			for(int i=0;i<classinCourseClassList.size();i++) {
//				ClassinCourseClass ClassinCourseClass = classinCourseClassList.get(i);
//				ClassinCourseClass.setDtCreated(dtCreated););
//			}
//		}
        model.addAttribute("classinCourseClassList", classinCourseClassList);
        return "/classin/replayList";
    }

    /**
     * 创建课节
     *
     * @param request
     * @param response
     * @throws PersistenceException
     */
    @RequestMapping("/create.do")
    public String create(HttpServletRequest request, HttpServletResponse response, String course_id, Model
            model, String type) throws PersistenceException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String classinCourseId = bbSession.getGlobalKey("classinCourseId");
        String replayFlag = "";
        if (type != null && "meetingroom".equals(type)) {
            //研讨室，需要去查询meeting
            ClassinClassMeeting classinClassMeeting = classinClassMeetingMapper.findByClassinCourseId(classinCourseId);
            if (classinClassMeeting != null) {
                //已有其他老师创建研讨室，请勿重复创建--直接返回，重新进入界面
                return "redirect:/bbCourseClassinCourse/create.do?course_id=" + course_id + "&type=" + type;
            }
        } else {
            ClassinCourseClass classinCourseClass = classinCourseClassMapper.findByClassinCourseId(classinCourseId);
            if (classinCourseClass != null) {
                //已有其他老师创建课节，请勿重复创建--直接返回，重新进入界面
                //判断是否过期？
                return "redirect:/bbCourseClassinCourse/create.do?course_id=" + course_id + "&type=" + type;
            }
        }
        //创建课节
        String infos = iClassinCourseClass.createClassinCourseClass(request, response, type, course_id);
        String errno = bbSession.getGlobalKey("errno");
        String error = bbSession.getGlobalKey("error");

        //唤醒客户端并进入教室入口
        if (infos.equals("awakeClassinClient")) {
            String tips = "classTeacher";
            return "redirect:/classinCourseClass/awakeClassinClient.do?course_id=" + course_id + "&tips=" + tips + "&type=" + type;
        } else if (infos.equals("ClassinClassCreatedFailed")) {
            model.addAttribute("source", "来自Classin的提示信息:课节创建提示");
            model.addAttribute("errno", errno);
            model.addAttribute("error", error);
            model.addAttribute("type", type);
            return "/classin/tips";
        } else if ("DeleteCourseStudnet".equals(infos)) {
            //删除课程下的学生
            return "redirect:/bbCourseClassinCourse/delCourseStudent.do?course_id=" + course_id + "&flag=createClass172&type=" + type;
        } else if (infos.equals("NeedAddTeacherToClassin")) {
            //用户不是classin机构下的教师用户，需要将该用户添加为该机构的教师
            return "redirect:/classinCourseClass/addTeacherToClassin.do?course_id=" + course_id + "&type=" + type;
        } else if ("replayFlagNull".equals(infos) || "NetworkIsInstability".equals(infos)) {
            //其他错误信息
            log.info("else if replayFlagNull");
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "由于网络不稳定，课节未成功创建，请返回上一步重新创建~");
            model.addAttribute("type", type);
            return "/classin/tips";
        } else {
            //其他错误信息
            model.addAttribute("type", type);
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "由于网络不稳定，课节未成功创建，请返回上一步重新创建~");
            return "/classin/tips";
        }

    }

    /**
     * 将教师添加到机构中
     *
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws PersistenceException
     */
    @RequestMapping("/addTeacherToClassin.do")
    public String addTeacherToClassin(String course_id, HttpServletRequest request, HttpServletResponse
            response, Model model, String flag, String type)
            throws JsonParseException, JsonMappingException, IOException, PersistenceException {
        log.info("classin addTeacher");
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        long currentCreateClassTime = System.currentTimeMillis() / 1000;

        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String param3 = "timeStamp=" + currentCreateClassTime;
        String param4 = "teacherAccount=" + telephone;
        String param5 = "teacherName=" + SystemUtil.getCurrentUser().getUserName();

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5);

        String classin_addteacher_url = systemRegistryMapper.getURLByKey("classin_addteacher_url");
        String addTeacherResultMap = HttpClient.doPost(classin_addteacher_url, strBuilder.toString());
        log.info("addTeacher resultMap>>>" + addTeacherResultMap);

        Map<String, Object> addTeacherMap = new HashMap<String, Object>();

        if (addTeacherResultMap != null && !addTeacherResultMap.equals("")) {
            ObjectMapper objectMapper = new ObjectMapper();
            addTeacherMap = objectMapper.readValue(addTeacherResultMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) addTeacherMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();

            if ("1".equals(errno)) {
                //成功将教师添加到机构中
                //继续创建课节/或继续添加助教
                if ("assistant".equals(flag)) {
                    return "redirect:/classinCourseClass/addAsAssistant.do?course_id=" + course_id + "&type=" + type;
                }
                return "redirect:/classinCourseClass/create.do?course_id=" + course_id + "&type=" + type;
            } else if ("113".equals(errno)) {
                String bbUserId = bbSession.getGlobalKey("userId");
                //注册用户
                String param_nickname = "nickname=" + bbUserId;
                String param_pwd = "password=" + telephone;
                String param_telephone = "telephone=" + telephone;
                String param_schoolMemeber = "addToSchoolMember=2";
                StringBuilder strsBuilder = new StringBuilder();
                strsBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param_telephone)
                        .append("&").append(param_nickname).append("&").append(param_pwd).append("&").append(param_schoolMemeber);

                String classin_register_url = systemRegistryMapper.getURLByKey("classin_register_url");
                String resultRegisterMap = HttpClient.doPost(classin_register_url, strsBuilder.toString());

                log.info("resultRegisterMap is >>>" + resultRegisterMap);

                Map<String, Object> registerMap = new HashMap<String, Object>();
                if (resultRegisterMap != null && !resultRegisterMap.equals("")) {
                    registerMap = objectMapper.readValue(resultRegisterMap, Map.class);
                    Map<String, Object> registerErrorInfo = (Map<String, Object>) registerMap.get("error_info");
                    String registerErrno = registerErrorInfo.get("errno").toString();
                    if ("1".equals(registerErrno)) {
                        //继续添加教师
                        return "redirect:/classinCourseClass/addTeacherToClassin.do?course_id=" + course_id + "&type=" + type;
                    } else {
                        //用户注册失败
                        model.addAttribute("source", "来自BB的提示消息");
                        model.addAttribute("error", "您还未注册classin账号，请先至classin客户端注册~");
                        model.addAttribute("noregist", "noregist");
                        model.addAttribute("type", type);
                        return "/classin/tips";
                    }
                }
                model.addAttribute("source", "来自BB的提示消息");
                model.addAttribute("noregist", "noregist");
                model.addAttribute("error", "您还未注册classin账号，请先至classin客户端注册~");
                model.addAttribute("type", type);
                return "/classin/tips";

            } else {
                model.addAttribute("source", "来自classin的提示消息");
                model.addAttribute("errno", errno);
                model.addAttribute("error", error);
                model.addAttribute("type", type);
                return "/classin/tips";
            }
        } else {
            model.addAttribute("source", "来自BB的提示消息");
            model.addAttribute("error", "课节创建成功，如未跳转，请刷新页面后获取进入教室的链接~");
            model.addAttribute("type", type);
            return "/classin/tips";
        }

    }

    /**
     * 唤醒客户端
     *
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws PersistenceException
     */
    @RequestMapping("/awakeClassinClient.do")
    public String awakeClassinClient(HttpServletRequest request, HttpServletResponse response, Model
            model, String
                                             course_id, String tips, String type) throws
            JsonParseException, JsonMappingException, IOException, PersistenceException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        //唤醒客户端并进入教室入口
        String classinEntranceUrl = systemRegistryMapper.getClassinEntranceURL();

        String telephone = bbSession.getGlobalKey("telephone");
        String classinUid = bbSession.getGlobalKey("classinUid");
        String classinCourseId = bbSession.getGlobalKey("classinCourseId");
        String classinClassId = bbSession.getGlobalKey("classinClassId");

        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        //String param4 = "telephone="+telephone;
        //手机号改为uid
        String param4 = "uid=" + classinUid;
        String param5 = "courseId=" + classinCourseId;
        String param6 = "classId=" + classinClassId;
        ObjectMapper objectMapper = new ObjectMapper();

        //跳转到唤醒客户端页面
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5).append("&").append(param6);

        String resultLoginMap = HttpClient.doPost(classinEntranceUrl, stringBuilder.toString());

        log.info("resultLoginMap >>>>" + resultLoginMap);

        Map<String, Object> classInCourseClassIdMap = new HashMap<String, Object>();

        if (resultLoginMap != null && !resultLoginMap.equals("")) {

            classInCourseClassIdMap = objectMapper.readValue(resultLoginMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();

            //成功返回信息
            if ("1".equals(errno)) {
                String data = (String) classInCourseClassIdMap.get("data");
                String conditions = "";
                if (data != null && !data.equals("")) {
                    conditions = data.split("\\?")[1];
                }
                if ("classTeacher".equals(tips)) {
                    tips = "您已作为教师注册进课节中！";
                } else if ("alreadyClassStudent".equals(tips)) {
                    tips = "您已经是该课程下的学生，直接进入教室即可！";
                } else if ("classNoAssistant".equals(tips)) {
                    tips = "已作为学生注册进该课节！";
                } else if ("classStudent".equals(tips)) {
                    tips = "您已作为学生注册进该课节！";
                } else if ("teacherAndAssistant".equals(tips)) {
                    tips = "您已经是该课节的教师/助教，直接进入即可！";
                } else if ("teacherAddAsStudent".equals(tips)) {
                    tips = "您已作为学生注册进该课节！";
                } else if ("classAssistant".equals(tips)) {
                    tips = "您已作为助教注册进课程中！";
                } else {
                    tips = "您已在课节中，点击上方按钮进入教室即可！";
                }
                if ("meetingroom".equals(type)) {
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("classinClassId", classinClassId);
                    paramMap.put("classinCourseId", classinCourseId);
                    String liveURL = classinClassMeetingMapper.getLiveURLByClassId(paramMap);
                    model.addAttribute("liveURL", liveURL);
                }
                model.addAttribute("type", type);
                model.addAttribute("tips", tips);
                model.addAttribute("conditions", conditions);
                return "/classin/awakeClassIn";
            } else {
                model.addAttribute("source", "来自Classin的提示信息:获取进入教室url失败");
                model.addAttribute("errno", errno);
                model.addAttribute("error", error);
                model.addAttribute("type", type);
                return "/classin/tips";
            }
        } else {
            model.addAttribute("type", type);
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "获取进入教室url成功，如未跳转，请刷新页面后进行下一步操作~");
            return "/classin/tips";
        }
    }

    /**
     * 为课节添加助教（实际上是修改课节信息）
     *
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws PersistenceException
     */
    @RequestMapping("/addAsAssistant.do")
    public String addAsAssistant(HttpServletRequest request, HttpServletResponse response, Model model, String
            course_id, String type) throws JsonParseException, JsonMappingException, IOException, PersistenceException {

        final String classin_editCourseClass_url = systemRegistryMapper.getURLByKey("classin_editCourseClass_url");
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        String classinUid = bbSession.getGlobalKey("classinUid");
        String classinCourseId = bbSession.getGlobalKey("classinCourseId");
        String classinClassId = bbSession.getGlobalKey("classinClassId");

        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param4 = "assistantUid=" + classinUid;
        String param5 = "courseId=" + classinCourseId;
        String param6 = "classId=" + classinClassId;

        ObjectMapper objectMapper = new ObjectMapper();

        //跳转到唤醒客户端页面
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5).append("&").append(param6);

        String reslutEditClassMapString = HttpClient.doPost(classin_editCourseClass_url, stringBuilder.toString());
        log.info("resultLoginMap >>>>" + reslutEditClassMapString);

        Map<String, Object> editClassResultMap = new HashMap<String, Object>();

        if (reslutEditClassMapString != null && !reslutEditClassMapString.equals("")) {

            editClassResultMap = objectMapper.readValue(reslutEditClassMapString, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) editClassResultMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();

            //课节修改成功
            if ("1".equals(errno)) {
                if ("meetingroom".equals(type)) {
                    ClassinClassMeeting classinCourseassMeeting = (ClassinClassMeeting) bbSession.getGlobalObject("classinCourseClass");
                    if (classinCourseassMeeting != null) {
                        Map<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("assistantPhone", telephone);
                        paramMap.put("classinCourseId", classinCourseassMeeting.getClassinCourseId());
                        paramMap.put("classinCourseId", classinCourseassMeeting.getClassinCourseId());
                        paramMap.put("classinClassId", classinCourseassMeeting.getClassinClassId());
                        classinClassMeetingMapper.updateAssistantPhone(paramMap);
                    }
                } else {
                    ClassinCourseClass classinCourseClass = (ClassinCourseClass) bbSession.getGlobalObject("classinCourseClass");
                    if (classinCourseClass != null) {
                        Map<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("assistantPhone", telephone);
                        paramMap.put("classinCourseId", classinCourseClass.getClassinCourseId());
                        paramMap.put("classinClassId", classinCourseClass.getClassinClassId());
                        classinCourseClassMapper.updateAssistantPhone(paramMap);
                    }
                }
                //同时传conditions
                return "redirect:/classinCourseClass/awakeClassinClient.do?course_id=" + course_id + "&tips=classAssistant&type=" + type;
            } else if ("140".equals(errno)) {
                //课节正在上课，不能编辑或删除
                return "redirect:/classinCourseClass/addAsClassStudent.do?course_id=" + course_id + "&tips140=teacherAddAsStudent&type=" + type;
            } else if ("318".equals(errno)) {
                // 	表示助教不是本机构老师
                return "redirect:/classinCourseClass/addTeacherToClassin.do?course_id=" + course_id + "&flag=assistant&type=" + type;
            } else if ("319".equals(errno)) {
                //表示课程下的学生不能添加为助教
                return "redirect:/bbCourseClassinCourse/delCourseStudent.do?course_id=" + course_id + "&flag=editClass319&type=" + type;
            } else {
                model.addAttribute("source", "来自Classin的提示信息:添加助教失败");
                model.addAttribute("errno", errno);
                model.addAttribute("error", error);
                model.addAttribute("type", type);
                return "/classin/tips";
            }
        } else {
            model.addAttribute("type", type);
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "为课节添加助教成功，如未跳转，请刷新页面后获取进入教室的链接~");
            return "/classin/tips";
        }
    }

    /**
     * 添加为课节的学生/旁听用户
     *
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws PersistenceException
     */
    @RequestMapping("/addAsClassStudent.do")
    public String addAsClassStudent(HttpServletRequest request, HttpServletResponse response, Model
            model, String
                                            course_id, String tips140, String type) throws
            JsonParseException, JsonMappingException, IOException, PersistenceException {

        log.info("do addAsClassStudent");
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String telephone = bbSession.getGlobalKey("telephone");
        String classinUid = bbSession.getGlobalKey("classinUid");
        String classInCourseId = bbSession.getGlobalKey("classinCourseId");
        String userId = bbSession.getGlobalKey("userId");

        long currentLoignTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
        String param3 = "timeStamp=" + currentLoignTime;
        String param5 = "courseId=" + classInCourseId;

        //课程下添加学生/旁听
        String param_identity = "identity=1";
        //String param_studentAccount = "studentAccount="+telephone;
        //手机号修改为uid
        String param_studentAccount = "studentUid=" + classinUid;
        String param_studentName = "studentName=" + userId;

        String classin_addcoursestudent_url = systemRegistryMapper.getURLByKey("classin_addcoursestudent_url");

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param_identity).append("&").append(param_studentAccount).append("&")
                .append(param_studentName).append("&").append(param5);
        ObjectMapper objectMapper = new ObjectMapper();
        String addCourseStudentResultMapString = HttpClient.doPost(classin_addcoursestudent_url, sBuilder.toString());
        log.info("addCourseStudentResultMapString is >>>" + addCourseStudentResultMapString);
        Map<String, Object> addCourseStudentResultMap = new HashMap<String, Object>();
        if (addCourseStudentResultMapString != null && !"".equals(addCourseStudentResultMapString)) {
            addCourseStudentResultMap = objectMapper.readValue(addCourseStudentResultMapString, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) addCourseStudentResultMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();

            //添加学生成功
            if ("1".equals(errno)) {
                //唤醒客户端
                String tips = "";
                if (SystemUtil.isTeacher()) {
                    tips = "classNoAssistant";//只能作为学生注册进课程！
                    if (tips140 != null && "teacherAddAsStudent".equals(tips140)) {
                        tips = tips140;
                    }
                } else {
                    tips = "classStudent";//您已作为学生注册进该课程！
                }
                return "redirect:/classinCourseClass/awakeClassinClient.do?course_id=" + course_id + "&tips=" + tips + "&type=" + type;
            } else if ("163".equals(errno)) {
                //该用户已注册进该课程，直接进入
                String tips = "alreadyClassStudent";//您已经是该课节下的学生，请直接进入教室即可！

                return "redirect:/classinCourseClass/awakeClassinClient.do?course_id=" + course_id + "&tips=" + tips + "&type=" + type;
            } else if ("113".equals(errno)) {
                //该学生还未注册classin
                String bbUserId = bbSession.getGlobalKey("userId");
                //注册用户
                String param_nickname = "nickname=" + bbUserId;
                String param_pwd = "password=" + telephone;
                String param_telephone = "telephone=" + telephone;
                String param_schoolMemeber = "addToSchoolMember=1";
                StringBuilder strsBuilder = new StringBuilder();
                strsBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param_telephone)
                        .append("&").append(param_nickname).append("&").append(param_pwd).append("&").append(param_schoolMemeber);

                String classin_register_url = systemRegistryMapper.getURLByKey("classin_register_url");
                String resultRegisterMap = HttpClient.doPost(classin_register_url, strsBuilder.toString());
                log.info("resultRegisterMap is >>>" + resultRegisterMap);

                //继续添加学生
                return "redirect:/classinCourseClass/addAsClassStudent.do?course_id=" + course_id + "&type=" + type;
            } else if ("332".equals(errno)) {
                //课程老师或助教不能添加为课程学生或旁听
                return "redirect:/bbCourseClassinCourse/removeCourseTeacher.do?course_id=" + course_id + "&type=" + type;
            } else {
                model.addAttribute("source", "来自Classin的提示信息:为课程添加学生失败");
                model.addAttribute("errno", errno);
                model.addAttribute("error", error);
                model.addAttribute("type", type);
                return "/classin/tips";
            }
        } else {
            model.addAttribute("type", type);
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "已将您成功注册为该课节的学生，如未跳转，请刷新页面后获取进入教室的链接~");
            return "/classin/tips";
        }
    }

    /**
     * 获取历史回放列表
     *
     * @param request
     * @param response
     * @param model
     * @param course_id
     * @return
     * @throws PersistenceException
     */
    @RequestMapping("/getRepalyList.do")
    public String getRepalyList(HttpServletRequest request, HttpServletResponse response, Model model, String
            course_id) throws PersistenceException {
        log.info("get getRepalyList");
        Course course = SystemUtil.getCourseById(course_id);
        String bbCourseId = course.getCourseId();
        long currentTimeStamp = System.currentTimeMillis()/1000;
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + bbCourseId);
        HashMap<String, Object> paraMap = new HashMap<>();
        paraMap.put("bbCourseId",bbCourseId);
        paraMap.put("currentTimeStamp",currentTimeStamp+"");
        if (bbCourseId != null && !"".equals(bbCourseId)) {
            //根据classinCourseId获取已过期的且有回放URL的
            List<ClassinCourseClass> classinCourseClassList = classinCourseClassMapper.getReplayList(paraMap);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>replay list counts"+classinCourseClassList.size());
            //   log.info("》》》》》》》》》》》》》》》liveURL" + classinCourseClassList.get(0).getLiveURL());
            if (classinCourseClassList != null && classinCourseClassList.size() != 0) {
                model.addAttribute("classinCourseClassList", classinCourseClassList);
                model.addAttribute("isTeacher", SystemUtil.isTeacher());
            }
            return "/classin/replayList";
        } else {
            return "/classin/replayList";
        }
    }

}
