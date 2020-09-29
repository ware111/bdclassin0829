package com.blackboard.classin.exception;
/**
 * 
 * @author lian.lixia
 * 2018-11-15
 */
//定义一个简单的异常类
public class CustomException extends Exception {

	// 异常信息
	public String message;
	public String className;
	public String methodName;

	public CustomException(String message) {
		super(message);
		this.message = message;

	}
	
	public CustomException(String message, String className, String methodName) {
		// super(message);
		this.message = message;
		this.className = className;
		this.methodName = methodName;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
