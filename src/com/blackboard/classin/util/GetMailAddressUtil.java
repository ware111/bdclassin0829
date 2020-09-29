package com.blackboard.classin.util;

import com.blackboard.classin.controller.ClassinCourseClassController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetMailAddressUtil {
    public static String getMailAddress() throws IOException {
        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("mailAddress.properties");
        Properties properties = new Properties();
        properties.load(resource);
        String address = properties.getProperty("address");
        return address;
    }
}
