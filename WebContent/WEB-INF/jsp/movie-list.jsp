<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="url" value="${pageContext.request.contextPath}"></c:set>

<head>

<title>Lista dei film</title>

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
				<c:forEach items="${movies}" var="movie">
					<div class="col-xs-6">
						<div class="item poster card">
							<div class="image_content">
								<a id="movie_${movie.id}" class="result"
									href="<c:url value="/movie/${movie.id}"/>"
									title="${movie.title}" alt="${movie.title}"> <img
									class="poster lazyautosizes lazyloaded" data-sizes="auto"
									data-src="${movie.poster}" data-srcset="${movie.poster}"
									alt="${movie.title}" sizes="185px" srcset="${movie.poster}"
									src="${movie.poster}">

								</a>
							</div>
							<div class="info">
								<p class="flex">
									<a id="movie_${movie.id}" class="title result"
										href="<c:url value="/movie/${movie.id}"/>"
										title="${movie.title}" alt="${movie.title}">
										${movie.title} </a> <span class="vote_average">${movie.voteAvg}<span
										id="rating_54c7e0bdc3a36874bf0046fb"
										class="glyphicons glyphicons-star x1 rating movie"></span></span>
								</p>
								<p class="meta flex">
									<span class="release_date"><span
										class="glyphicons glyphicons-calendar x1"></span>
										${movie.year}</span> <span class="genres">${movie.keywords}</span>
								</p>
								<p class="overview">${movie.overview}</p>
								<p class="view_more">
									<a class="btn btn-circle btn-inverse"
										href="<c:url value="/movie/${movie.id}"/>" title="trailer"
										alt="${movie.title}">Info <span class="fui-search"></span>
									</a> <a class="btn btn-circle btn-inverse"
										href="<c:url value="/movie/${movie.id}/actors"/>"
										title="${movie.title}">Cast <span class="fui-user"></a>
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