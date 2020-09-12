package com.blackboard.classin.mapper;

import org.apache.ibatis.annotations.Param;

import com.blackboard.classin.entity.BbClassInInfo;

/**
 * 
 * @date 2019-04-24
 * @author wangy
 *
 */
public interface BbClassInInfoMapper {
	
	BbClassInInfo findByBbCourseId(String bbCourseId);

	void setBbClassInToExpired(Integer pk1);

	void save(BbClassInInfo bbClassIn);

	int countIsCreatingCourseClass(@Param("course_id") String course_id);

	void insertCreatingCourseClass(String course_id);

	void deleteCreatingCourseClass(String course_id);
}
