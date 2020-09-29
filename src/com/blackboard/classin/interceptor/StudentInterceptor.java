package com.blackboard.classin.interceptor;

import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.BbServiceManager;
import blackboard.platform.context.Context;
import com.blackboard.classin.exception.CustomException;
import com.blackboard.classin.util.SystemUtil;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author WangYan 2018/12/7
 *
 * 定义一个拦截器，拦截非管理员用户访问项目
 */
public class StudentInterceptor implements HandlerInterceptor{

    //日志
    private Logger log = Logger.getLogger(StudentInterceptor.class);

    /**
     * 项目运行之前首先来此处判断登录用户是否系统管理员角色：
     *   是则放过，允许访问，不是则拦截住，返回异常页面
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("access preHandle method() ...");

        if(SystemUtil.isStudent() || SystemUtil.isAdministrator()){
            return true;
        }else{
            throw new Exception("非法访问，请联系管理员！");
        }

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
