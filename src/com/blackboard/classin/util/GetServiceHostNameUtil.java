package com.blackboard.classin.util;

import com.blackboard.classin.controller.ClassinCourseClassController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetServiceHostNameUtil {
    public static String getHostName() throws IOException {
        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("HostName.properties");
        Properties properties = new Properties();
        properties.load(resource);
        String hostName = properties.getProperty("hostName");
        return hostName;
    }
}
