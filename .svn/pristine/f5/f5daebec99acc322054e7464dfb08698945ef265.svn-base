package com.blackboard.classin.service.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.blackboard.classin.entity.ServerConfigForm;
import com.blackboard.classin.service.ConnectService;
/**
 * 
 * @author yunfeiwu 20181127
 *	
 */
@Service("ConnectService")
public class ConnectServiceImpl implements ConnectService{
	private static final String profilepath = ConnectServiceImpl.class
			.getResource("/").getPath() + "db.properties";// 我的配置文件在src根目录下
	private static Properties props = new Properties();

	//日志
	private Logger log = Logger.getLogger(ConnectServiceImpl.class);
	/**
	 * 获取db原有的配置
	 * 
	 * @return
	 */
	@Override
	public  ServerConfigForm getDBContent() {
		ServerConfigForm server = new ServerConfigForm();
		try {
			props.load(new FileInputStream(profilepath));
			OutputStream fos = new FileOutputStream(profilepath);
			server.setDatasource((String) props.get("driverClassName"));
			server.setServer_address((String) props.get("db.url"));
			server.setServer_name((String) props.get("db.username"));
			server.setServer_password((String) props.get("db.password"));

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("=====属性文件更新错误=====");
		}

		return server;
	}

	/**
	 *  修改db文件中数据库的配置信息
	 */
	@Override
	public  void saveDBProperties(ServerConfigForm f) {

		try {
			props.load(new FileInputStream(profilepath));
			OutputStream fos = new FileOutputStream(profilepath);
			props.setProperty("driverClassName", f.getDatasource());
			props.setProperty("db.url", f.getServer_address());
			props.setProperty("db.username", f.getServer_name());
			props.setProperty("db.password", f.getServer_password());
			props.store(fos, "Update value");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("=====属性文件更新错误=====");
		}
	}




	

}