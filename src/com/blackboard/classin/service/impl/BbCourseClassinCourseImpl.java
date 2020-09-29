package com.blackboard.classin.service.impl;

import blackboard.base.BbList;
import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.*;
import com.blackboard.classin.entity.CourseStudentPOJO.ErrorInfo;
import com.blackboard.classin.mapper.*;
import com.blackboard.classin.service.IBbCourseClassinCourse;
import com.blackboard.classin.util.HttpClient;
import com.blackboard.classin.util.SystemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BbCourseClassinCourseImpl implements IBbCourseClassinCourse {

    private Logger log = Logger.getLogger(BbCourseClassinCourseImpl.class);

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Autowired
    private ClassinClassMeetingMapper classinClassMeetingMapper;

    @Autowired
    private SystemRegistryMapper systemRegistryMapper;

    /**
     * 创建classinCourse 并绑定BBCourse
     * <p>
     * return 0:存在在线课堂，并且有助教
     * return 1:不存在在线课堂
     * return 2:代表创建在线课堂失败
     * return 3:未获取到classin返回信息
     *
     * @throws ParseException
     * @throws PersistenceException
     */
    @Override
    public String createClassinCourseOnBbCourse(HttpServletRequest request, HttpServletResponse response, String course_id, String classin_addcourse_url, String type) throws ParseException, PersistenceException {

        Course course = SystemUtil.getCourseById(course_id);
        String courseId = course.getCourseId();

        //课程下是否有classin course
        BbCourseClassinCourse bbCourseClassinCourse = bbCourseClassinCourseMapper.findByCourseId(courseId);
        SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
        BbSession bbSession = sessionManager.getSession(request, response);
        if (bbCourseClassinCourse == null) {//课程下未绑定classin course，需要绑定
            String userId = bbSession.getGlobalKey("userId");
            //创建classin课堂课程
            log.info("create classin course---courseId=" + courseId + "---userId=" + userId);

            //时间戳以秒为单位
            long currentTime = System.currentTimeMillis() / 1000;

            String parma1 = "SID=" + Constants.SID;
            String param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentTime);
            String param3 = "timeStamp=" + currentTime;
            String param4 = "courseName=" + course.getTitle();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param4);

            //classIn返回的信息
            String resultMap_classInCourseIdMap = HttpClient.doPost(classin_addcourse_url, stringBuilder.toString());

            log.info("resultMap_classInCourseIdMap is>>>" + resultMap_classInCourseIdMap);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> classInCourseIdMap = new HashMap<String, Object>();

            if (resultMap_classInCourseIdMap != null && !"".equals(resultMap_classInCourseIdMap)) {
                try {
                    classInCourseIdMap = objectMapper.readValue(resultMap_classInCourseIdMap, Map.class);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //解析返回的数据
                Map<String, Object> errorInfo = (Map<String, Object>) classInCourseIdMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();

                bbSession.setGlobalKey("errno", errno);
                bbSession.setGlobalKey("error", error);

                if ("1".equals(errno)) {
                    //创建classin课堂成功，获取classin_course_id
                    String classinCourseId = classInCourseIdMap.get("data").toString();
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("bbCourseId", courseId);
                    paramMap.put("classinCourseId", classinCourseId);
                    try {
                        BBCourseClassinCourseInfo courseInfo = new BBCourseClassinCourseInfo();
                        courseInfo.setBbCourseId(courseId);
                        courseInfo.setClassinCourseId(classinCourseId);
                        bbCourseClassinCourseMapper.createClassinCourseOnBbCourse(courseInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    bbSession.setGlobalKey("classinCourseId", classinCourseId);

                    return "NeedToAddAsTeacher";
                } else {
                    //创建在线课堂课程失败
                    return "CreateClassinCourseFailed";
                }
            } else {
                //未获取到classin返回信息，网络问题
                return "NetworkIsInstability";
            }
        } else {
            //bb课程已绑定classin course
            String classinCourseId = bbCourseClassinCourse.getClassinCourseId();
            bbSession.setGlobalKey("classinCourseId", classinCourseId);
            //查找课堂下有无研讨室
            if (type != null && "meetingroom".equals(type)) {
                ClassinClassMeeting classinClassMeeting = classinClassMeetingMapper.findByClassinCourseId(classinCourseId);
                //you研讨室，首先判断是否过期
                if (classinClassMeeting != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dtCreated = classinClassMeeting.getDtCreated();
                    Date date = sdf.parse(dtCreated);
                    if ((System.currentTimeMillis() - date.getTime()) > 4 * 60 * 60 * 1000) {
                        Map<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("classinCourseId", classinClassMeeting.getClassinCourseId());
                        paramMap.put("classinClassId", classinClassMeeting.getClassinClassId());
                        classinClassMeetingMapper.updateToExpired(paramMap);

                        //设置过期之后，代表课程下无课节，需要创建
                        return "NeedToCreateClassinClass";
                    } else {
                        bbSession.setGlobalObject("classinCourseClass", classinClassMeeting);
                        //课程下存在在线课堂，看老师是否注册为助教或学生
                        bbSession.setGlobalKey("classinClassId", classinClassMeeting.getClassinClassId());
                        String telephone = bbSession.getGlobalKey("telephone");

                        String assistantPhone = classinClassMeeting.getAssistantPhone();
                        System.out.println(assistantPhone);
                        //是该课节的教师或助教
                        if (telephone.equals(classinClassMeeting.getTeacherPhone()) || telephone.equals(assistantPhone)) {
                            return "awakeClassinClient";
                        }

                        if (assistantPhone == null || assistantPhone.equals("")) {
                            return "NeedToAddAsAssistant";
                        } else {
                            return "NeedToAddAsStudent";
                        }
                    }
                } else {
                    //课程下不存在进行中的课节，该教师作为主讲人进入课程
                    return "NeedToCreateClassinClass";
                }
            } else {
                //查找其下有无在线课堂
                //获取课节，首先判断其是否已过期，过期设置为1
                ClassinCourseClass classinCourseClass = classinCourseClassMapper.findByClassinCourseId(classinCourseId);
                if (classinCourseClass != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dtCreated = classinCourseClass.getDtCreated();
                    Date date = sdf.parse(dtCreated);

                    //课节总时长 分钟
                    int classTimeLength = classinCourseClass.getClassTimeLength();
                    //课节拖堂时间
                    int closeClassDelay = classinCourseClass.getCloseClassDelay();
                    //总课节时长+拖堂时间
                    int totalTimeLength = classTimeLength + closeClassDelay;

                    if ((System.currentTimeMillis() - date.getTime()) >= classTimeLength * 1000) {
                        Map<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("classinCourseId", classinCourseClass.getClassinCourseId());
                        paramMap.put("classinClassId", classinCourseClass.getClassinClassId());
                        classinCourseClassMapper.updateToExpired(paramMap);

                        //设置过期之后，代表课程下无课节，需要创建
                        return "NeedToCreateClassinClass";
                    } else {

                        bbSession.setGlobalObject("classinCourseClass", classinCourseClass);
                        //课程下存在在线课堂，看老师是否注册为助教或学生
                        bbSession.setGlobalKey("classinClassId", classinCourseClass.getClassinClassId());
                        String telephone = bbSession.getGlobalKey("telephone");

                        String assistantPhone = classinCourseClass.getAssistantPhone();
                        System.out.println(assistantPhone);
                        //是该课节的教师或助教
                        if (telephone.equals(classinCourseClass.getTeacherPhone()) || telephone.equals(assistantPhone)) {
                            return "awakeClassinClient";
                        }

                        if (assistantPhone == null || assistantPhone.equals("")) {
                            return "NeedToAddAsAssistant";
                        } else {
                            return "NeedToAddAsStudent";
                        }
                    }
                } else {
                    //课程下不存在进行中的课节，该教师作为主讲人进入课程
                    return "NeedToCreateClassinClass";
                }
            }
        }
    }

    /**
     * 比对删除Classin课程下的学生用户
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException   `
     */
    @Override
    public void deleteClassInCourseStudent() throws JsonParseException, JsonMappingException, IOException {
        String classin_getcoursestudent_url = systemRegistryMapper.getURLByKey("classin_getcoursestudent_url");
        String classin_deletecoursestudent_url = systemRegistryMapper.getURLByKey("classin_delCourseStudent_url");
        String param1 = "SID=" + Constants.SID;
        String param2 = "";
        String param3 = "";
        String param5 = "";
        String param4 = "identity=1";
        StringBuilder stringBuilder = null;
        List<BbCourseClassinCourse> BbCourseClassinCourseList = bbCourseClassinCourseMapper.findAllBbClassInCourse();
        for (BbCourseClassinCourse bbClassinCourse : BbCourseClassinCourseList) {
            String bbCourseId = bbClassinCourse.getBbCourseId();
            Course course = SystemUtil.getCourseByCourseId(bbCourseId);
            if (course == null) {
                continue;
            }
            String classinCourseId = bbClassinCourse.getClassinCourseId();
            long currentLoignTime = System.currentTimeMillis() / 1000;
            param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
            param3 = "timeStamp=" + currentLoignTime;
            param5 = "courseId=" + classinCourseId;
            stringBuilder = new StringBuilder();
            stringBuilder.append(param1).append("&").append(param2).append("&").append(param3)
                    .append("&").append(param4).append("&").append(param5);
            try {
                String resultCourseStudentMapString = HttpClient.doPost(classin_getcoursestudent_url, stringBuilder.toString());
                log.info("classinCourseId=" + classinCourseId + "======>resultCourseStudentMapString=" + resultCourseStudentMapString);
                if (resultCourseStudentMapString != null && !"".equals(resultCourseStudentMapString)) {
                    if (resultCourseStudentMapString.contains("[]")) {
                        //没有学生数据，直接跳过
                        continue;
                    }
                    CourseStudentPOJO courseStudentPOJO = JSONObject.parseObject(resultCourseStudentMapString, CourseStudentPOJO.class);
                    ErrorInfo errorInfo = courseStudentPOJO.getErrorInfo();
                    if ("1".equals(errorInfo.getErrno())) {
                        //程序正常执行，正确获取到学生数据
                        Map<String, String> data = courseStudentPOJO.getData();
                        data.forEach((telephone, userId) -> {

                            User user = SystemUtil.getUserByUserId(userId);
                            if (user == null) {
                                //用户不存在，需要删除
                                log.info("userId====>" + userId + "用户不存在,删除其classin的注册关系");
                                StringBuilder sb = new StringBuilder();
                                sb.append(param1)
                                        .append("&safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime))
                                        .append("&timeStamp=" + currentLoignTime)
                                        .append("&").append(param4)
                                        .append("&courseId=" + classinCourseId)
                                        .append("&studentUid=" + userPhoneMapper.findByPhone(telephone).getClassinUid());
                                String resultDeleteCourseStudentString = HttpClient.doPost(classin_deletecoursestudent_url, sb.toString());
                                log.info("bbCourseId=" + bbCourseId + "==>userId(不存在)=" + userId + "===>resultDeleteCourseStudentString===>" + resultDeleteCourseStudentString);
                            } else {
                                try {
                                    CourseMembership cmp = CourseMembershipDbLoader.Default.getInstance().loadByCourseAndUserId(course.getId(), user.getId());
                                } catch (KeyNotFoundException e) {
                                    log.info("bbCourseId===>" + course.getCourseId() + "&userId====>" + user.getUserName() + "注册关系不存在");
                                    //没有注册关系，需要删除
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(param1)
                                            .append("&safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime))
                                            .append("&timeStamp=" + currentLoignTime)
                                            .append("&").append(param4)
                                            .append("&courseId=" + classinCourseId)
                                            .append("&studentUid=" + userPhoneMapper.findByPhone(telephone).getClassinUid());
                                    String resultDeleteCourseStudentString = HttpClient.doPost(classin_deletecoursestudent_url, sb.toString());
                                    log.info("bbCourseId=" + bbCourseId + "==>userId=" + userId + "===>resultDeleteCourseStudentString===>" + resultDeleteCourseStudentString);

                                } catch (PersistenceException e) {

                                }
                            }
                        });
                    } else {
                        log.info("classinCourseId=" + classinCourseId + ";学生数据获取失败:errno=" + errorInfo.getErrno() + ",error=" + errorInfo.getError());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteCourseStudentByPhoneAndUid() {
        String classin_getcoursestudent_url = systemRegistryMapper.getURLByKey("classin_getcoursestudent_url");
        String classin_deletecoursestudent_url = systemRegistryMapper.getURLByKey("classin_delCourseStudent_url");
        String param1 = "SID=" + Constants.SID;
        String param2 = "";
        String param3 = "";
        String param5 = "";
        String param4 = "identity=1";
        StringBuilder stringBuilder = null;
        List<BbCourseClassinCourse> BbCourseClassinCourseList = bbCourseClassinCourseMapper.findAllBbClassInCourse();
        for (BbCourseClassinCourse bbClassinCourse : BbCourseClassinCourseList) {
            String bbCourseId = bbClassinCourse.getBbCourseId();
            Course course = SystemUtil.getCourseByCourseId(bbCourseId);
            if (course == null) {
                continue;
            }
            String classinCourseId = bbClassinCourse.getClassinCourseId();
            long currentLoignTime = System.currentTimeMillis() / 1000;
            param2 = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime);
            param3 = "timeStamp=" + currentLoignTime;
            param5 = "courseId=" + classinCourseId;
            stringBuilder = new StringBuilder();
            stringBuilder.append(param1).append("&").append(param2).append("&").append(param3)
                    .append("&").append(param4).append("&").append(param5);
            try {
                String resultCourseStudentMapString = HttpClient.doPost(classin_getcoursestudent_url, stringBuilder.toString());
                log.info("classinCourseId=" + classinCourseId + "======>resultCourseStudentMapString=" + resultCourseStudentMapString);
                if (resultCourseStudentMapString != null && !"".equals(resultCourseStudentMapString)) {
                    if (resultCourseStudentMapString.contains("[]")) {
                        //没有学生数据，直接跳过
                        continue;
                    }
                    CourseStudentPOJO courseStudentPOJO = JSONObject.parseObject(resultCourseStudentMapString, CourseStudentPOJO.class);
                    ErrorInfo errorInfo = courseStudentPOJO.getErrorInfo();
                    if ("1".equals(errorInfo.getErrno())) {
                        //程序正常执行，正确获取到学生数据
                        Map<String, String> data = courseStudentPOJO.getData();
                        data.forEach((telephone, userId) -> {
                            UserPhone userInfo = userPhoneMapper.findByPhone(telephone);
                            userId = userInfo.getUserId();
                            String uid = "";
                            if (userInfo == null) {
                                try {
                                    uid = SystemUtil.retriveStudentUid(telephone, systemRegistryMapper);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (uid.equals("fail")) {
                                    log.info("!!!!!!!!!!>>>>>>>!!!!!获取用户uid失败");
                                }

                                if (!uid.equals("fail")) {
                                    log.info("userId====>" + userId + "用户不存在,删除其classin的注册关系");
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(param1)
                                            .append("&safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime))
                                            .append("&timeStamp=" + currentLoignTime)
                                            .append("&").append(param4)
                                            .append("&courseId=" + classinCourseId)
                                            .append("&studentUid=" + uid);
                                    String resultDeleteCourseStudentString = HttpClient.doPost(classin_deletecoursestudent_url, sb.toString());
                                    log.info("bbCourseId=" + bbCourseId + "==>userId(不存在)=" + userId + "===>resultDeleteCourseStudentString===>" + resultDeleteCourseStudentString);
                                }
                            } else {
                                User user = SystemUtil.getUserByUserId(userId);
                                String classinUid = userInfo.getClassinUid();
                                if (classinUid == null){
                                    try {
                                        SystemUtil.getUid(userInfo, systemRegistryMapper, userPhoneMapper);
                                        classinUid = userPhoneMapper.findByPhone(telephone).getClassinUid();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (user == null) {
                                    //用户不存在，需要删除
                                    log.info("userId====>" + userId + "用户不存在,删除其classin的注册关系");
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(param1)
                                            .append("&safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime))
                                            .append("&timeStamp=" + currentLoignTime)
                                            .append("&").append(param4)
                                            .append("&courseId=" + classinCourseId)
                                            .append("&studentUid=" + classinUid);
                                    String resultDeleteCourseStudentString = HttpClient.doPost(classin_deletecoursestudent_url, sb.toString());
                                    log.info("bbCourseId=" + bbCourseId + "==>userId(不存在)=" + userId + "===>resultDeleteCourseStudentString===>" + resultDeleteCourseStudentString);
                                } else {
                                    try {
                                        CourseMembership cmp = CourseMembershipDbLoader.Default.getInstance().loadByCourseAndUserId(course.getId(), user.getId());
                                            if (!cmp.getIsAvailable()) {
                                                log.info(user.getUserName() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!state" + cmp.getIsAvailable());
                                                log.info("userId====>" + userId + "not use");
                                                StringBuilder sb = new StringBuilder();
                                                sb.append(param1)
                                                        .append("&safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime))
                                                        .append("&timeStamp=" + currentLoignTime)
                                                        .append("&").append(param4)
                                                        .append("&courseId=" + classinCourseId)
                                                        .append("&studentUid=" + classinUid);
                                                String resultDeleteCourseStudentString = HttpClient.doPost(classin_deletecoursestudent_url, sb.toString());
                                                log.info("bbCourseId=" + bbCourseId + "==>" + userId + "===>resultDeleteCourseStudentString===>" + resultDeleteCourseStudentString);
                                            }
                                    } catch (KeyNotFoundException e) {
                                        log.info("bbCourseId===>" + course.getCourseId() + "&userId====>" + user.getUserName() + "注册关系不存在");
                                        //没有注册关系，需要删除
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(param1)
                                                .append("&safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentLoignTime))
                                                .append("&timeStamp=" + currentLoignTime)
                                                .append("&").append(param4)
                                                .append("&courseId=" + classinCourseId)
                                                .append("&studentUid=" + classinUid);
                                        String resultDeleteCourseStudentString = HttpClient.doPost(classin_deletecoursestudent_url, sb.toString());
                                        log.info("bbCourseId=" + bbCourseId + "==>userId=" + userId + "===>resultDeleteCourseStudentString===>" + resultDeleteCourseStudentString);

                                    } catch (PersistenceException e) {

                                    }
                                }
                            }
                        });
                    } else {
                        log.info("classinCourseId=" + classinCourseId + ";学生数据获取失败:errno=" + errorInfo.getErrno() + ",error=" + errorInfo.getError());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
