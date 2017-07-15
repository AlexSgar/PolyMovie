<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="url" value="${pageContext.request.contextPath}"></c:set>

<head>

<title>Some Stats</title>

<link
	href="<c:url value="/resources/dist/css/vendor/bootstrap/css/bootstrap.min.css" />"
	rel="stylesheet">
<link href="<c:url value="/resources/dist/css/flat-ui.min.css" />"
	rel="stylesheet">
<link href="<c:url value="/resources/dist/css/movie-list.css" />"
	rel="stylesheet">
<link href="<c:url value="/resources/dist/css/stats.css" />"
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


	<div class="container">
		<div class="btn-group">
			<button id="mongo" type="button" class="btn btn-inverse">MongoDB</button>
			<button id="redis" type="button" class="btn btn-inverse">Redis</button>
			<button id="postg" type="button" class="btn btn-inverse">Postgres</button>
			<button id="neo" type="button" class="btn btn-inverse">Neo4j</button>
			<button id="cassandra" type="button" class="btn btn-inverse">Cassandra</button>
		</div>
		<div class="share mrl"
			style="background-color: #ebebeb; position: relative; border-radius: 6px; padding-left: 20px; padding-right: 20px; padding-top: 10px">


			<div id="mongoP">
				<h2>Our Stats on Mongo</h2>
				<p>An overview about our number of data</p>
				<div class="row demo-tiles">
					<div class="swatches-col">
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Movies</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~39700</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Keywords</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~39700</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div id="redisP">
				<h2>Our Stats on Redis</h2>
				<p>An overview about our number of data</p>
				<div class="row demo-tiles">
					<div class="swatches-col">
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Movie Images</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~269700</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Trailer</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~100000</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Actor Images</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~679500</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
				<div class="list-group" style="padding-bottom: 20px;">
					<a href="#" class="list-group-item active"
						style="background: #2c3e50;">Movies info</a> <a href="#"
						class="list-group-item">From TMDB we discovered that 17892
						movies aren't linked to any trailer </a> <a href="#"
						class="list-group-item">From TMDB we discovered that for 12727
						movies we don't have any backdrops or Posters </a> <a href="#"
						class="list-group-item active" style="background: #2c3e50;">Actors
						info</a> <a href="#" class="list-group-item">From TMDB we
						discovered that for 112510 actors we don't have any photo</a>
				</div>
			</div>
			<div id="postgP">
				<h2>Our Stats on Postgres</h2>
				<p>An overview about our number of data</p>
				<div class="row demo-tiles">
					<div class="swatches-col">
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Actors</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~180500</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>TV Show</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~26600</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Credits</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~487900</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>TV roles</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~376000</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
				<div class="list-group" style="padding-bottom: 20px;">
					<a href="#" class="list-group-item active"
						style="background: #2c3e50;">Actors info</a> <a href="#"
						class="list-group-item">From TMDB we found 112575 actors
						without a profile picture</a> <a href="#" class="list-group-item">From
						TMDB we found 130961 actors with an unknown birthday</a> <a href="#"
						class="list-group-item active" style="background: #2c3e50;">TV
						shows info</a> <a href="#" class="list-group-item">From TMDB we
						found 12497 tv shows without a poster</a> <a href="#"
						class="list-group-item">From TMDB we found 445 tv shows
						without a valid number of total episodes</a> <a href="#"
						class="list-group-item">From TMDB we discovered that 233239
						roles are with the character-name-field empty</a>
				</div>
			</div>
			<div id="neoP">
				<h2>Our Stats on Neo4j</h2>
				<p>An overview about our number of data</p>
				<div class="row demo-tiles">
					<div class="swatches-col">
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Reviews of Movies</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~3900</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Users of Reviews</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~450</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Related movies</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~30400</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="cassP">
				<h2>Our Stats on Cassandra</h2>
				<p>An overview about our number of data</p>
				<div class="row demo-tiles">
					<div class="swatches-col">
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Movie Languages</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~39700</dt>
								</dl>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="pallete-item">
								<dl class="palette palette-midnight-blue">
									<dt>Alternative titles</dt>
								</dl>
								<dl class="palette palette-silver">
									<dt>~39700</dt>
								</dl>
							</div>
						</div>
					</div>
				</div>
			</div>


		</div>
	</div>


</body>
</html>