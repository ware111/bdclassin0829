<%@ include file="../../webapis/ui/doctype.jspf"%>
<%@ page contentType="text/html; charset=UTF8"%>
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<%@ page contentType="text/html; charset=UTF8"
         import="blackboard.platform.BbServiceManager,
                 blackboard.base.*,             
                 java.util.*,              
                 blackboard.platform.intl.JsResource,
                 blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
	BbSession bbSession = sessionManager.getSession(request, response);
	String course_id = bbSession.getGlobalKey("course_id");
	String courseName = bbSession.getGlobalKey("courseName");
    String role = bbSession.getGlobalKey("role");
%>
<bbNG:learningSystemPage>
	<bbData:context id="ctx">
       	<bbNG:jsBlock>
           <SCRIPT type="text/javascript">
	           	
	           	/* function downloadFile(fileURL){
	           		var x = new XMLHttpRequest();
	           		x.open("GET",fileURL, true);
	           		x.responseType = 'blob';
	           		x.onload=function(e){
	           			download(x.response, "f0.mp4", "image/jpg");
	           		};
	           		x.send();
	           	} */
	           	function deleteVideo(fileId,classinClassId,expire_status){
	           		var course_id = document.getElementById("course_id").value;
	           		if(confirm("删除的视频片段将不可恢复，确认删除?")){
		           		window.location.href = "${pageContext.request.contextPath}/video/delete.do?course_id="
		           				+course_id
		           				+"&fileId="+fileId
		           				+"&classinClassId="+classinClassId
		           				+"&expire_status="+expire_status;
	           		}
	           	}
	           	
	           	function downloadFile(fileURL,classinClassId,vSequence,expire_status){
	           		var course_id = document.getElementById("course_id").value;
	           		window.location.href = "${pageContext.request.contextPath}/video/download.do?v_url="+fileURL
	           				+"&course_id="+course_id
	           				+"&classinClassId="+classinClassId
	           				+"&vSequence="+vSequence
	           				+"&expire_status="+expire_status;
	           	}
	           	
           </SCRIPT>
       	</bbNG:jsBlock>

       	<bbNG:pageHeader instructions="分段视频,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
	        <bbNG:breadcrumbBar environment="COURSE">
	            <bbNG:breadcrumb title="ClassIn课堂" href=""></bbNG:breadcrumb>
	        </bbNG:breadcrumbBar>
	        classin紧急求助，扫一扫
	        		<img src="/webapps/bb-ClassIn-bb_bb60/images/qrCode.jpg" style="height:60px;width:60px;">
           	<bbNG:pageTitleBar title="ClassIn课堂"></bbNG:pageTitleBar>
       	</bbNG:pageHeader>
       
		<bbNG:actionControlBar>
	        <bbNG:actionControlBar>
				${message}<br/>
	            <input type="hidden" name="course_id" id="course_id" value="${course_id}">
	            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
	                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
	                </bbNG:form>
	            </bbNG:actionPanelButton>
	        </bbNG:actionControlBar>
	 	</bbNG:actionControlBar>       
       
		<bbNG:inventoryList collection="${videoList}" initialSortCol="classinCourseId"  objectVar="classinClassVideo" className="com.blackboard.classin.entity.ClassinClassVideo" url="">
		
			<bbNG:listElement label="视频名称" name="videoName" isRowHeader="true">
	                	视频<%=classinClassVideo.getVst()%>
	        </bbNG:listElement>
	        
	        <bbNG:listElement label="开始时间" name="startTime">
	        	${classinClassVideo.vst }
	        </bbNG:listElement>
	        
	        <bbNG:listElement label="结束时间" name="endTime">
	        	${classinClassVideo.vet }
	        </bbNG:listElement>
	        
	        <bbNG:listElement label="时长(分钟)" name="duration">
	        	${classinClassVideo.vDuration }
	        </bbNG:listElement>
	        
	        <bbNG:listElement label="观看回放" name="replay">
	                <a href="${classinClassVideo.vURL}" target="_blank">观看</a>
	        </bbNG:listElement>
	        <bbNG:listElement label="下载回放" name="download">
	                <a href="javaScript:downloadFile('<%=classinClassVideo.getvURL()%>','<%=classinClassVideo.getClassinClassId()%>','<%=classinClassVideo.getvSequence() %>','${expire_status}');">下载</a>
	        </bbNG:listElement>
			<bbNG:listElement label="删除片段" name="delete">
       			<c:if test="${expire_status != '0'}">
	                <a href="javaScript:deleteVideo('<%=classinClassVideo.getFileId()%>','<%=classinClassVideo.getClassinClassId()%>','${expire_status}');">删除</a>
       			</c:if>
	        </bbNG:listElement>
	        
		</bbNG:inventoryList>
   	<bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)"/>
</bbData:context>
</bbNG:learningSystemPage>