package com.blackboard.classin.entity;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wangy on 2020-03-05.
 */
public class CourseStudentPOJO implements Serializable {
    private static final long serialVersionUID = -4521729850217638532L;

    private Map<String,String> data;

    @JSONField(name = "error_info")
    private ErrorInfo errorInfo;

    public static class ErrorInfo{
        private String errno;
        private String error;

        public String getErrno() {
            return errno;
        }

        public void setErrno(String errno) {
            this.errno = errno;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }
}
