package com.blackboard.classin.service;

import com.blackboard.classin.entity.CourseClassConditionData;

import java.util.List;
import java.util.Map;

public interface CourseDataService {
    List<CourseClassConditionData> getCourseData(Map map);
    Map getCourseCheckAndRate(String courseId);
    String getKeBiaoKeCheckRate(String courseId);
    List<CourseClassConditionData> getCourseAllData(String courseId);
    List<CourseClassConditionData> getPageClassData(Map map);
    List<CourseClassConditionData> getLastPageData();
    int getDataRows(String courseId);
}
