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
	src="<c:url value="/resources/dist/js/toggle.js"/>"></script>
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
									href="/movie/${movie.id}" title="${movie.title}"
									alt="${movie.title}"> <img
									class="poster lazyautosizes lazyloaded" data-sizes="auto"
									data-src="${movie.poster}" data-srcset="${movie.poster}"
									alt="${movie.title}" sizes="185px" srcset="${movie.poster}"
									src="${movie.poster}">



									<div class="meta" data-role="tooltip">
										<span id="popularity_54c7e0bdc3a36874bf0046fb_value"
											class="hide popularity_rank_value">
											<div class="tooltip_popup popularity">
												<h3>Classifica Di Popolarità</h3>
												<p>Oggi: 1</p>
												<p>Settimana Scorsa: 1</p>
											</div>
										</span> <span id="popularity_54c7e0bdc3a36874bf0046fb"
											class="glyphicons glyphicons-cardio x1 popularity_rank"></span>
										<span class="right"> </span>
									</div>
								</a>
							</div>
							<div class="info">
								<p class="flex">
									<a id="movie_${movie.id}" class="title result"
										href="/movie/${movie.id}" title="${movie.title}"
										alt="${movie.title}"> ${movie.title} </a> <span
										class="vote_average">${movie.popularity}<span
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
									<a id="movie_321612" class="result" href="/movie/${movie.id}"
										title="${movie.title}" alt="${movie.title}">Più Info</a>
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