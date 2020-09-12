<%@ include file="../../webapis/ui/doctype.jspf"%>
<%@ page contentType="text/html; charset=UTF8"%>
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
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

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
	BbSession bbSession = sessionManager.getSession(request, response);
	String course_id = bbSession.getGlobalKey("course_id");
	String courseName = bbSession.getGlobalKey("courseName");
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
            
	            function createClassinClass(){
            		var course_id = document.getElementById("course_id").value;
            		var typeName = document.getElementById("typeName");
            		if(typeName != null){
            			typeName = typeName.value;
            		}
            		window.location.href = "${pageContext.request.contextPath}/classinCourseClass/create.do?course_id="+course_id+"&type="+typeName;
            	}
            	
            	function getClassList() {
                    var course_id = document.getElementById("course_id").value;
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getClassScheduleList.do?course_id="+course_id;
                }
            	
            	function getReplayURLs(){
                    var course_id = document.getElementById("course_id").value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getRepalyList.do?course_id="+course_id;
        		}
        		
        		function advanceSet() {
                    var type = document.getElementById("typeName").value;
                    var course_id = document.getElementById("course_id").value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClass.do?course_id="+course_id+"&type="+type;
                }
        		
            </SCRIPT>
        </bbNG:jsBlock>
		<c:if test="${type == 'meetingroom'}">
			<bbNG:pageHeader instructions="创建在线研讨室,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
		        <bbNG:breadcrumbBar environment="COURSE">
	           			<bbNG:breadcrumb title="ClassIn在线研讨室" href=""></bbNG:breadcrumb>
		        </bbNG:breadcrumbBar>
		            <bbNG:pageTitleBar title="ClassIn在线研讨室"></bbNG:pageTitleBar>
	        </bbNG:pageHeader>
		</c:if>
        <c:if test="${type != 'meetingroom'}">
			<bbNG:pageHeader instructions="创建在线课堂,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
		        <bbNG:breadcrumbBar environment="COURSE">
	           			<bbNG:breadcrumb title="ClassIn课堂" href=""></bbNG:breadcrumb>
		        </bbNG:breadcrumbBar>
		       	 	classin紧急求助，扫一扫
	        		<img src="/webapps/bb-ClassIn-bb_bb60/images/qrCode.jpg" style="height:60px;width:60px;">
		            <bbNG:pageTitleBar title="ClassIn课堂"></bbNG:pageTitleBar>
	        </bbNG:pageHeader>
        </c:if>

        <bbNG:actionControlBar>
            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
            	<c:if test="${type != 'meetingroom'}">
		            	<bbNG:actionButton url="javaScript:getClassList();" title="当前课程日历" primary="true"/>
					  	<bbNG:actionButton url="javaScript:getReplayURLs();" title="获取历史回放列表" primary="true"/>
				</c:if> 
				<c:if test="${type == 'meetingroom'}">
		            	<bbNG:actionButton url="javaScript:createClassinClass();" title="创建在线研讨室" primary="true"/>
				</c:if> 
				<bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="更新手机号" />
				<bbNG:actionButton primary="true" url="javaScript:disbindPhone();" title="解绑手机号" />
				<bbNG:actionButton primary="true" url="javaScript:advanceSet();" title="创建classin教室"/>
                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
					<span style="color: #ac1d2a">${tips}</span><br/>
                    <c:if test="${type != 'meetingroom'}">
					  	提示：您将创建classin在线课堂，默认生成回放
					  </c:if>  
					  <c:if test="${type == 'meetingroom'}">
					  	提示：您将创建classin在线研讨室，默认开启直播
					  </c:if>    
					     
					  <!-- <input type="radio" name="replayFlag" value="1" checked>是
					  <input type="radio" name="replayFlag" value="0">否 -->
					  <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
					  <input type="hidden" name="typeName" id="typeName" value="${type}">
					  
                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>
    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)" />
</bbNG:learningSystemPage>