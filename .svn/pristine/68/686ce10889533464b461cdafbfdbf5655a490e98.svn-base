package com.blackboard.classin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.ClassinClassVideo;
import com.blackboard.classin.entity.ClassinCourseClass;
import com.blackboard.classin.mapper.ClassinClassVideoMapper;
import com.blackboard.classin.mapper.ClassinCourseClassMapper;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import com.blackboard.classin.service.ClassinClassVideoService;
import com.blackboard.classin.service.IClassinCourseClass;
import com.blackboard.classin.util.FileUtil;
import com.blackboard.classin.util.HttpClient;
import com.blackboard.classin.util.SystemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import blackboard.data.course.Course;
import blackboard.persist.PersistenceException;
import blackboard.platform.authentication.SessionManager;
import blackboard.platform.session.BbSession;
import lx.jave.AudioInfo;
import lx.jave.Encoder;
import lx.jave.EncoderException;
import lx.jave.InputFormatException;
import lx.jave.MultimediaInfo;
import lx.jave.VideoInfo;
import lx.jave.VideoSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/video")
public class ClssinClassVideoController {
	
	@Autowired
	private ClassinClassVideoMapper classinClassVideoMapper;
	
	@Autowired
	private ClassinClassVideoService classinClassVideoService;
	
	@Autowired
	private ClassinCourseClassMapper classinCourseClassMapper;
	
	@Autowired
	private SystemRegistryMapper systemRegistryMapper;
	
	@Autowired
	private IClassinCourseClass classinCourseClassService;
	
	private Logger log = Logger.getLogger(ClssinClassVideoController.class);
	/**
	 * classin调用BB接口保存录课片段信息
	 * @param request
	 * @param response
	 * @param resultMap
	 * @return
	 */
	@RequestMapping(value="/save.do",method= {RequestMethod.POST})
	@ResponseBody
	public JSONObject save(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> paramMap) {
		log.info("classin调用BB接口保存");
		String sid = paramMap.get("SID").toString();
		String cmd = paramMap.get("Cmd").toString();
		String result = "";
		if(!Constants.SID.equals(sid)) {
			return SystemUtil.buildResultMap(102, "无权限");
		}
		if("Record".equals(cmd)) {
			result = classinClassVideoService.saveVideo(request,response,paramMap);
		}else if("ClassLen".equals(cmd)) {
			//Classin课堂延时，回传BB
			classinCourseClassService.saveClassLen(paramMap);
		}
		if("1".equals(result)) {
			return SystemUtil.buildResultMap(1, "程序正常执行");
		}else {
			return SystemUtil.buildResultMap(0, "程序执行失败");
		}
	}
	
	/**
	 * 根据课节ID获取分段视频列表
	 * @param request
	 * @param response
	 * @param model
	 * @param course_id
	 * @param classClassId
	 * @return
	 */
	@RequestMapping("/findVediosByClassId.do")
	public String getVideoListByClassId(HttpServletRequest request,HttpServletResponse response,Model model,
			String course_id,String classinClassId,String expire_status) {
		
		List<ClassinClassVideo> videoList = classinClassVideoMapper.findVediosByClassId(classinClassId);
		if(videoList != null && videoList.size() != 0) {
			for(int i=0;i<videoList.size();i++) {
				ClassinClassVideo classinClassVideo = videoList.get(i);
				//将秒转换成分钟 vDuration
				int vDurationMin = (Integer.parseInt(classinClassVideo.getvDuration()) / 60) + 1;
				classinClassVideo.setvDuration(String.valueOf(vDurationMin));
				classinClassVideo.setvSequence(i+1);
			}
		}
		
		model.addAttribute("videoList", videoList);
		model.addAttribute("course_id", course_id);
		model.addAttribute("expire_status", expire_status);
		return "/classin/videoList";
	}
	
