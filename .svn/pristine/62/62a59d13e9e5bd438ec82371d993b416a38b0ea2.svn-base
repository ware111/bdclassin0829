package com.blackboard.classin.mapper;

import com.blackboard.classin.entity.SystemRegistry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemRegistryMapper {
    /**
     * 获取classin相关配置信息
     * @return
     */
    List<SystemRegistry> getClassinURLConfigs(String conditionStr);

    /**
     * 保存classIn相关配置信息
     * @param registryKey
     * @param registryValue
     */
    void save(@Param("registryKey") String registryKey, @Param("registryValue") String registryValue);

    /**
     * 修改classin配置信息
     * @param registryKey
     * @param registryValue
     */
    void modify(@Param("registryKey") String registryKey, @Param("registryValue") String registryValue);

    /**
     * 获取classIn入口url
     * @return
     */
    String getClassinEntranceURL();

    /**
     * 获取classIn导入成绩接口url
     * @return
     */
    String getClassinImportGradeURL();

    /**
     * 获取classIn展示活动信息接口URL
     * @return
     */
    String getClassinClassActivityInfoURL();
    
    /**
     * 获取classIn创建课程URL
     * @return
     */
    String getClassinAddCourseURL();
    
    /**
     * 获取classin创建课节URL
     * @return
     */
    String getClassinAddCourseClassURL();

    /**
     * 根据registryKey获取URL
     * @param registryKey
     * @return
     */
	String getURLByKey(@Param("registryKey") String registryKey);
	
}
