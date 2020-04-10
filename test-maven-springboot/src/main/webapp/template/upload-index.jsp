<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/template/taglibs.jsp" %>
<%-- It doesn't work? --%>
<%-- <jsp:include page="/pages/taglibs.jsp"></jsp:include> --%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Home Page</title>
	</head>
	<body>
		<h2>Upload File</h2>
		<input id="txtContextPath" type="hidden" value="${ctx}"/>
		<input id="txtServletPath" type="hidden" value="${servlet}"/>
		<form method="POST" enctype="multipart/form-data" action="${ctx}/mvc/upload/upload-file"> 
			文件：<input type="file" name="fileUpload" />
			<input type="submit" value="上传" />
		</form>
	</body>
</html>
