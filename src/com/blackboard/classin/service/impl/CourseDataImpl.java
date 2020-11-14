package com.blackboard.classin.service.impl;

import com.blackboard.classin.entity.CourseClassConditionData;
import com.blackboard.classin.mapper.ClassConditionMapper;
import com.blackboard.classin.service.CourseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseDataImpl implements CourseDataService {

    @Autowired
    private ClassConditionMapper classConditionMapper;


   @Override
    public List<CourseClassConditionData> getCourseData(Map map) {
        List<CourseClassConditionData> courseDatas = classConditionMapper.getCourseData(map);
        return courseDatas;
    }

    @Override
    public Map getCourseCheckAndRate(String courseId) {
        Map courseCheckAndRate = classConditionMapper.getCourseCheckAndRate(courseId);
        return courseCheckAndRate;
    }

    @Override
    public String getKeBiaoKeCheckRate(String courseId) {
        String keBiaoKeCheckRate = classConditionMapper.getKeBiaoKeCheckRate(courseId);
        return keBiaoKeCheckRate;
    }

    @Override
    public List<CourseClassConditionData> getCourseAllData(String courseId) {
        List<CourseClassConditionData> courseAllData = classConditionMapper.getCourseAllData(courseId);
        return courseAllData;
    }

    @Override
    public List<CourseClassConditionData> getPageClassData(Map map) {
        List<CourseClassConditionData> pageClassData = classConditionMapper.getPageClassData(map);
        return pageClassData;
    }

    @Override
    public List<CourseClassConditionData> getLastPageData() {
        List<CourseClassConditionData> lastPageData = classConditionMapper.getLastPageData();
        return lastPageData;
    }

    @Override
    public int getDataRows(String courseId) {
        int lastPage = classConditionMapper.getDataRows(courseId);
        return lastPage;
    }

}
