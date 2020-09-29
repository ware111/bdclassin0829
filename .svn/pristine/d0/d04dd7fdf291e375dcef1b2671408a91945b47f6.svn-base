package com.blackboard.classin.controller;

/*
 * @author lian.lixia
 * 2018-11-15
 */
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.entity.UserInfo;
import com.blackboard.classin.exception.CustomException;
import com.blackboard.classin.service.UserService;
import com.blackboard.classin.util.SystemUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	//日志
	private Logger log = Logger.getLogger(UserController.class);
	
	@RequestMapping("/getUsers.do")
	 public String getUserList(Model model) throws Exception {
		//List<UserInfo> userList = userInfoService.getUserList();
		//需要抛出异常示例：CustomException("提示异常信息","异常类","异常方法");
	    List<UserInfo> userList=null;			
		if(userList==null) 		
			throw new CustomException("user表没有数据","UserController","getUserList");				
		model.addAttribute("userList", userList);		
		return "/userInfo";
	}	

	@ResponseBody
	@RequestMapping("/getUserType.do")
	public String checkUser(){
		boolean isTeacher = SystemUtil.isTeacher();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isTeacher",isTeacher+"");
		return jsonObject.toJSONString();
	}

}
