<%@ include file="../../webapis/ui/doctype.jspf" %>
<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="/bbUI" prefix="bbUI" %>
<%@ taglib uri="/bbNG" prefix="bbNG" %>
<%@ taglib uri="/bbData" prefix="bbData" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF8"
         import="blackboard.platform.BbServiceManager,
                 blackboard.base.*,
                 java.util.*,
                 blackboard.platform.intl.JsResource,
                 blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession" %>
<%@ page import="com.blackboard.classin.util.SystemUtil" %>
<%@ page import="com.blackboard.classin.util.TimeStampUtil" %>
<%
    SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
    BbSession bbSession = sessionManager.getSession(request, response);
    String course_id = bbSession.getGlobalKey("course_id");
    String courseName = bbSession.getGlobalKey("courseName");
    String role = bbSession.getGlobalKey("role");
    boolean isTeacher = SystemUtil.isTeacher();
    boolean isStudent = SystemUtil.isStudent();
    boolean isAdministrator = SystemUtil.isAdministrator();
    //out.clear();      //清空缓存的内容
    //out=pageContext.pushBody();  //更新PageContext的out属性的内容
%>
<bbNG:learningSystemPage>
    <bbData:context id="ctx">
        <bbNG:jsBlock>
            <SCRIPT type="text/javascript">
                var courseId = document.getElementsByName("course_id")[0].value;
                var dt = new Date();
                var year = dt.getFullYear();
                var month = dt.getMonth() + 1;
                var day = dt.getDate();
                var toMonth = month - 1;
                if (month < 10) {
                    month = "0" + month;
                }
                if (toMonth < 10) {
                    toMonth = "0" + toMonth;
                }
                if (day < 10) {
                    day = "0" + day;
                }
                var today = year + "-" + month + "-" + day;
                var toToday = year + "-" + toMonth + "-" + day;
                document.getElementById("date").value = toToday + " - " + today;
                document.getElementById("listContainer_itemcount").style.display="none";
                var pageNow=document.getElementById("pageNumber").innerText;

                layui.use('laydate', function () {
                    var laydate = layui.laydate;
                    laydate.render({
                        elem: '#date',
                        trigger: 'click'
                        , type: 'date'
                        , range: true
                        , max: today
                    });
                });
                document.getElementById("learn-oe-body").style.width = "100%";
                // document.getElementById("learn-oe-body").style.overflow = "hidden";
                document.getElementById("learn-oe-body").style.padding = "0px";
                if(Number(document.getElementById("pageNumber").innerText)==Number(document.getElementById("pageNumTotal").innerText)){
                    document.getElementById("nextBtn").disabled=true;
                    document.getElementById("lasttBtn").disabled=true;
                }

                //下载课程课节数据
                function downloadCourseClassData() {
                    var timeFrame = document.getElementById("date").value;
                    var startTime = Date.parse(timeFrame.split(" - ")[0] + " 00:00");
                    var startTimeStamp = startTime / 1000;
                    var endTime = Date.parse(timeFrame.split(" - ")[1] + " 00:00");
                    var endTimeStamp = endTime / 1000;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/downloadCourseClassData.do?beginTime="
                        + startTimeStamp + "&endTime=" + endTimeStamp + "&courseId=" + courseId;

                }

                //下载课程学生数据
                function downloadCourseStudentData() {
                    var timeFrame = document.getElementById("date").value;
                    var startTime = Date.parse(timeFrame.split(" - ")[0] + " 00:00");
                    var startTimeStamp = startTime / 1000;
                    var endTime = Date.parse(timeFrame.split(" - ")[1] + " 00:00");
                    var endTimeStamp = endTime / 1000;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/downloadCourseStudentData.do?beginTime="
                        + startTimeStamp + "&endTime=" + endTimeStamp + "&courseId=" + courseId;

                }

                //下载单个课节学生数据
                function downloadClassData(id) {
                    var classId = document.getElementById(id+"classId").innerText;
                    var beginTime = document.getElementById(id+"startTime").innerText;
                    var timeFrame = document.getElementById("date").value;
                    var startTime = Date.parse(timeFrame.split(" - ")[0] + " 00:00");
                    var startTimeStamp = startTime / 1000;
                    var endTime = Date.parse(timeFrame.split(" - ")[1] + " 00:00");
                    var endTimeStamp = endTime / 1000;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/downloadClassStudentData.do?beginTime="
                        + startTimeStamp + "&endTime=" + endTimeStamp + "&classId=" + classId + "&courseId=" + courseId + "&startTime="+beginTime;
                }

                //隐藏原页面上的分页显示
                // document.getElementById("listContainer_pagingcontrols").style.display="none";

                if(Number(document.getElementById("pageNumber").innerText)==1){
                    document.getElementById("firstBtn").disabled=true;
                    document.getElementById("previousBtn").disabled=true;
                }
                var page=1;
                // console.log(page);
                document.getElementById("targetPage").value=document.getElementById("pageNumber").innerText;

                function goFirstPage(){
                    if(Number(document.getElementById("pageNumber").innerText)<=1){
                        return;
                    }else{
                        document.getElementById("pageNumber").innerText=1;
                        document.getElementById("firstBtn").disabled=true;
                        document.getElementById("previousBtn").disabled=true;
                        document.getElementById("lasttBtn").disabled=false;
                        document.getElementById("nextBtn").disabled=false;
                        document.getElementById("targetPage").value=document.getElementById("pageNumber").innerText;
                    }
                    page=1;
                    window.location.href="${pageContext.request.contextPath}/newclassinCourseClass/pageClassData.do?courseId="+courseId+"&page="+page;
                }
                function goEndPage(){
                    if(Number(document.getElementById("pageNumber").innerText)>=Number(document.getElementById("pageNumTotal").innerText)){
                        return;
                    }else{
                        document.getElementById("pageNumber").innerText=document.getElementById("pageNumTotal").innerText;
                        document.getElementById("lasttBtn").disabled=true;
                        document.getElementById("nextBtn").disabled=true;
                        document.getElementById("firstBtn").disabled=false;
                        document.getElementById("previousBtn").disabled=false;
                    }
                    document.getElementById("targetPage").value=document.getElementById("pageNumber").innerText;
                    page=-1;
                    window.location.href="${pageContext.request.contextPath}/newclassinCourseClass/pageClassData.do?courseId="+courseId+"&page="+page;
                }

                function goPreviousPage(){
                    document.getElementById("pageNumber").innerText=Number(document.getElementById("pageNumber").innerText)-1;
                    if(Number(document.getElementById("pageNumber").innerText)<=1){
                        document.getElementById("firstBtn").disabled=true;
                        document.getElementById("previousBtn").disabled=true;
                    }else{
                        document.getElementById("firstBtn").disabled=false;
                        document.getElementById("previousBtn").disabled=false;
                        if(Number(document.getElementById("pageNumber").innerText)<Number(document.getElementById("pageNumTotal").innerText)){
                            document.getElementById("lasttBtn").disabled=false;
                            document.getElementById("nextBtn").disabled=false;
                        }
                    }
                    document.getElementById("targetPage").value=document.getElementById("pageNumber").innerText;
                    page=document.getElementById("pageNumber").innerText;
                    // console.log(page);
                    window.location.href="${pageContext.request.contextPath}/newclassinCourseClass/pageClassData.do?courseId="+courseId+"&page="+page;
                }
                function goNextPage(){
                    document.getElementById("pageNumber").innerText=Number(document.getElementById("pageNumber").innerText)+1;
                    if(Number(document.getElementById("pageNumber").innerText)>=Number(document.getElementById("pageNumTotal").innerText)){
                        document.getElementById("lasttBtn").disabled=true;
                        document.getElementById("nextBtn").disabled=true;
                        page=-1;
                    }else{
                        document.getElementById("lasttBtn").disabled=false;
                        document.getElementById("nextBtn").disabled=false;
                        if(Number(document.getElementById("pageNumber").innerText)>1){
                            document.getElementById("firstBtn").disabled=false;
                            document.getElementById("previousBtn").disabled=false;
                        }
                        document.getElementById("targetPage").value=document.getElementById("pageNumber").innerText;
                        page=document.getElementById("pageNumber").innerText;
                    }
                    // console.log(page);
                    window.location.href="${pageContext.request.contextPath}/newclassinCourseClass/pageClassData.do?courseId="+courseId+"&page="+page;
                }

                function jump(){
                    var targetPage=document.getElementById("targetPage").value;
                    var reg=/^[0-9]+$/;
                    if(reg.test(targetPage)){
                        if(Number(targetPage)>Number(document.getElementById("pageNumTotal").innerText)){
                            document.getElementById("pageNumber").innerText=Number(document.getElementById("pageNumTotal").innerText);
                            document.getElementById("targetPage").value=document.getElementById("pageNumTotal").innerText;
                        }else if(Number(targetPage)==1){
                            document.getElementById("pageNumber").innerText=1;
                        }else{
                            document.getElementById("pageNumber").innerText=targetPage;
                        }
                    }else{
                        document.getElementById("targetPage").value="";
                    }
                    // page=document.getElementById("pageNumber").innerText;
                    // page = Number(page);
                    // alert("_"+page+"_")
                    // alert(page);
                    var currentPage = document.getElementById("pageNumber").innerText;
                    if(document.getElementById("pageNumber").innerText==pageNow){
                        return;
                    }
                    window.location.href="${pageContext.request.contextPath}/newclassinCourseClass/pageClassData.do?courseId="+courseId+"&page="+currentPage;
                }

                //返回首页
                function goBack() {
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

        <%--<bbNG:--%>
        <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
        <input type="hidden" name="typeName" id="typeName" value="${type}">

        <bbNG:actionControlBar>
            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
                    <%--<link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">--%>
                    <link href="https://cdn.bootcss.com/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css"
                          rel="stylesheet">
                    <link href="/webapps/bb-ClassIn-bb_bb60/layui/css/layui.css">
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/layui/layui.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/moment.min.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/jquery.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/bootstrap.min.js"></script>
                    <style>
                        body {
                            padding: 30px;
                            width: 1200px;
                            margin: 0 auto;
                        }

                        .lf {
                            float: left;
                        }

                        .rt {
                            float: right;
                        }

                        ul.attendance {
                            width: 100%;
                        }

                        ul.attendance li {
                            float: left;
                            margin-right: 50px;
                            line-height: 40px;
                        }

                        ul.download li {
                            float: left;
                            margin-right: 50px;
                            line-height: 40px;
                        }

                        ul.list li {
                            float: left;
                            margin: 0 10px;
                        }

                        .clearfix:after {
                            content: "";
                            height: 0;
                            display: block;
                            clear: both;
                            visibility: hidden;
                        }

                        .clear {
                            *zoom: 1;
                        }
                    </style>
                    <div>
                        <div class="clearfix">
                            <ul class="lf attendance">
                                <li>课程名：${courseName}</li>
                                <li>课程进度：课表课${KeBiaoKe}节，非课表课${notKeBiaoKe}节</li>
                                <li>课程考勤：${checkinStudents}/${courseCheckin}人次</li>
                                <li>总出勤率：${courseCheckinRate}%</li>
                                <li>课表课出勤率：${keBiaoKeCheckRate}%</li>
                            </ul>
                            <ul class="lf download">
                                <li><input type="button" value="课程课节数据下载" onclick="downloadCourseClassData()"></li>
                                <li><input type="button" value="课程学生明细下载" onclick="downloadCourseStudentData()"></li>
                                <li>请选择要下载的时间段（北京时间）:<input type="text" id="date"
                                                            style="margin-left:5px;width:200px;text-align:center;"></li>
                            </ul>
                        </div>
                    </div>
                    <script>
                        // document.getElementById("date").onclick=function(){
                        //     var date_header=document.getElementById("layui-laydate1");
                        //     var date_icon=date_header.getElementsByTagName("i");
                        //     console.log(date_icon);
                        //     date_icon[0].innerText="";
                        //     date_icon[1].innerText="";
                        //     date_icon[6].innerText="";
                        //     date_icon[7].innerText="";
                        //     date_icon[0].className="glyphicon glyphicon-chevron-left laydate-prev-y";
                        //     date_icon[1].className="glyphicon glyphicon-chevron-left laydate-prev-m";
                        //     date_icon[6].className="glyphicon glyphicon-chevron-right laydate-next-m";
                        //     date_icon[7].className="glyphicon glyphicon-chevron-right laydate-next-y";
                        // }
                    </script>

                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>
        <bbNG:inventoryList collection="${classData}" initialSortCol="startTime" objectVar="classinClassInfo"
                            className="com.blackboard.classin.entity.CourseClassConditionData" url="">
            <bbNG:listElement label="课节名称" name="classinClassName" isRowHeader="true">
                <%=classinClassInfo.getClassName()%>
                <label hidden="hidden" id="${classinClassInfo.id}classId">${classinClassInfo.classId}</label>
            </bbNG:listElement>
            <bbNG:listElement label="类型" name="type">
                <label style="">${classinClassInfo.classType}</label>
            </bbNG:listElement>
            <bbNG:listElement label="开课日期" name="startDate">
                <label id="${classinClassInfo.id}startTime"><%=TimeStampUtil.timeStampToTimeNotSecond(classinClassInfo.getStartTime()+"")%></label>
            </bbNG:listElement>
            <bbNG:listElement label="授课教师" name="teacherName">
                <%=classinClassInfo.getTeacherName()%>
            </bbNG:listElement>
            <bbNG:listElement label="授课时长" name="duration">
                <%=classinClassInfo.getTeacheInClassTime()%>
            </bbNG:listElement>
            <bbNG:listElement label="学生出勤" name="studentCheckin">
                <%=classinClassInfo.getCheckinStudent()%>
            </bbNG:listElement>
            <bbNG:listElement label="出勤率" name="checkinRate">
                <%=String.format("%.1f", classinClassInfo.getCheckinRate())%>%
            </bbNG:listElement>
            <bbNG:listElement label="迟到人数" name="lateCount">
                <%=classinClassInfo.getLaterTotal()%>
            </bbNG:listElement>
            <bbNG:listElement label="早退人数" name="backCount">
                <%=classinClassInfo.getLeaveEarly()%>
            </bbNG:listElement>
            <bbNG:listElement label="奖励次数" name="awareCount">
                <%=classinClassInfo.getAwardCount()%>
            </bbNG:listElement>
            <bbNG:listElement label="授权次数" name="authCount">
                <%=classinClassInfo.getAuthorizeCount()%>
            </bbNG:listElement>
            <bbNG:listElement label="举手次数" name="handsupCount">
                <%=classinClassInfo.getHandsupCount()%>
            </bbNG:listElement>
            <bbNG:listElement label="答题器次数" name="answerCount">
                <%=classinClassInfo.getAnswerCount()%>
            </bbNG:listElement>
            <bbNG:listElement label="答题器平均正确率" name="averageRate">
                <%=Double.valueOf(classinClassInfo.getAverageAccuracy()) * 100%>%
            </bbNG:listElement>
            <bbNG:listElement label="学生明细" name="detail">
                <label onclick="downloadClassData(${classinClassInfo.id})" style="cursor: pointer">下载</label>
            </bbNG:listElement>
        </bbNG:inventoryList>
        <div style="float:right;">
            共<span id="pageNumTotal">&nbsp;${pages}&nbsp;</span>页&nbsp;&nbsp;
            当前第&nbsp;<span id="pageNumber">${currentPage}</span>&nbsp;页
            <input type="button" value="第一页" id="firstBtn" onclick="goFirstPage()">
            <input type="button" value="上一页" id="previousBtn" onclick="goPreviousPage()">
            <input type="button" value="下一页" id="nextBtn" onclick="goNextPage()">
            <input type="button" value="最后一页" id="lasttBtn" onclick="goEndPage()">
            前往<input type="text" style="width:30px;height:20px;line-height:20px;text-align:center;" id="targetPage" onblur="jump()">页
        </div>
        <bbNG:button type="PageLevel" label="返回" url="javascript:goBack()"/>
    </bbData:context>
</bbNG:learningSystemPage>