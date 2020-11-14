package com.blackboard.classin.entity;

public class ErrorInfo {
    private int errno;
    private String error;

    public ErrorInfo() {
    }

    public int getErrno() {
        return this.errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
