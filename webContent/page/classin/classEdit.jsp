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
            <script type="text/javascript">
                var courseId = document.getElementById("course_id").value;

                //获取默认的老师及助教
                jQuery.ajax({
                    type: "GET",
                    url: "${pageContext.request.contextPath}/classinCourseClass/getDefaultValue.do",
                    // data:{"id":val},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                    data: "course_id=" + courseId,
                    async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                    cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                    dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                    success: function (data) {
                        document.getElementById("className").value = data.className;
                        $("#teacher").get(0).selectedIndex = new Number(data.teacherNum);
                        $("#assistantTeacher").get(0).selectedIndex = new Number(data.assistantNum);
                        // alert("刷新成功了")
                    },
                    error: function () {
                        alert("发生错误~");
                    },
                    complete: function () {
                        //alert("ajax请求完成")
                    }
                });

                // jQuery.ajax({
                //     type:"GET",
                //     url:"${pageContext.request.contextPath}/classinCourseClass/getDefaultValue.do",
                //     data:{course_id:courseId},
                //     async:true,
                //     cache:true,
                //     dataType:"json",
                //     success:function (data) {
                //         alert("回调了");
                //         $("#clssName").val(data.courseName);
                //         $("#teacher").get(0).selectedIndex=new Number(data.teacherNum);
                //         $("#assistantTeacher").get(0).selectedIndex=new Number(data.assistantNum);
                //     },
                //     error:function () {
                //         alert("系统错误");
                //     }
                // });

            </script>
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
            <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
            <input type="hidden" name="typeName" id="typeName" value="${type}">

        </bbNG:pageHeader>
        <bbNG:actionControlBar>
            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
                    <c:if test="${tips != null }">
                        <span style="font-size:15px;font-color:blue;" id="spanTips">${tips}</span> <br/><br/>
                    </c:if>
                    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
                    <link href="https://cdn.bootcss.com/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css"
                          rel="stylesheet">
                    <link href="/webapps/bb-ClassIn-bb_bb60/layui/css/layui.css">
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/layui/layui.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/moment.min.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/jquery.js"></script>
                    <script type="text/javascript" src="/webapps/bb-ClassIn-bb_bb60/js/bootstrap.min.js"></script>
                    <script type="text/javascript"
                            src="/webapps/bb-ClassIn-bb_bb60/js/bootstrap-datetimepicker.min.js"></script>
                    <style>
                        .layui-laydate-content > .layui-laydate-list {
                            padding-bottom: 0px;
                            overflow: hidden;
                        }

                        .layui-laydate-content > .layui-laydate-list > li {
                            width: 50%
                        }

                        .merge-box .scrollbox .merge-list {
                            padding-bottom: 5px;
                        }

                        #test2, #test, .open_time, .open_date, #test1 {
                            position: absolute;
                            left: -34px;
                            top: -20px;
                            z-index: 10;
                            border: 1px solid #adadad;
                            line-height: 27px;
                            width: 30px;
                            text-align: center;
                            background-color: #ccc;
                        }

                        .open_time, .open_date {
                            z-index: 1;
                        }

                        .open_time span {
                            font-size: 20px;
                        }

                        .open_date, #test1 {
                            line-height: 28px;
                        }

                        #test2, #test1, #test {
                            width: 30px;
                            height: 28px;
                            opacity: 0;
                        }

                        #startTime, #startDate {
                            height: 30px;
                            line-height: 30px;
                            border-width: 1px;
                        }
                    </style>
                    <div style="margin-left: 3%">
                        <div class="row" style="margin-top:20px;">
                            <div class="col-sm-2" style="width: 10%;">
                                <span style="line-height:34px;width:100%;">课节名称:</span>
                            </div>
                            <div class="col-sm-3" style="padding-left:10px;">
                                <input type="text" name="className" value="" id="className"
                                       style="height: 34px;width:100%;" onblur="checkClassRoom()" maxlength="50">
                            </div>
                            <div class="col-sm-2" style="padding-left:10px;line-height:32px;">
                                <span id="checkClassName"></span>
                            </div>
                        </div>
                        <div class="row" style="margin-top:20px;margin-bottom:20px;">
                            <div class="col-sm-2" style="width: 10%;">
                                <span>课节类型:</span>
                            </div>
                            <div class="col-sm-3" style="padding-left:10px;">
                                <select id="classType" style="width:100%;">
                                        <%--<option value="课表课">课表课</option>--%>
                                    <option value="课表课" selected="selected">课表课</option>
                                    <option value="小组会">小组会</option>
                                    <option value="研讨课">研讨课</option>
                                    <option value="答疑课">答疑课</option>
                                    <option value="公开课">公开课</option>
                                    <option value="其他">其他</option>
                                </select>
                            </div>
                            <div class="col-sm-2" style="padding-left:10px;">
                                <span id="timeTable"></span>
                            </div>
                        </div>
                        <div class="row">
                            <div class='col-sm-2' style="width: 10%;">
                                <span style="line-height:30px;">开课日期:</span>
                            </div>
                            <div class='col-sm-3' style="padding-left:10px;">
                                <div class='form-group date' style="position:relative;">
                                    <input type="text" id="startDate" style="width:100%;">
                                    <div style="position:absolute;top:20px;right:-5px;display:inline-block;">
                                        <input type="button" value="" id="test1">
                                        <span class="glyphicon glyphicon-calendar open_date"></span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-sm-2" style="padding-left:10px;">
                                <span id="checkStartDate"></span>
                            </div>
                        </div>
                        <div class="row">
                            <div class='col-sm-2' style="width: 10%;">
                                <span style="line-height:30px;">开课时间:</span>
                            </div>
                            <div class='col-sm-3' style="padding-left:10px;">
                                <div class="form-group" style="position:relative;">
                                    <input type="text" id="startTime" style="width:100%;">
                                    <div style="position:absolute;top:20px;right:-5px;display:inline-block;">
                                        <input type="button" id="test">
                                        <input type="button" id="test2" style="display:none;">
                                        <div class="open_time">
                                            <span class="glyphicon glyphicon-time" style="line-height:28px;"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-sm-2" style="padding-left:10px;">
                                <span id="checkStartTime"></span>
                            </div>
                        </div>
                        <div class="row">
                            <div class='col-sm-2' style="width: 10%;">
                                <span>课节时长:</span>
                            </div>
                            <div class='col-sm-3' style="padding-left:10px;">
                                <div>
                                    <select id="hour" onblur="computeOverTime()" style="float: left;width:30%;">
                                        <option value="0">0</option>
                                        <option value="1">1</option>
                                        <option value="2" selected="selected">2</option>
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
                                    </select>
                                    <span style="float: left;line-height:26px;">小时</span>
                                </div>
                                <div>
                                    <select id="minute" onblur="computeOverTime()" style="float: left;width:30%;">
                                        <option value="0" selected="selected">0</option>
                                        <option value="5">5</option>
                                        <option value="10">10</option>
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
                                    <span style="float: left;line-height:26px;">分钟</span>
                                </div>
                                <br/><br/>
                            </div>
                            <div class='col-sm-2' style="width: 10%;padding-left:10px;">
                                <div id="text" style="margin-top: -1%;width:200px;line-height:26px;"></div>
                            </div>
                        </div>
                        <div class="row">
                            <div class='col-sm-2' style="width: 10%;">
                                <span>授课教师:</span>
                            </div>
                            <div class="col-sm-3" style="padding-left:10px;">
                                <select id="teacher" onblur="checkTeacher()" style="width:100%;">
                                    <option>请选择授课教师</option>
                                    <c:forEach items="${teachers}" var="bbUser">
                                        <c:if test="${bbUser.phone=='   (请绑定手机号)'}">
                                            <option disabled=""
                                                    style="color: rgba(44,50,108,0.14)">${bbUser.userName}&nbsp;${bbUser.phone}</option>
                                        </c:if>
                                        <c:if test="${bbUser.phone!='   (请绑定手机号)'}">
                                            <option>${bbUser.userName}&nbsp;${bbUser.phone}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-sm-2" style="padding-left:10px;line-height:26px;">
                                <span id="choiceTeacher"></span>
                            </div>
                        </div>
                        <div class="row" style="margin-top:20px;">
                            <div class='col-sm-2' style="width: 10%;">
                                <span>助&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;教:</span>
                            </div>
                            <div class="col-sm-3" style="padding-left:10px;">
                                <select id="assistantTeacher" style="width:100%;">
                                    <option>请选择助教教师</option>
                                    <c:forEach items="${assistantTeachers}" var="bbUser">
                                        <c:if test="${bbUser.phone=='   (请绑定手机号)'}">
                                            <option disabled=""
                                                    style="color: rgba(44,50,108,0.14)">${bbUser.userName}&nbsp;${bbUser.phone}</option>
                                        </c:if>
                                        <c:if test="${bbUser.phone!='   (请绑定手机号)'}">
                                            <option>${bbUser.userName}&nbsp;${bbUser.phone}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row" id="check_record" style="margin-top:20px;">
                            <span style="margin-left:11%;">录课</span>
                            <input type="checkbox" id="record" checked
                                   style="vertical-align:middle;margin-left:2px;margin-top:-3px;">
                            <span>直播</span>
                            <input type="checkbox" id="live" checked
                                   style="vertical-align:middle;margin-left:2px;margin-top:-3px;">
                            <span>回放</span>
                            <input type="checkbox" id="replay" checked
                                   style="vertical-align:middle;margin-left:2px;margin-top:-3px;">
                        </div>
                        <div class="row" style="margin-left: 10%;margin-top:20px;">
                            <input type="button" name="btn" value="取消" onclick="goBack()">
                            <input type="button" name="btn" value="确定" onclick="save()" id="btn"
                                   style="margin-left:40px;">
                        </div>
                    </div>

                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>

        <script>
            function my$(id) {
                return document.getElementById(id);
            }

            //判断录课、直播、回放
            var all_record = my$("check_record").getElementsByTagName("input");
            my$("record").onclick = function () {
                for (var i = 1; i < all_record.length; i++) {
                    if (my$("record").checked == true) {
                        all_record[i].disabled = false;
                    } else {
                        all_record[i].disabled = true;
                        all_record[i].checked = false;
                    }
                }
            }

            var dt = new Date();
            var year = dt.getFullYear();
            var month = dt.getMonth() + 1;
            var day = dt.getDate();
            var hour = dt.getHours();
            var minute = dt.getMinutes();
            if (month < 10) {
                month = "0" + month;
            }
            if (day < 10) {
                day = "0" + day;
            }
            var toYear = year + 3;
            var toToday = toYear + "-" + month + "-" + day;
            var today = year + "-" + month + "-" + day;
            my$("test1").value = today;
            my$("startDate").value = today;
            if (minute % 5 == 0) {
                minute = minute + 5;
            } else {
                while (minute % 5 != 0) {
                    minute++;
                }
                minute = minute + 5;
            }
            if (minute >= 60) {
                hour = hour + 1;
                minute = minute - 60;
                if (minute < 10) {
                    minute = "0" + minute;
                }
                if (hour < 10) {
                    hour = "0" + hour;
                }
            }
            var time = hour + ":" + minute;
            my$("test").value = time;
            my$("startTime").value = hour + ":" + minute;//时间
            my$("test2").value = time;


            var day_date = new Object();
            var time_date = new Object();


            //设置开课日期和时间
            my$("test2").style.display = "none";
            my$("test").style.display = "block";
            layui.use('laydate', function () {
                var laydate = layui.laydate;
                var min_hour = hour;
                var min_minute = minute;
                var timexin = min_hour + ':' + min_minute + ':' + '00';
                laydate.render({
                    elem: '#test',
                    type: 'time',
                    format: 'HH:mm',
                    btns: ['confirm'],
                    min: timexin,
                    done: function (value, date, endDate) {
                        my$("startTime").value = value;
                        time_date = this.date;
                        computeOverTime();
                    }
                });
            });
            layui.use('laydate', function () {
                var laydate = layui.laydate;
                laydate.render({
                    elem: '#test1',
                    showBottom: false,
                    min: today,
                    max: toToday,
                    value: dt,
                    position: 'fixed',
                    done: function (value, date, endDate) {
                        my$("startDate").value = value;
                        day_date = this.date;
                        if (date.year == year && date.month == month && date.date == day) {
                            my$("test2").style.display = "none";
                            my$("test").style.display = "block";
                            layui.use('laydate', function () {
                                var laydate = layui.laydate;
                                var min_hour = hour;
                                var min_minute = minute;
                                var timexin = min_hour + ':' + min_minute + ':' + '00';
                                laydate.render({
                                    elem: '#test',
                                    type: 'time',
                                    format: 'HH:mm',
                                    btns: ['confirm'],
                                    min: timexin,
                                    done: function (value, date, endDate) {
                                        my$("startTime").value = value;
                                        time_date = this.date;
                                    }
                                });
                            });
                            if (my$("startTime").value != my$("test").value) {
                                my$("startTime").value = my$("test").value;
                            }
                        } else if (date.year != year || date.month != month || date.date != day) {
                            my$("test").style.display = "none";
                            my$("test2").style.display = "block";
                            layui.use('laydate', function () {
                                var laydate = layui.laydate;
                                laydate.render({
                                    elem: '#test2',
                                    type: 'time',
                                    format: 'HH:mm',
                                    btns: ['confirm'],
                                    min: '00:00:00',
                                    done: function (value, date, endDate) {
                                        my$("startTime").value = value;
                                        time_date = this.date;
                                        computeOverTime();
                                    }
                                });
                            });
                        }
                    }
                });

            });


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

                var startTimeStamp = Date.parse(startDate + " " + startTime) / 1000;
                if (teacher == '请选择授课教师') {
                    document.getElementById("choiceTeacher").innerText = "请选择一个授课教师";
                } else if (checkStartTime() != 1 || checkStartDate() != 1 || checkClassRoom() != 1) {
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/store.do?className=" + className +
                        "&classType=" + classType + "&startDate=" + startDate + "&startTime=" + startTime + "&hour=" + hour + "&minute=" + minute +
                        "&teacher=" + teacher + "&assistantTeacher=" + assistantTeacher + "&bbCourseId=" + bbCourseId +
                        "&isLive=" + live + "&isRecord=" + record + "&isReplay=" + replay + "&startTimeStamp=" + startTimeStamp;
                }


            }

            //检查是否输入教室名称
            function checkClassRoom() {
                var className = document.getElementById('className').value;
                if (className == "") {
                    document.getElementById('checkClassName').innerText = "请输入教室名称";
                    $("#checkClassName").css("color", "red");
                    return 1;
                } else {
                    document.getElementById('checkClassName').innerText = ""
                    return 0;
                }
            }

            //检查是否输入授课教师
            function checkTeacher() {
                var teacherSelect = document.getElementById('teacher');
                var teacherIndex = teacherSelect.selectedIndex;
                var teacher = teacherSelect.options[teacherIndex].text;
                if (teacher == "请选择授课教师") {
                    document.getElementById("choiceTeacher").innerText = "请选择一个授课教师";
                    $("#choiceTeacher").css("color", "red");
                    return 1;
                } else {
                    document.getElementById("choiceTeacher").innerText = "";
                    return 0;
                }
            }

            //检查是否输入开课日期
            function checkStartDate() {
                var startDate = document.getElementById('startDate').value;
                if (startDate == "") {
                    document.getElementById('checkStartDate').innerText = "请选择日期或者输入'xxxx-xx-xx'格式的日期"
                    return 1;
                } else {
                    document.getElementById('checkStartDate').innerText = "";
                    return 0;
                }
            }

            //检查是否输入开课时间
            function checkStartTime() {
                var startTime = document.getElementById('startTime').value;
                if (startTime == "") {
                    document.getElementById('checkStartTime').innerText = "请选择时间或者输入'xx:xx'格式的时间";
                    $("#checkStartTime").css("color", "red");
                    return 1;
                } else {
                    document.getElementById('checkStartTime').innerText = "";
                    return 0;
                }

            }


            //计算下课时间
            function computeOverTime() {
                var startTime = document.getElementById('startTime').value;
                var hourSelect = document.getElementById('hour');
                var hourIndex = hourSelect.selectedIndex;
                var hour = hourSelect.options[hourIndex].text;
                var minuteSelect = document.getElementById('minute');
                var minuteIndex = minuteSelect.selectedIndex;
                var minute = minuteSelect.options[minuteIndex].text;
                var overHour = 0;
                var allMinute = 0;
                var times = startTime.split(":");
                var allHour = new Number(times[0]) + new Number(hour);
                if (allHour >= 24) {
                    overHour = allHour - 24
                } else {
                    overHour = allHour;
                }

                if (times.length == 2) {
                    allMinute = new Number(times[1]) + new Number(minute);
                    if (allMinute >= 60) {
                        overHour = overHour + 1;
                        allMinute = allMinute - 60;
                    }
                } else {
                    allMinute = new Number(minute);
                }

                if (allMinute < 10) {
                    allMinute = "0" + allMinute;
                }
                var overClassTime = "下课时间：" + overHour + ":" + allMinute;
                document.getElementById("text").innerText = overClassTime;

                var timelength = Number(hour) * 60 + Number(minute);
                if (timelength < 15) {
                    minuteSelect.options[3].selected = "selected";
                    document.getElementById("text").innerText = "课节时长最短需要15分钟";
                    document.getElementById("text").style.color = "red";
                } else {
                    document.getElementById("text").style.color = "black";
                    document.getElementById("text").innerText = overClassTime;
                }
            }

            computeOverTime();

            function goBack() {
                var courseId = document.getElementsByName("course_id")[0].value;
                window.location.href = "${pageContext.request.contextPath}/classinCourseClass/goBack.do?course_id=" + courseId
            }

            //检查是否输入教室名称
            function checkClassRoom() {
                var className = document.getElementById('className').value;
                if (className == "") {
                    document.getElementById('checkClassName').innerText = "请输入教室名称"
                } else if (className.length == 50) {
                    document.getElementById('checkClassName').innerText = "教室名称最大长度为50"
                } else {
                    document.getElementById('checkClassName').innerText = ""
                }

            }
        </script>


    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:goBack()"/>
</bbNG:learningSystemPage>