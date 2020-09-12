package com.blackboard.classin.controller;

import com.blackboard.classin.entity.SystemRegistry;
import com.blackboard.classin.service.SystemRegistryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.blackboard.classin.entity.ServerConfigForm;
import com.blackboard.classin.service.ConnectService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author yunfeiwu 20181127
 *	
 */
@Controller
@RequestMapping("/config")
public class ConfigController {
	
	@Autowired
	private ConnectService connectService;

	@Autowired
	private SystemRegistryService systemRegistryService;
	//日志
	private Logger log = Logger.getLogger(ConfigController.class);
		
	/**
	 * 修改db.properties文件的数据库配置
	 * 
	 * @param model
	 * @param server_address
	 * @return
	 */
	@RequestMapping("/modifyServer.do")
	public String modifyServer(Model model, ServerConfigForm server_address) {
		ServerConfigForm f = server_address;
		log.info("=====修改开始=====");
		connectService.saveDBProperties(f);
		log.info("=====修改结束=====");
		return "/config/configPage";
	}
	
	/**
	 *  初始化显示
	 * @param model
	 * @return
	 */
	@RequestMapping("/initPageForDisplay.do")
	private String initPageForDisplay(Model model) {
		ServerConfigForm myForm = new ServerConfigForm();
		myForm = connectService.getDBContent();
		model.addAttribute("myForm", myForm);
		return "/config/connect/configDataSource";
	}

	/**
	 * 初始化配置
	 * @return
	 */
	@RequestMapping("/initClassInServer.do")
	public String initClassinServer(Model model){
		List<SystemRegistry> systemRegistryServiceList = systemRegistryService.getClassinURLConfigs();
		for (SystemRegistry systemRegistry : systemRegistryServiceList ){
			//唤醒客户端并进入教室url
			if(systemRegistry.getRegistryKey().equals("classin_entrance_url")){
				model.addAttribute("classin_entrance_url",systemRegistry.getRegistryValue());
			}
			//获取成绩信息
			if(systemRegistry.getRegistryKey().equals("classin_import_grade_url")){
				model.addAttribute("classin_import_grade_url",systemRegistry.getRegistryValue());
			}
			//获取活动信息
			if(systemRegistry.getRegistryKey().equals("classin_class_activity_info_url")){
				model.addAttribute("classin_class_activity_info_url",systemRegistry.getRegistryValue());
			}
			//创建课程
			if(systemRegistry.getRegistryKey().equals("classin_addcourse_url")){
				model.addAttribute("classin_addcourse_url",systemRegistry.getRegistryValue());
			}
			//创建课节
			if(systemRegistry.getRegistryKey().equals("classin_addcourseclass_url")){
				model.addAttribute("classin_addcourseclass_url",systemRegistry.getRegistryValue());
			}
			//课程下添加学生
			if(systemRegistry.getRegistryKey().equals("classin_addcoursestudent_url")){
				model.addAttribute("classin_addcoursestudent_url",systemRegistry.getRegistryValue());
			}
			//注册用户
			if(systemRegistry.getRegistryKey().equals("classin_register_url")){
				model.addAttribute("classin_register_url",systemRegistry.getRegistryValue());
			}
			//添加教师
			if(systemRegistry.getRegistryKey().equals("classin_addteacher_url")){
				model.addAttribute("classin_addteacher_url",systemRegistry.getRegistryValue());
			}
		}
		return "/config/classinServer/configClassinServer";
	}

	/**
	 * classin配置信息的保存
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifyClassInServerURLs.do",method = RequestMethod.POST)
	public String modifyClassinServerURLs(Model model, HttpServletRequest request){

		log.info("modify and save ClassinServerURLs ");

		String classinEntranceURL = request.getParameter("ClassInEntranceURL");
		String classinImportGradeURL = request.getParameter("ClassInImportGradeURL");
		String classinClassActivityInfoURL = request.getParameter("ClassInClassActivityInfoURL");
		String classInAddCourseURL = request.getParameter("ClassInAddCourseURL");
		String classInAddCourseClassURL = request.getParameter("ClassInAddCourseClassURL");
		String classinRegisterUrl = request.getParameter("ClassinRegisterUrl");
		String classinAddCourseStudentUrl = request.getParameter("ClassinAddCourseStudentUrl");
		String classinAddTeacherURL = request.getParameter("ClassinAddTeacherURL");
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("classinEntranceURL", classinEntranceURL);
		paramMap.put("classinImportGradeURL", classinImportGradeURL);
		paramMap.put("classinClassActivityInfoURL", classinClassActivityInfoURL);
		paramMap.put("classinAddCourseURL", classInAddCourseURL);
		paramMap.put("classinAddCourseClassURL", classInAddCourseClassURL);
		paramMap.put("classinRegisterUrl", classinRegisterUrl);
		paramMap.put("classinAddCourseStudentUrl", classinAddCourseStudentUrl);
		paramMap.put("classinAddTeacherURL", classinAddTeacherURL);

		systemRegistryService.modifyOrSaveClassinServerURLs(paramMap);

		return "/config/configPage";
	}

}
