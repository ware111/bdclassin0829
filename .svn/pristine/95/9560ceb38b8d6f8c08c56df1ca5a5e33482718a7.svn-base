package com.blackboard.classin.service;

import com.blackboard.classin.entity.BbClassInUserInfo;

public interface BbClassInUserService {

	/**
	 * 根据手机号查找是否已注册过用户
	 * @param telephone
	 * @return
	 */
	BbClassInUserInfo findByTelephone(String telephone);

	/**
	 * 保存绑定信息
	 * @param bbUserId
	 * @param classinUid
	 * @param telephone
	 */
	void saveBbClassinUser(String bbUserId, String classinUid, String telephone);

}
