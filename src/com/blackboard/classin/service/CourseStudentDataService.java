package com.blackboard.classin.service;

import com.blackboard.classin.entity.StudentDetail;

import java.util.List;
import java.util.Map;

public interface CourseStudentDataService {
    List<StudentDetail> getCourseStudentData(Map map);
    int getCourseCheckinStudentTotal(String courseId);
}
