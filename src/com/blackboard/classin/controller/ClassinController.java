package com.blackboard.classin.controller;

import blackboard.data.course.Course;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.BbClassInInfo;
import com.blackboard.classin.entity.BbClassInUserInfo;
import com.blackboard.classin.service.BbClassInInfoService;
import com.blackboard.classin.service.BbClassInUserService;
import com.blackboard.classin.service.SystemRegistryService;
import com.blackboard.classin.service.UserScoreService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author wangy
 * @Date 2019/4/16
 */
@Controller
@RequestMapping("/classin")
public class ClassinController {

    @Autowired
    private UserScoreService userScoreService;
    
    @Autowired
    private BbClassInUserService bbClassInUserService;

    @Autowired
    private SystemRegistryService registryService;
    
    @Autowired
    private BbClassInInfoService bbClassInInfoService;

    private Logger log = Logger.getLogger(ClassinController.class);



    /**
     * classIn课堂入口：首先判断在BB方此课程有无创建classIn课堂相关信息，有则直接进入课堂，无则去往手机登录页面
     * @return
     */
    @RequestMapping(value = "/toLogin.do")
    public String  toLogin(String course_id, HttpServletRequest request,Model model){
        log.info("to input telephone number page");
        //获取BB course
        Course course = SystemUtil.getCourseById(course_id);
        String bbCourseId = course.getCourseId();
        BbClassInInfo bbClassIn = bbClassInInfoService.findNoExpiredBbClassIn(bbCourseId);
        
        request.getSession().setAttribute("course_id",course_id);
        request.getSession().setAttribute("bbCourse",course);
        
        //bbClassIn为空，说明此为第一次进入
        if(bbClassIn == null) {
        	model.addAttribute("classInIsExsist", "no");
        	//是教师，则去输入手机号，完成一系列注册动作
        	if(SystemUtil.isTeacher()) {
        		return "/classin/phone";
        	}else {//是学生，则提示无课堂
        		model.addAttribute("errno", "500");
        		model.addAttribute("error", "该课程还未创建相应的ClassIn课堂，请等待教师用户创建之后再尝试进入！");
        		return "/classin/tips";
        	}
        }else {//课堂不为空，所有人允许进入课堂
        	log.info("bbClassIn is not null ,allow user to enter classIn");
        	
        	//获取已有课堂的相关信息
        	String classinCourseId = bbClassIn.getClassInCourseId();
        	String classinClassId = bbClassIn.getClassInClassId();
        	
        	model.addAttribute("classInIsExsist", "yes");
        	model.addAttribute("classinCourseId",classinCourseId);
        	model.addAttribute("classinClassId",classinClassId);
        	
        	//有进行中的classin课程,提示非授课教师
        	if(SystemUtil.isTeacher()) {
        		model.addAttribute("tips",SystemUtil.getCurrentUser().getUserName()+"老师您好,该课程已有进行中的classin课堂,请输入手机号直接进入即可！");
        		return "/classin/phone";
        	}else {
        		return "/classin/phone";
        	}
        	
        }
    }
    
    /**
     * 提交创建之前再次验证该课程下未有其他classin课堂
     * @param course_id
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/validAgain.do")
    public String validAgain(String telephone,String course_id,HttpServletRequest request,Model model) {
    	//获取BB course
        Course course = SystemUtil.getCourseById(course_id);
        String bbCourseId = course.getCourseId();
        BbClassInInfo bbClassIn = bbClassInInfoService.findNoExpiredBbClassIn(bbCourseId);
        
        if(bbClassIn == null) {//允许创建
        	return "redirect:/classin/createCourseAndClass.do?telephone="+telephone
        			+"&course_id="+course_id;
        	
        }else {//直接进入
        	
        	return "redirect:/classin/loginClassIn.do?telephone="+telephone
                    +"&classInCourseId="+bbClassIn.getClassInCourseId()
                    +"&classinCourseClassId="+bbClassIn.getClassInClassId()
                    +"&course_id="+course_id;
        }
        	
    }
    
    /**
     * 创建ClassIn课堂
     * @param telephone
     * @param course_id
     * @return
     */
    @RequestMapping(value = "/createCourseAndClass.do")
    public String createCourseAndClass(String telephone,String course_id, Model model,HttpServletRequest request) throws IOException {
        log.info("to create classin course class method");
        
        final String classin_addcourse_url = registryService.getURLByKey("classin_addcourse_url");

        if(classin_addcourse_url == null || classin_addcourse_url.equals("")){
        	model.addAttribute("telephone",telephone);
            model.addAttribute("errno","200");
            model.addAttribute("error","检测到还未配置classIn服务器相关信息，请联系管理员进行相关配置！");
            model.addAttribute("classInIsExsist", "no");
            return "/classin/phone";
        }

        //go to 创建课程
        return "redirect:/classin/createClassInCourse.do?telephone="+telephone
        		+"&classin_addcourse_url="+classin_addcourse_url
        		+"&course_id="+course_id;
    }
    
