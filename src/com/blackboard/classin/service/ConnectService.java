package com.blackboard.classin.service;

import com.blackboard.classin.entity.ServerConfigForm;

public interface ConnectService {
	
	/**
	 * 获取db原有的配置
	 * @return
	 */
	public ServerConfigForm getDBContent();
	
	
	/**
	 * 修改db文件中数据库的配置信息
	 */
	public void saveDBProperties(ServerConfigForm f);

}
