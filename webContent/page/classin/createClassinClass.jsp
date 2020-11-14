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
                 blackboard.platform.session.BbSession,
                 blackboard.platform.BbServiceManager,
                 blackboard.base.*,
                 java.util.*,
                 blackboard.util.*,
                 blackboard.platform.intl.JsResource,
                 blackboard.data.course.*,
                 blackboard.data.user.*,
                 blackboard.persist.course.*,
                 blackboard.persist.Id,
                 blackboard.platform.authentication.SessionManager,
                 blackboard.platform.session.BbSession" %>
<%@ page import="com.blackboard.classin.util.SystemUtil" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.time.ZonedDateTime" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
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
                document.getElementById("listContainer_itemcount").style.display="none";
                var pageNow=document.getElementById("pageNumber").innerText;
                var intervalTimeArray = new Array();
                var classStatus = [];
                // alert(pageNow);
                refresh(pageNow);
                initSeleted();
                if(Number(document.getElementById("pageNumber").innerText)==Number(document.getElementById("pageNumTotal").innerText)){
                    document.getElementById("nextBtn").disabled=true;
                    document.getElementById("lasttBtn").disabled=true;
                }

                //让全部select标签选第一项
                function initSeleted() {
                    var selects = document.getElementsByTagName("select");
                    for (var i = 0; i < selects.length; i++) {
                        selects[i].selectedIndex = 0;
                    }
                }

                //更新手机号
                function updatePhone() {
                    if (confirm("是否更新当前手机号？")) {
                        var type = document.getElementById("typeName").value;
                        var course_id = document.getElementById("course_id").value;
                        window.location.href = "${pageContext.request.contextPath}/userPhone/toUpdatePhone.do?course_id=" + courseId + "&type=" + type;
                    }
                }

                //原有的创建classin课节方法
                function createClassinClass() {
                    var course_id = document.getElementById("course_id").value;
                    var typeName = document.getElementById("typeName");
                    if (typeName != null) {
                        typeName = typeName.value;
                    }
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/create.do?course_id=" + course_id + "&type=" + typeName;
                }

                //刷新当前页
                function refreshCurrrentPage() {
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+courseId+"&page="+pageNow;
                    refresh(pageNow);
                }

                //获取历史回放
                function getReplayURLs() {
                    var course_id = document.getElementById("course_id").value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getRepalyList.do?course_id=" + course_id;
                }

                //创建classin课节
                function advanceSet() {
                    var type = document.getElementById("typeName").value;
                    var course_id = document.getElementById("course_id").value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClass.do?course_id=" + course_id + "&type=" + type;
                }

                //显示教学数据
                function getEducationData() {
                    var type = document.getElementById("typeName").value;
                    var course_id = document.getElementById("course_id").value;
                    jQuery.ajax({
                        type:"GET",
                        url:"${pageContext.request.contextPath}/classinCourseClass/getData.do",
                        data:{courseId:course_id},
                        async:true,
                        cache:true,
                        dataType:"json",
                        success:function (data) {
                            if (data.data == 1){
                                window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getCourseClassData.do?courseId=" + course_id;
                            } else {
                                alert("暂无教学数据");
                            }
                        },
                        error:function () {
                            alert("网络错误");
                        }
                    });

                }

                //解绑手机号
                function disbindPhone() {
                    var telephone = document.getElementById("currentUserTelephone").innerText;
                    var userName = document.getElementById("userName").innerText;
                    if (confirm("手机号" + telephone + "是否解绑账号" + userName)) {
                        var type = document.getElementById("typeName").value;
                        var identity = document.getElementById("isTeacher").innerText;
                        var course_id = document.getElementById("course_id").value;
                        if (identity == "student") {
                            window.location.href = "${pageContext.request.contextPath}/userPhone/disbindPhone.do?course_id=" + course_id + "&type=" + type;
                        } else {
                            jQuery.ajax({
                                type: "GET",
                                url: "${pageContext.request.contextPath}/classinCourseClass/getClassStatus.do",
                                data: {course_id: course_id},
                                async: true,
                                cache: true,
                                dataType: "json",
                                success: function (data) {
                                    var flag = 1;
                                    //  alert(data.data);
                                    for (var i = 0; i < data.data.length; i++) {
                                        //  alert(data.data[i].hasFinished);
                                        if (data.data[i].hasFinished == "yes") {
                                            window.location.href = "${pageContext.request.contextPath}/userPhone/disbindPhone.do?course_id=" + course_id + "&type=" + type;
                                            flag = 0;
                                            // alert("解绑解绑")
                                        }
                                    }
                                    if (flag == 1) {
                                        alert("您尚有未结束的课节，不支持解绑手机号。如果您需要更新手机号，请点击更新手机号。");
                                    }
                                },
                                error: function () {
                                    alert("系统错误");
                                }
                            });
                        }
                    }
                }


                //处理课表信息集合
                function handleData(data) {
                    data = data.data;
                    for (var i = 0; i < data.length; i++) {
                        // alert(i);
                        var currentTimeStamp = Date.parse(new Date()) / 1000;
                        var isTeacher = document.getElementById("isTeacher").innerText + "";
                        var isAdministrator = document.getElementById("isAdministrator").innerText;
                        currentTimeStamp = new Number(currentTimeStamp);
                        var startTimeStamp = new Number(data[i].START_TIME_STAMP);
                        var endTimeStamp = new Number(data[i].END_TIME_STAMP);

                        if (isTeacher.indexOf("teacher") != -1 || isAdministrator.indexOf("administrator") != -1) {
                            if (currentTimeStamp < startTimeStamp - (20 * 60)) {
                                intervalTimeArray[i] = startTimeStamp - (20 * 60) - currentTimeStamp;
                                var classTotalTime = endTimeStamp - (startTimeStamp - (20 * 60));
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                                classStatus[i] = 0;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "notYet");
                                // alert(data[i].CLASS_TYPE);
                                if (data[i].CLASS_TYPE != '课表课') {
                                    document.getElementById(data[i].CLASSIN_CLASS_ID + "delete").innerText = "删除";
                                }
                                if (intervalTimeArray[i] * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(intervalTimeArray[i] * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                                }
                            } else if (currentTimeStamp >= startTimeStamp - (20 * 60) && currentTimeStamp < endTimeStamp) {
                                intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                                classStatus[i] = 1;
                                document.getElementById(data[i].CLASSIN_CLASS_ID + "delete").innerText = "";
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "hasBegan");
                                var lastTime = endTimeStamp - currentTimeStamp;
                                if (lastTime * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(lastTime * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                                }
                            }
                        } else {
                            if (currentTimeStamp < startTimeStamp - (10 * 60)) {
                                intervalTimeArray[i] = startTimeStamp - (10 * 60) - currentTimeStamp;
                                var classTotalTime = endTimeStamp - (startTimeStamp - (10 * 60));
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                                classStatus[i] = 0;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "notYet");
                                if (intervalTimeArray[i] * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(intervalTimeArray[i] * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                                }
                            } else if (currentTimeStamp >= startTimeStamp - (10 * 60) && currentTimeStamp < endTimeStamp) {
                                intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                                classStatus[i] = 1;
                                document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "hasBegan");
                                var lastTime = endTimeStamp - currentTimeStamp;
                                if (lastTime * 1000 < 24 * 60 * 60 * 1000) {
                                    alterStatus(lastTime * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                                }
                            }
                        }
                    }
                }


                //处理课表信息集合
                // function handleData(data) {
                //     data = data.data;
                //     //alert(data.length);
                //     for (var i = 0; i < data.length; i++) {
                //         // alert(i);
                //         var currentTimeStamp = Date.parse(new Date()) / 1000;
                //         var isTeacher = document.getElementById("isTeacher").innerText + "";
                //         var isAdministrator = document.getElementById("isAdministrator").innerText;
                //         currentTimeStamp = new Number(currentTimeStamp);
                //         var startTimeStamp = new Number(data[i].START_TIME_STAMP);
                //         var endTimeStamp = new Number(data[i].END_TIME_STAMP);
                //
                //         if (isTeacher.indexOf("teacher") != -1 || isAdministrator.indexOf("administrator") != -1) {
                //             if (currentTimeStamp < startTimeStamp - (20 * 60)) {
                //                 intervalTimeArray[i] = startTimeStamp - (20 * 60) - currentTimeStamp;
                //                 var classTotalTime = endTimeStamp - (startTimeStamp - (20 * 60));
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                //                 classStatus[i] = 0;
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "notYet");
                //                 // alert(data[i].CLASS_TYPE);
                //                 if (data[i].CLASS_TYPE != '课表课') {
                //                     document.getElementById(data[i].CLASSIN_CLASS_ID + "delete").innerText = "删除";
                //                 }
                //                 if (intervalTimeArray[i] * 1000 < 24 * 60 * 60 * 1000) {
                //                     alterStatus(intervalTimeArray[i] * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                //                 }
                //             } else if (currentTimeStamp >= startTimeStamp - (20 * 60) && currentTimeStamp < endTimeStamp) {
                //                 intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                //                 classStatus[i] = 1;
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID + "delete").innerText = "";
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "hasBegan");
                //                 var lastTime = endTimeStamp - currentTimeStamp;
                //                 if (lastTime * 1000 < 24 * 60 * 60 * 1000) {
                //                     alterStatus(lastTime * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                //                 }
                //             }
                //         } else {
                //             if (currentTimeStamp < startTimeStamp - (10 * 60)) {
                //                 intervalTimeArray[i] = startTimeStamp - (10 * 60) - currentTimeStamp;
                //                 var classTotalTime = endTimeStamp - (startTimeStamp - (10 * 60));
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                //                 classStatus[i] = 0;
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "notYet");
                //                 if (intervalTimeArray[i] * 1000 < 24 * 60 * 60 * 1000) {
                //                     alterStatus(intervalTimeArray[i] * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                //                 }
                //             } else if (currentTimeStamp >= startTimeStamp - (10 * 60) && currentTimeStamp < endTimeStamp) {
                //                 intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                //                 classStatus[i] = 1;
                //                 document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag", "hasBegan");
                //                 var lastTime = endTimeStamp - currentTimeStamp;
                //                 if (lastTime * 1000 < 24 * 60 * 60 * 1000) {
                //                     alterStatus(lastTime * 1000, data[i].CLASSIN_CLASS_ID, classTotalTime * 1000, classStatus[i]);
                //                 }
                //             }
                //         }
                //     }
                // }


                //改变课节状态
                function alterStatus(interval, classID, classTotalTime, status) {
                    setInterval(function flush() {
                        var elementById = document.getElementById(classID + "");
                        var text = elementById.innerText;
                        if (status == 1 && elementById.getAttribute("tag") == "hasBegan") {
                            document.getElementById(classID + "").innerText = "课节已结束";
                            document.getElementById(classID + "delete").innerText = "";
                        } else if (status == 0) {
                            text = document.getElementById(classID + "").innerText = "进入教室";
                            document.getElementById(classID + "delete").innerText = "";
                            setInterval(function lastFlush() {
                                if (status == 1 && elementById.getAttribute("tag") == "notYet") {
                                    document.getElementById(classID + "").innerText = "课节已结束";
                                    document.getElementById(classID + "delete").innerText = "";
                                }
                            }, classTotalTime);
                        }
                    }, interval);
                }

                //刷新
                function refresh(page) {
                    jQuery.ajax({
                        type: "GET",
                        url: "${pageContext.request.contextPath}/classinCourseClass/getClassStatus.do",
                        // data:{"id":val},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                        data: "course_id=" + courseId+"&page="+page,
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
                function classBegin(classinClassId, id) {
                    id--;
                    //  alert(id);
                    // alert(classStatus.length);
                    var identity = document.getElementById("isTeacher").innerText;
                    var text = document.getElementById(classinClassId).innerText;
                    if (identity.indexOf("teacher") != -1) {
                        var assistantTeacherSelect = document.getElementById('assistantTeacher' + classinClassId);
                        var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;
                        var assistantContent = assistantTeacherSelect.options[assistantTeacherIndex].text;
                        var teacherSelect = document.getElementById('teacher' + classinClassId);
                        var teacherIndex = teacherSelect.selectedIndex;
                        var teacher = teacherSelect.options[teacherIndex].value;
                        var teacherInfo = teacher;
                        var assistPhone = document.getElementById("assistantPhone" + classinClassId).innerText;
                        var teacherPhone = document.getElementById("teacherPhone" + classinClassId).innerText;
                        var currentUserPhone = document.getElementById("currentUserTelephone").innerText;
                        currentUserPhone = currentUserPhone.trim();
                        if (assistPhone.indexOf(currentUserPhone) != -1 || teacherPhone.indexOf(currentUserPhone) != -1) {
                            if (classStatus[id] == 1) {
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
                                            window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=" + data.error);
                                        } else {
                                            window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=" + data.error);
                                        }
                                    },
                                    error: function () {
                                        alert("发生错误123~");
                                    },
                                    complete: function () {
                                        //alert("ajax请求完成")
                                    }
                                });
                            } else if (classStatus[id] == 0) {
                                alert("课节未开始");
                            } else {
                                alert("课节已结束");
                            }
                        } else if (assistPhone != "" || assistPhone == "") {
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
                                        if (classStatus[id] == 1) {  //getClassScheduleList.do
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
                                                        window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=" + data.error);
                                                    } else {
                                                        window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=" + data.error);
                                                    }
                                                },
                                                error: function () {
                                                    alert("发生错误123~");
                                                },
                                                complete: function () {
                                                    //alert("ajax请求完成")
                                                }
                                            });
                                        } else if (classStatus[id] == 0) {
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
                    } else {
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
                                    if (classStatus[id] == 1) {
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
                                                    window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=" + data.error);
                                                } else {
                                                    window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=" + data.error);
                                                }
                                            },
                                            error: function () {
                                                alert("发生错误123~");
                                            },
                                            complete: function () {
                                                //alert("ajax请求完成")
                                            }
                                        });
                                    } else if (classStatus[id] == 0) {
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
                }


                //删除课节
                function deleteClass(classId) {
                    var courseId = document.getElementById("course_id").value;
                    if (confirm("是否删除此节课？")) {
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/deleteClass.do?classId=" + classId + "&bbCourseId=" + courseId+"&page="+pageNow;
                    }

                }

                //获取数据库内授课老师的option id
                var teacherIndex111;
                var teacherNum = 1;

                function getTeacherOption(id) {
                    var teacherSelect = document.getElementById('teacher' + id);
                    var teacherSelectedIndex = teacherSelect.selectedIndex;
                    //alert(teacherSelectedIndex);
                    if (teacherNum == 1) {
                        teacherIndex111 = teacherSelectedIndex;
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
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClassTeacher.do?classId=" + classId + "&teacherInfo=" + teacherInfo + "&course_id=" + course_id + "&position=1" + "&page="+pageNow;
                    } else {
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
                        window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClassTeacher.do?classId=" + classId + "&teacherInfo=" + teacherInfo + "&assistantInfo=" + assistantInfo + "&course_id=" + course_id + "&position=1" + "&page="+pageNow;;
                    } else {
                        var assistantTeacherSelect = document.getElementById('assistantTeacher' + classId);
                        assistantTeacherSelect.selectedIndex = -1;
                        //alert(assistantTeacherSelect.selectedIndex);
                    }
                }

                //获取华西云课表数据
                function getHuaXiData() {
                    window.open("${pageContext.request.contextPath}/classinCourseClass/error.do?error=123");
                    var elementsByTagName = document.getElementsByTagName("label");
                    for (var i = 0; i < elementsByTagName.length; i++) {
                        var id = elementsByTagName[i].id;
                    }
                }


                function getCreateStatus() {
                    var courseId = document.getElementById("course_id").value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getFailureData.do?course_id=" + courseId
                }

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
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+courseId+"&page="+page;
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
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+courseId+"&page="+page;
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
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+courseId+"&page="+page;
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
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+courseId+"&page="+page;
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
                    window.location.href="${pageContext.request.contextPath}/classinCourseClass/getHomeClassList.do?course_id="+courseId+"&page="+currentPage;
                }


                //返回首页
                function goBack() {
                    var courseId = document.getElementsByName("course_id")[0].value;
                    window.location.href = "${pageContext.request.contextPath}/classinCourseClass/goBack.do?course_id=" + courseId
                }

                <%--function editClass(id,classId) {--%>
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

                var lableList = document.getElementsByTagName("label");
                for (var i = 0; i < lableList.length; i++) {
                    if (lableList[i].id.indexOf("startTime") != -1 || lableList[i].id.indexOf("startDate") != -1) {
                        var lableListValue = lableList[i].innerText;
                        lableListValue = lableListValue * 1000;
                        console.log(lableListValue);
                        var timeArr = startTime2(lableListValue);
                        console.log(timeArr);
                        if (i % 2 == 0) {
                            lableList[i].innerText = timeArr[0];
                        } else {
                            lableList[i].innerText = timeArr[1];
                        }
                    }
                }

                function getLocalTime(i, len) {
                    //参数i为时区值数字，比如北京为东八区则输进8,西5输入-5
                    if (typeof i !== 'number') return;
                    var d = new Date();
                    //本地时间与GMT时间的时间偏移差
                    var offset = d.getTimezoneOffset() * 60000;
                    //得到现在的格林尼治时间
                    var utcTime = len + offset;
                    return new Date(utcTime + 3600000 * i);
                }

                //时间戳按照时区转换成时间
                function startTime2(dTimeStamp) {
                    var datey = new Date();
                    var doffset = datey.getTimezoneOffset() / 60;
                    var i = doffset >= 0 ? -doffset : Math.abs(doffset);
                    var localTime = String(getLocalTime(i, dTimeStamp));
                    var arr = localTime.split(" ");
                    switch (arr[1]) {
                        case "Jan":
                            arr[1] = "01";
                            break;
                        case "Feb":
                            arr[1] = "02";
                            break;
                        case "Mar":
                            arr[1] = "03";
                            break;
                        case "Apr":
                            arr[1] = "04";
                            break;
                        case "May":
                            arr[1] = "05";
                            break;
                        case "Jun":
                            arr[1] = "06";
                            break;
                        case "Jul":
                            arr[1] = "07";
                            break;
                        case "Aug":
                            arr[1] = "08";
                            break;
                        case "Sep":
                            arr[1] = "09";
                            break;
                        case "Oct":
                            arr[1] = "10";
                            break;
                        case "Nov":
                            arr[1] = "11";
                            break;
                        case "Dec":
                            arr[1] = "12";
                            break;
                    }
                    var arr1 = arr[4].split(":");
                    var startTime = arr[4] = arr1[0] + ":" + arr1[1];
                    var startDate = arr[3] + "-" + arr[1] + "-" + arr[2]
                    var time = [startDate, startTime];
                    return time;
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

        <bbNG:actionControlBar>
            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                <c:if test="${type != 'meetingroom'}">
                    <bbNG:actionButton url="javaScript:refreshCurrrentPage();" title="刷新当前页课表" primary="true"/>
                    <bbNG:actionButton url="javaScript:getReplayURLs();" title="获取历史回放列表" primary="true"/>
                </c:if>
                <c:if test="${type == 'meetingroom'}">
                    <bbNG:actionButton url="javaScript:createClassinClass();" title="创建在线研讨室" primary="true"/>
                </c:if>
                <bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="更新手机号"/>
                <bbNG:actionButton primary="true" url="javaScript:disbindPhone();" title="解绑手机号"/>
                <%--<bbNG:actionButton primary="true" url="javaScript:getHuaXiData();" title="测试读取数据"/>--%>
                <%--&lt;%&ndash;<c:if test="${isTeacher==true}">&ndash;%&gt;--%>
                <%--<bbNG:actionButton primary="true" url="javaScript:getCreateStatus();" title="查看创建课节失败数据"/>--%>
                <%--</c:if>--%>
                <%--<c:if test="${isTeacher==true || isAdministrator == true}">--%>
                    <bbNG:actionButton primary="true" url="javaScript:advanceSet();" title="创建classin课堂"/>
                <%--</c:if>--%>
                <%--<c:if test="${isTeacher==true || isAdministrator == true}">--%>
                    <bbNG:actionButton primary="true" url="javaScript:getEducationData();" title="教学数据"/>
                <%--</c:if>--%>
                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
                    <span style="color: #ac1d2a">${tips}</span><br/>
                    <c:if test="${type != 'meetingroom'}">
                        提示：您将创建classin在线课堂，默认生成回放
                    </c:if>
                    <c:if test="${type == 'meetingroom'}">
                        提示：您将创建classin在线研讨室，默认开启直播
                    </c:if>

                    <!-- <input type="radio" name="replayFlag" value="1" checked>是
                    <input type="radio" name="replayFlag" value="0">否 -->
                    <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
                    <input type="hidden" name="typeName" id="typeName" value="${type}">

                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>
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

        <span hidden id="isAdministrator">
                <c:if test="${isAdministrator==true}">
                    administrator
                </c:if>
        </span>

        <span hidden id="userName">
                ${userName}
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
                <label id="startDate${classInfo.id}">${classInfo.START_TIME_STAMP}</label>
            </bbNG:listElement>
            <bbNG:listElement label="开课时间" name="start">
                <label id="startTime${classInfo.id}">${classInfo.START_TIME_STAMP}</label>
            </bbNG:listElement>
            <bbNG:listElement label="课节时长" name="total">
                ${classInfo.CLASS_TOTAL_TIME}
            </bbNG:listElement>
            <bbNG:listElement label="授课教师" name="teacher">
                <c:if test="${isTeacher==true || isAdministrator == true}">
                    <select id="teacher${classInfo.CLASSIN_CLASS_ID}"
                            onclick="getTeacherOption(${classInfo.CLASSIN_CLASS_ID})"
                            onchange="editTeacher(${classInfo.CLASSIN_CLASS_ID})" style="width: 100%">
                        <option value="${classInfo.TEACHER_NAME},${classInfo.TEACHER_PHONE}">${classInfo.TEACHER_NAME}&nbsp;&nbsp;&nbsp;${classInfo.TEACHER_PHONE}</option>
                        <option id="teacherPhone${classInfo.CLASSIN_CLASS_ID}"
                                hidden>${classInfo.TEACHER_PHONE}</option>
                        <c:forEach items="${teachers}" var="bbUser">
                            <c:if test="${bbUser.phone!='   (请绑定手机号)' && (classInfo.TEACHER_PHONE != bbUser.phone)}">
                                <option value="${bbUser.userName},${bbUser.phone}"
                                        style="width: 50%">${bbUser.getUserName()}&nbsp;&nbsp;&nbsp;${bbUser.phone}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </c:if>
                <c:if test="${isTeacher!=true && isAdministrator!=true}">
                    <span>${classInfo.TEACHER_NAME}</span>
                </c:if>
            </bbNG:listElement>
            <bbNG:listElement label="助教" name="assistant">
                <c:if test="${isTeacher==true || isAdministrator == true}">
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
                    <%--var assistantTeacherSelect = document.getElementById('assistantTeacher' +${classInfo.CLASSIN_CLASS_ID});--%>
                    <%--var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;--%>
                    <%--var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].value;--%>
                    <%--if (assistantTeacher.indexOf("1") != -1) {--%>
                    <%--//alert(assistantTeacher)--%>
                    <%--assistantTeacherSelect.setAttribute("disabled", "disabled");--%>
                    <%--}--%>
                    <%--</script>--%>
                </c:if>
                <c:if test="${isTeacher!=true && isAdministrator != true}">
                    <span>${classInfo.ASSISTANT_NAME}</span>
                </c:if>
            </bbNG:listElement>
            <bbNG:listElement label="操作" name="action">
                <%--<span id="${classInfo.CLASSIN_CLASS_ID}edit" onclick="editClass(${classInfo.CLASSIN_CLASS_ID})"--%>
                <%--style="color: #210a04;cursor: pointer">保存</span>--%>
                <%--<c:if test="${isTeacher==true || isAdministrator == true}">
                    <c:if test="${classInfo.START_TIME_STAMP-(20*60) < currentTimeStamp}">
                        <button id="${classInfo.CLASSIN_CLASS_ID}"
                                onclick="classBegin(${classInfo.CLASSIN_CLASS_ID},${classInfo.id})" tag="hasBegan"
                                style="cursor: pointer;background-color:#dadada;border-radius:3px;border-style:none;line-height:30px;width:80px;">
                            进入教室
                        </button>
                    </c:if>
                    <c:if test="${classInfo.START_TIME_STAMP-(20*60) > currentTimeStamp}">
                        <button id="${classInfo.CLASSIN_CLASS_ID}"
                                onclick="classBegin(${classInfo.CLASSIN_CLASS_ID},${classInfo.id})" tag="hasBegan"
                                style="cursor: pointer;background-color:#dadada;border-radius:3px;border-style:none;line-height:30px;width:80px;">
                            课节未开始
                        </button>
                    </c:if>
                </c:if>

                <c:if test="${isTeacher==false && isAdministrator == false}">
                    <c:if test="${classInfo.START_TIME_STAMP-(20*60) < currentTimeStamp}">
                        <button id="${classInfo.CLASSIN_CLASS_ID}"
                                onclick="classBegin(${classInfo.CLASSIN_CLASS_ID},${classInfo.id})" tag="hasBegan"
                                style="cursor: pointer;background-color:#dadada;border-radius:3px;border-style:none;line-height:30px;width:80px;">
                            进入教室
                        </button>
                    </c:if>
                    <c:if test="${classInfo.START_TIME_STAMP-(20*60) > currentTimeStamp}">
                        <button id="${classInfo.CLASSIN_CLASS_ID}"
                                onclick="classBegin(${classInfo.CLASSIN_CLASS_ID},${classInfo.id})" tag="hasBegan"
                                style="cursor: pointer;background-color:#dadada;border-radius:3px;border-style:none;line-height:30px;width:80px;">
                            课节未开始
                        </button>
                    </c:if>
                </c:if>--%>

                <button id="${classInfo.CLASSIN_CLASS_ID}"
                        onclick="classBegin(${classInfo.CLASSIN_CLASS_ID},${classInfo.id})" tag="hasBegan"
                        style="cursor: pointer;background-color:#dadada;border-radius:3px;border-style:none;line-height:30px;width:80px;">
                    进入教室
                </button>
                <c:if test="${isTeacher==true || isAdministrator == true}">
                    <span id="${classInfo.CLASSIN_CLASS_ID}delete" onclick="deleteClass(${classInfo.CLASSIN_CLASS_ID})"
                          style="color: #f44b1c;cursor: pointer"></span>
                </c:if>
                <%--<span id="${classInfo.CLASSIN_CLASS_ID}" onclick="classBegin(${classInfo.CLASSIN_CLASS_ID})"--%>
                <%--style="cursor: pointer"></span>--%>

                <%--<c:if test="${classInfo.TYPE==非课表课}">--%>

                <%--</c:if>--%>
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