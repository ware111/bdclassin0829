package com.blackboard.classin.service.impl;

import com.blackboard.classin.entity.StudentDetail;
import com.blackboard.classin.mapper.StudentDetailMapper;
import com.blackboard.classin.service.ClassStudentDataService;
import com.blackboard.classin.service.CourseStudentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClassStudentDataImp implements ClassStudentDataService {

    @Autowired
    private StudentDetailMapper studentDetailMapper;


    @Override
    public List<StudentDetail> getClassStudentData(Map map) {
        List<StudentDetail> classStudentData = studentDetailMapper.getClassStudentData(map);
        return classStudentData;
    }
}
