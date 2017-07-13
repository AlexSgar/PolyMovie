<%@ page contentType="text/html; charset = UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<head>
<title>Lista degli attori</title>
<link
	href="<c:url value="/resources/dist/css/vendor/bootstrap/css/bootstrap.min.css" />"
	rel="stylesheet">
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
<script type="text/javascript" src="<c:url value="/resources/dist/js/toggle.js"/>"></script>
</head>

<body>

	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<h2>${message}</h2>
				<a href='<c:url value="/actor" />'>vai alla tv</a> <a
					href='<c:url value="/actor/444" />'>vai all'attore sconosciuto</a>
				<div class="panel panel-success">
					<div style="height: 40" class="panel-heading" id="primafiltro">
						<div class="pull-right" id="filtra">
							<span class="clickable filter" data-toggle="tooltip"
								title="Toggle table filter" data-container="body"> <i
								class="glyphicon glyphicon-filter"></i>
							</span>
						</div>
						<div class="pull-left">Tasks</div>
					</div>
					<div class="panel-body" id="appari">
						<input type="text" class="form-control" id="task-table-filter"
							data-action="filter" data-filters="#task-table"
							placeholder="Filter Tasks" />
					</div>
					<table class="table table-hover" id="task-table">
						<thead>
							<tr>

								<th>id_movie</th>

							</tr>
						</thead>
						<tbody>
							<c:forEach items="${actors}" var="act">
								<tr>
									<td>${act}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>


</body>
</html>