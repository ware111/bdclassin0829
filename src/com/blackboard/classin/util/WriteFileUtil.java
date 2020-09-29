package com.blackboard.classin.util;

import com.blackboard.classin.mapper.ClassScheduleDataMapper;

import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

//写入csv文件工具类
public class WriteFileUtil {
    public static void writeFile(ClassScheduleDataMapper classScheduleDataMapper,String fileName,String SID) throws IOException {


        BufferedWriter bufferedWriters = null;
        bufferedWriters = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream
                (fileName, true)), "gbk"));
        bufferedWriters.append("编号#####" + "错误内容#####" + "结果#####" + "错误原因#####" + "课程ID#####" +"课表时间#####" + "SID"+"\n");
        List<Map<String, String>> failureData = classScheduleDataMapper.getAllCreateClassResult(TimeStampUtil.getTodayTime());

        for (int i = 0; i < failureData.size(); i++) {
            try {
                bufferedWriters.append(i + "#####" + failureData.get(i).get("CONTENT") + "#####" +
                        failureData.get(i).get("RESULT") + "#####" +
                        failureData.get(i).get("REASON") + "#####" + failureData.get(i).get("COURSE_ID") + "#####"+failureData.get(i).get("YEAR_DATE")  + "#####"+ SID +"\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
            bufferedWriters.flush();
        }
    }
}
