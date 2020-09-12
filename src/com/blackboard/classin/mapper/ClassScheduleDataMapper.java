package com.blackboard.classin.mapper;

import java.util.List;
import java.util.Map;

public interface ClassScheduleDataMapper {
    List<Map<String,String>> getCreateClassResult(Map<String, String> paraMap);
    void saveHandledScheduleData(Map<String, String> paraMap) ;
    List<Map<String,String>> getAllCreateClassResult(String todayTime);
    String getCreateClassStatus(String subId);
    void deleteFailureDataBySubId(String content);
    void deleteFailureDataByReason (Map<String, String> paraMap);
    List<Map<String,String>> getDataByReason(Map<String,String> paraMap);

}
