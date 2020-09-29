package com.blackboard.classin.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.BbCourseClassinCourse;
import com.blackboard.classin.entity.ClassinClassMeeting;
import com.blackboard.classin.entity.ClassinCourseClass;
import com.blackboard.classin.mapper.BbCourseClassinCourseMapper;
import com.blackboard.classin.mapper.ClassinClassMeetingMapper;
import com.blackboard.classin.mapper.ClassinCourseClassMapper;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import com.blackboard.classin.service.IBbCourseClassinCourse;
import com.blackboard.classin.service.SystemRegistryService;
import com.blackboard.classin.util.HttpClient;
import com.blackboard.classin.util.SystemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import blackboard.persist.PersistenceException;
import blackboard.platform.BbServiceManager;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import blackboard.data.user.User;

/**
 * 2019-10-10
 * @author wangy
 *
 */
@Controller
@RequestMapping("/bbCourseClassinCourse")
public class BbCourseClassinCourseController {

	@Autowired
	private IBbCourseClassinCourse iBbCourseClassinCourse;
	
	@Autowired
	private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;
	
	@Autowired
	private ClassinCourseClassMapper classinCourseClassMapper;
	
	@Autowired
	private ClassinClassMeetingMapper classinClassMeetingMapper;
	
	@Autowired
	private SystemRegistryMapper systemRegistryMapper;
	
	@Autowired
    private SystemRegistryService registryService;
	
