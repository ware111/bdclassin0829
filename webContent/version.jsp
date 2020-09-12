<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="java.net.*" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>程序加载校验页面</title>
</head>
<body>
<%
try{
InetAddress address = InetAddress.getLocalHost();
out.println("<font color='red' size='3'>计算机名:"+address.getHostName()+"</font><br>");
}catch(Exception e){
	
}
out.println("<font color='red' size='3'>版本号: 2.0.1.9.1.82223.0 </font> <br/>");
out.println("<font color='red' size='3'>部署日期：2019-10-12</font>");
%>
</body>
</html>