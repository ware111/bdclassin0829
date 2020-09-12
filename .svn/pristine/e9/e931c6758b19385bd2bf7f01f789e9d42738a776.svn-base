package com.blackboard.classin.exception;
/**
 * @author lian.lixia
 * 2018-11-15
 */

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


public class CustomExceptionResolver implements HandlerExceptionResolver {
	
	private final static Logger LOG = Logger.getLogger(CustomExceptionResolver.class);
 
    @Override
    //该方法是捕获项目抛出的异常
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
 
        ex.printStackTrace();
        CustomException customException = null;
        
        if(ex instanceof CustomException) {
            customException = (CustomException) ex;
            LOG.error("异常信息："+customException.getMessage()+"异常类："+customException.getClassName()+"异常方法："+customException.getMethodName());
        } else {           
            customException = new CustomException("系统出现异常，请联系管理员");
            LOG.error("系统出现异常，请联系管理员");
        } 
      
        Map<String, String> model = new HashMap<String, String>();
        model.put("message", customException.getMessage());
        model.put("className", customException.getClassName());
        model.put("methodName", customException.getMethodName());
        ModelAndView modelAndView = new ModelAndView("/error/error", model);             
        return modelAndView;
    }
}