    /**
     * 创建classin课程
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @RequestMapping("/createClassInCourse.do")
    public String createClassInCourse(String telephone,String course_id,String classin_addcourse_url,HttpServletRequest request,Model model) 
    		throws JsonParseException, JsonMappingException, IOException {
    	
    	log.info("to create classin course method");
    	
    	Course bbCourse = (Course) request.getSession().getAttribute("bbCourse");
    	
    	//时间戳以秒为单位
        long currentTime = System.currentTimeMillis()/1000;

        String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentTime);
        String param3 = "timeStamp="+currentTime;
        String param4 = "courseName="+bbCourse.getTitle();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param4);
        //TODO 查询course_id
    	int count = bbClassInInfoService.findIsCreatingCourseClass(course_id);
    	if(count == 0) {
    		try {
    			//TODO 添加course_id
				bbClassInInfoService.insertCreatingCourseClass(course_id);
			} catch (Exception e) {
	        	return "redirect:/classin/validAgain.do?telephone="+telephone
                        +"&course_id="+course_id;
			}
    	}else {
    		return "redirect:/classin/validAgain.do?telephone="+telephone
                    +"&course_id="+course_id;
    	}
    	
    	try {
    		//classIn返回的信息
            String resultMap_classInCourseIdMap = HttpClient.doPost(classin_addcourse_url,stringBuilder.toString());

            log.info("resultMap_classInCourseIdMap is>>>"+resultMap_classInCourseIdMap);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> classInCourseIdMap = new HashMap<String, Object>();

            if(resultMap_classInCourseIdMap != null && !"".equals(resultMap_classInCourseIdMap)){
            	classInCourseIdMap = objectMapper.readValue(resultMap_classInCourseIdMap,Map.class);

                //解析返回的数据
                Map<String,Object> errorInfo = (Map<String, Object>) classInCourseIdMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                if("1".equals(errno)){
                	//成功获取到ClassInCourseId,即表示ClassIn课程创建成功
                    String classInCourseId = classInCourseIdMap.get("data").toString();
                    return "redirect:/classin/createClassInCourseClass.do?telephone="+telephone
                    		+"&classInCourseId="+classInCourseId
                    		+"&course_id="+course_id;
                }else {
                	model.addAttribute("errno", errno);
                	model.addAttribute("error", error);
                	model.addAttribute("classInIsExsist", "no");
                	return "/classin/phone";
                }
            }else {
//            	model.addAttribute("errno", "500");
//            	model.addAttribute("classInIsExsist", "no");
//            	model.addAttribute("telephone", telephone);
//            	model.addAttribute("error", "未获取到classIn成功创建课程的信息，请查看网络或classin服务器是否正常。稍后请重试！");
//            	return "/classin/phone";
            	return "redirect:/classin/validAgain.do?telephone="+telephone
                        +"&course_id="+course_id;
            }
    	}finally {
    		bbClassInInfoService.deleteCreatingCourseClass(course_id);
    	}
        
    }
    
    /**
     * 
     * @param telephone
     * @param classInCourseId
     * @param request
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @RequestMapping("/createClassInCourseClass.do")
    public String createClassInCourseClass(String telephone,String course_id,String classInCourseId,HttpServletRequest request,Model model) 
    		throws JsonParseException, JsonMappingException, IOException {
    	log.info("to create classin class method");
    	
    	Course bbCourse = (Course) request.getSession().getAttribute("bbCourse");
    	String classin_addCourseClass_url = registryService.getURLByKey("classin_addcourseclass_url");
        long currentCreateClassTime = System.currentTimeMillis()/1000;
        
        String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentCreateClassTime);
        String param3 = "timeStamp="+currentCreateClassTime;
        String param4 = "teacherAccount="+telephone;
        String param5 = "teacherName="+SystemUtil.getCurrentUser().getUserName();
        String param6 = "courseId="+ classInCourseId;
        String param7 = "className="+ bbCourse.getTitle();
        long beginTime = currentCreateClassTime + 100;//开课时间定为当前时间的100s之后
        long endTime = beginTime + 4*60*60;//开课时长4小时
        String param8 = "beginTime="+beginTime ;
        String param9 = "endTime="+ endTime;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param5).append("&").append(param6)
        	.append("&").append(param7).append("&").append(param8).append("&").append(param9);
        
        String resultMap_classInCourseClassIdMap = HttpClient.doPost(classin_addCourseClass_url,stringBuilder.toString());
        
        log.info("resultMap_classInCourseClassIdMap is>>>"+resultMap_classInCourseClassIdMap);
        
        //解析返回的课节信息
        Map<String,Object> classInCourseClassIdMap = new HashMap<String,Object>();
        if(resultMap_classInCourseClassIdMap != null && !resultMap_classInCourseClassIdMap.equals("")) {
        	ObjectMapper objectMapper = new ObjectMapper();
        	classInCourseClassIdMap = objectMapper.readValue(resultMap_classInCourseClassIdMap, Map.class);
        	//解析返回的数据
            Map<String,Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            
            //成功创建
        	if("1".equals(errno)) {
        		String classinCourseClassId = classInCourseClassIdMap.get("data").toString();
        		
        		//获取classInCourseClassId，说明课节创建成功,保存课程、课节信息
        		bbClassInInfoService.saveBbClassInInfo(bbCourse.getCourseId(),classInCourseId,classinCourseClassId,System.currentTimeMillis());
        		return "redirect:/classin/loginClassIn.do?classInCourseId="+classInCourseId
        				+"&classinCourseClassId="+classinCourseClassId
        				+"&telephone="+telephone
        				+"&course_id="+course_id;
        	}else if("136".equals(errno)) {//机构下面没有该老师，请在机构下添加该老师
        		
        		return "redirect:/classin/addTeacher.do?classInCourseId="+classInCourseId
        				+"&telephone="+telephone
        				+"&course_id="+course_id;
            }else {
        		model.addAttribute("errno", errno);
        		model.addAttribute("error", error);
        		model.addAttribute("classInIsExsist", "no");
        		return "/classin/phone";
        	}
        }else {
        	model.addAttribute("classInIsExsist", "no");
        	model.addAttribute("errno", "200");
        	model.addAttribute("telephone", telephone);
        	model.addAttribute("error", "同一时间课程内已有其他教师创建在线课堂，请重新点击\"进入classin课堂\"按钮!");
        	return "/classin/phone";
        }
    }
    
    /**
     * 将教师添加到机构中
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @RequestMapping("/addTeacher.do")
    public String addTeacher(String telephone,String course_id,String classInCourseId,HttpServletRequest request,Model model) 
    		throws JsonParseException, JsonMappingException, IOException {
    	log.info("classin addTeacher");
    	
    	long currentCreateClassTime = System.currentTimeMillis()/1000;	
        
        String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentCreateClassTime);
        String param3 = "timeStamp="+currentCreateClassTime;
        String param4 = "teacherAccount="+telephone;
        String param5 = "teacherName="+SystemUtil.getCurrentUser().getUserName();
        
        StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param5);
		
		String classin_addteacher_url = registryService.getURLByKey("classin_addteacher_url");
    	String addTeacherResultMap = HttpClient.doPost(classin_addteacher_url, strBuilder.toString());
    	log.info("addTeacher resultMap>>>"+addTeacherResultMap);
    	
    	Map<String,Object> addTeacherMap = new HashMap<String,Object>();
    	
    	if(addTeacherResultMap != null && !addTeacherResultMap.equals("")) {
        	ObjectMapper objectMapper = new ObjectMapper();
        	addTeacherMap = objectMapper.readValue(addTeacherResultMap, Map.class);
        	//解析返回的数据
            Map<String,Object> errorInfo = (Map<String, Object>) addTeacherMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            
            if("1".equals(errno)) {//成功将教师添加到机构中
            	//继续创建课节
            	return "redirect:/classin/createClassInCourseClass.do?telephone="+telephone
                		+"&classInCourseId="+classInCourseId
                		+"&course_id="+course_id;
            }else if("113".equals(errno)){
            	String bbUserId = SystemUtil.getCurrentUser().getUserName();
            	//注册用户
        		String param_nickname = "nickname="+bbUserId;
        		String param_pwd = "password="+telephone;
        		String param_telephone = "telephone="+telephone;
        		StringBuilder strsBuilder = new StringBuilder();
        		strsBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param_telephone)
        			.append("&").append(param_nickname).append("&").append(param_pwd);
        		
        		String classin_register_url = registryService.getURLByKey("classin_register_url");
        		String resultRegisterMap = HttpClient.doPost(classin_register_url, strsBuilder.toString());
        		
        		log.info("resultRegisterMap is >>>"+resultRegisterMap); 
        		
        		Map<String,Object> registerMap = new HashMap<String,Object>();
        		if(resultRegisterMap != null && !resultRegisterMap.equals("")) {
        			registerMap = objectMapper.readValue(resultRegisterMap, Map.class);
        			Map<String,Object> registerErrorInfo = (Map<String, Object>) registerMap.get("error_info");
        			String registerErrno = registerErrorInfo.get("errno").toString();
        			if("1".equals(registerErrno)) {
        				//保存注册信息
        				String classinUid = registerMap.get("data").toString();
        				bbClassInUserService.saveBbClassinUser(bbUserId,classinUid,telephone);
        				
        				return "redirect:/classin/addTeacher.do?classInCourseId="+classInCourseId
                				+"&telephone="+telephone
                				+"&course_id="+course_id;
        			}
        		}
        		
        		model.addAttribute("classInIsExsist", "no");
            	model.addAttribute("errno", errno);
            	model.addAttribute("error", error);
            	return "/classin/phone";
            }else {
            	model.addAttribute("classInIsExsist", "no");
            	model.addAttribute("errno", errno);
            	model.addAttribute("error", error);
            	return "/classin/phone";
            }
        }else {
        	model.addAttribute("classInIsExsist", "no");
        	model.addAttribute("errno", "500");
        	model.addAttribute("telephone", telephone);
        	model.addAttribute("error", "未获取到classin添加教师请求的返回信息，请查看网络或classin服务器是否正常。稍后请重试！");
        	return "/classin/phone";
        }
    	
    }
    
    /**
     * 已经创建课程课节，直接进入（唤醒classin客户端）
     * @return
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @RequestMapping("/loginClassIn.do")
    public String loginClassIn(String telephone,String classInCourseId,
    		String classinCourseClassId,HttpServletRequest request,Model model,String course_id) throws JsonParseException, JsonMappingException, IOException {

    	log.info("to awake classin client");
    	
    	long currentLoignTime = System.currentTimeMillis()/1000;
    	String parma1 = "SID="+Constants.SID;
    	String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentLoignTime);
    	String param3 = "timeStamp="+currentLoignTime;
    	String param4 = "telephone="+telephone;
    	String param5 = "courseId="+ classInCourseId;
    	String param6 = "classId="+classinCourseClassId;
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	//登录之前先注册用户，再将用户注册进classin课程中
    	BbClassInUserInfo bbClassInUserInfo = bbClassInUserService.findByTelephone(telephone);
    	String bbUserId = SystemUtil.getCurrentUser().getUserName();
    	if(bbClassInUserInfo == null) {
    		//注册用户
    		String param_nickname = "nickname="+bbUserId;
    		String param_pwd = "password="+telephone;
    		
    		StringBuilder strBuilder = new StringBuilder();
    		strBuilder.append(parma1).append("&").append(param2).append("&").append(param3).append("&").append(param4)
    			.append("&").append(param_nickname).append("&").append(param_pwd);
    		
    		String classin_register_url = registryService.getURLByKey("classin_register_url");
    		String resultRegisterMap = HttpClient.doPost(classin_register_url, strBuilder.toString());
    		
    		log.info("resultRegisterMap is >>>"+resultRegisterMap); 
    		
    		Map<String,Object> registerMap = new HashMap<String,Object>();
    		if(resultRegisterMap != null && !resultRegisterMap.equals("")) {
    			registerMap = objectMapper.readValue(resultRegisterMap, Map.class);
    			Map<String,Object> registerErrorInfo = (Map<String, Object>) registerMap.get("error_info");
    			String registerErrno = registerErrorInfo.get("errno").toString();
    			if("1".equals(registerErrno)) {
    				//保存注册信息
    				String classinUid = registerMap.get("data").toString();
    				bbClassInUserService.saveBbClassinUser(bbUserId,classinUid,telephone);
    			}
    		}
    	}else {
    		//如果手机号已被别的用户使用，则返回提示信息
    		if(!bbClassInUserInfo.getBbUserId().equals(bbUserId)) {
    			
    			model.addAttribute("classInIsExsist", "yes");
        		model.addAttribute("errno", "500");
        		model.addAttribute("error", "该手机号被其他用户绑定，请更换手机号！");
        		model.addAttribute("classinCourseId",classInCourseId);
        		model.addAttribute("classinClassId",classinCourseClassId);
        		return "/classin/phone";
    		}
    		
    	}
    	//课程下添加学生/旁听
    	String param_identity = "identity=1";
    	String param_studentAccount = "studentAccount="+telephone;
    	String param_studentName = "studentName="+bbUserId;
    	
    	String classin_addcoursestudent_url = registryService.getURLByKey("classin_addcoursestudent_url");
    	
    	StringBuilder sBuilder = new StringBuilder();
    	sBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
    		.append("&").append(param_identity).append("&").append(param_studentAccount).append("&")
    		.append(param_studentName).append("&").append(param5);
    	
    	String addCourseStudentResultMap = HttpClient.doPost(classin_addcoursestudent_url, sBuilder.toString());
    	
    	log.info("addCourseStudentResultMap is >>>"+addCourseStudentResultMap);
    	
    	//唤醒客户端URL
    	String classin_loginEntrance_url = registryService.getURLByKey("classin_entrance_url");
        
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param5).append("&").append(param6);
        
        String resultLoginMap = HttpClient.doPost(classin_loginEntrance_url, stringBuilder.toString());
        
        log.info("resultLoginMap >>>>"+resultLoginMap);
        
        Map<String,Object> classInCourseClassIdMap = new HashMap<String,Object>();
        if(resultLoginMap != null && !resultLoginMap.equals("")) {
        	
        	classInCourseClassIdMap = objectMapper.readValue(resultLoginMap, Map.class);
        	//解析返回的数据
            Map<String,Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            
            //成功返回信息
        	if("1".equals(errno)) {
        		String data = (String) classInCourseClassIdMap.get("data");
        		String conditions = "";
        		if(data != null && !data.equals("")) {
        			conditions = data.split("\\?")[1];
        		}
        		model.addAttribute("conditions", conditions);
//        		return "redirect:"+ data;
        		return "/classin/awakeClassIn";
        	}else {
        		model.addAttribute("classInIsExsist", "no");
        		model.addAttribute("errno", errno);
        		model.addAttribute("error", error);
        		return "/classin/phone";
        	}
          }else {
        	  model.addAttribute("classInIsExsist", "no");
      	      model.addAttribute("errno", "500");
      	      model.addAttribute("telephone", telephone);
      		  model.addAttribute("error", "未获取到classin唤醒客户端并进入教室的返回信息，请查看网络或classin服务器是否正常。稍后请重试！");
      		  return "/classin/phone";
          }
    }

    /**
     * 保存classin返回的成绩信息
     * @return
     */
    @RequestMapping("/saveUserScore.do")
    public String saveUserScore(){

        return "";
    }
    
}
