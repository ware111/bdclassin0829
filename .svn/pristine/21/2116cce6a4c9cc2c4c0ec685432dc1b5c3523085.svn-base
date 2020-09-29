package com.blackboard.classin.service.impl;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackboard.classin.entity.ClassinClassVideo;
import com.blackboard.classin.mapper.ClassinClassVideoMapper;
import com.blackboard.classin.service.ClassinClassVideoService;

@Component("classinClassVideoService")
public class ClassinClassVideoServiceImpl implements ClassinClassVideoService{
	
	@Autowired
	private ClassinClassVideoMapper classinClassVideoMapper;

	@Override
	public String saveVideo(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramMap) {
		
		String classinClassId = paramMap.get("ClassID").toString();
		String actionTime = paramMap.get("ActionTime").toString();
		String classinCourseId = paramMap.get("CourseID").toString();
		String sid = paramMap.get("SID").toString();
		String vTimeStamp = paramMap.get("TimeStamp").toString();
		long vet = Long.parseLong(paramMap.get("VET").toString());
		long vst = Long.parseLong(paramMap.get("VST").toString());
		String cmd = paramMap.get("Cmd").toString();
		String vURL = paramMap.get("VUrl").toString();
		String vDuration = paramMap.get("Duration").toString();
		String fileId = paramMap.get("FileId").toString();
		int vSize = Integer.parseInt(paramMap.get("Size").toString());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String startTime = sdf.format(vst*1000);
		String endTime = sdf.format(vet*1000);
		
		ClassinClassVideo classinClassVideo = new ClassinClassVideo();
		
		classinClassVideo.setActionTime(actionTime);
		classinClassVideo.setClassinClassId(classinClassId);
		classinClassVideo.setClassinCourseId(classinCourseId);
		classinClassVideo.setCmd(cmd);
		classinClassVideo.setSid(sid);
		classinClassVideo.setVst(startTime);
		classinClassVideo.setVet(endTime);
		classinClassVideo.setvSize(vSize);
		classinClassVideo.setvURL(vURL);
		classinClassVideo.setvDuration(vDuration);
		classinClassVideo.setFileId(fileId);
		classinClassVideo.setvTimestamp(vTimeStamp);
		classinClassVideo.setDeleteStatus("N");
		
		try {
			classinClassVideoMapper.save(classinClassVideo);
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		
		return "1";
	}

}
