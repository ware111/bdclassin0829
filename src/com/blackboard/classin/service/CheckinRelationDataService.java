package com.blackboard.classin.service;

import blackboard.persist.PersistenceException;

import java.io.IOException;

/**
 * 处理推送数据
 * @author panhaiming
 * @date 20200928
 * */
public interface CheckinRelationDataService {
    void handleClassSituationData(String datas) throws PersistenceException, IOException;
    void handleStudentDetailData(String datas) throws IOException, PersistenceException;
}
