<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/template/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Login</title>
	</head>
	<body>
		<form method="post" action="${ctx}">
			Username <input id="txtUsername" type="text" name="username"/><br/>
			Password <input id="txtPassword" type="text" name="password"/><br/>
			<input type="submit" name="submit" /><br/>
		</form>
	</body>
</html>