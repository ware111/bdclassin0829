package com.blackboard.classin.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class BaseHandlerException implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		BaseException baseEX = new BaseException("系统出现异常，请联系管理员");
		if(ex instanceof BaseException) {
			baseEX = (BaseException) ex;
		}
		Map<String, String> model = new HashMap<String, String>();
        model.put("message", baseEX.getMessage());
        model.put("className", baseEX.getClassName());
        model.put("methodName", baseEX.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/error/error", model);             
        return modelAndView;
	
	}

}
