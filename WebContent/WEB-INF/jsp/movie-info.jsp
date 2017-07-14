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
</head>

<body>
	<%@ include file="static.html"%>


	<div>
		<div class="container"
			style="margin: 0 auto; margin-top: 50px; width: 1000px;">
			<div class="row">
				<div class="col-xs-12">
					<div class="item poster card" style="width: 100%; height: 41%;">
						<div class="image_content" style="width: 241px; height: 100%;">
							<img class="v2 poster lazyautosizes lazyloaded" data-sizes="auto"
								data-src="${movie.poster}" data-srcset="${movie.poster}"
								alt="${movie.title}" sizes="185px" srcset="${movie.poster}"
								src="${movie.poster}">
						</div>
						<div class="info" style="width: 80%;">
							<p class="flex">
								<a id="movie_${movie.id}" class="title result"
									href="#" title="${movie.title}"
									alt="${movie.title}"> ${movie.title} </a> <span
									class="vote_average">${movie.voteAvg}<span
									id="rating_54c7e0bdc3a36874bf0046fb"
									class="glyphicons glyphicons-star x1 rating movie"></span></span>
							</p>
							<p class="meta flex">
								<span class="release_date"><span
									class="glyphicons glyphicons-calendar x1"></span> ${movie.year}</span>
							</p>
							<p class="overview">${movie.overview}</p>
							<p class="view_more">
								<a id="movie_321612" class="btn  btn-circle btn-inverse"
									href="<c:url value="/movie/${movie.id}/actors"/>"
									title="${movie.title}" alt="${movie.title}">Cast <span
									class="fui-user"></span></a> <a class="btn btn-circle btn-inverse"
									href="${movie.trailer}" title="trailer" alt="${movie.title}">Trailer
									<span class="fui-youtube"></span>
								</a><a class="btn btn-circle btn-inverse" href="<c:url value="/movie/${movie.id}/related"/>"
									title="trailer" alt="${movie.title}">Related <span
									class="fui-video"></span>
								</a>
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
</body>

<div class="bootstrap-tagsinput" style="margin: 0 auto; width: 970px;">
	<c:forEach items="${movie.keywordTag}" var="tag">
		<span class="tag label label-info">${tag}</span>
	</c:forEach>
</div>
<div class="container" style="margin-top: 20px;">
	<h6 class="demo-panel-title" style="color: #1abc9c" >Reviews</h6>
	<div class="panel-group" id="accordion">
		<c:forEach items="${movie.review}" var="rev">

			<div class="panel panel-default">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a data-toggle="collapse" data-parent="#accordion"
							href="#collapse${rev.collapse}">Review of ${rev.user}</a>
					</h4>
				</div>
				<div id="collapse${rev.collapse}" class="panel-collapse collapse">
					<div class="panel-body">${rev.content}</div>
				</div>
			</div>

		</c:forEach>
	</div>
</div>


</html>