	/**
	 * 删除视频片段
	 * @param course_id
	 * @param fileId
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping("/delete.do")
	public String deleteVideo(String course_id,String fileId,HttpServletRequest request,
			HttpServletResponse response,Model model,String classinClassId,String expire_status) 
					throws JsonParseException, JsonMappingException, IOException {
		log.info("删除视频文件：fileId="+fileId);
		ObjectMapper objectMapper = new ObjectMapper();
		//删除classin端数据
		String deleteCourseClassURL = systemRegistryMapper.getURLByKey("classin_deletecourseclass_url");
		
		long currentCreateClassTime = System.currentTimeMillis()/1000;
		String parma1 = "SID="+Constants.SID;
        String param2 = "safeKey="+SystemUtil.MD5Encode(Constants.SECRET+currentCreateClassTime);
        String param3 = "timeStamp="+currentCreateClassTime;
        String param4 = "classId="+classinClassId;
        String param5 = "fileId="+fileId;
        
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(parma1).append("&").append(param2).append("&").append(param3)
        	.append("&").append(param4).append("&").append(param5);
        
        String resultLoginMap = HttpClient.doPost(deleteCourseClassURL, stringBuilder.toString());
        log.info("resultDeleteClassVideoMap >>>>"+resultLoginMap);
        Map<String,Object> classInCourseClassIdMap = new HashMap<String,Object>();
        
        if(resultLoginMap != null && !resultLoginMap.equals("")) {
			classInCourseClassIdMap = objectMapper.readValue(resultLoginMap, Map.class);
        	//解析返回的数据
            Map<String,Object> errorInfo = (Map<String, Object>) classInCourseClassIdMap.get("error_info");
            String errno = errorInfo.get("errno").toString();
            if("1".equals(errno) || "630".equals(errno) || "632".equals(errno) || "634".equals(errno) ) {
            	classinClassVideoMapper.delete(fileId);
            }
        }
		List<ClassinClassVideo> videoList = classinClassVideoMapper.findVediosByClassId(classinClassId);
		if(videoList != null && videoList.size() != 0) {
			for(int i=0;i<videoList.size();i++) {
				ClassinClassVideo classinClassVideo = videoList.get(i);
				//将秒转换成分钟 vDuration
				int vDurationMin = (Integer.parseInt(classinClassVideo.getvDuration()) / 60) + 1;
				classinClassVideo.setvDuration(String.valueOf(vDurationMin));
				classinClassVideo.setvSequence(i+1);
			}
		}
		model.addAttribute("course_id", course_id);
		model.addAttribute("videoList", videoList);
		model.addAttribute("expire_status", expire_status);
		return "/classin/videoList";
	}
	
	
	/**
	 * 
	 * @version [版本号, 2015-7-10]
	 * @throws PersistenceException 
	 * @throws Exception 
	 * @see [相关类/方法]
	 * @since [产品/模块版本]
	 */
	@RequestMapping("/download.do")
	public String downloadVideo(String v_url,String vSequence,String course_id,
			HttpServletRequest request,HttpServletResponse response,
			Model model,String classinClassId,String expire_status) throws PersistenceException{
		SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
		BbSession bbSession = sessionManager.getSession(request, response);
		log.info("下载回放记录"+vSequence);
		String courseName = bbSession.getGlobalKey("courseName");
		String fileName = courseName + "_视频" + vSequence + ".mp4";
		try {
			FileUtil.downLoadFromUrl(v_url,fileName,response);
			model.addAttribute("message", "下载成功");
		} catch (Exception e) {
			model.addAttribute("message", "下载失败,请重试~");
			e.printStackTrace();
		}
		List<ClassinClassVideo> videoList = classinClassVideoMapper.findVediosByClassId(classinClassId);
		
		if(videoList != null && videoList.size() != 0) {
			for(int i=0;i<videoList.size();i++) {
				ClassinClassVideo classinClassVideo = videoList.get(i);
				int vDurationMin = (Integer.parseInt(classinClassVideo.getvDuration()) / 60) + 1;
				classinClassVideo.setvDuration(String.valueOf(vDurationMin));
				classinClassVideo.setvSequence(i+1);
			}
		}
		model.addAttribute("course_id", "course_id");
		model.addAttribute("videoList", videoList);
		model.addAttribute("expire_status", expire_status);
		return "/classin/videoList";
	}
	
	/**
	 * 下载该课节下的所有分段视频为压缩包
	 * @param request
	 * @param response
	 * @param course_id
	 * @param classinClassId
	 * @param classinCourseId
	 * @param model
	 * @return
	 */
	@RequestMapping("/downloadVideos.do")
	public String downloadVideos(HttpServletRequest request,HttpServletResponse response,String course_id,String classinClassId,String classinCourseId,Model model) {
		List<ClassinClassVideo> videoList = classinClassVideoMapper.findVediosByClassId(classinClassId);
		if(videoList != null && videoList.size() != 0) {
			String courseName = SystemUtil.getCourseById(course_id).getTitle();
			try {
				FileUtil.batchDownLoadFile(request, response, videoList, courseName);
				model.addAttribute("tips","文件下载成功！");
			} catch (IOException e) {
				model.addAttribute("tips","文件下载失败，请重试！");
				e.printStackTrace();
			}
		}else {
			model.addAttribute("tips","该课节没有回放记录！");
		}
		
		List<ClassinCourseClass> classinCourseClassList = classinCourseClassMapper.getReplayList(classinCourseId);
//		if(classinCourseClassList != null && classinCourseClassList.size() != 0) {
//			for(int i=0;i<classinCourseClassList.size();i++) {
//				ClassinCourseClass ClassinCourseClass = classinCourseClassList.get(i);
//				ClassinCourseClass.setcSequence(i+1);
//			}
//		}
		model.addAttribute("classinCourseClassList", classinCourseClassList);
		return "/classin/replayList";
	}
}
