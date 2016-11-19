<%@page import="uit.se.recsys.bean.RowInfoBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="uit.se.recsys.bean.MetricBean"%>
<%@page import="uit.se.recsys.bean.TaskBean"%>
<%@page import="uit.se.recsys.bean.UserBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Đánh giá thuật toán - Recommender</title>

<!-- jquery -->
<script type="text/javascript" src="resources/js/jquery-2.0.0.min.js"></script>

<!-- bootstrap -->
<link rel="stylesheet"
	href="resources/libs/bootstrap-3.3.6-dist/css/bootstrap.min.css">
<script type="text/javascript"
	src="resources/libs/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>

<!-- bootstrap upload file style -->
<script type="text/javascript"
	src="resources/js/bootstrap-filestyle.min.js"></script>

<!-- custom css -->
<link rel="stylesheet" href="resources/css/main.css">

<!-- menu script -->
<script type="text/javascript" src="resources/js/menu.js"></script>
<script type="text/javascript" src="resources/js/ajax-evaluation.js"></script>
<script type="text/javascript" src="resources/js/evaluation.js"></script>

<!-- jquery ui -->
<link rel="stylesheet"
	href="resources/libs/jquery-ui-1.12.0/jquery-ui.css">
<script type="text/javascript"
	src="resources/libs/jquery-ui-1.12.0/jquery-ui.js"></script>

<script>
	$(function() {
		$(document).tooltip();
	});
</script>
<style>
.ui-tooltip {
	max-width: 500px;
	white-space: pre-line;
}
</style>
</head>
<body>
	<!-- include header file -->
	<%
		UserBean user = (UserBean) session.getAttribute("user");
		if (user == null || user.getUserName() == null) {
	%>
	<jsp:include page="header.jsp"></jsp:include>
	<%
		} else {
	%>
	<jsp:include page="loggedInHeader.jsp"></jsp:include>
	<%
		}
	%>
	<div class="container-fluid">
		<div class="row" style="margin: 0 auto;">
			<div class="panel-group">
				<div class="panel panel-primary">
					<div class="panel-heading" data-toggle="collapse"
						data-target="#panel-content2">
						<label class="text-uppercase">Tổng hợp kết quả đánh giá
							${type }</label> <a href="<%=request.getContextPath()%>/tong-hop.tai-ve"
							class='btn btn-danger pull-right'><span
							class='glyphicon glyphicon-download-alt'></span> Tải file kết quả</a>
					</div>
					<div id="panel-content2" class="panel-collapse collapse in">
						<div class="panel-body">
							<%
								String noti = (String) request.getAttribute("noti");
								if (noti != null) {
									out.write("<p class='bg-danger'>" + noti + "</p>");
								}
							%>
							<div class="table-responsive">
								<table class="table table-striped table-bordered table-hover">
									<col>
									<colgroup span="3"></colgroup>
									<colgroup span="3"></colgroup>
									<colgroup span="3"></colgroup>
									<colgroup span="3"></colgroup>
									<colgroup span="3"></colgroup>
									<colgroup span="3"></colgroup>
									<tr>
										<td rowspan="2"></td>
										<th colspan="3" scope="colgroup">Precision</th>
										<th colspan="3" scope="colgroup">Recall</th>
										<th colspan="3" scope="colgroup">F-Measure</th>
										<th colspan="3" scope="colgroup">NDCG</th>
										<th colspan="3" scope="colgroup">RMSE</th>
										<th colspan="3" scope="colgroup">MRR</th>
										<th colspan="3" scope="colgroup">MAP</th>
									</tr>
									<tr>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
										<th scope="col">Top5</th>
										<th scope="col">Top10</th>
										<th scope="col">Top15</th>
									</tr>
									<%
										HashMap<String, List<RowInfoBean>> rowInfos = (HashMap) request.getAttribute("rowInfos");
										if (rowInfos != null)
											for (String key : rowInfos.keySet()) {
												for (RowInfoBean row : rowInfos.get(key)) {
													out.write("<tr>");
													out.write("<th scope='row'>" + row.getDisplayName() + "</th>");
													for (Float score : row.getScores()) {
														if (!score.isNaN())
															out.write("<td>" + score + "</td>");
														else
															out.write("<td></td>");
													}
													out.write("</tr>");
												}
											}
									%>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>