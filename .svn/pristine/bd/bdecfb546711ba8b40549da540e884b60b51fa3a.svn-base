<%@ include file="/webapis/ui/doctype.jspf" %>
<%@ page language = "java" %>

<%@ page import= " blackboard.platform.*,
                  java.util.ResourceBundle,
                  blackboard.platform.intl.JsResource,
                  java.util.*" %>

<%@ page errorPage="../../error/error.jsp"%>
<%-- <%@ taglib uri="http://struts.apache.org/tags-bean"   prefix="bean"   %>
<%@ taglib uri="http://struts.apache.org/tags-html"   prefix="html"   %>
<%@ taglib uri="http://struts.apache.org/tags-logic"   prefix="logic" %> --%>

<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>



<% 
    Locale locale = BbServiceManager.getLocaleManager().getLocale().getLocaleObject(); 
    ResourceBundle adminBundle = ResourceBundle.getBundle( "admin", locale );
    
    String titleIconPath = "/images/ci/icons/bookopen_u.gif";  
    String title = adminBundle.getString("config.database.config.name");
    String strConfigPage = adminBundle.getString("config.title");
    
    String strStepTitleOne = adminBundle.getString("server.config.name");

    String strLabelOne = adminBundle.getString("server.config.serveraddress");
    String strLabelTwo = adminBundle.getString("serverconfig.server.id");
    String strLabelThree = adminBundle.getString("server.config.password");
    String strLabelEtc = adminBundle.getString("server.config.instance");
    
    String strStepTitleTwo = adminBundle.getString("server.config.datasource.title");
    
    String strSubmit = adminBundle.getString("server.config.submit");
    
    String strErrNoAddress = adminBundle.getString("server.config.noaddress");
    String strErrNoID = adminBundle.getString("server.config.noid");
    String strErrNoServername= adminBundle.getString("server.config.noname");
    String strErrNoPassword = adminBundle.getString("server.config.nopassword");
    String strErrNoDatasource = adminBundle.getString("server.config.nodatasource");

    String strLabelFour = adminBundle.getString("server.config.datasource.name");
    String driver = adminBundle.getString("server.config.datasource.names");
    String strLabelFive = adminBundle.getString("server.config.datasource.desc");
    //String strAction = (String)request.getAttribute("do");
    String actionstring =  "../config/modifyServer.do";
    String okUrl =  "../page/config/configPage.jsp";
%>

<bbData:context id="ctx">

<bbNG:genericPage>
<bbNG:jsBlock>

<script type="text/javascript">
    function validateConfigServer() {
    
    if (document.serverConfigForm.server_address.value.trim() == ""){
    alert("<%=JsResource.encode(strErrNoAddress)%>");
    document.serverConfigForm.server_address.focus();
    return false;
    }
    if (document.serverConfigForm.server_name.value.trim() == ""){
    alert("<%=JsResource.encode(strErrNoServername)%>");
    document.serverConfigForm.server_name.focus();
    return false;
    }
    if (document.serverConfigForm.server_id.value.trim() == "") {
    alert("<%=JsResource.encode(strErrNoID)%>");  
    document.serverConfigForm.server_id.focus();
    return false;
    }
    if (document.serverConfigForm.server_password.value.trim() == ""){
    alert("<%=JsResource.encode(strErrNoPassword)%>");
    document.serverConfigForm.server_password.focus();
    return false;
    }    
    
    if (document.serverConfigForm.datasource.value.trim() == ""){
    alert("<%=JsResource.encode(strErrNoDatasource)%>");
    document.serverConfigForm.datasource.focus();
    return false;
    }          
     return true;
   }
</script>

</bbNG:jsBlock>

<bbNG:breadcrumbBar environment="sys_admin" navItem="admin_plugin_manage">
<bbNG:breadcrumb href="<%=okUrl%>"><%=strConfigPage%></bbNG:breadcrumb>
<bbNG:breadcrumb><%=title%></bbNG:breadcrumb>
</bbNG:breadcrumbBar>

<bbNG:pageHeader>
  <bbNG:pageTitleBar iconUrl="<%=titleIconPath%>"><%=title%></bbNG:pageTitleBar>
</bbNG:pageHeader>


<form action="<%=actionstring%>"  onsubmit="return validateConfigServer()" method="post">
<!--start step 1-->
<bbNG:dataCollection>
<!--start step 1-->
<bbNG:step title="<%=strStepTitleOne%>" hideNumber="1">

<!-- 数据库驱动 -->
<bbNG:dataElement isRequired="true" label="<%=driver%>">
   <%--  <html:text  name="server_id" property="${myForm.server_id}" size="45" maxlength="64"></html:text> --%>
     <input type="text" name="datasource" value="${myForm.datasource}" size="45" maxlength="64">
</bbNG:dataElement>
<!-- 数据库连接地址 -->
<bbNG:dataElement isRequired="true" label="<%=strLabelOne%>">
    <%-- <html:text name="server_address" property='${myForm.server_address}' size="60" maxlength="128"></html:text> --%>
    <input type="text" name="server_address" value="${myForm.server_address}" size="60" maxlength="128">
</bbNG:dataElement>

<!-- 教务系统数据库用户名 -->
<bbNG:dataElement isRequired="true" label="<%=strLabelTwo%>">
    <%-- <html:text  name="server_name" property="${myForm.server_name}" size="45" maxlength="64"></html:text> --%>
    <input type="text" name="server_name" value="${myForm.server_name}" size="45" maxlength="64">
</bbNG:dataElement>

 <!-- 教务系统数据库密码 -->
<bbNG:dataElement isRequired="true" label="<%=strLabelThree%>">
   <%--  <html:text name="server_password" property="${myForm.server_password}" size="45" maxlength="64"></html:text> --%>
     <input type="text" name="server_password" value="${myForm.server_password}" size="45" maxlength="64">
</bbNG:dataElement>

</bbNG:step>

<bbNG:stepSubmit title="<%=strSubmit%>" cancelUrl="<%=okUrl%>" />

</bbNG:dataCollection>

</form>

</bbNG:genericPage>

</bbData:context>