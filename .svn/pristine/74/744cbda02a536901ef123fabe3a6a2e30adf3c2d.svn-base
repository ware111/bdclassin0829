<%@ include file="/webapis/ui/doctype.jspf" %>
<%@ page contentType="text/html; charset=UTF8"
    import="blackboard.platform.intl.*,
            blackboard.platform.*,
            blackboard.data.user.*,
            blackboard.platform.context.*,
            java.util.*" %>
<%@ page errorPage="../error/error.jsp"%>

<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<SCRIPT type="text/javascript">
   function go_parent() {
      document.location = "/webapps/blackboard/admin/manage_plugins.jsp";
   }
</SCRIPT>

<bbNG:genericPage title="config">
<% 
    Locale locale = BbServiceManager.getLocaleManager().getLocale().getLocaleObject();
    ResourceBundle myBundle = ResourceBundle.getBundle( "admin", locale );

    String titleIconPath = "/images/ci/icons/tools_u.gif";

    String title = myBundle.getString("config.title");
    String link1_name = myBundle.getString("config.database.config.name");
    String link1_desc = myBundle.getString("config.database.config.desc");
    String link2_name = myBundle.getString("config.autoupdate.name");
    String link2_desc = myBundle.getString("config.autoupdate.desc");
    
    String link3_name = myBundle.getString("config.enrollupdate.name");
    String link3_desc = myBundle.getString("config.enrollupdate.desc");
    String strButOk = myBundle.getString("adminconfig.admin.ok"); 
  
%>
<bbNG:breadcrumbBar  environment="sys_admin" navItem="admin_plugin_manage">
<bbNG:breadcrumb><%=title%></bbNG:breadcrumb>
</bbNG:breadcrumbBar>

<bbNG:pageHeader>
  <bbNG:pageTitleBar iconUrl="<%=titleIconPath%>"><%=title%></bbNG:pageTitleBar>
</bbNG:pageHeader>


<table cellSpacing="0" cellPadding="2" width="100%" border="0">
  <tr>
    <td vAlign="top" width="20">
    <img src="/images/ci/icons/arrow.gif" align="middle" border="0" width="16" height="14"></td>
    <td vAlign="top" width="100%">
    <a href="${pageContext.request.contextPath}/config/initPageForDisplay.do">
    <font face="Arial, Helvetica, sans-serif" size="3"><b><%=link1_name%></b></font></a>
    <br>
    <font face="Arial, Helvetica, sans-serif" size="3"><%=link1_desc%></font></td>
  </tr>
   <tr>
    <td vAlign="top" width="20">
    <img src="/images/ci/icons/arrow.gif" align="middle" border="0" width="16" height="14"></td>
    <td vAlign="top" width="100%">
    <a href="${pageContext.request.contextPath}/config/initClassInServer.do">
    <font face="Arial, Helvetica, sans-serif" size="3"><b><%=link2_name%></b></font></a>
    <br>
    <font face="Arial, Helvetica, sans-serif" size="3"><%=link2_desc%></font></td>
  </tr>
        <%--
        <tr>
        <td vAlign="top" width="20">
        <img src="/images/ci/icons/arrow.gif" align="middle" border="0" width="16" height="14"></td>
        <td vAlign="top" width="100%">
        <a href="../execute/enrollmentUpdate">
        <font face="Arial, Helvetica, sans-serif" size="3"><b><%=link3_name%></b></font></a>
        <br>
        <font face="Arial, Helvetica, sans-serif" size="3"><%=link3_desc%></font></td>
      </tr> --%>
  </table>
<bbNG:button type="PageLevel" label="<%=strButOk%>" url="javascript:go_parent()" />
</bbNG:genericPage>









