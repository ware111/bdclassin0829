package com.blackboard.classin.mapper;

import com.blackboard.classin.entity.CourseClassConditionData;

import java.util.List;
import java.util.Map;

public interface ClassConditionMapper {
    void saveClassCondition(CourseClassConditionData courseClassConditionData);
    List<CourseClassConditionData> getCourseData(Map map);
    Map getCourseCheckAndRate(String courseId);
    String getKeBiaoKeCheckRate(String courseId);
    List<CourseClassConditionData> getCourseAllData(String courseId);
    List<CourseClassConditionData> getPageClassData(Map map);
    List<CourseClassConditionData> getLastPageData();
    int getDataRows(String courseId);
}