	private Logger log = Logger.getLogger(BbCourseClassinCourseController.class);
	
	
	/**
	 * 全量每门课程进行比对，删除课程内的多余学生用户
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping("/deleteCourseStudent.do")
	@ResponseBody
	public JSONObject deleteClassInCourseStudent(HttpServletRequest request,HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
		log.info("exec deleteClassInCourseStudent start");
		iBbCourseClassinCourse.deleteClassInCourseStudent();
		log.info("exec deleteClassInCourseStudent end");
		return SystemUtil.buildResultMap(1, "执行完成");
	}
	/**
	 * 学生进入之前首先判断是否有进行中的课节
	 * @param request
	 * @param response
	 * @param course_id
	 * @param model
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/findClass.do")
	public String findClass(HttpServletRequest request,HttpServletResponse response,String course_id,Model model,String type) throws ParseException, PersistenceException {
		SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
		BbSession bbSession = sessionManager.getSession(request, response);
		
		String courseId = bbSession.getGlobalKey("courseId");
		BbCourseClassinCourse bbCourseClassinCourse = bbCourseClassinCourseMapper.findByCourseId(courseId);
		if(bbCourseClassinCourse != null) {
			String classinCourseId = bbCourseClassinCourse.getClassinCourseId();
			bbSession.setGlobalKey("classinCourseId", classinCourseId);
			//学生查找在线研讨室
			if(type != null && "meetingroom".equals(type)) {
				ClassinClassMeeting classinClassMeeting = classinClassMeetingMapper.findByClassinCourseId(classinCourseId);
				if(classinClassMeeting != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dtCreated = classinClassMeeting.getDtCreated();
					Date date = sdf.parse(dtCreated);
					bbSession.setGlobalKey("classinClassId", classinClassMeeting.getClassinClassId());
					//但已过期
					if((System.currentTimeMillis() - date.getTime()) > 4*60*60*1000) {
						Map<String,String> paramMap = new HashMap<String, String>();
						paramMap.put("classinCourseId", classinClassMeeting.getClassinCourseId());
						paramMap.put("classinClassId", classinClassMeeting.getClassinClassId());
						classinClassMeetingMapper.updateToExpired(paramMap);
						classinClassMeeting = null;
					}else {
						//直接进入课节
						return "redirect:/classinCourseClass/addAsClassStudent.do?course_id="+course_id+"&type="+type;
					}
				}
			}else {
				ClassinCourseClass classinCourseClass = classinCourseClassMapper.findByClassinCourseId(classinCourseId);
				//课节不为空
				if(classinCourseClass != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dtCreated = classinCourseClass.getDtCreated();
					Date date = sdf.parse(dtCreated);
					bbSession.setGlobalKey("classinClassId", classinCourseClass.getClassinClassId());
					//课节总时长 分钟
					int classTimeLength = classinCourseClass.getClassTimeLength();
					//课节拖堂时间
					int closeClassDelay = classinCourseClass.getCloseClassDelay();
					//总课节时长+拖堂时间
					int totalTimeLength = classTimeLength+closeClassDelay;
					//但已过期
					if((System.currentTimeMillis() - date.getTime()) > classTimeLength*1000) {
						Map<String,String> paramMap = new HashMap<String, String>();
						paramMap.put("classinCourseId", classinCourseClass.getClassinCourseId());
						paramMap.put("classinClassId", classinCourseClass.getClassinClassId());
						classinCourseClassMapper.updateToExpired(paramMap);
						classinCourseClass = null;
					}else {
						//是系统管理员但不是课程下的学生用户
						if(SystemUtil.isAdministrator() && !SystemUtil.isStudent()) {
							model.addAttribute("type", type);
							return "/classin/tips";
						}else {
							//符合条件的课节为空
							//直接进入课节
							//直接进入课节
							return "redirect:/classinCourseClass/addAsClassStudent.do?course_id="+course_id+"&type="+type;
						}
					}
				}
			}
		}
		//如果是课程下学生，直接注册进classin课程中
		if(SystemUtil.isStudent()) {
			log.info("do addAsClassStudent");
	    	
			String telephone = bbSession.getGlobalKey("telephone");
			String classInCourseId = bbSession.getGlobalKey("classinCourseId");
			User user = SystemUtil.getCurrentUser();
			
			long currentLoignTime = System.currentTimeMillis()/1000;
			String parma1 = "SID="+Constants.SID;
			String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentLoignTime);
			String param3 = "timeStamp="+currentLoignTime;
			String param5 = "courseId="+ classInCourseId;
			
			//课程下添加学生/旁听
	    	String param_identity = "identity=1";
	    	//替换uid
	    	//String param_studentAccount = "studentAccount="+telephone;
	    	String studentUid = bbSession.getGlobalKey("classinUid");
	    	String param_studentAccount = "studentUid="+studentUid;
	    	String param_studentName = "studentName="+user.getUserName();
	    	
	    	String classin_addcoursestudent_url = systemRegistryMapper.getURLByKey("classin_addcoursestudent_url");
	    	
	    	StringBuilder sBuilder = new StringBuilder();
	    	sBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
	    		.append("&").append(param_identity).append("&").append(param_studentAccount).append("&")
	    		.append(param_studentName).append("&").append(param5);
	    	System.out.println(sBuilder.toString());
	    	String addCourseStudentResultMapString = HttpClient.doPost(classin_addcoursestudent_url, sBuilder.toString());
	    	System.out.println("addCourseStudentResultMapString>>>>>>>"+addCourseStudentResultMapString);
		}
		//符合条件的课节为空
		model.addAttribute("source","来自BB的提示消息");
		model.addAttribute("error","课程中还未创建classin在线课堂，请等待老师创建之后再进入！");
		model.addAttribute("type", type);
		return "/classin/tips";
	}
	
	/**
	 * 创建课程，并与BB course 进行绑定
	 * @param request
	 * @param response
	 * @param course_id
	 * @param model
	 * @return
	 * @throws ParseException 
	 * @throws PersistenceException 
	 */
	@RequestMapping("/create.do")
	public String createClassinCourseOnBbCourse(HttpServletRequest request,HttpServletResponse response,String course_id,Model model,String type) throws ParseException, PersistenceException {
		
		
		final String classin_addcourse_url = registryService.getURLByKey("classin_addcourse_url");
		SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
		BbSession bbSession = sessionManager.getSession(request, response);
		
		bbSession.setGlobalKey("course_id", course_id);

        if(classin_addcourse_url == null || classin_addcourse_url.equals("")){
            model.addAttribute("error","检测到还未配置classIn服务器相关信息，请联系管理员进行相关配置！");
            model.addAttribute("type", type);
            return "/classin/tips";
        }
		
		String infos = iBbCourseClassinCourse.createClassinCourseOnBbCourse(request,response,course_id,classin_addcourse_url,type);
		
		if(infos.equals("CreateClassinCourseFailed")) {
			String errno = bbSession.getGlobalKey("errno");
			String error = bbSession.getGlobalKey("error");
			
			model.addAttribute("source", "来自Classin的提示信息:创建classin课程失败");
			model.addAttribute("errno", errno);
			model.addAttribute("error", error);
			model.addAttribute("type", type);
			return "/classin/tips";
		}else if(infos.equals("NeedToAddAsTeacher") || infos.equals("NeedToCreateClassinClass")) {
			log.info("NeedToAddAsTeacher or NeedToCreateClassinClass");
			//创建课节并将该教师作为主讲人注册进去
			model.addAttribute("type", type);
			return "/classin/createClassinClass";
		}else if(infos.equals("NeedToAddAsAssistant")){
			//为课节添加助教
			log.info("NeedToAddAsAssistant");
			return "redirect:/classinCourseClass/addAsAssistant.do?course_id="+course_id+"&type="+type;
		}else if(infos.equals("awakeClassinClient")){
			//唤醒客户端
			log.info("awakeClassinClient");
			return "redirect:/classinCourseClass/awakeClassinClient.do?course_id="+course_id+"&tips=teacherAndAssistant&type="+type;
		}else if(infos.equals("NeedToAddAsStudent")) {
			//将教师添加为学生
			log.info("NeedToAddAsStudent");
			return "redirect:/classinCourseClass/addAsClassStudent.do?course_id="+course_id+"&type="+type;
		}else {
			log.info("else");
			model.addAttribute("source", "来自BB的提示消息");
			model.addAttribute("error", "classin在线课堂创建成功，如未跳转，请刷新网页后跳转至创建课节页面~");
			model.addAttribute("type", type);
			return "/classin/tips";
		}
	}
	
