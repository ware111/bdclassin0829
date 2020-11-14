package com.blackboard.classin.service;

import blackboard.persist.PersistenceException;

import java.io.IOException;

/**
 * 处理推送数据
 * @author panhaiming
 * @date 20200928
 * */
public interface CheckinRelationDataService {
    //处理课节情况出勤数据
    void handleClassSituationData(String datas) throws PersistenceException, IOException;
    //处理学生详情出勤数据
    void handleStudentDetailData(String datas) throws IOException, PersistenceException;
}
