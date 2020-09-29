<%@ include file="../../webapis/ui/doctype.jspf"%>
<%@ page contentType="text/html; charset=UTF8"%>
<%@ taglib uri="/bbUI" prefix="bbUI"%>
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib uri="/bbData" prefix="bbData"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
                 blackboard.platform.session.BbSession"%>
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
               // getClassList();
			   refresh();

			   //解绑手机号
               function disbindPhone(){
                   var type = document.getElementById("typeName").value;
                   var course_id = document.getElementById("course_id").value;
                   window.location.href = "${pageContext.request.contextPath}/userPhone/disbindPhone.do?course_id="+course_id+"&type="+type;
               }

               //更新手机号
               function updatePhone(){
                   var type = document.getElementById("typeName").value;
                   var course_id = document.getElementById("course_id").value;
                   window.location.href = "${pageContext.request.contextPath}/userPhone/toUpdatePhone.do?course_id="+course_id+"&type="+type;
               }

            	//原有的创建classin课节方法
               function createClassinClass(){
                   var course_id = document.getElementById("course_id").value;
                   var typeName = document.getElementById("typeName");
                   if(typeName != null){
                       typeName = typeName.value;
                   }
                   window.location.href = "${pageContext.request.contextPath}/classinCourseClass/create.do?course_id="+course_id+"&type="+typeName;
               }

               //获取课节列表
               function getClassList() {
                   var course_id = document.getElementById("course_id").value;
                   window.location.href="${pageContext.request.contextPath}/classinCourseClass/getClassScheduleList.do?course_id="+course_id;
               }

               //获取历史回放
               function getReplayURLs(){
                   var course_id = document.getElementById("course_id").value;
                   window.location.href = "${pageContext.request.contextPath}/classinCourseClass/getRepalyList.do?course_id="+course_id;
               }

               //创建classin课节
               function advanceSet() {
                   var type = document.getElementById("typeName").value;
                   var course_id = document.getElementById("course_id").value;
                   window.location.href = "${pageContext.request.contextPath}/classinCourseClass/editClass.do?course_id="+course_id+"&type="+type;
               }

			   //处理课表信息集合
			   function handleData(data) {
                   for (var i = 0;i < data.length;i++) {
                       var currentTimeStamp = Date.parse(new Date()) / 1000;
                       var isTeacher = document.getElementById("isTeacher").innerText+"";
                       currentTimeStamp = new Number(currentTimeStamp);
                       var startTimeStamp = new Number(data[i].START_TIME_STAMP);
                       var endTimeStamp = new Number(data[i].END_TIME_STAMP);
                       if (isTeacher.indexOf("teacher") != -1){
                           if (currentTimeStamp < startTimeStamp-(20*60)) {
                               intervalTimeArray[i] = startTimeStamp-(20*60) - currentTimeStamp;
                               var classTotalTime  = endTimeStamp - (startTimeStamp-(20*60));
                               document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                               document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag","notYet");
                               if (data[i].CLASS_TYPE == '非课表课'){
                                   document.getElementById(data[i].CLASSIN_CLASS_ID+"delete").innerText = "删除";
							   }
                               if (intervalTimeArray[i]*1000 < 24 * 60 * 60 * 1000){
                                   alterStatus(intervalTimeArray[i]*1000,data[i].CLASSIN_CLASS_ID,classTotalTime*1000);
                               }
                           } else if (currentTimeStamp >= startTimeStamp-(20*60) && currentTimeStamp < endTimeStamp) {
                               intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                               document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                               document.getElementById(data[i].CLASSIN_CLASS_ID+"delete").innerText = "";
                               document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag","hasBegan");
                               var lastTime = endTimeStamp-currentTimeStamp;
                               if (lastTime * 1000 < 24 * 60 * 60 * 1000){
                                   alterStatus(lastTime * 1000,data[i].CLASSIN_CLASS_ID);
                               }
                           }
					   } else {
                           if (currentTimeStamp < startTimeStamp-(10*60)) {
                               intervalTimeArray[i] = startTimeStamp-(10*60) - currentTimeStamp;
                               var classTotalTime  = endTimeStamp - (startTimeStamp-(10*60));
                               document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "课节未开始";
                               document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag","notYet");
                               if (intervalTimeArray[i]*1000 < 24 * 60 * 60 * 1000){
                                   alterStatus(intervalTimeArray[i]*1000,data[i].CLASSIN_CLASS_ID,classTotalTime*1000);
                               }
                           } else if (currentTimeStamp >= startTimeStamp-(10*60) && currentTimeStamp < endTimeStamp) {
                               intervalTimeArray[i] = endTimeStamp - currentTimeStamp;
                               document.getElementById(data[i].CLASSIN_CLASS_ID).innerText = "进入教室";
                               document.getElementById(data[i].CLASSIN_CLASS_ID+"delete").innerText = "";
                               document.getElementById(data[i].CLASSIN_CLASS_ID).setAttribute("tag","hasBegan");
                               var lastTime = endTimeStamp-currentTimeStamp;
                               if (lastTime * 1000 < 24 * 60 * 60 * 1000){
                                   alterStatus(lastTime * 1000,data[i].CLASSIN_CLASS_ID);
                               }
                           }
					   }


                   }

               }


               //改变课节状态
               function alterStatus(interval,classID,classTotalTime) {
                   setInterval(function flush() {
                       var elementById = document.getElementById(classID+"");
                       var text = elementById.innerText;
                       if (text == "进入教室" && elementById.getAttribute("tag") == "hasBegan"){
                           document.getElementById(classID+"").innerText = "课节已结束";
                           document.getElementById(classID+"delete").innerText = "";
                       } else if(text == "课节未开始"){
                           text = document.getElementById(classID+"").innerText = "进入教室";
                           document.getElementById(classID+"delete").innerText = "";
                           setInterval(function lastFlush() {
                               if (text == "进入教室" && elementById.getAttribute("tag") == "notYet"){
                                   document.getElementById(classID+"").innerText = "课节已结束";
                                   document.getElementById(classID+"delete").innerText = "";
                               }
                           },classTotalTime);
                       }
                   }, interval);
               }

               //刷新
               function refresh(){
                   jQuery.ajax({
                       type:"GET",
                       url:"/webapps/bb-ClassIn-BBLEARN/classinCourseClass/getClassStatus.do",
                       // data:{"id":val},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                       data:"course_id="+courseId,
                       async:true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                       cache:true,  // 表示浏览器是否缓存被请求页面,默认是 true
                       dataType:"json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                       success:function(data){
                           handleData(data);
                           // alert("刷新成功了")
                       },
                       error:function(){
                           alert("发生错误~");
                       },
                       complete:function(){
                           //alert("ajax请求完成")
                       }
                   });
                   return intervalTimeArray;
               }

               //进入教室
               function classBegin(classinClassId) {
                   var assistantElement = document.getElementById("assistantPhone");
                   var assistantPhone="";
                   var assistantInfo ="";
                   var teacherSelect = document.getElementById('teacher');
                   var teacherIndex = teacherSelect.selectedIndex;
                   var teacher = teacherSelect.options[teacherIndex].value;
                   var assistantTeacherSelect = document.getElementById('assistantTeacher');
                   var assistantTeacherIndex = assistantTeacherSelect.selectedIndex;
                   var assistantTeacher = assistantTeacherSelect.options[assistantTeacherIndex].value;
                   var teacherInfo = teacher;
                   var text = document.getElementById(classinClassId + "").innerText;
                   if (assistantElement){
                       assistantPhone = assistantElement.innerText;
                       assistantInfo = assistantTeacher+" "+assistantPhone;
                       jQuery.ajax({
                           type: "GET",
                           url: "/webapps/bb-ClassIn-BBLEARN/classinCourseClass/editClassTeacher.do",
                           data:"teancherInfo="+teacherInfo+"&assistantInfo="+assistantInfo+"&classId="+classinClassId,     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                           async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                           cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                           dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                           success: function (data) {
                               if (data.errno != "1"){
                                   alert(data.errno+data.error);
                               } else{
                                   if (text == "进入教室") {
                                       jQuery.ajax({
                                           type: "GET",
                                           url: "/webapps/bb-ClassIn-BBLEARN/classinCourseClass/turnIntoClassRoom.do",
                                           data:{"classId":classinClassId,"teacherInfo":teacherInfo,"assistantInfo":assistantInfo},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                                           async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                                           cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                                           dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                                           success: function (data) {
                                               if (data.condition != "error"){
                                                   window.open("https://www.eeo.cn/client/invoke/index.html?" + data.condition);
                                               } else if (data.source == "classin提示您----"){
                                                   window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source="+data.source+"&"+"errno="+data.errorno+"&error="+data.error);
                                               } else {
                                                   window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source="+data.source+"&error="+data.error);
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
                                   } else{
                                       alert("课节已结束");
                                   }
							   }
                           },
                           error: function () {
                               alert("发生错误123456789~");
                           },
                           complete: function () {
                               //alert("ajax请求完成")
                           }
                       });
                   } else{
                       jQuery.ajax({
                           type: "GET",
                           url: "/webapps/bb-ClassIn-BBLEARN/classinCourseClass/editClassTeacher.do",
                           data:"teancherInfo="+teacherInfo+"&classId="+classinClassId,     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                           async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                           cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                           dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                           success: function (data) {
                               if (data.errno != "1"){
                                   alert(data.errno+data.error);
                               } else{
                                   if (text == "进入教室") {
                                       jQuery.ajax({
                                           type: "GET",
                                           url: "/webapps/bb-ClassIn-BBLEARN/classinCourseClass/turnIntoClassRoom.do",
                                           data:{"classId":classinClassId,"teacherInfo":teacherInfo},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                                           async: true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                                           cache: true,  // 表示浏览器是否缓存被请求页面,默认是 true
                                           dataType: "json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                                           success: function (data) {
                                               if (data.condition != "error"){
                                                   window.open("https://www.eeo.cn/client/invoke/index.html?" + data.condition);
                                               } else if (data.source == "classin提示您----"){
                                                   window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source="+data.source+"&"+"errno="+data.errorno+"&error="+data.error);
                                               } else {
                                                   window.open("${pageContext.request.contextPath}/page/classin/tips.jsp?source="+data.source+"&error="+data.error);
                                               }
                                           },
                                           error: function () {
                                               alert("发生错误~");
                                           },
                                           complete: function () {
                                               //alert("ajax请求完成")
                                           }
                                       });
                                   } else if (text == "课节未开始") {
                                       alert("课节未开始");
                                   } else{
                                       alert("课节已结束");
                                   }
                               }
                           },
                           error: function () {
                               alert("发生错误~");
                           },
                           complete: function () {
                               //alert("ajax请求完成")
                           }
                       });
                   }
               }

               function  deleteClass(classId) {
                   var courseId = document.getElementById("course_id").value;
				   window.location.href="${pageContext.request.contextPath}/classinCourseClass/deleteClass.do?classId="+classId+"&bbCourseId="+courseId;
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
                    <bbNG:actionButton url="javaScript:getClassList();" title="当前课程日历" primary="true"/>
                    <bbNG:actionButton url="javaScript:getReplayURLs();" title="获取历史回放列表" primary="true"/>
                </c:if>
                <c:if test="${type == 'meetingroom'}">
                    <bbNG:actionButton url="javaScript:createClassinClass();" title="创建在线研讨室" primary="true"/>
                </c:if>
                <bbNG:actionButton primary="true" url="javaScript:updatePhone();" title="更新手机号" />
                <bbNG:actionButton primary="true" url="javaScript:disbindPhone();" title="解绑手机号" />
                <c:if test="${isTeacher==true}">
                <bbNG:actionButton primary="true" url="javaScript:advanceSet();" title="创建classin教室"/>
                </c:if>
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

		<bbNG:inventoryList collection="${classList}"  objectVar="classInfo" className="java.util.Map" url="">
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
                    <select id="teacher">
                        <c:forEach items="${teachers}" var="bbUser">
                            <c:if test="${classInfo.TEACHER_PHONE == bbUser.phone}">
                                <option>${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                            <c:if test="${bbUser.phone!='   (请绑定手机号)' && classInfo.TEACHER_PHONE != bbUser.phone}">
                                <option>${bbUser.userName}  ${bbUser.phone}</option>
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
                    <select id="assistantTeacher">
                        <c:forEach items="${assistantTeachers}" var="bbUser">
                            <c:if test="${classInfo.TEACHER_PHONE == bbUser.phone}">
                                <option value="${bbUser.userName}&${bbUser.phone}">${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                            <c:if test="${bbUser.phone!='   (请绑定手机号)' && classInfo.TEACHER_PHONE != bbUser.phone}">
                                <option value="${bbUser.userName}&${bbUser.phone}">${bbUser.userName}  ${bbUser.phone}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </c:if>
                <c:if test="${isTeacher!=true}">
                    <span>${classInfo.ASSISTANT_NAME}</span>
                </c:if>
			</bbNG:listElement>
			<bbNG:listElement label="操作" name="action">

				<span id="${classInfo.CLASSIN_CLASS_ID}"  onclick="classBegin(${classInfo.CLASSIN_CLASS_ID})" style="cursor: pointer"></span>

                <c:if test="${isTeacher==true}">
                    <span id="${classInfo.CLASSIN_CLASS_ID}delete" onclick="deleteClass(${classInfo.CLASSIN_CLASS_ID})" style="color: #f44b1c;cursor: pointer"></span>
                </c:if>
				<%--<c:if test="${classInfo.TYPE==非课表课}">--%>

				<%--</c:if>--%>
			</bbNG:listElement>
		</bbNG:inventoryList>
   	<bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)" />
</bbData:context>
</bbNG:learningSystemPage>