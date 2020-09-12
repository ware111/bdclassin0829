package com.blackboard.classin.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blackboard.persist.PersistenceException;

public interface IClassinCourseClass {

	String createClassinCourseClass(HttpServletRequest request, HttpServletResponse response, String type, String course_id) throws PersistenceException;

	String autoCreateClassinCourseClass() throws PersistenceException;

	void saveClassLen(Map<String, Object> paramMap);


}
