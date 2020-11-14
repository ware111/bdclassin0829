package com.blackboard.classin.controller;

import blackboard.data.course.Course;
import blackboard.persist.PersistenceException;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.entity.BbCourseClassinCourse;
import com.blackboard.classin.entity.CourseClassConditionData;
import com.blackboard.classin.mapper.BbCourseClassinCourseMapper;
import com.blackboard.classin.mapper.ClassConditionMapper;
import com.blackboard.classin.mapper.UserPhoneMapper;
import com.blackboard.classin.service.CourseDataService;
import com.blackboard.classin.service.CourseStudentDataService;
import com.blackboard.classin.service.CreateCourseClassService;
import com.blackboard.classin.service.IClassinCourseClass;
import com.blackboard.classin.util.SystemUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/newclassinCourseClass")
public class NewClassinCourseClassController {

    private Logger log = Logger.getLogger(NewClassinCourseClassController.class);

    @Autowired
    private CreateCourseClassService createCourseClassService;

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private CourseDataService courseDataService;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private IClassinCourseClass iClassinCourseClass;

    @Autowired
    private CourseStudentDataService courseStudentDataService;

    @RequestMapping("batchClass.do")
    public String batchClass(HttpServletRequest request, HttpServletResponse response, String className, String classType, String startDate,
                             String startTime, String hour, String minute, String teacher,
                             String assistantTeacher, String bbCourseId, String isLive,
                             String isRecord, String isReplay, String startTimeStamp,
                             Model model, String isBathClass, String classAmount, String days, String currentDay,String classNameSuffix) throws IOException, PersistenceException {
        Properties properties = createCourseClassService.batcheCreateCourseClass(request, response, className, classType, startDate, startTime,
                hour, minute, teacher, assistantTeacher, bbCourseId, isLive, isRecord, isReplay, startTimeStamp, classAmount, days, currentDay,classNameSuffix);
        String errorMsg = (String)properties.get("errorMsg");
        if (errorMsg != null){
            model.addAttribute("error", errorMsg);

        }
        String url = (String)properties.get("url");
        return url;
    }

    @ResponseBody
    @RequestMapping("test.do")
    public String test() throws IOException {
//        InputStream resource = NewClassinCourseClassController.class.getClassLoader().getResourceAsStream("bb-user_course.csv");
//        String property = System.getProperty("user.dir");
////        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(property+File.separator+"bb-user_course.csv")));
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource));
//        String content="";
//        int i = 0;
//        while ((content=bufferedReader.readLine()) != null){
//            i++;
//            if (i == 1){
//                continue;
//            }
//            String[] split = content.split(",");
//            String userId = split[0];
//            String phone = split[20];
//            String uid = split[26];
//            HashMap<String, String> params = new HashMap<>();
//            params.put("userId",userId);
//            params.put("phone",phone);
//            params.put("classinUid",uid);
//            userPhoneMapper.save(params);
//
//        }
        JSONObject jsonObject = new JSONObject();
        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
        Properties properties = new Properties();
        properties.load(resource);
        String textFile = properties.getProperty("textFile");
        String av = properties.getProperty("av");
        jsonObject.put("text",textFile);
        jsonObject.put("av",av);
        return jsonObject.toJSONString();
    }

//    @ResponseBody
    @RequestMapping("pageClassData.do")
    public String getClassDataByPage(String courseId,String page,Model model){
//        if(page.contains(" ")){
//            page=page.trim();
//        }
        Course course = SystemUtil.getCourseById(courseId);
        String bbcourseId = course.getCourseId();
        BbCourseClassinCourse classinCourse = bbCourseClassinCourseMapper.findByCourseId(bbcourseId);
        String classinCourseId = classinCourse.getClassinCourseId();
        List<CourseClassConditionData> courseDatas=new ArrayList<>();
        int k = 0;
        String currentPage="";
        int totalPage=0;
        int rows = courseDataService.getDataRows(classinCourseId);
        log.info("***************rows************"+rows);
        log.info("***************classinCourseId************"+classinCourseId);
        int i = rows % 10;
        if (i == 0){
            totalPage = rows / 10;
        } else {
            totalPage = rows / 10 + 1;
        }
        if (page.equals("-1")){
            currentPage = totalPage+"";
        }else {
            currentPage = page;
        }
        model.addAttribute("pages",totalPage);
        model.addAttribute("currentPage",currentPage);
        HashMap<String, String> params = new HashMap<>();
        if (!page.equals("-1")) {
            int beginRow = (Integer.valueOf(page) - 1) * 10+0;
            int endRow = (Integer.valueOf(page) - 1) * 10+11;
            params.put("beginRow", beginRow + "");
            params.put("endRow", endRow + "");
            params.put("courseId", classinCourseId);
        } else {
            if (i == 0){
                int beginRow = rows - 10;
                int endRow = rows+1;
                params.put("beginRow", beginRow + "");
                params.put("endRow", endRow + "");
                params.put("courseId", classinCourseId);
            }else{
                int beginRow = rows - i;
                int endRow = rows+1;
                params.put("beginRow", beginRow + "");
                params.put("endRow", endRow + "");
                params.put("courseId", classinCourseId);
            }
        }
        courseDatas = courseDataService.getPageClassData(params);
        if (courseDatas != null) {
            for (CourseClassConditionData courseData : courseDatas) {
                String classId = courseData.getClassId();
                String classType = iClassinCourseClass.getClassType(classId);
                courseData.setClassType(classType);
                courseData.setId(++k);
            }
            model.addAttribute("classData", courseDatas);
        }

        //课程名称
        String courseName = course.getTitle();
        model.addAttribute("courseName", courseName);
        //课程进度
        List<Map<String, Object>> summaryData = iClassinCourseClass.getSummaryDataByClassType();
        if (summaryData != null) {
            summaryData.forEach((result) -> {
                String classType = result.get("CLASS_TYPE").toString();
                BigDecimal bigDecimal = (BigDecimal) result.get("TOTAL");
                int total = bigDecimal.intValue();
                if (classType.equals("课表课")) {
                    model.addAttribute("KeBiaoKe", total);
                } else {
                    model.addAttribute("notKeBiaoKe", total);
                }
            });

            model.addAttribute("courseProcess", summaryData);
        }


        //课程出勤
        Map courseCheckAndRate = courseDataService.getCourseCheckAndRate(classinCourseId);
        if (courseCheckAndRate != null) {
            int studentTotal = ((BigDecimal) courseCheckAndRate.get("STUDENTTOTAL")).intValue();
            int courseCheckinStudentTotal = courseStudentDataService.getCourseCheckinStudentTotal(classinCourseId);
            model.addAttribute("checkinStudents",courseCheckinStudentTotal);
            //课程出勤率
            String courseCheckinRate = (String)courseCheckAndRate.get("RATE");
            model.addAttribute("courseCheckin",studentTotal);
            model.addAttribute("courseCheckinRate",String.format("%.1f",Double.valueOf(courseCheckinRate)*100));
        }

        //课表课出勤率
        String keBiaoKeCheckRate = courseDataService.getKeBiaoKeCheckRate(classinCourseId);
        if (keBiaoKeCheckRate != null) {
            model.addAttribute("keBiaoKeCheckRate", String.format("%.1f", Double.valueOf(keBiaoKeCheckRate) * 100));
        } else {
            model.addAttribute("keBiaoKeCheckRate", "");
        }
        return "/classin/educationData";
    }
}
