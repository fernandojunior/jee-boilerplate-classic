<!-- http://www.tutorialspoint.com/jsp/jsp_standard_tag_library.htm -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1" />

	<title>Insert title here</title>

	<!-- Bootstrap Core CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

	<!-- Custom CSS -->
    <!-- <link href="css/style.css" rel="stylesheet"> -->

</head>
<!--
<c:set var="helloWorld" value="Hello World"/>
<c:if test="${fn:endsWith(helloWorld, 'World')}">
	<c:out value="${ helloWorld }"></c:out>
</c:if>
<c:forTokens items="${ helloWorld }" delims=" " var="letter">
	<c:out value="${ letter }"/><br>
</c:forTokens>
 -->
<body>

	<header>
		<div class="container">
			<h1>Hello World</h1>
		</div>
	</header>

	<nav class="navbar navbar-default" role="navigation">
        <div class="container">
            <div class="navbar-brand">
                Hello World
            </div>
        </div>
    </nav>

	<main>
        <div class="container">
            <div id="content">
	            <div class="row">
	            	<section class="col-md-4">

							Hello World

					</section>
					<section class="col-md-4">

							Hello World

					</section>
				</div>
            </div>
        </div>
    </main>

    <footer class="footer">
        <div class="container">
            <span class="">
            &copy; 2016 Fernando Felix do Nascimento Junior
            </span>
        </div>
    </footer>

	<!-- jQuery -->
	<script src="https://code.jquery.com/jquery-2.2.3.js"></script>

	<!-- Bootstrap -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" ></script>

</body>
</html>
