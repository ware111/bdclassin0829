<%@ include file="../../webapis/ui/doctype.jspf"%>
<%@ page contentType="text/html; charset=UTF8"%>
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF8"
         import="blackboard.platform.BbServiceManager,
                 blackboard.base.*,             
                 java.util.*,
                 blackboard.platform.intl.JsResource,
                 blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession"%>
<%@ page import="com.blackboard.classin.util.SystemUtil" %>
<%
	SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
	BbSession bbSession = sessionManager.getSession(request, response);
	String course_id = bbSession.getGlobalKey("course_id");
	String courseName = bbSession.getGlobalKey("courseName");
	String role = bbSession.getGlobalKey("role");
	boolean isTeacher = SystemUtil.isTeacher();
	//out.clear();      //清空缓存的内容
    //out=pageContext.pushBody();  //更新PageContext的out属性的内容
%>
<bbNG:learningSystemPage>
	<bbData:context id="ctx">
       	<bbNG:jsBlock>
           <SCRIPT type="text/javascript">
           	function deleteReplay(classinClassId,classinCourseId){
           		var course_id = document.getElementById("course_id").value;
           		if(confirm("删除操作将删除课节所有内容，请您再次确认是否删除？")){
           			window.location.href = "${pageContext.request.contextPath}/classinCourseClass/delete.do?course_id="+course_id
           					+"&classinClassId="+classinClassId
           					+"&classinCourseId="+classinCourseId;
           		}
           	}

            //返回
            function goBack() {
                var courseId = document.getElementById("course_id").value;
                window.location.href = "${pageContext.request.contextPath}/classinCourseClass/goBack.do?course_id=" + courseId
            }
           </SCRIPT>
       	</bbNG:jsBlock>

       	<bbNG:pageHeader instructions="回放记录,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
	        <bbNG:breadcrumbBar environment="COURSE">
	            <bbNG:breadcrumb title="ClassIn课堂" href=""></bbNG:breadcrumb>
	        </bbNG:breadcrumbBar>
	        classin紧急求助，扫一扫
	        		<img src="/webapps/bb-ClassIn-bb_bb60/images/qrCode.jpg" style="height:60px;width:60px;">
           	<bbNG:pageTitleBar title="ClassIn课堂"></bbNG:pageTitleBar>
       	</bbNG:pageHeader>
       
		<bbNG:actionControlBar>
	        <bbNG:actionControlBar>
	
	            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
	                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
			            ${tips}<br/>
		              <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
	                </bbNG:form>
	            </bbNG:actionPanelButton>
	        </bbNG:actionControlBar>
	 	</bbNG:actionControlBar>

	 	<%if(isTeacher == false){
	 	%>
	 		<h5>学生查看回放，请登录ClassIn客户端，找到对应课程和课节，点击观看回放即可。</h5><br/><br/><br/>
	 		<img src="/webapps/bb-ClassIn-bb_bb60/images/tip.png">
	 	<%
	 	} %>     
       <%if(isTeacher == true){%>
		<bbNG:inventoryList collection="${classinCourseClassList}" initialSortCol="classinCourseId"  objectVar="classinClassInfo" className="com.blackboard.classin.entity.ClassinCourseClass" url="">
		
			<bbNG:listElement label="课节名称" name="classinClassName" isRowHeader="true">
	                	<%=classinClassInfo.getClassName()%>
	        </bbNG:listElement>
	        
	        <bbNG:listElement label="分段查看" name="view_part">
	                <a href="/webapps/bb-ClassIn-bb_bb60/video/findVediosByClassId.do?classinClassId=${classinClassInfo.getClassinClassId()}&course_id=<%=course_id %>&expire_status=${classinClassInfo.expireStatus}">查看</a>
	        </bbNG:listElement>
	        
	        <bbNG:listElement label="观看回放" name="replay">
	                <a href="${classinClassInfo.liveURL}" target="_blank">观看回放</a>
	        </bbNG:listElement>
	        <bbNG:listElement label="下载回放" name="download">
	                <a href="/webapps/bb-ClassIn-bb_bb60/video/downloadVideos.do?classinClassId=<%=classinClassInfo.getClassinClassId()%>&course_id=<%=course_id %>&classinCourseId=<%=classinClassInfo.getClassinCourseId()%>&expire_status=${classinClassInfo.expireStatus}">下载</a>
	        </bbNG:listElement>
			<bbNG:listElement label="删除回放" name="delete">
	       		<c:if test="${classinClassInfo.expireStatus == '1'}">
		                <a href="javaScript:deleteReplay(<%=classinClassInfo.getClassinClassId()%>,<%=classinClassInfo.getClassinCourseId()%>);">删除回放</a>
		                <%-- <a href="/webapps/bb-ClassIn-bb_bb60/classinCourseClass/delete.do?course_id=<%=course_id%>&classinClassId=&classinCourseId=">删除</a> --%>
	       		</c:if>
		    </bbNG:listElement>
		</bbNG:inventoryList>
		<%} %>
   	<bbNG:button type="PageLevel" label="返回" url="javascript:goBack()" />
</bbData:context>
</bbNG:learningSystemPage>