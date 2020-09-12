package com.blackboard.classin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.blackboard.classin.util.SystemUtil;


public class DeleteInterceptor implements HandlerInterceptor{

	    //日志
	    private Logger log = Logger.getLogger(DeleteInterceptor.class);

	    /**
	     * 用户是教师或助教可删除，其他角色不能删除
	     * @param httpServletRequest
	     * @param httpServletResponse
	     * @param o
	     * @return
	     * @throws Exception
	     */
	    @Override
	    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
	        log.info("delete preHandle method() ...");

	        //访问之前先看session中有没有系统角色
	        // 有则判断是否教师
	        if(SystemUtil.isTeacher()){
	            return true;
	        }else{
	        	throw new Exception("您未登录或您没有执行该操作的权限！");
	        }

	    }

	    @Override
	    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

	    }

	    @Override
	    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

	    }
	}
