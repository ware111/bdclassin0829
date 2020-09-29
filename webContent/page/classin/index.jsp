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
                 blackboard.util.*,
                 blackboard.platform.intl.JsResource,
                 blackboard.data.course.*,
                 blackboard.data.user.*,
                 blackboard.persist.course.*,
                 blackboard.persist.Id,
                 blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession"%>
<%
SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
BbSession bbSession = sessionManager.getSession(request, response);
String course_id = bbSession.getGlobalKey("course_id");
%>

    <script type="text/javascript" charset="utf-8" src="https://www.eeo.cn/partner/api/geturl_classindownload.js"></script>
<bbNG:learningSystemPage>
    <bbData:context id="ctx">
        <bbNG:jsBlock>
            <SCRIPT type="text/javascript">
            function disbindPhone(){
    			var type = document.getElementById("typeName").value;
    			var course_id = document.getElementById("course_id").value;
    			window.location.href = "${pageContext.request.contextPath}/userPhone/disbindPhone.do?course_id="+course_id+"&type="+type;
    		}
	            function updatePhone(){
	    			var type = document.getElementById("typeName").value;
	    			var course_id = document.getElementById("course_id").value;
	    			window.location.href = "${pageContext.request.contextPath}/userPhone/toUpdatePhone.do?course_id="+course_id+"&type="+type;
	    		}
            	//创建在线课堂classin课程并与BB课程绑定
            	function createBbCourseClassinCourse(){
            		var course_id = document.getElementById("course_id").value;
            		var typeName = document.getElementById("typeName");
            		if(typeName != null){
            			typeName = typeName.value;
            		}
            		window.location.href = "${pageContext.request.contextPath}/bbCourseClassinCourse/create.do?course_id="+course_id+"&type="+typeName;
            	}
            	
            	function findClass(){
            		var course_id = document.getElementById("course_id").value;
            		var typeName = document.getElementById("typeName");
            		if(typeName != null){
            			typeName = typeName.value;
            		}
            		window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+course_id;
            	}
            </SCRIPT>
        </bbNG:jsBlock>

        <bbNG:breadcrumbBar environment="COURSE">
        		<c:if test="${type == 'meetingroom'}">
            		<bbNG:breadcrumb title="ClassIn在线研讨室" href=""></bbNG:breadcrumb>
        		</c:if>
        		<c:if test="${type != 'meetingroom'}">
		            <bbNG:breadcrumb title="ClassIn课堂" href=""></bbNG:breadcrumb>
        		</c:if>
        </bbNG:breadcrumbBar>

        <bbNG:pageHeader instructions="请按照是否安装了classin客户端选择您的具体操作,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
        	<c:if test="${type == 'meetingroom'}">
	            <bbNG:pageTitleBar title="ClassIn在线研讨室"></bbNG:pageTitleBar>
        	</c:if>
        	<c:if test="${type != 'meetingroom'}">
        			classin紧急求助，扫一扫
	        		<img src="/webapps/bb-ClassIn-bb_bb60/images/qrCode.jpg" style="height:60px;width:60px;">
	            <bbNG:pageTitleBar title="ClassIn课堂"></bbNG:pageTitleBar>
        	</c:if>
        </bbNG:pageHeader>
        <bbNG:actionControlBar>
			<bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="更新手机号" />
			<bbNG:actionButton primary="true" url="javaScript:disbindPhone();" title="解绑手机号" />
        	<input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
        	<input type="hidden" name="typeName" id="typeName" value="${type}">
        	<%--<c:if test="${role == 'teacher' }">--%>
				<%--<span style="font-size:20px;font-family:微软雅黑;">已安装ClassIn客户端-点击<a href="javaScript:createBbCourseClassinCourse();" style="color:red;">此处</a>进行下一步</span><br/>--%>
        	<%--</c:if>--%>
        	<%--<c:if test="${role == 'student' }">--%>
        		<span style="font-size:20px;font-family:微软雅黑;">已安装ClassIn客户端-点击<a href="javaScript:findClass();" style="color:red;">此处</a>进行下一步</span><br/>
        	<%--</c:if>--%>
        	<br/>
			<span style="font-size:20px;font-family:微软雅黑;">未安装ClassIn客户端-点击<a href="http://www.eeo.cn/cn/download.html" target="_blank" style="color:red;">此处</a>下载和安装</span><br/>
			<br/>
        </bbNG:actionControlBar>

    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)" />
</bbNG:learningSystemPage>