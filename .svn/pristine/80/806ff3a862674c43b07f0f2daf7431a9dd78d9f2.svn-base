package com.blackboard.classin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackboard.classin.entity.TimerTaskBean;
import com.blackboard.classin.mapper.TimerTaskMapper;
import com.blackboard.classin.service.ITimerTask;

@Service
public class TimerTask implements ITimerTask {
	
	@Autowired
	private TimerTaskMapper timerTaskMapper;

	@Override
	public void insertTimerTaskInfo(TimerTaskBean timerTaskBean) {
		timerTaskMapper.insertTimerTaskInfo(timerTaskBean);
	}

}
