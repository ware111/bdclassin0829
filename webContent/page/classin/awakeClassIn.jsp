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
        			var type = document.getElementById("type").value;
        			var course_id = document.getElementById("course_id").value;
        			window.location.href = "${pageContext.request.contextPath}/userPhone/disbindPhone.do?course_id="+course_id+"&type="+type;
        		}
        		function updatePhone(){
        			var type = document.getElementById("type").value;
        			var course_id = document.getElementById("course_id").value;
        			window.location.href = "${pageContext.request.contextPath}/userPhone/toUpdatePhone.do?course_id="+course_id+"&type="+type;
        		}
        		
        		function openClassInClientAndEnterClass(){
        			var conditions = document.getElementById("conditions").value;
        			window.open("https://www.eeo.cn/client/invoke/index.html?"+conditions);
        		}
        		
        		function getReplayURLs(){
        			var course_id = document.getElementById("course_id").value;
        			window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getRepalyList.do?course_id="+course_id;
        		}
        		
        		//分享到QQ
        		function shareToQQ(targetURL) {
        			var courseName = document.getElementById("courseName").value;
        			var url = "https://connect.qq.com/widget/shareqq/index.html";
        			var shareUrl = targetURL;
        			var title = "classin在线研讨室";
        			var summary = courseName + "在线研讨室直播入口";
        			var pics = "${pageContext.request.contextPath}/images/icon_classinapp.png";
        			var width = "32";
        			var height = "32";
        			var allUrl = url + "?url=" + encodeURIComponent(shareUrl) + "&title=" + title + "&summary="+ summary 
        					+ "&pics=" + pics + "&width=" + width + "&height="+ height;
        			window.open(allUrl,"_blank");
        		}
        		
        		//分享到微信
        		function shareToWechat(targetURL){
        			window.open("${pageContext.request.contextPath}/page/classin/qrcode.jsp?live_url="+targetURL,"_blank");
        		}
        		
        		//分享到新浪微博
        		function shareToSina(targetURL) {
        			var title = "classin在线研讨室";
        			var picurl = "${pageContext.request.contextPath}/images/icon_classinapp.png";
        			var sharesinastring='http://v.t.sina.com.cn/share/share.php?title='+title+'&url='+targetURL+'&content=utf-8&sourceUrl='+targetURL+'&pic='+picurl;
        			window.open(sharesinastring,'_blank','height=400,width=400,top=100,left=100');  
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

        <bbNG:pageHeader instructions="唤醒ClassIn客户端并进入教室,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
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
	        <bbNG:actionButton primary="true" url="javaScript:openClassInClientAndEnterClass();" title="当前课标日历" />
	        <c:if test="${type != 'meetingroom'}">
		        <bbNG:actionButton primary="true" url="javaScript:getReplayURLs();" title="获取历史回放列表" />
	        </c:if>
	        <bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="更新手机号" />
	        <bbNG:actionButton primary="true" url="javaScript:disbindPhone();" title="解绑手机号" />
	        <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                <bbNG:form method="post" name="courseDetailForm" action="">
					${tips}<br/>
					<c:if test="${type == 'meetingroom'}">
						本在线研讨室的直播地址为：${liveURL},分享至&nbsp;
						<img src="/webapps/bb-ClassIn-bb_bb60/images/qq.png" onclick="shareToQQ('${liveURL}')" id="qq_share" style="height:20px;width:20px;" alt="分享至QQ"/>&nbsp;&nbsp;
						<img src="/webapps/bb-ClassIn-bb_bb60/images/weixin.png" onclick="shareToWechat('${liveURL}')" id="weixin_share" style="height:20px;width:20px;" alt="分享至微信"/>&nbsp;&nbsp;
						<img src="/webapps/bb-ClassIn-bb_bb60/images/sina.png" onclick="shareToSina('${liveURL}')" id="sina_share" style="height:20px;width:20px;" alt="分享至新浪微博 "/>
					</c:if>
		  			<input type="hidden" name="conditions" value="${conditions}" id="conditions" >
		  			<input type="hidden" name="type" value="${type}" id="type" >
		  			<input type="hidden" name="course_id" value="<%=course_id%>" id="course_id" >
		  			<input type="hidden" name="courseName" value="<%=courseName%>" id="courseName" >
                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>


    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)" />
</bbNG:learningSystemPage>