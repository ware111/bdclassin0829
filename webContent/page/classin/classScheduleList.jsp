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
<%
    SessionManager sessionManager = (SessionManager) SessionManager.Factory.getInstance();
    BbSession bbSession = sessionManager.getSession(request, response);
    String course_id = bbSession.getGlobalKey("course_id");
    String courseName = bbSession.getGlobalKey("courseName");
    String role = bbSession.getGlobalKey("role");
    int i = 0;
    //out.clear();      //清空缓存的内容
    //out=pageContext.pushBody();  //更新PageContext的out属性的内容
%>
<bbNG:learningSystemPage>
    <bbData:context id="ctx">
        <bbNG:jsBlock>
            <SCRIPT type="text/javascript">
                var courseId = document.getElementById("course_id").value;
                var intervalTimeArray = new Array();
                refresh();
                initSeleted();

                //让全部select标签选第一项
                function initSeleted() {
                    var selects = document.getElementsByTagName("select");
                    for (var i = 0; i < selects.length; i++){
                        selects[i].selectedIndex = 0;
                    }
                }

                //处理课表信息集合
                function handleData(data) {
                    data = data.data;
                    for (var i = 0; i < data.length; i++) {
                        var currentTimeStamp = Date.parse(new Date()) / 1000;
                        var isTeacher = document.getElementById("isTeacher").innerText + "";
                        currentTimeStamp = new Number(currentTimeStamp);
                        var startTimeStamp = new Number(data[i].START_TIME_STAMP);
                        var endTimeStamp = new Number(data[i].END_TIME_STAMP);
                        if (isTeacher.indexOf("teacher") != -1) {
                            if (currentTimeStamp < startTimeStamp - (20 * 60)) {
                                intervalTimeArray[i] = startTimeStamp - (20 * 60) - currentTimeStamp;
                                var classTotalTime = endTimeStamp - (startTimeStamp - (20 * 60));
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "notYet");
                                if (data[i].CLASS_TYPE != '课表课') {
                                    document.getElementById(data[i].CLASSIN_CLASS_ID + "delete").innerText = "删除";
                                }
                                if (intervalTimeArray[i] * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(intervalTimeArray[i] * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000);
                                }
                            } else if (currentTimeStamp >= startTimeStamp - (20 * 60) && currentTimeStamp < endTimeStamp) {
                                intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                                document.getElementById(data[i].CLASSIN_CLASS_ID + "delete").innerText = "";
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "hasBegan");
                                var lastTime = endTimeStamp - currentTimeStamp;
                                if (lastTime * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(lastTime * 1000, data[i].CLASSIN_CLASS_ID);
                                }
                            }
                        } else {
                            if (currentTimeStamp < startTimeStamp - (10 * 60)) {
                                intervalTimeArray[i] = startTimeStamp - (10 * 60) - currentTimeStamp;
                                var classTotalTime = endTimeStamp - (startTimeStamp - (10 * 60));
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "notYet");
                                if (intervalTimeArray[i] * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(intervalTimeArray[i] * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000);
                                }
                            } else if (currentTimeStamp >= startTimeStamp - (10 * 60) && currentTimeStamp < endTimeStamp) {
                                intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "hasBegan");
                                var lastTime = endTimeStamp - currentTimeStamp;
                                if (lastTime * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(lastTime * 1000, data[i].CLASSIN_CLASS_ID);
                                }
                            }
                        }
                    }

                }


                //改变课节状态
                function alterStatus(interval, classID, classTotalTime) {
                    setInterval(function flush() {
                        var elementById = document.getElementById(classID + "");
                        var text = elementById.innerText;
                        if (text == "进入教室" && elementById.getAttribute("tag") == "hasBegan") {
                            document.getElementById(classID + "").innerText = "课节已结束";
                            document.getElementById(classID + "delete").innerText = "";
                        } else if (text == "课节未开始") {
                            text = document.getElementById(classID + "").innerText = "进入教室";
                            document.getElementById(classID + "delete").innerText = "";
                            setInterval(function lastFlush() {
                                if (text == "进入教室" && elementById.getAttribute("tag") == "notYet") {
                                    document.getElementById(classID + "").innerText = "课节已结束";
                                    document.getElementById(classID + "delete").innerText = "";
                                }
                            }, classTotalTime);
                        }
                    }, interval);
                }

                //刷新
                function refresh() {
                    jQuery.ajax({
                        type: "GET",
                        url: "${pageContext.request.contextPath}/classinCourseClass/getClassStatus.do",
                        // data:{"id":val},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                        data: "course_id=" + courseId,
                        async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                        cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                        dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                        success: function (data) {
                            handleData(data);
                            // alert("刷新成功了")
                        },
                        error: function () {
                            alert("发生错误~");
                        },
                        complete: function () {
                            //alert("ajax请求完成")
                        }
                    });
                    return intervalTimeArray;
                }

                //进入教室
                function classBegin(classinClassId) {
                    var identity = document.getElementById("isTeacher").innerText;
                    var text = document.getElementById(classinClassId).innerText;
                    if (identity.indexOf("teacher") != -1) {
                        var teacherSelect = document.getElementById('teacher'+classinClassId);
                        var teacherIndex = teacherSelect.selectedIndex;
                        var teacher = teacherSelect.options[teacherIndex].value;
                        var teacherInfo = teacher;
                        var assistPhone = document.getElementById("assistantPhone"+classinClassId).innerText;
                        var teacherPhone = document.getElementById("teacherPhone"+classinClassId).innerText;
                        var currentUserPhone = document.getElementById("currentUserTelephone").innerText;
                        currentUserPhone = currentUserPhone.trim();
                        if (assistPhone.indexOf(currentUserPhone) != -1 || teacherPhone.indexOf(currentUserPhone) != -1) {

                            if (text == "进入教室") {
                                jQuery.ajax({
                                    type: "GET",
                                    url: "${pageContext.request.contextPath}/classinCourseClass/turnIntoClassRoom.do",
                                    data: {
                                        "classId": classinClassId,
                                    },     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                                    async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                                    cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                                    dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                                    success: function (data) {
                                        if (data.condition != "error") {
                                            window.open("https://www.eeo.cn/client/invoke/index.html?" + data.condition);
                                        } else if (data.source == "classin提示您----") {
                                            window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&" + "errno=" + data.errorno + "&error=" + data.error);
                                        } else {
                                            window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&error=" + data.error);
                                        }
                                    },
                                    error: function () {
                                        alert("发生错误123~");
                                    },
                                    complete: function () {
                                        //alert("ajax请求完成")
                                    }
                                });
                            } else if (text == "课节未开始") {
                                alert("课节未开始");
                            } else {
                                alert("课节已结束");
                            }
                        } else if( (assistPhone != "" || assistPhone == "") ){
                            jQuery.ajax({
                                type: "GET",
                                url: "${pageContext.request.contextPath}/classinCourseClass/addClassStudent.do",
                                data: {"classId": classinClassId},
                                async: true,
                                cache: true,
                                dataType: "json",
                                success: function (data) {
                                    if (data.errno != "1" && data.errno != "166") {
                                        alert(data.errno + data.error);
                                    } else {
                                        if (text == "进入教室") {  //getClassScheduleList.do
                                            jQuery.ajax({
                                                type: "GET",
                                                url: "${pageContext.request.contextPath}/classinCourseClass/turnIntoClassRoom.do",
                                                data: {
                                                    "classId": classinClassId,
                                                },     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                                                async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                                                cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                                                dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                                                success: function (data) {
                                                    if (data.condition != "error") {
                                                        window.open("https://www.eeo.cn/client/invoke/index.html?" + data.condition);
                                                    } else if (data.source == "classin提示您----") {
                                                        window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&" + "errno=" + data.errorno + "&error=" + data.error);
                                                    } else {
                                                        window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&error=" + data.error);
                                                    }
                                                },
                                                error: function () {
                                                    alert("发生错误123~");
                                                },
                                                complete: function () {
                                                    //alert("ajax请求完成")
                                                }
                                            });
                                        } else if (text == "课节未开始") {
                                            alert("课节未开始");
                                        } else {
                                            alert("课节已结束");
                                        }
                                    }
                                },
                                error: function () {
                                    alert("系统错误")
                                }
                            })
                        }
                        <%--} else if (assistPhone == ""){--%>
                            <%--jQuery.ajax({--%>
                                <%--type:"GET",--%>
                                <%--url:"${pageContext.request.contextPath}/classinCourseClass/addClassAssitant.do",--%>
                                <%--data:{"classId":classinClassId,"listenClass":"listen"},--%>
                                <%--async:true,--%>
                                <%--cache:true,--%>
                                <%--dataType:"json",--%>
                                <%--success:function (data) {--%>
                                    <%--if (data.errno == "1"){--%>
                                        <%--if (text == "进入教室") {--%>
                                            <%--jQuery.ajax({--%>
                                                <%--type: "GET",--%>
                                                <%--url: "${pageContext.request.contextPath}/classinCourseClass/turnIntoClassRoom.do",--%>
                                                <%--data: {--%>
                                                    <%--"classId": classinClassId,--%>
                                                <%--},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,--%>
                                                <%--async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行--%>
                                                <%--cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true--%>
                                                <%--dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据--%>
                                                <%--success: function (data) {--%>
                                                    <%--if (data.condition != "error") {--%>
                                                        <%--window.location.href="${pageContext.request.contextPath}/classinCourseClass/getClassScheduleList.do";--%>
                                                        <%--window.open("https://www.eeo.cn/client/invoke/index.html?" + data.condition);--%>
                                                    <%--} else if (data.source == "classin提示您----") {--%>
                                                        <%--window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&" + "errno=" + data.errorno + "&error=" + data.error);--%>
                                                    <%--} else {--%>
                                                        <%--window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&error=" + data.error);--%>
                                                    <%--}--%>
                                                <%--},--%>
                                                <%--error: function () {--%>
                                                    <%--alert("发生错误123~");--%>
                                                <%--},--%>
                                                <%--complete: function () {--%>
                                                    <%--//alert("ajax请求完成")--%>
                                                <%--}--%>
                                            <%--});--%>
                                        <%--} else if (text == "课节未开始") {--%>
                                            <%--alert("课节未开始");--%>
                                        <%--} else {--%>
                                            <%--alert("课节已结束");--%>
                                        <%--}--%>
                                    <%--}else {--%>
                                        <%--alert(data.errno+data.error);--%>
                                    <%--}--%>
                                <%--},--%>
                                <%--error:function () {--%>

                                <%--}--%>
                            <%--});--%>
                        <%--}--%>
                    } else{
                        jQuery.ajax({
                            type: "GET",
                            url: "${pageContext.request.contextPath}/classinCourseClass/addCourseStudent.do",
                            data: {"classId": classinClassId},
                            async: true,
                            cache: true,
                            dataType: "json",
                            success: function (data) {
                                if (data.errno != "1" && data.errno != "163") {
                                    alert(data.errno + data.error);
                                } else {
                                    if (text == "进入教室") {
                                        jQuery.ajax({
                                            type: "GET",
                                            url: "${pageContext.request.contextPath}/classinCourseClass/turnIntoClassRoom.do",
                                            data: {
                                                "classId": classinClassId,
                                            },     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                                            async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                                            cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                                            dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                                            success: function (data) {
                                                if (data.condition != "error") {
                                                    window.open("https://www.eeo.cn/client/invoke/index.html?" + data.condition);
                                                } else if (data.source == "classin提示您----") {
                                                    window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&" + "errno=" + data.errorno + "&error=" + data.error);
                                                } else {
                                                    window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source=" + data.source + "&error=" + data.error);
                                                }
                                            },
                                            error: function () {
                                                alert("发生错误123~");
                                            },
                                            complete: function () {
                                                //alert("ajax请求完成")
                                            }
                                        });
                                    } else if (text == "课节未开始") {
                                        alert("课节未开始");
                                    } else {
                                        alert("课节已结束");
                                    }
                                }
                            },
                            error: function () {
                                alert("系统错误"+classinClassId);
                            }
                        })
                    }
                }


                //输出未开始课节
                function deleteClass(classId) {
                    var courseId = document.getElementById("course_id").value;
                    if (confirm("是否删除此节课？")) {
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/deleteClass.do?classId=" + classId + "&bbCourseId=" + courseId;
                    }

                }

                //返回首页
                function goBack() {
                    var courseId = document.getElementById("course_id").value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/goBack.do?course_id=" + courseId
                }

                <%--function editClass(classId) {--%>
                    <%--var course_id = document.getElementById("course_id").value;--%>
                    <%--var teacherSelect = document.getElementById('teacher'+classId);--%>
                    <%--var teacherIndex = teacherSelect.selectedIndex;--%>
                    <%--var teacher = teacherSelect.options[teacherIndex].value;--%>
                    <%--var teacherInfo = teacher;--%>
                    <%--var assistantTeacherSelect = document.getElementById('assistantTeacher'+classId);--%>
                    <%--var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;--%>
                    <%--var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].value;--%>
                    <%--assistantInfo = assistantTeacher;--%>
                    <%--if (assistantTeacher.indexOf("1") != -1) {--%>
                        <%--if (confirm("确认保存更新信息？")) {--%>
                            <%--window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClassTeacher.do?classId=" + classId + "&teacherInfo=" + teacherInfo + "&assistantInfo=" + assistantInfo+"&course_id="+course_id+"&position=1";--%>
                        <%--}--%>
                    <%--} else {--%>
                        <%--if (confirm("确认保存更新信息？")) {--%>
                            <%--window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClassTeacher.do?classId=" + classId + "&teacherInfo=" + teacherInfo+"&course_id="+course_id+"&position=1";--%>
                        <%--}--%>
                    <%--}--%>
                <%--}--%>

                //读取课节原有的老师和助教
                function addAssistant(classId) {
                    var course_id = document.getElementById("course_id").value;
                    var teacherSelect = document.getElementById('teacher' + classId);
                    var teacherIndex = teacherSelect.selectedIndex;
                    var teacher = teacherSelect.options[teacherIndex].value;
                    var teacherInfo = teacher;
                    var assistantTeacherSelect = document.getElementById('assistantTeacher' + classId);
                    var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;
                    var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].value;
                    var assistantInfo = assistantTeacher;

                }

                var teacherIndex111;
                var teacherNum=1;
                function getTeacherOption(id) {
                    var teacherSelect = document.getElementById('teacher'+id);
                    var teacherSelectedIndex = teacherSelect.selectedIndex;
                    //alert(teacherSelectedIndex);
                    if (teacherNum==1){
                        teacherIndex111 = teacherSelectedIndex;
                        //alert(teacherIndex111);
                    }
                    teacherNum++;
                }

                // //获取数据库内助教的option id
                // function getAssistantOption(id) {
                //     var assistantTeacherSelect = document.getElementById('assistantTeacher' + classId);
                //     var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;
                //     if (assistantNum == 1){
                //         assistantIndex111 = assistantTeacherIndex;
                //     }
                //     assistantNum++;
                // }



                //编辑老师
                function editTeacher(classId) {
                    var course_id = document.getElementById("course_id").value;
                    var teacherSelect = document.getElementById('teacher' + classId);
                    var teacherIndex = teacherSelect.selectedIndex;
                    var teacher = teacherSelect.options[teacherIndex].value;
                    var teacherInfo = teacher;
                    // alert(teacherInfo);
                    if (confirm("确认更换授课教师？")) {
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClassTeacher.do?classId=" + classId + "&teacherInfo=" + teacherInfo + "&course_id=" + course_id + "&position=1";
                    }else{
                        var teacherSelect = document.getElementById('teacher' + classId);
                        teacherSelect.selectedIndex = teacherIndex111;
                    }
                }

                //添加助教
                function addAssistant(classId) {
                    var course_id = document.getElementById("course_id").value;
                    var teacherSelect = document.getElementById('teacher' + classId);
                    var teacherIndex = teacherSelect.selectedIndex;
                    var teacher = teacherSelect.options[teacherIndex].value;
                    var teacherInfo = teacher;

                    if (confirm("确认添加助教？")) {
                        var assistantTeacherSelect = document.getElementById('assistantTeacher' + classId);
                        var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;
                        var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].value;
                        var assistantInfo = assistantTeacher;
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClassTeacher.do?classId=" + classId + "&teacherInfo=" + teacherInfo + "&assistantInfo=" + assistantInfo + "&course_id=" + course_id + "&position=1";
                    } else {
                        var assistantTeacherSelect = document.getElementById('assistantTeacher' + classId);
                        assistantTeacherSelect.selectedIndex = -1;
                        //alert(assistantTeacherSelect.selectedIndex);
                    }
                }

            </SCRIPT>
        </bbNG:jsBlock>

        <bbNG:pageHeader instructions="唤醒classin客户端并进入教室,推荐使用Firefox或Chrome浏览器，不建议使用IE浏览器">
            <bbNG:breadcrumbBar environment="COURSE">
                <bbNG:breadcrumb title="ClassIn课堂" href=""></bbNG:breadcrumb>
            </bbNG:breadcrumbBar>
            classin紧急求助，扫一扫
            <img src="/webapps/bb-ClassIn-bb_bb60/images/qrCode.jpg" style="height:60px;width:60px;">
            <bbNG:pageTitleBar title="ClassIn课堂"></bbNG:pageTitleBar>
        </bbNG:pageHeader>
        <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
        <input type="hidden" name="typeName" id="typeName" value="${type}">

        <bbNG:actionControlBar>

            <bbNG:actionControlBar>

                <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                    <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
                        ${tips}<br/>
                        <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
                    </bbNG:form>
                </bbNG:actionPanelButton>
            </bbNG:actionControlBar>
        </bbNG:actionControlBar>

        <%--<%if("student".equals(role)){--%>
        <%--%>--%>
        <%--<h5>学生查看回放，请登录ClassIn客户端，找到对应课程和课节，点击观看回放即可。</h5><br/><br/><br/>--%>
        <%--<img src="/webapps/bb-ClassIn-bb_bb60/images/tip.png">--%>
        <%--<%--%>
        <%--} %>     --%>
        <span hidden id="isTeacher">
			<c:if test="${isTeacher==true}">
                teacher
            </c:if>
			<c:if test="${isTeacher==false}">
                student
            </c:if>
		</span>
        <span hidden id="currentUserTelephone">
                ${currentUserTelephone}
        </span>
        <bbNG:inventoryList collection="${classList}" objectVar="classInfo" className="java.util.Map" url="">
            <bbNG:listElement label="序号" name="classinClassName" isRowHeader="true">
                ${classInfo.id}
            </bbNG:listElement>

            <bbNG:listElement label="创建类型" name="view_part">
                ${classInfo.CLASS_TYPE}
            </bbNG:listElement>

            <bbNG:listElement label="课节名称" name="replay">
                ${classInfo.CLASS_NAME}
            </bbNG:listElement>
            <bbNG:listElement label="开课日期" name="download">
                ${classInfo.STARTDATE}
            </bbNG:listElement>
            <bbNG:listElement label="开课时间" name="start">
                ${classInfo.START_TIME}
            </bbNG:listElement>
            <bbNG:listElement label="课节时长" name="total">
                ${classInfo.CLASS_TOTAL_TIME}
            </bbNG:listElement>
            <bbNG:listElement label="授课教师" name="teacher">
                <c:if test="${isTeacher==true}">
                    <select id="teacher${classInfo.CLASSIN_CLASS_ID}"
                            onchange="editTeacher(${classInfo.CLASSIN_CLASS_ID})" style="width: 100%">
                        <option value="${classInfo.TEACHER_NAME},${classInfo.TEACHER_PHONE}" style="width: 50%"> ${classInfo.TEACHER_NAME}&nbsp;&nbsp;&nbsp;${classInfo.TEACHER_PHONE}</option>
                        <option id="teacherPhone${classInfo.CLASSIN_CLASS_ID}" hidden style="width: 50%">${classInfo.TEACHER_PHONE}</option>
                        <c:forEach items="${teachers}" var="bbUser">
                            <c:if test="${bbUser.phone!='   (请绑定手机号)' && (classInfo.TEACHER_PHONE != bbUser.phone)}">
                                <option value="${bbUser.userName},${bbUser.phone}" style="width: 50%">${bbUser.userName}&nbsp;&nbsp;&nbsp;${bbUser.phone}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </c:if>
                <c:if test="${isTeacher!=true}">
                    <span>${classInfo.TEACHER_NAME}</span>
                </c:if>
            </bbNG:listElement>
            <bbNG:listElement label="助教" name="assistant">
                <c:if test="${isTeacher==true}">
                    <select id="assistantTeacher${classInfo.CLASSIN_CLASS_ID}"
                            onchange="addAssistant(${classInfo.CLASSIN_CLASS_ID})" style="width: 100%">
                        <option value="${classInfo.ASSISTANT_NAME},${classInfo.ASSISTANT_PHONE}">${classInfo.ASSISTANT_NAME}&nbsp;&nbsp;&nbsp;${classInfo.ASSISTANT_PHONE}</option>
                        <option id="assistantPhone${classInfo.CLASSIN_CLASS_ID}"
                                hidden>${classInfo.ASSISTANT_PHONE}</option>
                        <c:forEach items="${assistantTeachers}" var="bbUser">
                            <c:if test="${bbUser.phone!='   (请绑定手机号)' && classInfo.ASSISTANT_PHONE != bbUser.phone}">
                                <option value="${bbUser.userName},${bbUser.phone}">${bbUser.userName}&nbsp;&nbsp;&nbsp;${bbUser.phone}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                    <%--<script type="text/javascript">--%>
                        <%--var assistantTeacherSelect = document.getElementById('assistantTeacher'+${classInfo.CLASSIN_CLASS_ID});--%>
                        <%--var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;--%>
                        <%--var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].value;--%>
                        <%--if (assistantTeacher.indexOf("1") != -1){--%>
                            <%--//alert(assistantTeacher)--%>
                            <%--assistantTeacherSelect.setAttribute("disabled","disabled");--%>
                        <%--}--%>


                    <%--</script>--%>
                </c:if>
                <c:if test="${isTeacher!=true}">
                    <span>${classInfo.ASSISTANT_NAME}</span>
                </c:if>
            </bbNG:listElement>
            <bbNG:listElement label="操作" name="action">
                <c:if test="${isTeacher==true}">
                    <%--<span id="${classInfo.CLASSIN_CLASS_ID}edit" onclick="editClass(${classInfo.CLASSIN_CLASS_ID})"--%>
                          <%--style="color: #210a04;cursor: pointer">保存</span>--%>
                    <c:if test="${isTeacher==true}">
                    <span id="${classInfo.CLASSIN_CLASS_ID}delete" onclick="deleteClass(${classInfo.CLASSIN_CLASS_ID})"
                          style="color: #f44b1c;cursor: pointer"></span>
                    </c:if>
                </c:if>
                <button id="${classInfo.CLASSIN_CLASS_ID}" onclick="classBegin(${classInfo.CLASSIN_CLASS_ID})" tag="hasBegan"
                        style="cursor: pointer;background-color:#dadada;border-radius:3px;border-style:none;line-height:30px;width:80px;">
                    进入教室
                </button>

                <%--<c:if test="${classInfo.TYPE==非课表课}">--%>

                <%--</c:if>--%>
            </bbNG:listElement>
        </bbNG:inventoryList>
        <bbNG:button type="PageLevel" label="返回" url="javascript:goBack()"/>
    </bbData:context>
</bbNG:learningSystemPage>