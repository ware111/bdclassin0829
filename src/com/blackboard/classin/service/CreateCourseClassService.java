package com.blackboard.classin.service;

import blackboard.persist.PersistenceException;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 * 创建课节接口
 * @date 20201022
 * @author panhaiming
 *
 * */
public interface CreateCourseClassService {

    /**
     * 创建单个课节
     *  * @date 20201022
     *  * @author panhaiming
     *
     */
    void createCourseClass(HttpServletRequest request, HttpServletResponse response,String className, String classType, String startDate,
                           String startTime, String hour, String minute, String teacher,
                           String assistantTeacher, String bbCourseId, String isLive,
                           String isRecord, String isReplay, String startTimeStamp) throws PersistenceException, IOException;
    /**
     * 批量创建课节
     *  * @date 20201022
     *  * @author panhaiming
     *
     */
    Properties batcheCreateCourseClass(HttpServletRequest request, HttpServletResponse response, String className, String classType, String startDate, String startTime,
                                       String hour, String minute, String teacher, String assistantTeacher,
                                       String bbCourseId, String isLive, String isRecord, String isReplay,
                                       String startTimeStamp, String classAmount, String days, String currentDay, String classNameSuffix) throws PersistenceException, IOException;
}
