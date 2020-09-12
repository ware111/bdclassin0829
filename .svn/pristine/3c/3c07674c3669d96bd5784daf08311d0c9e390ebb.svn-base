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
    String title = adminBundle.getString("config.ClassIn.config.name");
    String strConfigPage = adminBundle.getString("config.title");
    
    String strStepTitleOne = adminBundle.getString("server.ClassIn.config.name");

    String strSubmit = adminBundle.getString("server.config.submit");
    String notnull = adminBundle.getString("server.config.input.notnull");


    String ClassInEntrance = adminBundle.getString("server.config.ClassInEntrance");
    String ClassInAddCourse = adminBundle.getString("server.config.ClassInAddCourse");
    String ClassInAddCourseClass = adminBundle.getString("server.config.ClassInAddCourseClass");
    String ClassInImportGrade = adminBundle.getString("server.config.ClassInExportGrade");
    String ClassInClassActivityInfo = adminBundle.getString("server.config.ClassInClassActivityInfo");
    String ClassInRegister = adminBundle.getString("server.config.ClassInRegister");
    String ClassInAddTeacher = adminBundle.getString("server.config.ClassInAddTeacher");
    String ClassInAddCourseStudent = adminBundle.getString("server.config.ClassInAddCourseStudent");
    String actionString =  "../config/modifyClassInServerURLs.do";
    String okUrl =  "../page/config/configPage.jsp";
%>

<bbData:context id="ctx">

<bbNG:genericPage>
    <bbNG:jsBlock>

        <script type="text/javascript">
            function validateConfigServer() {
                var ClassInEntranceURL = document.getElementById("ClassInEntranceURL").value;
                var ClassInImportGradeURL = document.getElementById("ClassInImportGradeURL").value;
                var ClassInClassActivityInfoURL = document.getElementById("ClassInClassActivityInfoURL").value;
                var ClassinRegisterUrl = document.getElementById("ClassinRegisterUrl").value;
                var ClassinAddCourseStudentUrl = document.getElementById("ClassinAddCourseStudentUrl").value;
                var ClassInAddCourseURL = document.getElementById("ClassInAddCourseURL").value;
                var ClassInAddCourseClassURL = document.getElementById("ClassInAddCourseClassURL").value;
                var ClassinAddTeacherURL = document.getElementById("ClassinAddTeacherURL").value;


                if(ClassInEntranceURL == null || "" == ClassInEntranceURL
                    || ClassinRegisterUrl == null || "" == ClassinRegisterUrl
                    || ClassinAddCourseStudentUrl == null || "" == ClassinAddCourseStudentUrl
                    || ClassInAddCourseURL == null || "" == ClassInAddCourseURL
                    || ClassInClassActivityInfoURL == null || "" == ClassInClassActivityInfoURL
                    || ClassInImportGradeURL == null || "" == ClassInImportGradeURL
                    || ClassinAddTeacherURL == null || "" == ClassinAddTeacherURL
                    || ClassInAddCourseClassURL == null || "" == ClassInAddCourseClassURL){
                    alert("<%=JsResource.encode(notnull)%>");
                    return false;
                }
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


<form action="<%=actionString%>"  onsubmit="return validateConfigServer()" method="post">
    <bbNG:dataCollection>
        <bbNG:step title="<%=strStepTitleOne%>" hideNumber="1">
            <!-- ClassIn唤醒客户端并进入教室URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInEntrance%>">
                 <input type="text" id="ClassInEntranceURL" name="ClassInEntranceURL" value="${classin_entrance_url}" size="60" maxlength="128">
            </bbNG:dataElement>
            <!-- ClassIn创建课程URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInAddCourse%>">
                 <input type="text" id="ClassInAddCourseURL" name="ClassInAddCourseURL" value="${classin_addcourse_url}" size="60" maxlength="128">
            </bbNG:dataElement>
            <!-- ClassIn创建课节URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInAddCourseClass%>">
                 <input type="text" id="ClassInAddCourseClassURL" name="ClassInAddCourseClassURL" value="${classin_addcourseclass_url}" size="60" maxlength="128">
            </bbNG:dataElement>
            <!-- ClassIn注册用户URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInRegister%>">
                <input type="text" id="ClassinRegisterUrl" name="ClassinRegisterUrl" value="${classin_register_url}" size="60" maxlength="128">
            </bbNG:dataElement>
            <!-- ClassIn添加教师URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInAddTeacher%>">
                <input type="text" id="ClassinAddTeacherURL" name="ClassinAddTeacherURL" value="${classin_addteacher_url}" size="60" maxlength="128">
            </bbNG:dataElement>

            <!-- ClassIn课程下添加学生/旁听接口URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInAddCourseStudent%>">
                <input type="text" id="ClassinAddCourseStudentUrl" name="ClassinAddCourseStudentUrl" value="${classin_addcoursestudent_url}" size="60" maxlength="128">
            </bbNG:dataElement>
             <!-- ClassIn成绩导入接口URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInImportGrade%>">
                <input type="text" id="ClassInImportGradeURL" name="ClassInImportGradeURL" value="${classin_import_grade_url}" size="60" maxlength="128">
            </bbNG:dataElement>

            <!-- ClassIn课堂活动信息接口URL -->
            <bbNG:dataElement isRequired="true" label="<%=ClassInClassActivityInfo%>">
                <input type="text" id="ClassInClassActivityInfoURL" name="ClassInClassActivityInfoURL" value="${classin_class_activity_info_url}" size="60" maxlength="128">
            </bbNG:dataElement> 

        </bbNG:step>

    <bbNG:stepSubmit title="<%=strSubmit%>" cancelUrl="<%=okUrl%>" />

    </bbNG:dataCollection>

</form>

</bbNG:genericPage>

</bbData:context>