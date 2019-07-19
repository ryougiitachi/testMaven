<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%-- "xml" must not be used as prefix! It is keyword or reserved word? --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="servlet" value="${pageContext.request.servletPath}"/>
<!-- This page is ${pageContext.request.requestURI}.  -->
