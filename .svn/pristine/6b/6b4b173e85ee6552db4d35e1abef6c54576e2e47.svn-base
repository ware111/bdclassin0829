package com.blackboard.classin.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackboard.classin.entity.UserInfo;
import com.blackboard.classin.mapper.UserInfoMapper;
import com.blackboard.classin.service.UserInfoService;

@Service("UserInfoService")
public class UserInfoServiceImpl implements UserInfoService{
	
	//日志
	private Logger log = Logger.getLogger(UserInfoServiceImpl.class);

	@Autowired
	private UserInfoMapper mapperp;
	
	@Override
	public List<UserInfo> getUserList() {
		log.info("查询用户信息");
		return mapperp.getUsers();
	}
	
}