	/**
	 * 课程下有的教师角色被注册为学生，之后再操作的时候不能作为老师创建课节，需要在课程下删除学生/旁听角色
	 * @param request
	 * @param response
	 * @param course_id
	 * @param model
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws PersistenceException 
	 */
	@RequestMapping("/delCourseStudent.do")
	public String delCourseStudent(HttpServletRequest request,HttpServletResponse response,String course_id,Model model,String flag,String type)
			throws JsonParseException, JsonMappingException, IOException, PersistenceException {
		SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
		BbSession bbSession = sessionManager.getSession(request, response);
		log.info("172 delCourseStudent");
		
		String telephone = bbSession.getGlobalKey("telephone");
		String classinUid = bbSession.getGlobalKey("classinUid");
		String classinCourseId = bbSession.getGlobalKey("classinCourseId");
		
		long currentLoignTime = System.currentTimeMillis()/1000;
		
		final String classin_delCourseStudent_url = registryService.getURLByKey("classin_delCourseStudent_url");
		String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentLoignTime);
        String param3 = "timeStamp="+currentLoignTime;
        String param4 = "courseId="+classinCourseId;
        String param5 = "identity=1";
        //修改telephone为uid
        //String param6 = "studentAccount="+telephone;
		String param6 = "studentUid="+classinUid;
		
		ObjectMapper objectMapper = new ObjectMapper();
    	
