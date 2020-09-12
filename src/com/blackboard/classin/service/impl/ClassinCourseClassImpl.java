package com.blackboard.classin.service.impl;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.ClassinClassMeeting;
import com.blackboard.classin.entity.ClassinCourseClass;
import com.blackboard.classin.mapper.ClassinClassMeetingMapper;
import com.blackboard.classin.mapper.ClassinCourseClassMapper;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import com.blackboard.classin.service.IClassinCourseClass;
import com.blackboard.classin.util.HttpClient;
import com.blackboard.classin.util.SystemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClassinCourseClassImpl implements IClassinCourseClass{

	@Autowired
	private SystemRegistryMapper systemRegistryMapper;
	
	@Autowired
	private ClassinCourseClassMapper classinCourseClassMapper;
	
	@Autowired
	private ClassinClassMeetingMapper classinClassMeetingMapper;
	
	private Logger log = Logger.getLogger(ClassinCourseClassImpl.class);
	
	@Override
	public String createClassinCourseClass(HttpServletRequest request, HttpServletResponse response, String type,String course_id) throws PersistenceException {
		log.info("create classin class ");
		Course bbCourse = SystemUtil.getCourseById(course_id);
		SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
		BbSession bbSession = sessionManager.getSession(request, response);
		String classinCourseId = bbSession.getGlobalKey("classinCourseId");
		String telephone = bbSession.getGlobalKey("telephone");
		String classinUid = bbSession.getGlobalKey("classinUid");
		System.out.println("==创建课节==classinUid===="+classinUid);
		String classin_addCourseClass_url = systemRegistryMapper.getURLByKey("classin_addcourseclass_url");
		long beginTimeMillis = System.currentTimeMillis();
        long currentCreateClassTime = beginTimeMillis/1000;
        User user = SystemUtil.getCurrentUser();
        String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentCreateClassTime);
        String param3 = "timeStamp="+currentCreateClassTime;
        //telephone改为uid
        //String param4 = "teacherAccount="+telephone;
        String param4 = "teacherUid="+classinUid;
        //新接口取消了本参数
        //String param5 = "teacherName=" + user.getFamilyName()+"-"+user.getUserName();
        String param6 = "courseId="+ classinCourseId;
        String param7 = "className="+ bbCourse.getCourseId()+"-"+bbCourse.getTitle();
        long beginTime = currentCreateClassTime + 100;//开课时间定为当前时间的100s之后
        long endTime = beginTime + 4*60*60;//开课时长4小时
        String param8 = "beginTime="+beginTime ;
        String param9 = "endTime="+ endTime;
        String param10 = "";
        String param11 = "record=1";//无论哪种方式都需要录课
		if("meetingroom".equals(type)) {//在线研讨室，需要直播
			param10 = "live=1";
		}else {//在线课堂，需要回放
			param10 = "replay=1";
		}
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param6)
        	.append("&").append(param7).append("&").append(param8).append("&").append(param9).append("&").append(param10).append("&").append(param11);
        
        String resultMap_classInCourseClassIdMap = HttpClient.doPost(classin_addCourseClass_url,stringBuilder.toString());
        
        log.info("resultMap_classInCourseClassIdMap is>>>"+resultMap_classInCourseClassIdMap);
        
        //解析返回的课节信息
        Map<String,Object> classInCourseClassIdMap = new HashMap<String,Object>();
        if(resultMap_classInCourseClassIdMap != null && !resultMap_classInCourseClassIdMap.equals("")) {
        	ObjectMapper objectMapper = new ObjectMapper();
        	try {
				classInCourseClassIdMap = objectMapper.readValue(resultMap_classInCourseClassIdMap, Map.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	//解析返回的数据
            Map<String,Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            String error = errorInfo.get("error").toString();
            
            //成功创建
        	if("1".equals(errno)) {
        		//保存回放url
        		String liveURL = "";
        		String liveInfo = "";
        		
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		String dtCreated = sdf.format(beginTimeMillis + 100*1000);

        		//设置课节ID classinId
        		String classinCourseClassId = classInCourseClassIdMap.get("data").toString();
        		bbSession.setGlobalKey("classinCourseId", classinCourseId);
    			bbSession.setGlobalKey("classinClassId", classinCourseClassId);
    			bbSession.setGlobalKey("telephone", telephone);
    			
    			Map<String,Object> moreData = (Map<String,Object>)classInCourseClassIdMap.get("more_data");
    			liveURL = moreData.get("live_url").toString();
    			String lessonKey = liveURL.split("=")[1];
    			liveURL = "https://www.eeo.cn/live.php?lessonKey="+lessonKey;
    			liveInfo = moreData.get("live_info").toString();
    			//将创建的在线研讨室课节信息保存到数据库
        		if("meetingroom".equals(type)) {
        			ClassinClassMeeting classinClassMeeting = new ClassinClassMeeting();
        			classinClassMeeting.setClassinCourseId(classinCourseId);
        			classinClassMeeting.setClassinClassId(classinCourseClassId);
        			classinClassMeeting.setDtCreated(dtCreated);
        			classinClassMeeting.setTeacherPhone(telephone);
        			classinClassMeeting.setAssistantPhone("");
        			classinClassMeeting.setExpireStatus("0");
        			classinClassMeeting.setLiveURL(liveURL);
        			
        			classinClassMeetingMapper.save(classinClassMeeting);
        		}else {//将创建的在线课堂课节信息保存到数据库
        			Map<String,Object> paramMap = new HashMap<String,Object>();
        			paramMap.put("classinCourseId", classinCourseId);
        			paramMap.put("classinClassId", classinCourseClassId);
        			paramMap.put("teacherPhone", telephone);
        			paramMap.put("liveURL", liveURL);
        			paramMap.put("liveInfo", liveInfo);
        			paramMap.put("dtCreated", dtCreated);
        			
        			classinCourseClassMapper.save(paramMap);
        			
        		}
        		//唤醒客户端
        		return "awakeClassinClient";
        	}else if("136".equals(errno)) {
        		//机构下面没有该老师，请在机构下添加该老师
        		return "NeedAddTeacherToClassin";
            }else if("172".equals(errno)){
            	//该课程下的学生不能添加为老师
            	return "DeleteCourseStudnet";
            }else {
            	//其他错误代码
        		bbSession.setGlobalKey("errno", errno);
        		bbSession.setGlobalKey("error", error);
        		return "ClassinClassCreatedFailed";
        	}
        }else {//未获取到classin返回的信息
        	return "elseErrors";
        }
		
	}

	@Override
	public String autoCreateClassinCourseClass() throws PersistenceException {
		return null;
	}

	/**
	 *classin课节延时,BB课堂同样延迟
	 */
	@Override
	public void saveClassLen(Map<String, Object> paramMap) {
		
		String classinClassId = paramMap.get("ClassId").toString();
		//课节延长后的总时长 ，单位-分钟
		int prelectTimeLength = Integer.parseInt(paramMap.get("PrelectTimeLength").toString());
		//拖堂时间 单位-分钟
		int closeClassDelay = Integer.parseInt(paramMap.get("CloseClassDelay").toString());
		
		//根据课节ID查询课节详细信息,并更新课节时长
		ClassinCourseClass classinCourseClass = classinCourseClassMapper.findByClassId(classinClassId);
		
		//更新课节时长
		classinCourseClass.setClassTimeLength(prelectTimeLength);
		classinCourseClass.setClassTimeLength(closeClassDelay);
		
		classinCourseClassMapper.updateClassToDelay(classinCourseClass);
	}
	
}
