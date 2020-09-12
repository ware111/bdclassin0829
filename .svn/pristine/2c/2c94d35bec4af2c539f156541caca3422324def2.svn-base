package com.blackboard.classin.exception;

/**
 * 基础异常类
 * 若无特殊处理可直接抛出该类
 * @author Administrator
 *
 */
public class BaseException extends Exception{

	//异常信息
	public String message;
	//类名
	public String className;
	//方法名
	public String methodName;
	//跳转页面
	public String path = "/error/error";
	
	public BaseException(String message) {
		this.message = message;
	}
	
	public BaseException(String message,String className,String methodName) {
		this.message = message;
		this.className = className;
		this.methodName = methodName;
	}
	
	public BaseException(String message,String className,String methodName,String path) {
		this.message = message;
		this.className = className;
		this.methodName = methodName;
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
}
