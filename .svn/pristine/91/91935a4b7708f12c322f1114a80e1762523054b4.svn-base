package com.blackboard.classin.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blackboard.classin.entity.BbCourseClassinCourse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import blackboard.persist.PersistenceException;

public interface IBbCourseClassinCourse {

	String createClassinCourseOnBbCourse(HttpServletRequest request, HttpServletResponse response,String course_id, String classin_addcourse_url, String type) throws ParseException, PersistenceException;

	void deleteClassInCourseStudent() throws JsonParseException, JsonMappingException, IOException;

}
