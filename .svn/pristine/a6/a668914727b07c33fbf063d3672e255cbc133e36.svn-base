package com.blackboard.classin.mapper;

import com.blackboard.classin.entity.ClassinCourseClass;

import java.util.List;
import java.util.Map;

public interface ClassinCourseClassMapper {

	ClassinCourseClass findByClassinCourseId(String classinCourseId);

	void save(Map<String, Object> paramMap);

	void updateToExpired(Map<String, String> paramMap);

	void updateAssistantPhone(Map<String, String> paramMap);

	List<ClassinCourseClass> getReplayList(String courseId);

	List<Map<String,Object>> getClassList(Map<String,Object> paraMap);

	List<Map<String,Object>> getClassStatus(Map<String,Object> paraMap);

	void delete(String classinClassId);

	ClassinCourseClass findByClassId(String classinClassId);

	void updateClassToDelay(ClassinCourseClass classinCourseClass);

	void deleteClass(String classId,String courseId);

	void editClassTeacher(Map<String,Object> paraMap);
    void editAssistantTeacher(Map<String,Object> paraMap);

	String findByBBCourseId(String bbCourseId);

    List<Map<String,String>> findClassinIdByBBCourseId(Map<String,String> param);
	//编辑授课老师
	void editTeacher(Map<String,Object> paraMap);

	//编辑助教老师
	void editAssistant(Map<String,Object> paraMap);


}
