<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<body onload="init()">
	<h1>userinfo</h1>
	<input type="hidden" id="in" disabled="disabled"
		value="${requestScope.userInfo.uid}">
	uid:${requestScope.userInfo.uid}
	<br> name:${requestScope.userInfo.name}
	<br> pid:${requestScope.userInfo.pid}
	<br> tel:${requestScope.userInfo.tel}
	<br> address:${requestScope.userInfo.address}
	<br>

	<script type="text/javascript">
		function init() {
			if (document.getElementById("in").value == "") {
				window.location.href = "http://localhost:8080/first/ui.getCookies.do";
			}
			
		}
	</script>
</body>
</html>