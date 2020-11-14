package com.blackboard.classin.mapper;

import com.blackboard.classin.entity.StudentDetail;

import java.util.List;
import java.util.Map;

public interface StudentDetailMapper {
    void saveStudentDetail(StudentDetail studentDetail);
    List<StudentDetail> getCourseStudentData(Map map);
    List<StudentDetail> getClassStudentData(Map map);
    int getCourseCheckinStudentTotal(String courseId);
}
