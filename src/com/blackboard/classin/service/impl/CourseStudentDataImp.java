package com.blackboard.classin.service.impl;

import com.blackboard.classin.entity.StudentDetail;
import com.blackboard.classin.mapper.StudentDetailMapper;
import com.blackboard.classin.service.CourseStudentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourseStudentDataImp implements CourseStudentDataService {

    @Autowired
    private StudentDetailMapper studentDetailMapper;

    @Override
    public List<StudentDetail> getCourseStudentData(Map map) {
        List<StudentDetail> courseStudentDatas = studentDetailMapper.getCourseStudentData(map);
        return courseStudentDatas;
    }

    @Override
    public int getCourseCheckinStudentTotal(String courseId) {
        int courseCheckinStudentTotal = studentDetailMapper.getCourseCheckinStudentTotal(courseId);
        return courseCheckinStudentTotal;
    }
}
