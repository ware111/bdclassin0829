package com.blackboard.classin.controller;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.UserPhone;
import com.blackboard.classin.entity.UserPhoneName;
import com.blackboard.classin.mapper.ClassinCourseClassMapper;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import com.blackboard.classin.mapper.UserPhoneMapper;
import com.blackboard.classin.util.HttpClient;
import com.blackboard.classin.util.SystemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userPhone")
public class UserPhoneController {

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private SystemRegistryMapper systemRegistryMapper;

    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    private Logger log = Logger.getLogger(UserPhoneController.class);


    @RequestMapping("/isBindPhone.do")
    public String bindPhone(HttpServletRequest request, HttpServletResponse response, String course_id, String type, Model model) throws PersistenceException, JsonParseException, JsonMappingException, IOException {

        User user = SystemUtil.getCurrentUser();
        Course course = SystemUtil.getCourseById(course_id);
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        bbSession.setGlobalKey("course_id", course_id);
        bbSession.setGlobalKey("userId", user.getUserName());
        bbSession.setGlobalKey("courseId", course.getCourseId());
        bbSession.setGlobalKey("courseName", course.getTitle());
        log.info("用户名:" + user.getUserName());
        //查找手机号
        UserPhone userPhone = userPhoneMapper.findPhoneByUserId(user.getUserName());
        if (userPhone != null) {
            log.info("phone=" + userPhone.getPhone());
            //用户绑定了手机号，直接用
            String phone = userPhone.getPhone();
            String classinUid = userPhone.getClassinUid();
            String parma1 = "";
            String parma2 = "";
            String parma3 = "";
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultRegisterMap = new HashMap<String, Object>();
            if (classinUid == null || "".equals(classinUid)) {
                //注册用户
                long currentCreateClassTime = System.currentTimeMillis() / 1000;
                parma1 = "SID=" + Constants.SID;
                parma2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
                parma3 = "timeStamp=" + currentCreateClassTime;
                String param_nickname = "nickname=" + user.getUserName();
                String param_pwd = "password=" + phone;
                String param_telephone = "telephone=" + phone;
                String param_identity = "";

                StringBuilder strsBuilder = new StringBuilder();
                strsBuilder.append(parma1).append("&").append(parma2).append("&").append(parma3).append("&").append(param_telephone)
                        .append("&").append(param_nickname).append("&").append(param_pwd).append("&");

                String classin_register_url = systemRegistryMapper.getURLByKey("classin_register_url");
                String resultRegisterMapStr = HttpClient.doPost(classin_register_url, strsBuilder.toString());
                log.info("resultRegisterMap is >>>" + resultRegisterMapStr);

                if (resultRegisterMapStr != null && !"".equals(resultRegisterMapStr)) {
                    resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
                    //解析返回的数据
                    Map<String, Object> errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
                    String errno = errorInfo.get("errno").toString();
                    String error = errorInfo.get("error").toString();
                    if ("1".equals(errno) || "135".equals(errno)) {
                        classinUid = resultRegisterMap.get("data").toString();
                        userPhone.setClassinUid(classinUid);
                        bbSession.setGlobalKey("classinUid", classinUid);
                        userPhoneMapper.updatePhone(userPhone);
                    } else {
                        model.addAttribute("type", type);
                        model.addAttribute("source", "来自BB的提示信息");
                        model.addAttribute("error", "由于网络不稳定，获取用户uid失败，请刷新页面后重试~");
                        return "/classin/tips";
                    }
                }
//            } else {
//                User currentUser = SystemUtil.getCurrentUser();
//                if (SystemUtil.isTeacher()) {
//                    String classin_addteacher_url = systemRegistryMapper.getURLByKey("classin_addteacher_url");
//                    long currentCreateClassTime = System.currentTimeMillis() / 1000;
//                    parma1 = "SID=" + Constants.SID;
//                    parma2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
//                    parma3 = "timeStamp=" + currentCreateClassTime;
//                    String param_teacherAccount = "teacherAccount=" + phone;
//                    String param_teacherName = "teacherName=" + currentUser.getFamilyName() + currentUser.getMiddleName()
//                            + currentUser.getGivenName();
//                    StringBuilder strsBuilder = new StringBuilder();
//                    strsBuilder.append(parma1).append("&").append(parma2).append("&").append(parma3).append("&").
//                            append(param_teacherAccount).append("&").append(param_teacherName);
//                    String resultRegisterMapStr = HttpClient.doPost(classin_addteacher_url, strsBuilder.toString());
//                    resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
//                    Map<String, Object> errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
//                    String errno = errorInfo.get("errno").toString();
//                    String error = errorInfo.get("error").toString();
//                    if (!"1".equals(errno) && !"133".equals(errno)){
//                        model.addAttribute("type", type);
//                        model.addAttribute("source", "来自BB的提示信息");
//                        model.addAttribute("error", error);
//                        return "/classin/tips";
//                    }
//                } else{
//
//                    String classin_addstudent_url = systemRegistryMapper.getURLByKey("classin_addstudent_url");
//                    long currentCreateClassTime = System.currentTimeMillis() / 1000;
//                    parma1 = "SID=" + Constants.SID;
//                    parma2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
//                    parma3 = "timeStamp=" + currentCreateClassTime;
//                    String param_studentAccount = "studentAccount=" + phone;
//                    String param_studentName = "studentName=" + currentUser.getFamilyName() + currentUser.getMiddleName()
//                            + currentUser.getGivenName();;
//                    StringBuilder strsBuilder = new StringBuilder();
//                    strsBuilder.append(parma1).append("&").append(parma2).append("&").append(parma3).append("&").
//                            append(param_studentAccount).append("&").append(param_studentName);
//                    String resultRegisterMapStr = HttpClient.doPost(classin_addstudent_url, strsBuilder.toString());
//                    resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
//                    Map<String, Object> errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
//                    String errno = errorInfo.get("errno").toString();
//                    String error = errorInfo.get("error").toString();
//                    if (!"1".equals(errno) && !"133".equals(errno)){
//                        model.addAttribute("type", type);
//                        model.addAttribute("source", "来自BB的提示信息");
//                        model.addAttribute("error", "由于网络不稳定，获取用户uid失败，请刷新页面后重试~");
//                        return "/classin/tips";
//                    }
//                }
            }
            bbSession.setGlobalKey("telephone", phone);
            bbSession.setGlobalKey("classinUid", classinUid);
            return "redirect:/classinCourseClass/getHomeClassList.do?course_id=" + course_id;
        } else {
            //用户未绑定手机号，需要初始化
            model.addAttribute("type", type);
            log.info("no phone");
            return "/classin/phone";
        }
        // return "redirect:/bbCourseClassinCourse/create.do?course_id="+course_id+"&type="+type;
    }


