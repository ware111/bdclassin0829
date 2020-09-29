<%@ include file="webapis/ui/doctype.jspf" %>
<%@ page contentType="text/html; charset=UTF8"
    import=" blackboard.platform.*,
            java.util.*" %>
<%@ page errorPage="/error.jsp"%>

<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>
<bbData:context id="ctx">
<bbNG:genericPage title="config">
<bbNG:jsBlock>
<SCRIPT type="text/javascript">
   function go_parent() {
      document.location = "/webapps/portal/execute/tabs/tabAction?tabType=admin";
   }
</SCRIPT>
</bbNG:jsBlock>

<%
	String titleIconPath = "/images/ci/icons/bookopen_u.gif";
%>

<bbNG:breadcrumbBar  environment="sys_admin" navItem="admin_main">
  <bbNG:breadcrumb>用户信息</bbNG:breadcrumb>
</bbNG:breadcrumbBar>

<bbNG:pageHeader>
  <bbNG:pageTitleBar iconUrl="<%=titleIconPath%>">用户信息</bbNG:pageTitleBar>
</bbNG:pageHeader>

<table cellSpacing="0" cellPadding="4" width="100%" border="0">
  <tr>
    <td vAlign="top" width="25">
    <img src="/images/ci/icons/arrow.gif" align="middle" border="0" width="16" height="14"></td>
    <td vAlign="top" width="100%">
    <a href="user/getUsers.do">
    <font face="Arial, Helvetica, sans-serif" size="2"><b>查询用户信息</b></font></a>
    <br>
  </tr>
  
 </table>
 <bbNG:button type="PageLevel" label="确定" url="javascript:go_parent();" />
</bbNG:genericPage>
</bbData:context>
