<!-- http://www.tutorialspoint.com/jsp/jsp_standard_tag_library.htm -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	Servlet: foo.bar.servlets.EventServlet
	
	<br>
	
	Resource: <a href="/project_name/event">/event</a>
	
	<br>
	
	Template: <a href="/project_name/events.jsp">/events.jsp</a>
	
	<br>

	<h1> Events </h1>
	
	<br>
	
	<c:forEach items="${requestScope.data}" var="item">
	    ${item.title} 
	</c:forEach>
	
	<br>

</body>
</html>
