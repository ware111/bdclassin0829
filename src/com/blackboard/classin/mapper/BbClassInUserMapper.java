package com.blackboard.classin.mapper;

import org.apache.ibatis.annotations.Param;

import com.blackboard.classin.entity.BbClassInUserInfo;

public interface BbClassInUserMapper {

	BbClassInUserInfo getByTelephone(@Param("telephone") String telephone);

	void save(BbClassInUserInfo bbClassInUser);

}
