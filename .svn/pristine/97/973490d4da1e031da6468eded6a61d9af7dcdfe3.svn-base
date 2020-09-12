<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>message page</title>
</head>
<body>

<bbNG:genericPage>
<bbNG:jsBlock>
			<SCRIPT type="text/javascript">
				function go_parent() {
					document.location = "/webapps/portal/execute/tabs/tabAction?tabType=admin";
				}
								
				
			</SCRIPT>
		</bbNG:jsBlock>
		<style>		
			.style {
				color:#F00;
				font-size:14px;
				font-weight:bold;
			}
			.messageInfo {
				font-size:14px;
			}
		</style>

     <bbNG:pageHeader>
		<bbNG:pageTitleBar>异常信息显示</bbNG:pageTitleBar>
	</bbNG:pageHeader>

<div style="white-space:nowrap"><span class="styles" >异常信息：</span> <span class="messageInfo">${message }</span></div>
<br/>

<div style="white-space:nowrap"><c:if test=" ${not empty className } "><span class="style" >异常类：</span><span class="messageInfo">${className }</span>
</c:if>
</div>
<br/>
<div style="white-space:nowrap"><c:if test=" ${not empty className } "><span class="style" >抛出异常方法：</span><span class="messageInfo">${methodName }</span>
</c:if>
</div>

<bbNG:button type="PageLevel" label="确定" url="javascript:go_parent();" />
</bbNG:genericPage>
	
</body>
</html>