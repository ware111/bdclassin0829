<%@ include file="../../webapis/ui/doctype.jspf" %>
<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="/bbUI" prefix="bbUI" %>
<%@ taglib uri="/bbNG" prefix="bbNG" %>
<%@ taglib uri="/bbData" prefix="bbData" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF8"
         import="blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession" %>
<%
    SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
    BbSession bbSession = sessionManager.getSession(request, response);
    String course_id = bbSession.getGlobalKey("course_id");
%>

<bbNG:learningSystemPage>
    <bbData:context id="ctx">
        <bbNG:jsBlock>
            <script src="http://code.jquery.com/jquery-latest.js"></script>
            <SCRIPT type="text/javascript">

                //保存设置
                function save() {
                    checkStartTime();
                    checkClassRoom();
                    checkStartDate();
                    var className = document.getElementById("className").value;
                    var classType = document.getElementById('classType').value;
                    var startDate = document.getElementById('startDate').value;
                    var startTime = document.getElementById('startTime').value;
                    var bbCourseId = document.getElementById('course_id').value;
                    var hourSelect = document.getElementById('hour');
                    var hourIndex = hourSelect.selectedIndex;
                    var hour = hourSelect.options[hourIndex].text;
                    var minuteSelect = document.getElementById('minute');
                    var minuteIndex = minuteSelect.selectedIndex;
                    var minute = minuteSelect.options[minuteIndex].text;
                    var teacherSelect = document.getElementById('teacher');
                    var teacherIndex = teacherSelect.selectedIndex;
                    var teacher = teacherSelect.options[teacherIndex].text;
                    var assistantTeacherSelect = document.getElementById('assistantTeacher');
                    var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;
                    var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].text;
                    var live = document.getElementById('live').checked;
                    var record = document.getElementById('record').checked;
                    var replay = document.getElementById('replay').checked;
                    if (teacher == '请选择授课教师'){
                        document.getElementById("choiceTeacher").innerText="请选择一个授课教师";
                    }else  if (checkStartTime() != 1 || checkStartDate() != 1 || checkClassRoom() != 1){
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/store.do?className="+className +
                            "&classType="+classType+"&startDate="+startDate+"&startTime="+startTime+"&hour="+hour+"&minute="+minute+
                            "&teacher="+teacher+"&assistantTeacher="+assistantTeacher+"&bbCourseId="+bbCourseId+
                        "&isLive="+live+"&isRecord="+record+"&isReplay="+replay;
                    }

                }

                //检查是否输入教室名称
                function checkClassRoom() {
                    var className = document.getElementById('className').value;
                    if (className == ""){
                        document.getElementById('checkClassName').innerText="请输入教室名称"
                    }else {
                        document.getElementById('checkClassName').innerText=""
                    }
                }

                //检查是否输入授课教师
                function checkTeacher() {
                    var teacherSelect = document.getElementById('teacher');
                    var teacherIndex = teacherSelect.selectedIndex;
                    var teacher = teacherSelect.options[teacherIndex].text;
                    if (teacher == "请选择授课教师"){
                        document.getElementById("choiceTeacher").innerText="请选择一个授课教师";
                    } else{
                        document.getElementById("choiceTeacher").innerText="";
                    }
                }

                //检查是否输入开课日期
                function checkStartDate() {
                    var startDate = document.getElementById('startDate').value;
                    if (startDate == ""){
                        document.getElementById('checkStartDate').innerText="请选择日期或者输入'xxxx-xx-xx'格式的日期"
                        return 1;
                    } else{
                        document.getElementById('checkStartDate').innerText="";
                        return 0;
                    }
                }

                //检查是否输入开课时间
                function checkStartTime() {
                    var startTime = document.getElementById('startTime').value;
                    if (startTime == ""){
                        document.getElementById('checkStartTime').innerText="请选择时间或者输入'xx:xx'格式的时间";
                        return 1;
                    } else{
                        document.getElementById('checkStartTime').innerText="";
                        return 0;
                    }

                }

                //日期插件点击触发
                $(function () {
                    $('#datetimepicker1').datetimepicker({format:'yyyy-MM-DD'});
                });

                //时间插件点击触发
                $(function () {
                    $('#datetimepicker2').datetimepicker({format:'HH:mm'});
                });

                //计算下课时间
                function computeOverTime(){
                    var startTime = document.getElementById('startTime').value;
                    var hourSelect = document.getElementById('hour');
                    var hourIndex = hourSelect.selectedIndex;
                    var hour = hourSelect.options[hourIndex].text;
                    var minuteSelect = document.getElementById('minute');
                    var minuteIndex = minuteSelect.selectedIndex;
                    var minute = minuteSelect.options[minuteIndex].text;
                    var overHour = 0;
                    var allMinute=0;
                    var times = startTime.split(":");
                    var allHour = new Number(times[0])+new Number(hour);
                    if (allHour >= 24){
                        overHour = allHour-24
                    }else{
                        overHour = allHour;
                    }

                    if (times.length == 2){
                        allMinute = new Number(times[1]) + new Number(minute);
                        if (allMinute >= 60){
                            overHour = overHour + 1;
                            allMinute = allMinute - 60;
                        }
                    } else{
                        allMinute=new Number(minute);
                    }

                    if (allMinute < 10){
                        allMinute = "0" + allMinute;
                    }
                    var overClassTime="下课时间："+overHour+":"+allMinute;
                    document.getElementById("text").innerText=overClassTime;
                }

                computeOverTime();

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

        <bbNG:pageHeader instructions="您还未绑定手机号，请先绑定,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
            <c:if test="${type == 'meetingroom'}">
                <bbNG:pageTitleBar title="ClassIn在线研讨室"></bbNG:pageTitleBar>
            </c:if>
            <c:if test="${type != 'meetingroom'}">
                classin紧急求助，扫一扫
                <img src="/webapps/bb-ClassIn-bb_bb60/images/qrCode.jpg" style="height:60px;width:60px;">
                <bbNG:pageTitleBar title="ClassIn课堂"></bbNG:pageTitleBar>
            </c:if>
        </bbNG:pageHeader>
        <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
        <input type="hidden" name="typeName" id="typeName" value="${type}">

        <bbNG:actionControlBar>

            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
                    <c:if test="${tips != null }">
                        <span style="font-size:15px;font-color:blue;" id="spanTips">${tips}</span> <br/><br/>
                    </c:if>
                    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
                    <link href="https://cdn.bootcss.com/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/moment.min.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/jquery.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/bootstrap.min.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/bootstrap-datetimepicker.min.js"></script>
                    <div style="margin-left: 3%">
                        <span style="margin-right: 6%">教室名称</span><input type="text" onblur="checkClassRoom()" name="className" value="" id="className" style="height: 34px">
                        <span style="color: rgba(172,29,42,0.82)" id="checkClassName"></span><br/><br/>
                        <span style="margin-right: 6%">课节类型</span><select id="classType">
                        <option value="课表课">课表课</option>
                        <option value="非课表课">非课表课</option>
                    </select><br/><br/>
                        <div class="row">
                            <div class='col-sm-2' style="width: 10%;">
                                <span>开课日期</span>
                            </div>
                            <div class='col-sm-2' style="padding-left:10px;">
                                <div class="form-group">
                                    <div class='input-group date' id='datetimepicker1'>
                                        <input type='text' class="form-control" id="startDate" onblur="checkStartDate()"/>
                                        <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
                                    </div>
                                </div>
                            </div>
                            <div class='col-sm-2'>
                                <span  style="color: rgba(172,29,42,0.82)" id="checkStartDate" onblur="checkStartDate()"></span>
                            </div>
                        </div>

                        <div class="row" >
                            <div class='col-sm-2' style="width: 10%;padding-left:10px;">
                                <span> 开课时间</span>
                            </div>
                            <div class='col-sm-2' style="padding-left:10px;">
                                <div class="form-group">
                                    <div class='input-group date' id='datetimepicker2'>
                                        <input type='text' class="form-control" id="startTime" onblur="computeOverTime();checkStartTime()"/>
                                        <span class="input-group-addon">
                        <span class="glyphicon glyphicon-time"></span>
                    </span>
                                    </div>
                                </div>
                            </div>
                            <div class='col-sm-2'>
                                <span style="color: rgba(172,29,42,0.82)" id="checkStartTime" onblur=""></span>
                            </div>
                        </div>


                        <div>
                            <span style="float: left;margin-right: 6%">课节时长</span><select id="hour" onblur="computeOverTime()" style="float: left">
                            <option value="0">0</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                            <option value="7">7</option>
                            <option value="8">8</option>
                            <option value="9">9</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                            <option value="13">13</option>
                            <option value="14">14</option>
                            <option value="15">15</option>
                            <option value="16">16</option>
                            <option value="17">17</option>
                            <option value="18">18</option>
                            <option value="19">19</option>
                            <option value="20">20</option>
                            <option value="21">21</option>
                            <option value="22">22</option>
                            <option value="23">23</option>
                        </select><span style="float: left">小时</span>
                        </div>
                        <div>
                            <select id="minute" onblur="computeOverTime()" style="float: left">
                                <option value="15">15</option>
                                <option value="20">20</option>
                                <option value="25">25</option>
                                <option value="30">30</option>
                                <option value="35">35</option>
                                <option value="40">40</option>
                                <option value="45">45</option>
                                <option value="50">50</option>
                                <option value="55">55</option>
                            </select>
                            <span style="float: left">分钟</span>
                        </div>
                        <br/><br/>
                        <div id="text" style="margin-top: -1%;margin-left: 10%"></div><br/>
                        <span style="margin-right: 6%">授课教师</span><select id="teacher" onblur="checkTeacher()">
                        <option>请选择授课教师</option>
                        <c:forEach items="${teachers}" var="bbUser">
                            <c:if test="${bbUser.phone=='   (请绑定手机号)'}">
                                <option disabled="" style="color: rgba(44,50,108,0.14)">${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                            <c:if test="${bbUser.phone!='   (请绑定手机号)'}">
                                <option>${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                        <span style="color: rgba(172,29,42,0.82)" id="choiceTeacher"></span><br/><br/>
                        <span style="margin-right: 6%">助&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;教</span><select id="assistantTeacher">
                        <option>请选择助教教师</option>
                        <c:forEach items="${assistantTeachers}" var="bbUser">
                            <c:if test="${bbUser.phone=='   (请绑定手机号)'}">
                                <option disabled="" style="color: rgba(44,50,108,0.14)">${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                            <c:if test="${bbUser.phone!='   (请绑定手机号)'}">
                                <option>${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                        </c:forEach>
                    </select><br/><br/>
                        <span style="margin-left:10%;">直播</span><input type="checkbox" id="live" checked="checked" style="vertical-align:middle;margin-left:2px;margin-top:-3px;">
                        <span>录课</span><input type="checkbox" id="record" checked="checked" style="vertical-align:middle;margin-left:2px;margin-top:-3px;">
                        <span>回放</span><input type="checkbox" id="replay" checked="checked" style="vertical-align:middle;margin-left:2px;margin-top:-3px;">
                        <div style="margin-left: 10%;margin-top:20px;">
                            <input type="button" name="btn" value="取消" onclick="history.go(-1)">
                            <input type="button" name="btn" value="确定" onclick="save()" id="btn" style="margin-left:40px;">
                        </div>
                    </div>
                    <%--<input type="button" name="btn" value="取消" onclick="back()">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp--%>
                    <%--<input type="button" name="btn" value="确定" onclick="save()" id="btn">--%>
                    <script>
                        // $("#btn").click(function () {
                        //     alert("确定确定")
                        // })
                    </script>
                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>

    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)"/>
</bbNG:learningSystemPage>