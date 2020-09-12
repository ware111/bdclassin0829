package com.blackboard.classin.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ClassinClassVideoService {

	/**
	 * 保存录课视频片段信息
	 * @param request
	 * @param response
	 * @param resultMap
	 * @return
	 */
	String saveVideo(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramMap);

}
