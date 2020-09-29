package com.blackboard.classin.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackboard.classin.entity.BbClassInUserInfo;
import com.blackboard.classin.mapper.BbClassInUserMapper;
import com.blackboard.classin.service.BbClassInUserService;

@Service
public class BbClassInUserServiceImpl implements BbClassInUserService{

	@Autowired
	private BbClassInUserMapper mapper;
	
	@Override
	public BbClassInUserInfo findByTelephone(String telephone) {
		return mapper.getByTelephone(telephone);
	}
	
	@Override
	public void saveBbClassinUser(String bbUserId, String classinUid, String telephone) {
		
		BbClassInUserInfo bbClassInUser = new BbClassInUserInfo();
		
		Timestamp time = new Timestamp(System.currentTimeMillis());
		
		bbClassInUser.setBbUserId(bbUserId);
		bbClassInUser.setClassinUid(classinUid);
		bbClassInUser.setTelephone(telephone);
		bbClassInUser.setDtcreated(time);
		
		mapper.save(bbClassInUser);
	}

}
