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
                 blackboard.platform.session.BbSession"%>
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
            
            	//已有课堂，直接进入
                function save() {
                	if(validTelephone()){
                		var telephone = document.getElementById("telephone").value;
                		var course_id = document.getElementById("course_id").value;
                		var typeName = document.getElementById("typeName");
                		if(typeName != null){
                			typeName = typeName.value;
                		}
                		var validNum = document.getElementById("validNum").value;
                		if(validNum == null || validNum == ""){
                			alert("短信验证码不能为空！");
                			return false;
                		}
		                window.location.href = "${pageContext.request.contextPath}/userPhone/save.do?telephone="
		                		+telephone+"&course_id="+course_id
		                		+"&type="+typeName
		                		+"&validNum="+validNum;
                	}
                }
                
            	//验证手机号是否为空，及确认两次输入手机号相同
                function validTelephone(){
                	var telephone = document.getElementById("telephone").value;
                    var confirmTelephone = document.getElementById("confirmTelephone").value;

                    if(telephone == null || "" == telephone){
                        alert("手机号不能为空，请输入手机号！");
                        return false;
                    }
                    //校验手机号的合法性
                    var myreg= /^[1][3,4,5,7,8,9,6][0-9]{9}$/;
                    var gjreg = /^[0][0][0-9]+[-][0-9]+$/;
                    if (!myreg.test(telephone) && !gjreg.test(telephone)) {
                    	alert("未输入有效的手机号，请重试！");
                        return false;
                    }
                    
                    if(telephone != confirmTelephone){
                        alert("两次输入的手机号不一致，请核对！");
                        return false;
                    }
                    return true;
                }
            	
                function getValidNum(o){
            		if(validTelephone()){
            			var telephone = document.getElementById("telephone").value;
                		var course_id = document.getElementById("course_id").value;
                		var typeName = document.getElementById("typeName");
                		if(typeName != null){
                			typeName = typeName.value;
                		}
                		time(o);
                		jQuery.ajax({
                            type:"GET",
                            url:"/webapps/bb-ClassIn-bb_bb60/userPhone/getValidNum.do",
                            // data:{"id":val},     // data参数是可选的，有多种写法，也可以直接在url参数拼接参数上去，例如这样：url:"getUser?id="+val,
                            data:"telephone="+telephone+"&course_id="+course_id+"&type="+typeName,
                            async:true,   // 异步，默认开启，也就是$.ajax后面的代码是不是跟$.ajx里面的代码一起执行
                            cache:true,  // 表示浏览器是否缓存被请求页面,默认是 true
                            dataType:"json",   // 返回浏览器的数据类型，指定是json格式，前端这里才可以解析json数据
                            success:function(data){
                            	alert(data.error_info.error);
                            },
                            error:function(){
                                console.log("发生错误")
                                alert("获取验证码失败，稍后请重试~");
                            },
                            complete:function(){
                                console.log("ajax请求完成")
                            }
                        });
            		}
            	}
                
                var wait=60;
                function time(o) {
                 	if (wait == 0) {
                  		o.removeAttribute("disabled");  
                  		o.value="获取验证码";
                  		wait = 60;
                 	} else { 
                  		o.setAttribute("disabled", true);
                  		o.value="重新发送(" + wait + ")";
                  		wait--;
                  		setTimeout(function() {
                  			time(o)
                  		},1000)
                  	}
                 } 
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

        <bbNG:actionControlBar>

            <bbNG:actionPanelButton type="SEARCH" alwaysOpen="true">
                <bbNG:form id="searchForm" name="searchForm" action="" method="POST">
	                <c:if test="${tips != null }">
	                	<span style="font-size:15px;font-color:blue;" id="spanTips">${tips}</span> <br/><br/>
	                </c:if>
				           请输入手机号：<input type="text" name="telephone" value="${telephone}" id="telephone"><br/>
				           请确认手机号：<input type="text" name="confirmTelephone" value="${telephone}" id="confirmTelephone"><br/>
				           请输入短信码：<input type="text" name="validNum" value="" id="validNum">
			        <input type="button" name="validNumber" value="获取验证码" onclick="getValidNum(this)">
                    <input type="hidden" name="course_id" id="course_id" value="<%=course_id%>">
                    <input type="hidden" name="typeName" id="typeName" value="${type}">
                    <input type="button" name="btn" value="保存手机号" onclick="save()">
                    <br/><br/>
                    	<span style="font-size:15px;">手机号格式为:00国家号-手机号；注意：中国大陆手机号不写国家号。</span><br/>
                    	<span style="font-size:15px;">例如:美国手机号 1 (800) 643-7676 填成 001-8006437676；中国大陆手机号填成 15800000001</span>
                </bbNG:form>
            </bbNG:actionPanelButton>
        </bbNG:actionControlBar>

    </bbData:context>
    <bbNG:button type="PageLevel" label="返回" url="javascript:history.go(-1)" />
</bbNG:learningSystemPage>