		//跳转到唤醒客户端页面
		StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param5).append("&").append(param6);
        
        //调用classin接口，删除本课程下的学生
        String resultdelCourseStudentMapStr = HttpClient.doPost(classin_delCourseStudent_url, stringBuilder.toString());
        
        log.info("resultdelCourseStudentMapStr >>>>"+resultdelCourseStudentMapStr);
        
        Map<String,Object> classInCourseClassIdMap = new HashMap<String,Object>();
        
        if(resultdelCourseStudentMapStr != null && !"".equals(resultdelCourseStudentMapStr)) {
        	classInCourseClassIdMap = objectMapper.readValue(resultdelCourseStudentMapStr, Map.class);
        	Map<String,Object> errorInfo =  (Map<String, Object>) classInCourseClassIdMap.get("error_info");
        	String errno = errorInfo.get("errno").toString();
        	String error = errorInfo.get("error").toString();
        	
        	if("1".equals(errno)) {
        		if("editClass319".equals(flag)) {
        			//课程下删除学生用户成功，继续添加为助教
        			return "redirect:/classinCourseClass/addAsAssistant.do?course_id="+course_id+"&type="+type;
        		}else /*if("createClass172".equals(flag))*/{
        			//课程下删除学生用户成功，继续创建课节
        			return "redirect:/classinCourseClass/create.do?course_id="+course_id+"&type="+type;
        		}
        	}else {
        		model.addAttribute("source", "来自classin的提示信息");
        		model.addAttribute("errno", errno);
            	model.addAttribute("error", error);
            	model.addAttribute("type", type);
            	return "/classin/tips";
        	}
        }else {
        	model.addAttribute("type", type);
        	model.addAttribute("source", "来自BB的提示信息");
        	model.addAttribute("error", "classin课程下删除用户成功，如未跳转，请刷新页面后继续注册为该课节的教师/助教~");
        	return "/classin/tips";
        }
	}
	
	/**
	 * 移除课程下的老师
	 * @param request
	 * @param response
	 * @param course_id
	 * @param model
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws PersistenceException 
	 */
	@RequestMapping("/removeCourseTeacher.do")
	public String removeCourseTeacher(HttpServletRequest request,HttpServletResponse response,String course_id,Model model,String type) throws JsonParseException, JsonMappingException, IOException, PersistenceException {
		log.info("removeCourseTeacher");
		SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
		BbSession bbSession = sessionManager.getSession(request, response);
		final String classin_removeCourseTeacher_url = registryService.getURLByKey("classin_removeCourseTeacher_url");
		
		String telephone = bbSession.getGlobalKey("telephone");
		String classinUid = bbSession.getGlobalKey("classinUid");
		String classinCourseId = bbSession.getGlobalKey("classinCourseId");
		
		long currentLoignTime = System.currentTimeMillis()/1000;
		
		String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentLoignTime);
        String param3 = "timeStamp="+currentLoignTime;
        String param4 = "courseId="+classinCourseId;
        //telephone 改为uid
		//String param5 = "teacherAccount="+telephone;
        String param5 = "teacherUid="+classinUid;
		
		ObjectMapper objectMapper = new ObjectMapper();
    	
		//跳转到唤醒客户端页面
		StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param5);
        
        //调用classin接口，删除本课程下的学生
        String resultRemoveCourseTeacherMapStr = HttpClient.doPost(classin_removeCourseTeacher_url, stringBuilder.toString());
        
        log.info("resultRemoveCourseTeacherMapStr >>>>"+resultRemoveCourseTeacherMapStr);
        
        Map<String,Object> removeCourseTeacherMap = new HashMap<String,Object>();
        
        if(resultRemoveCourseTeacherMapStr != null && !"".equals(resultRemoveCourseTeacherMapStr)) {
        	removeCourseTeacherMap = objectMapper.readValue(resultRemoveCourseTeacherMapStr, Map.class);
        	Map<String,Object> errorInfo =  (Map<String, Object>) removeCourseTeacherMap.get("error_info");
        	String errno = errorInfo.get("errno").toString();
        	String error = errorInfo.get("error").toString();
        	
        	if("1".equals(errno)) {
        		//课程下教师删除成功，继续添加为学生
        		return "redirect:/classinCourseClass/addAsClassStudent.do?course_id="+course_id+"&type="+type;
        	}else {
        		model.addAttribute("source", "来自classin的提示信息");
        		model.addAttribute("errno", errno);
            	model.addAttribute("error", error);
            	model.addAttribute("type", type);
            	return "/classin/tips";
        	}
        }else {
        	model.addAttribute("type", type);
        	model.addAttribute("source", "来自BB的提示信息");
        	model.addAttribute("error", "已将您的教师角色从课程中成功移除，如未跳转，请刷新网页后继续注册为该课程的学生~");
        	return "/classin/tips";
        }
		
	}
	
}
