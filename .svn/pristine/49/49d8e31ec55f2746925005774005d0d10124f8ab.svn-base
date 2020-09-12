package com.blackboard.classin.interceptor;

import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.BbServiceManager;
import blackboard.platform.context.Context;
import com.blackboard.classin.exception.CustomException;
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
public class AccessInterceptor implements HandlerInterceptor{

    //日志
    private Logger log = Logger.getLogger(AccessInterceptor.class);

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

        //访问之前先看session中有没有系统角色
        // 有则判断是否管理员
        String systemRole = (String) httpServletRequest.getSession().getAttribute("systemRole");
        if(systemRole != null && systemRole.equals("SYSTEM_ADMIN")){
            return true;
        }else{
            //无则判断登录的用户是否管理员
            //使用BB API获取已登录的用户信息
            Context _ctx = BbServiceManager.getContextManager().getContext();
            Id userPk = _ctx.getUserId();
            UserDbLoader userDbLoader = null;
            try {
                userDbLoader = UserDbLoader.Default.getInstance();
                User user = userDbLoader.loadById(userPk);

                //获取用户的系统角色
                User.SystemRole role  = user.getSystemRole();
                //只有系统管理员才有权限进入系统
                if(role.getFieldName().equals("SYSTEM_ADMIN")){
                    //是系统管理员则将系统角色放入session，供访问后面的URL使用
                    httpServletRequest.getSession().setAttribute("systemRole",role.getFieldName());
                    return true;
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
            //判断该用户的系统角色不是系统管理员，则不让用户访问该请求。
            throw new CustomException("由于您不是系统管理员角色，您无权访问该插件中的内容！");
        }

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
