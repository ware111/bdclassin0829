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
	String role = bbSession.getGlobalKey("role");
%>

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
        	
        		function getReplayURLs(){
        			var course_id = document.getElementById("course_id").value;
        			window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getRepalyList.do?course_id="+course_id;
        		}
        		
        	</SCRIPT>
        </bbNG:jsBlock>

        <bbNG:pageHeader instructions="提示信息,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
	        <bbNG:breadcrumbBar environment="COURSE">
	            <c:if test="${type == 'meetingroom'}">
            		<bbNG:breadcrumb title="ClassIn在线研讨室" href=""></bbNG:breadcrumb>
        		</c:if>
        		<c:if test="${type != 'meetingroom'}">
		            <bbNG:breadcrumb title="ClassIn课堂" href=""></bbNG:breadcrumb>
        		</c:if>
	        </bbNG:breadcrumbBar>
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
			<% if(role != null && role.equals("student")){
			%>
				<bbNG:actionButton primary="true" url="javaScript:getReplayURLs();" title="获取历史回放列表" />
			<%	
			}%>
			<bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="当前课程日历" />
			<bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="更新手机号" />
            <bbNG:actionButton primary="true" url="javaScript:disbindPhone();" title="解绑手机号" />
            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
            <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
            	<input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
            	<input type="hidden" name="typeName" id="typeName" value="${type }">
            	<c:if test="${errno != null }">
	                <span style="color: #FF0000;font-size: 15px;">${source}</span><br/>
	                <span style="color: #FF0000;font-size: 15px;">提示代码: ${errno}</span><br/>
	                <span style="color: #FF0000;font-size: 15px;">提示信息: ${error}</span>
                </c:if>
                <c:if test="${errno == null }">
                	<span style="font-size: 15px;">${source}</span><br/>
	                <span style="font-size: 15px;">提示信息: ${error}</span>
                </c:if>
                <c:if test="${noregist != null }">
                	<br/><span style="font-size: 15px;"><a href="http://www.eeo.cn/cn/download.html" target="_blank">点击此处</a>下载客户端并注册</span>
                </c:if>
            </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>

    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)" />
</bbNG:learningSystemPage>