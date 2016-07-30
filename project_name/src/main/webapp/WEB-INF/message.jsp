<!-- http://www.tutorialspoint.com/jsp/jsp_standard_tag_library.htm -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1" />

<title>Hello World</title>

<!-- Bootstrap Core CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

<!-- Custom CSS -->
<link href="${pageContext.request.contextPath}/assets/css/main.css"
	rel="stylesheet">

</head>
<body>

	<header>
		<div class="container">
			<h1>Hello World</h1>
		</div>
	</header>

	<nav class="navbar navbar-default" role="navigation">
		<div class="container">
			<div class="navbar-brand">
				<ul class="list-inline">
					<li>Resource: <a href="/project_name/message">/message</a></li>
					<li>Template: WEB-INF/message.jsp</li>
					<li>Servlet: foo.bar.servlets.MessageServlet</li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="main">
		<div class="container">

			<c:if test="${not empty success}">
				<div class="alert alert-success">
					<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
					<strong>Success!</strong> ${success}
				</div>
			</c:if>

			<c:if test="${not empty warning}">
				<div class="alert alert-warning">
					<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
					<strong>Warning!</strong> ${warning}
				</div>
			</c:if>

			<c:if test="${not empty messages}">

				<form action="message" method="post">
					<div class="form-group">
						<input class="hidden" name="action" id="action" value="posta">
						<input name="message" id="message" class="form-control"
							placeholder="Add message"> <input type="submit"
							class="btn btn-primary hidden" value="submit">
					</div>
				</form>

				<div class="table-responsive">
					<table class="table">
						<thead>
							<tr>
								<th>Message</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${messages}" var="message">
								<tr>
									<td>${message}</td>
									<td>
										<ul class="list-inline">
											<li><a class="btn btn-primary"
												href="message?id=${message.id}">datail</a></li>
											<li>
												<form action="message" method="post">
													<input class="hidden" name="action" id="action"
														value="delete"> <input class="hidden" name="id"
														id="id" value="${message.id}"> <input
														type="submit" class="btn btn-danger" value="delete">
												</form>
											</li>
										</ul>

									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

			</c:if>
			<c:if test="${not empty message}">
				<div>${message}</div>
			</c:if>
		</div>
	</div>

	<footer class="footer">
		<div class="container">
			<span class=""> &copy; 2016 Fernando Felix do Nascimento
				Junior </span>
		</div>
	</footer>

	<!-- jQuery -->
	<script src="https://code.jquery.com/jquery-2.2.3.js"></script>

	<!-- Bootstrap -->
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

	<!-- Custom JS -->
	<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>
</html>
