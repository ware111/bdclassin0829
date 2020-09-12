package com.blackboard.classin.service;

import com.blackboard.classin.entity.SystemRegistry;

import java.util.List;
import java.util.Map;

public interface SystemRegistryService {

    /**
     * 获取所有和Classin相关的配置信息
     * @return
     */
    List<SystemRegistry> getClassinURLConfigs();


    /**
     * 保存配置信息
     * @param classinEntranceURL
     * @param classinImportGradeURL
     */
    void modifyOrSaveClassinServerURLs(Map<String,String> paramMap);

	/**
	 * 根据Registry_key 获取配置的url
	 * @param string
	 * @return
	 */
	String getURLByKey(String registryKey);
}
