<%@ include file="../../webapis/ui/doctype.jspf"%>
<%@ page contentType="text/html; charset=UTF8"%>
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF8"
         import="blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession,
                 com.blackboard.classin.util.SystemUtil"%>
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
       <b>没有登陆，请点击重新登陆</b>
		<a src="">重新登陆</a>
	</bbData:context>
</bbNG:learningSystemPage>