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
            //返回
            function goBack() {
                var courseId=document.getElementById("course_id").value;
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

		<bbNG:inventoryList collection="${createClassResultList}" objectVar="classInfo" className="java.util.Map" url="">
			<bbNG:listElement label="序号" name="num" isRowHeader="true">
				${classInfo.num}
			</bbNG:listElement>

			<bbNG:listElement label="编号" name="ids">
				${classInfo.ID}
			</bbNG:listElement>

			<bbNG:listElement label="课节信息" name="infor">
				${classInfo.CONTENT}
			</bbNG:listElement>
			<bbNG:listElement label="创建结果" name="result">
				${classInfo.RESULT}
			</bbNG:listElement>
			<bbNG:listElement label="失败原因" name="reason">
				${classInfo.REASON}
			</bbNG:listElement>
            <bbNG:listElement label="课程时间" name="time">
                ${classInfo.TODAY_TIME}
            </bbNG:listElement>
		</bbNG:inventoryList>
   	<bbNG:button type="PageLevel" label="返回" url="javascript:goBack()" />
</bbData:context>
</bbNG:learningSystemPage>