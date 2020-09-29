package com.blackboard.classin.service.impl;

import com.blackboard.classin.entity.SystemRegistry;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import java.util.Map;
import com.blackboard.classin.service.SystemRegistryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description TODO
 * @Author wangy
 * @Date 2019/4/17
 */
@Service
public class SystemRegistryServiceImpl implements SystemRegistryService {

    private Logger log = Logger.getLogger(SystemRegistryService.class);

    @Autowired
    SystemRegistryMapper mapper;

    @Override
    public List<SystemRegistry> getClassinURLConfigs() {
        log.info("to getClassinURLConfigs");

        //获取所有的关于classin的URL配置信息
        StringBuilder conditionStr = new StringBuilder();
        conditionStr.append("classin_");
        conditionStr.append("%");

        return mapper.getClassinURLConfigs(conditionStr.toString());
    }

    /**
     * 配置信息，第一次插入，后面都是修改
     * @param paramMap
     */
    @Override
    public void modifyOrSaveClassinServerURLs(Map<String,String> paramMap) {
        List<SystemRegistry> systemRegistries =  getClassinURLConfigs();
        if(systemRegistries == null || systemRegistries.size() == 0){
            mapper.save("classin_entrance_url",paramMap.get("classinEntranceURL"));
            mapper.save("classin_import_grade_url",paramMap.get("classinImportGradeURL"));
            mapper.save("classin_class_activity_info_url",paramMap.get("classinClassActivityInfoURL"));
            mapper.save("classin_addcourse_url",paramMap.get("classinAddCourseURL"));
            mapper.save("classin_addcourseclass_url",paramMap.get("classinAddCourseClassURL"));
            mapper.save("classin_register_url",paramMap.get("classinRegisterUrl"));
            mapper.save("classin_addcoursestudent_url",paramMap.get("classinAddCourseStudentUrl"));
            mapper.save("classin_addteacher_url",paramMap.get("classinAddTeacherURL"));
        }else{
        	 mapper.modify("classin_entrance_url",paramMap.get("classinEntranceURL"));
             mapper.modify("classin_import_grade_url",paramMap.get("classinImportGradeURL"));
             mapper.modify("classin_class_activity_info_url",paramMap.get("classinClassActivityInfoURL"));
             mapper.modify("classin_addcourse_url",paramMap.get("classinAddCourseURL"));
             mapper.modify("classin_addcourseclass_url",paramMap.get("classinAddCourseClassURL"));
             mapper.modify("classin_register_url",paramMap.get("classinRegisterUrl"));
             mapper.modify("classin_addcoursestudent_url",paramMap.get("classinAddCourseStudentUrl"));
             mapper.modify("classin_addteacher_url",paramMap.get("classinAddTeacherURL"));
        }
    }

	@Override
	public String getURLByKey(String registryKey) {
		return mapper.getURLByKey(registryKey);
	}
}
