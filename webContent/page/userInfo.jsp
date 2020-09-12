<%@ include file="../webapis/ui/doctype.jspf"%>
<%@ page contentType="text/html; charset=UTF8"
         import=" blackboard.platform.*,
                  java.util.*"%>
<%--<%@ page errorPage="/error.jsp"%>--%>

<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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

        <bbNG:pageHeader>
            <bbNG:breadcrumbBar  environment="sys_admin" navItem="admin_main">
                <bbNG:breadcrumb href="../../index.jsp">用户数据</bbNG:breadcrumb>
                <bbNG:breadcrumb>查询用户信息</bbNG:breadcrumb>
            </bbNG:breadcrumbBar>
            <bbNG:pageTitleBar iconUrl="<%=titleIconPath%>">查询用户信息</bbNG:pageTitleBar>
        </bbNG:pageHeader>


        <bbNG:button type="PageLevel" label="确定" url="javascript:go_parent();" />
    </bbNG:genericPage>
</bbData:context>