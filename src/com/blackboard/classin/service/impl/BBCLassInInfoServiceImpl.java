package com.blackboard.classin.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackboard.classin.entity.BbClassInInfo;
import com.blackboard.classin.mapper.BbClassInInfoMapper;
import com.blackboard.classin.service.BbClassInInfoService;
/**
 * @date 2019-04-24
 * @author wangy
 *
 */
@Service
public class BBCLassInInfoServiceImpl implements BbClassInInfoService{
	
	@Autowired
	private BbClassInInfoMapper mapper;

	@Override
	public BbClassInInfo findNoExpiredBbClassIn(String bbCourseId) {
		
		BbClassInInfo  bbClassInInfo = mapper.findByBbCourseId(bbCourseId);
		if(bbClassInInfo != null) {
			java.sql.Timestamp dtCreated = bbClassInInfo.getDtCreated();
			System.out.println(dtCreated.getTime()/1000);
			Date now = new Date();
			System.out.println(now.getTime()/1000);
			System.out.println(now.getTime() - dtCreated.getTime());
			
			//判断当前时间是否超过4小时
			//是则代表已过期，可重新创建课堂；否则代表未过期，不能重复创建课堂
			if((now.getTime() - dtCreated.getTime()) >= (4*60*60*1000)) {
				System.out.println("setBbClassInToExpired ");
				//课堂过期需要设置其状态为1，即过期
				mapper.setBbClassInToExpired(bbClassInInfo.getPk1());
				//返回空对象
				return null;
			}
		}
		//验证通过则返回BBClassIn对象
		return bbClassInInfo;
	}

	@Override
	public void saveBbClassInInfo(String courseId, String classInCourseId, String classinCourseClassId,
			long currentCreateClassTime) {
		
		//保存之前再判断
		BbClassInInfo bbClassInInfo = mapper.findByBbCourseId(courseId);
		if(bbClassInInfo == null) {
			BbClassInInfo bbClassIn = new BbClassInInfo();
			
			//时间
			java.sql.Timestamp time = new java.sql.Timestamp(currentCreateClassTime);
			bbClassIn.setBbCourseId(courseId);
			bbClassIn.setClassInCourseId(classInCourseId);
			bbClassIn.setClassInClassId(classinCourseClassId);
			bbClassIn.setExpireStatus("0");
			bbClassIn.setDtCreated(time);
			
			mapper.save(bbClassIn);
		}
	}

	@Override
	public int findIsCreatingCourseClass(String course_id) {
		
		return mapper.countIsCreatingCourseClass(course_id);
	}
	

	@Override
	public void insertCreatingCourseClass(String course_id) {
		mapper.insertCreatingCourseClass(course_id);
	}

	@Override
	public void deleteCreatingCourseClass(String course_id) {
		mapper.deleteCreatingCourseClass(course_id);
	}
}
