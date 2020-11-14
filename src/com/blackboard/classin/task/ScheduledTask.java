package com.blackboard.classin.task;

import blackboard.data.course.Course;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.*;
import com.blackboard.classin.mapper.*;
import com.blackboard.classin.service.IBbCourseClassinCourse;
import com.blackboard.classin.service.TimerTaskService;
import com.blackboard.classin.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ScheduledTask {

    private Logger log = Logger.getLogger(ScheduledTask.class);


    @Autowired
    private ClassinCourseClassMapper classinCourseClassMapper;

    @Autowired
    private SystemRegistryMapper systemRegistryMapper;

    @Autowired
    private UserPhoneMapper userPhoneMapper;

    @Autowired
    private BbCourseClassinCourseMapper bbCourseClassinCourseMapper;

    @Autowired
    private ClassScheduleDataMapper classScheduleDataMapper;

    @Autowired
    private IBbCourseClassinCourse classinCourseServuce;

    @Autowired
    TimerTaskService timerTaskService;

    /**
     * 定时删除已经从bb网移除的课程下学生
     *
     * @author panhaiming
     * @date 20200905
     */
    @Scheduled(cron = "0 0 4 ? * SUN")
    public void deleteStudentTask() throws IOException {
        Process hostname = Runtime.getRuntime().exec("hostname");
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(hostname.getInputStream()));
        String hostName;
        String targetHostName = GetServiceHostNameUtil.getSecondHostName();
        while ((hostName = stdInput.readLine()) != null) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>主机名ip地址" + hostName);
            if (hostName.equals(targetHostName)) {
                classinCourseServuce.deleteCourseStudentByPhoneAndUid();
            }
        }

    }


    /**
     * 创建课节定时任务
     *
     * @author panhaiming
     * @date 20200825
     */
    @Scheduled(cron = "0 0 5 * * *")
    public void firstCreateTask() throws IOException, MessagingException, InterruptedException {
        Process hostname = Runtime.getRuntime().exec("hostname");
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(hostname.getInputStream()));
        String hostName;
        String targetHostName = GetServiceHostNameUtil.getHostName();
        while ((hostName = stdInput.readLine()) != null) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hostname is " + hostName);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>target is " + targetHostName);
            if (hostName.equals(targetHostName)) {
                timerTaskService.scheduleTask();
                System.out.println(">>>>>>>>>>>>>>one task" + Thread.currentThread());
            }
        }


    }


    @Scheduled(cron = "0 0 6 * * *")
    public void secondCreateClassTask() throws IOException, MessagingException, InterruptedException {
        Process hostname = Runtime.getRuntime().exec("hostname");
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(hostname.getInputStream()));
        String hostName;
        String targetHostName = GetServiceHostNameUtil.getHostName();
        while ((hostName = stdInput.readLine()) != null) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hostname is " + hostName);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>target is " + targetHostName);
            if (hostName.equals(targetHostName)) {
                List<Map<String, String>> createClassResult = classScheduleDataMapper.getAllCreateClassResult(TimeStampUtil.getTodayTime());
                if (createClassResult != null && createClassResult.size() != 0) {
                    timerTaskService.scheduleTask();
                }
                System.out.println(">>>>>>>>>>>>>>second task" + Thread.currentThread());
            }
        }
    }
}