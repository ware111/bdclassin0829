package com.blackboard.classin.service;

import com.blackboard.classin.entity.BbClassInInfo;

/**
 * @date 2019-04-24
 * @author wangy
 *
 */
public interface BbClassInInfoService {
	
	/**
	 * 根据课程ID查找本课程未过期的ClassIn课堂
	 * @return
	 */
	BbClassInInfo findNoExpiredBbClassIn(String bbCourseId);

	/**
	 * 保存该门课程的classIn课堂
	 * @param courseId
	 * @param classInCourseId
	 * @param classinCourseClassId
	 * @param currentCreateClassTime
	 */
	void saveBbClassInInfo(String courseId, String classInCourseId, String classinCourseClassId,
			long currentCreateClassTime);

	int findIsCreatingCourseClass(String course_id);
	
	void insertCreatingCourseClass(String course_id);

	void deleteCreatingCourseClass(String course_id);
	
}
