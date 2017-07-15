<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="url" value="${pageContext.request.contextPath}"></c:set>

<head>

<title>Lista degli attori</title>

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


	<div>
		<div class="container"
			style="margin: 0 auto; margin-top: 50px; width: 1000px; margin-bottom: 50px;">
			<div class="row demo-tiles">
				<c:forEach items="${actors}" var="act">
					<div class="col-xs-6">
						<div class="item poster card">
							<div class="image_content">
								<a id="movie_${act.id}" class="result" href="#"
									title="${act.name}" alt="${act.name}"> <img
									class="poster lazyautosizes lazyloaded" data-sizes="auto"
									data-src="${act.photo}" data-srcset="${act.photo}"
									alt="${act.name}" sizes="185px" srcset="${act.photo}"
									src="${act.photo}">

								</a>
							</div>
							<div class="info">
								<p class="flex">
									<a id="movie_${act.id}" class="title result" href="#"
										title="${act.name}" alt="${act.name}"> ${act.name} </a> <span
										class="vote_average">${act.popularity}<span
										id="rating_54c7e0bdc3a36874bf0046fb"
										class="glyphicons glyphicons-star x1 rating movie"></span></span>
								</p>
								<p class="meta flex">
									<span class="release_date"><span
										class="glyphicons glyphicons-calendar x1"></span> Born on:
										${act.birthday}</span><span> </span>
								</p>
								<p class="overview"></p>


								<p class="view_more">
									<a id="movie_321612" class="btn  btn-circle btn-inverse"
										href="<c:url value="/actor/${act.id}/tvshows"/>"
										title="${movie.title}" alt="${movie.title}">Tv Roles <span
										class="fui-play"></span></a> <a class="btn btn-circle btn-inverse"
										href="<c:url value="/actor/${act.id}/movies"/>"
										title="trailer" alt="${movie.title}">Movies <span
										class="fui-video"></span>
									</a>
								</p>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>


		</div>
	</div>


</body>
</html>