    @RequestMapping("/save.do")
    public String save(HttpServletRequest request, HttpServletResponse response,
                       String telephone, String course_id, String type, Model model, String validNum) throws PersistenceException, JsonParseException, JsonMappingException, IOException {

        log.info("save phone");
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String validNumSession = bbSession.getGlobalKey("validNumSession");
        if (validNumSession == null || !validNumSession.equals(validNum)) {
            model.addAttribute("telephone", telephone);
            model.addAttribute("type", type);
            model.addAttribute("course_id", course_id);
            model.addAttribute("tips", "您输入的手机验证码不正确，请重新输入~");
            return "/classin/phone";
        }

        String userId = bbSession.getGlobalKey("userId");
        UserPhone userPhone = userPhoneMapper.findByPhone(telephone);
        if (userPhone != null && !userPhone.getUserId().equals(userId)) {
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "该手机号已被其他账号绑定，请重新输入！");
            model.addAttribute("type", type);
            return "/classin/tips";
        }
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("userId", userId);
        paramMap.put("phone", telephone);

        //注册用户
        long currentCreateClassTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String param3 = "timeStamp=" + currentCreateClassTime;
        String param_nickname = "nickname=" + userId;
        String param_pwd = "password=" + telephone;
        String param_telephone = "telephone=" + telephone;
        String param_identity = "";
        if (SystemUtil.isStudent()) {
            param_identity = "addToSchoolMember=1";
        } else {
            param_identity = "addToSchoolMember=2";
        }
        StringBuilder strsBuilder = new StringBuilder();
        strsBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param_telephone)
                .append("&").append(param_nickname).append("&").append(param_pwd).append("&").append(param_identity);
        String classinUid = "";
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
                try {
                    paramMap.put("classinUid", classinUid);
                    //保存用户和手机号
                    userPhoneMapper.save(paramMap);


//                    //编辑课节
//                    boolean isClassTeacher = SystemUtil.isClassTeacher();
//                    Course course = SystemUtil.getCourseById(course_id);
//                    String bbCourseId = course.getCourseId();
//                    HashMap<String, Object> paraMap = new HashMap<>();
//                    paraMap.put("teacherPhone", telephone);
//                    paraMap.put("bbCourseId", bbCourseId);
////                    if (isClassTeacher) {
////                        classinCourseClassMapper.editTeacher(paraMap);
////                    }
////                    if (isAssistantTeacher) {
////                        List<Map<String, String>> classinIds = classinCourseClassMapper.findClassinIdByBBCourseId(bbCourseId);
////                        for (Map<String, String> classinId : classinIds) {
////                            String classinCourseId = classinId.get("classin_course_id");
////                            String classId = classinId.get("classin_class_id");
////                            long currentLoignTime = System.currentTimeMillis() / 1000;
////                            String param5 = "courseId=" + classinCourseId;
////                            String param6 = "classId=" + classId;
////                            String param4 = "teacherUid=" + classinUid;
////                            String classinEditCourseClassURL = "";
////                            classinEditCourseClassURL = systemRegistryMapper.getURLByKey("classin_editCourseClass_url");
////                            StringBuilder stringBuilder = new StringBuilder();
////                            String param7 = "assistantUid=" + classinUid;
////                            stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
////                                    .append("&").append(param4).append("&").append(param5).append("&").append(param7).append("&").append(param6);
////                            String resultMap = HttpClient.doPost(classinEditCourseClassURL, stringBuilder.toString());
////                            JSONObject jsonObject = new JSONObject();
////                            objectMapper = new ObjectMapper();
////                            Map<String, Object> resultHashMap = new HashMap<>();
////                            HashMap<String, Object> classMap = new HashMap<>();
////                            if (resultMap != null && !resultMap.equals("")) {
////                                resultHashMap = objectMapper.readValue(resultMap, Map.class);
////                                //解析返回的数据
////                                errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
////                                errno = errorInfo.get("errno").toString();
////                                error = errorInfo.get("error").toString();
////                                //成功返回信息
////                                if ("1".equals(errno)) {
////                                    Map<String, String> moreData = (Map<String, String>) resultHashMap.get("more_data");
////                                } else {
////                                    model.addAttribute("source", "来自Classin的提示信息");
////                                    model.addAttribute("error", error);
////                                    return "/classin/tips";
////                                }
////                            } else {
////                                model.addAttribute("source", "来自BB的提示信息");
////                                model.addAttribute("error", "系统错误");
////                                return "/classin/tips";
////                            }
////                        }
////
////                    }
//                    if (isClassTeacher) {
//                        long currentTimeStamp = System.currentTimeMillis() / 1000;
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("bbCourseId",bbCourseId);
//                        map.put("currentTimeStamp",currentTimeStamp+"");
//                        List<Map<String, String>> classinIds = classinCourseClassMapper.findClassinIdByBBCourseId(map);
//                        for (Map<String, String> classinId : classinIds) {
//                            String classinCourseId = classinId.get("CLASSIN_COURSE_ID");
//                            String classId = classinId.get("CLASSIN_CLASS_ID");
//                            String param5 = "courseId=" + classinCourseId;
//                            String param6 = "classId=" + classId;
//                            String param4 = "teacherUid=" + classinUid;
//                            String classinEditCourseClassURL = "";
//                            classinEditCourseClassURL = systemRegistryMapper.getURLByKey("classin_editCourseClass_url");
//                            StringBuilder stringBuilder = new StringBuilder();
//                                stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
//                                        .append("&").append(param4).append("&").append(param5).append("&").append(param6);
//                            String resultMap = HttpClient.doPost(classinEditCourseClassURL, stringBuilder.toString());
//                            objectMapper = new ObjectMapper();
//                            Map<String, Object> resultHashMap = new HashMap<>();
//                            HashMap<String, Object> classMap = new HashMap<>();
//                            if (resultMap != null && !resultMap.equals("")) {
//                                resultHashMap = objectMapper.readValue(resultMap, Map.class);
//                                //解析返回的数据
//                                errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
//                                errno = errorInfo.get("errno").toString();
//                                error = errorInfo.get("error").toString();
//                                //成功返回信息
//                                if ("1".equals(errno)) {
//                                    log.info(">>>>>>>>>>>>>>绑定手机后更新成功");
//                                    Map<String, String> moreData = (Map<String, String>) resultHashMap.get("more_data");
//                                    String liveURL = moreData.get("live_url");
//                                    classMap.put("classId", classId);
//                                    classMap.put("teacherPhone", telephone);
//                                    classMap.put("liveURL",liveURL);
//                                    log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<修改新手机号成功");
//                                    classinCourseClassMapper.editClassTeacher(classMap);
//                                } else {
//                                    model.addAttribute("source", "来自Classin的提示信息");
//                                    model.addAttribute("error", error);
//                                    return "/classin/tips";
//                                }
//                            } else {
//                                model.addAttribute("source", "来自BB的提示信息");
//                                model.addAttribute("error", "系统错误");
//                                return "/classin/tips";
//                            }
//                        }
//                    }

                } catch (Exception e) {
                    UserPhone userPhone1 = userPhoneMapper.findByPhone(telephone);
                    if (userPhone1 != null && userId.equals(userPhone1.getUserId())) {
                        return "redirect:/userPhone/isBindPhone.do?type=" + type + "&course_id=" + course_id;
                    }
                }
            } else {
                model.addAttribute("type", type);
                model.addAttribute("source", "来自BB的提示信息");
                model.addAttribute("error", "手机号绑定失败，请刷新页面重试~");
                return "/classin/tips";
            }

        }
        boolean isTeacher = SystemUtil.isTeacher();
        bbSession.setGlobalKey("telephone", telephone);
        bbSession.setGlobalKey("classinUid", classinUid);
        if (SystemUtil.isTeacher()) {//教师
            model.addAttribute("role", "teacher");
        } else {//学生
            model.addAttribute("role", "student");
        }
        model.addAttribute("type", type);
        return "/classin/index";
    }

    /**
     * 修改绑定的手机号
     *
     * @param request
     * @param response
     * @return
     * @throws PersistenceException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @RequestMapping("/updatePhoneNum.do")
    public String updatePhoneNum(Model model, HttpServletRequest request, HttpServletResponse response,
                                 String newPhoneNum, String type, String course_id, String validNum) throws PersistenceException, JsonParseException, JsonMappingException, IOException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String verifcode = bbSession.getGlobalKey("validNumSession");
        System.out.println("validNumSession=====2===" + verifcode);
        String userId = bbSession.getGlobalKey("userId");
        UserPhone userPhone = userPhoneMapper.findPhoneByUserId(userId);
        userPhone.setPhone(newPhoneNum);

        if (verifcode == null || !verifcode.equals(validNum)) {
            model.addAttribute("type", type);
            model.addAttribute("course_id", course_id);
            model.addAttribute("userPhone", userPhone);
            model.addAttribute("confirmTelephone", newPhoneNum);
            model.addAttribute("tips", "您输入的手机验证码不正确，请重新获取并输入~");
            return "/classin/updatePhone";
        }
        //注册用户
        long currentCreateClassTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String param3 = "timeStamp=" + currentCreateClassTime;
        String param_nickname = "nickname=" + userId;
        String param_pwd = "password=" + newPhoneNum;
        String param_telephone = "telephone=" + newPhoneNum;
        StringBuilder strsBuilder = new StringBuilder();
        strsBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param_telephone)
                .append("&").append(param_nickname).append("&").append(param_pwd).append("&");

        String classin_register_url = "https://www.eeo.cn/partner/api/course.api.php?action=register";
        String resultRegisterMapStr = HttpClient.doPost(classin_register_url, strsBuilder.toString());
        log.info("resultRegisterMap is >>>" + resultRegisterMapStr);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultRegisterMap = new HashMap<String, Object>();
        if (resultRegisterMapStr != null && !"".equals(resultRegisterMapStr)) {
            resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            if ("1".equals(errno) || "135".equals(errno)) {
                String classinUid = resultRegisterMap.get("data").toString();
                userPhone.setClassinUid(classinUid);
                bbSession.setGlobalKey("classinUid", classinUid);
                userPhoneMapper.updatePhone(userPhone);
                boolean isClassTeacher = SystemUtil.isClassTeacher();
                Course course = SystemUtil.getCourseById(course_id);
                String bbCourseId = course.getCourseId();
                HashMap<String, Object> paraMap = new HashMap<>();
                paraMap.put("teacherPhone", newPhoneNum);
                paraMap.put("bbCourseId", bbCourseId);
//
                if (isClassTeacher) {
                    long currentTimeStamp = System.currentTimeMillis() / 1000;
                    HashMap<String, String> map = new HashMap<>();
                    map.put("bbCourseId", bbCourseId);
                    map.put("currentTimeStamp", currentTimeStamp + "");
                    List<Map<String, String>> classinIds = classinCourseClassMapper.findClassinIdByBBCourseId(map);
                    User currentUser = SystemUtil.getCurrentUser();
                    String userName = currentUser.getUserName();
                    for (Map<String, String> classinId : classinIds) {
                        if (userName.equals(classinId.get("USER_NAME"))) {
                            String classinCourseId = classinId.get("CLASSIN_COURSE_ID");
                            String classId = classinId.get("CLASSIN_CLASS_ID");
                            String param5 = "courseId=" + classinCourseId;
                            String param6 = "classId=" + classId;
                            String param4 = "teacherUid=" + classinUid;
                            String classinEditCourseClassURL = "";
                            classinEditCourseClassURL = systemRegistryMapper.getURLByKey("classin_editCourseClass_url");
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                                    .append("&").append(param4).append("&").append(param5).append("&").append(param6);
                            String resultMap = HttpClient.doPost(classinEditCourseClassURL, stringBuilder.toString());
                            objectMapper = new ObjectMapper();
                            Map<String, Object> resultHashMap = new HashMap<>();
                            HashMap<String, Object> classMap = new HashMap<>();
                            if (resultMap != null && !resultMap.equals("")) {
                                resultHashMap = objectMapper.readValue(resultMap, Map.class);
                                //解析返回的数据
                                errorInfo = (Map<String, Object>) resultHashMap.get("error_info");
                                errno = errorInfo.get("errno").toString();
                                String error = errorInfo.get("error").toString();
                                //成功返回信息
                                if ("1".equals(errno)) {
                                    log.info(">>>>>>>>>>>>>>绑定手机后更新成功");
                                    Map<String, String> moreData = (Map<String, String>) resultHashMap.get("more_data");
                                    String liveURL = moreData.get("live_url");
                                    classMap.put("classId", classId);
                                    classMap.put("teacherPhone", newPhoneNum);
                                    classMap.put("liveURL", liveURL);
                                    classMap.put("userName", userId);
                                    classMap.put("teacherName", currentUser.getFamilyName() + currentUser.getMiddleName() + currentUser.getGivenName());
                                    log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<修改新手机号成功");
                                    classinCourseClassMapper.editClassTeacher(classMap);
                                } else {
                                    model.addAttribute("source", "来自Classin的提示信息");
                                    model.addAttribute("error", error);
                                    return "/classin/tips";
                                }
                            } else {
                                model.addAttribute("source", "来自BB的提示信息");
                                model.addAttribute("error", "系统错误");
                                return "/classin/tips";
                            }
                        }
                    }
                }
            }
        } else {
            model.addAttribute("type", type);
            model.addAttribute("source", "来自BB的提示信息");
            model.addAttribute("error", "手机号修改失败，请刷新页面重试~");
            return "/classin/tips";
        }
        return "redirect:/userPhone/isBindPhone.do?type=" + type + "&course_id=" + course_id;
    }

    /**
     * 解绑手机号
     *
     * @param model
     * @param request
     * @param response
     * @param type
     * @return
     * @throws PersistenceException
     */
    @RequestMapping("/disbindPhone.do")
    public String disbindPhone(Model model, HttpServletRequest request, HttpServletResponse response,
                               String course_id, String type) throws PersistenceException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String userId = bbSession.getGlobalKey("userId");
        UserPhone userPhone = userPhoneMapper.findPhoneByUserId(userId);
        if (userPhone != null) {
            userPhoneMapper.delete(userPhone);
        }
        return "redirect:/userPhone/isBindPhone.do?type=" + type + "&course_id=" + course_id;
    }

    /**
     * 跳转到修改页面
     *
     * @param model
     * @param request
     * @param response
     * @param type
     * @param course_id
     * @return
     * @throws PersistenceException
     */
    @RequestMapping("/toUpdatePhone.do")
    public String toUpdatePhone(Model model, HttpServletRequest request, HttpServletResponse response,
                                String type, String course_id) throws PersistenceException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String userId = bbSession.getGlobalKey("userId");
        UserPhone userPhone = userPhoneMapper.findPhoneByUserId(userId);
        model.addAttribute("userPhone", userPhone);
        model.addAttribute("type", type);
        return "/classin/updatePhone";
    }

    /**
     * 获取短信验证码
     *
     * @param model
     * @param request
     * @param response
     * @param type
     * @param course_id
     * @param telephone
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws PersistenceException
     */
    @RequestMapping("/getValidNum.do")
    @ResponseBody
    public JSONObject getValidNum(Model model, HttpServletRequest request, HttpServletResponse response,
                                  String type, String course_id, String telephone, String page) throws JsonParseException, JsonMappingException, IOException, PersistenceException {
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        String validNum = SystemUtil.getlinkNo();
        log.info(validNum + "***********************");
        bbSession.setGlobalKey("validNumSession", validNum);
        System.out.println("validNumSession===1===" + bbSession.getGlobalKey("validNumSession"));
        ObjectMapper objectMapper = new ObjectMapper();
        String sendValidNumURL = "https://api.eeo.cn/partner/api/schooin.api.php?action=sendSmsVerifcode";

        long currentCreateClassTime = System.currentTimeMillis() / 1000;
        String parma1 = "SID=" + Constants.SID;
        String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
        String param3 = "timeStamp=" + currentCreateClassTime;
        String param4 = "telephone=" + telephone;
        String param5 = "verifcode=" + validNum;

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                .append("&").append(param4).append("&").append(param5);

        String resultMap = HttpClient.doPost(sendValidNumURL, stringBuilder.toString());

        log.info("resultSendVerifcodeMap >>>>" + resultMap);
        log.info(sendValidNumURL + "/" + stringBuilder);
        Map<String, Object> sendVerifcodeMap = new HashMap<String, Object>();
        String tips = "";
        if (resultMap != null && !resultMap.equals("")) {
            sendVerifcodeMap = objectMapper.readValue(resultMap, Map.class);
            //解析返回的数据
            Map<String, Object> errorInfo = (Map<String, Object>) sendVerifcodeMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            if ("1".equals(errno)) {
                //获取验证码成功，跳转到页面输入
                tips = "验证码已发送到您的手机,请注意查收,如未收到,请立即联系classin人员~";
            } else if ("108".equals(errno)) {
                tips = "验证码发送次数已达上限";
            } else {
                //验证码获取失败，需要提示，重新获取
                tips = "获取验证码失败,请重试~";
            }
        }
        return SystemUtil.buildResultMap(1, tips);
    }

    /**
     * 调用classin接口编辑教师
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @RequestMapping(value = "/editTeacher.do", method = {RequestMethod.GET})
    @ResponseBody
    public JSONObject editTeacher(HttpServletRequest request, HttpServletResponse response)
            throws JsonParseException, JsonMappingException, IOException {
        log.info("classin调用接口编辑教师");

        //查找用户、姓名、手机号对应关系
        List<UserPhoneName> userPhoneNameList = userPhoneMapper.findNameList();
        if (userPhoneNameList != null && userPhoneNameList.size() != 0) {
            int i = 0;
            long currentTime = System.currentTimeMillis() / 1000;
            ObjectMapper objectMapper = new ObjectMapper();
            String classin_regist_url = "https://www.eeo.cn/partner/api/schooin.api.php?action=register";
            String classin_editteacher_url = "https://www.eeo.cn/partner/api/course.api.php?action=editTeacher";
            for (UserPhoneName userPhoneName : userPhoneNameList) {
                String phone = userPhoneName.getPhone();
                String familyName = userPhoneName.getFamilyname();
                String userId = userPhoneName.getUserId();

                String parma1 = "SID=" + Constants.SID;
                String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentTime);
                String param3 = "timeStamp=" + currentTime;
                String param4 = "telephone=" + phone;
                String param5 = "password=" + phone;
                String param6 = "addToSchoolMember=2";
                String param7 = "nickName=" + familyName + "-" + userId;

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
                        .append("&").append(param4).append("&").append(param5).append("&").append(param6)
                        .append("&").append(param7);
                //调用注册接口，主要想返回用户uid
                String registResult = HttpClient.doPost(classin_regist_url, stringBuilder.toString());
                Map<String, Object> registerMap = new HashMap<String, Object>();
                if (registResult != null && !"".equals(registResult)) {
                    registerMap = objectMapper.readValue(registResult, Map.class);
                    Map<String, Object> registerErrorInfo = (Map<String, Object>) registerMap.get("error_info");
                    String registerErrno = registerErrorInfo.get("errno").toString();
                    //已经存在
                    if ("133".equals(registerErrno)) {
                        String teacherUid = registerMap.get("data").toString();

                        String param_uid = "teacherUid=" + teacherUid;
                        String param_uname = "teacherName=" + familyName + "-" + userId;

                        StringBuilder stringBuilder1 = new StringBuilder();
                        stringBuilder1.append(parma1).append("&").append(param2).append("&").append(param3)
                                .append("&").append(param_uid).append("&").append(param_uname);
                        //编辑教师
                        String editTeacherResult = HttpClient.doPost(classin_editteacher_url, stringBuilder1.toString());
                        Map<String, Object> editResultMap = new HashMap<>();
                        if (editTeacherResult != null && !"".equals(editTeacherResult)) {
                            editResultMap = objectMapper.readValue(editTeacherResult, Map.class);
                            Map<String, Object> editErrorInfo = (Map<String, Object>) editResultMap.get("error_info");
                            String editTeacherErrno = editErrorInfo.get("errno").toString();
                            if ("1".equals(editTeacherErrno)) {
                                //编辑成功
                                i++;
                                userPhoneMapper.updateUpdateFlag(userPhoneName);
                            }
                        }
                        continue;
                    } else if ("1".equals(registerErrno)) {
                        //注册成功
                        System.out.println("用户：" + phone + "-注册成功！");
                        continue;
                    }
                }
            }
            return SystemUtil.buildResultMap(i, "程序正常执行");
        }
        return SystemUtil.buildResultMap(0, "程序执行失败");
    }
}
