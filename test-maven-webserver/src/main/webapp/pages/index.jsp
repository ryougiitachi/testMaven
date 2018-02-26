<%@ include file="/pages/taglibs.jsp" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>index</title>
	</head>
	<body>
		<div>index</div>
		<c:set var="sessionID" value="${pageContext.request.requestedSessionId}"/>
		<c:set var="requestURI" value="${pageContext.request.requestURI}"/>
		<c:set var="requestURL" value="${pageContext.request.requestURL}"/>
		<c:set var="localAddr" value="${pageContext.request.localAddr}"/>
		<c:set var="localPort" value="${pageContext.request.localPort}"/>
		<p>
			You are in <c:out value="${requestURI}"/> with session <c:out value="${sessionID}"></c:out>
		</p>
		<p><c:out value="${requestURL}"></c:out></p>
		<p><c:out value="${localAddr}:${localPort}"></c:out></p>
	</body>
</html>
