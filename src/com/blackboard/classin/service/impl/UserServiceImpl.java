package com.blackboard.classin.service.impl;

import com.blackboard.classin.entity.UserInfo;
import com.blackboard.classin.mapper.UserInfoMapper;
import com.blackboard.classin.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangyan on 2018/11/5.
 */
@Service("UserService")
public class UserServiceImpl implements UserService {

    //日志
    private Logger log = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private UserInfoMapper mapper;

    @Override
    public List<UserInfo> getUserList() {
        log.info("查询用户信息Impl");
        return mapper.getUsers();
    }
}
