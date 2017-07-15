<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="url" value="${pageContext.request.contextPath}"></c:set>

<head>

<title>PolyMovie Info</title>

<link
	href="<c:url value="/resources/dist/css/vendor/bootstrap/css/bootstrap.min.css" />"
	rel="stylesheet">
<link href="<c:url value="/resources/dist/css/flat-ui.min.css" />"
	rel="stylesheet">
<link href="<c:url value="/resources/dist/css/movie-list.css" />"
	rel="stylesheet">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
<script type="text/javascript"
	src="<c:url value="/resources/dist/js/flat-ui.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/dist/js/search.js"/>"></script>
</head>

<body>
	<%@ include file="static.html"%>
	<div class="container"
		style="margin: 0 auto; margin-top: 50px; width: 1000px;">
		<div class="share mrl"
			style="background-color: #ebebeb; position: relative; border-radius: 6px; padding-left: 20px; padding-right: 20px; padding-top: 10px">
			<h2>Welcome to our PolyStore</h2>
			<p>You can start surfing on it with the navbar or <a href="/PolyMovie/movie"> here</a></p>
		</div>
		<p align="center">
			<img alt="polyMovie" style="max-width:900px;"
				src="<c:url value="/resources/img/overview.png" />">
		</p>
	</div>

</body>


</html>