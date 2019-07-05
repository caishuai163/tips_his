<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>LOGINs</h1>
	<form action="ui.login.do" method="post">
		USERNAME:<input type="text" id="username" name="username"><br>
		PASSWORD:<input type="password" id="password" name="password"><br>
		<input type="submit" value="提交">
	</form>
	<br>${requestScope.message}
</body>
</html>