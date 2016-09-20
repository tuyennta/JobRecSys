<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="uit.se.recsys.bean.TaskBean"%>
<%@page import="uit.se.recsys.bean.RecommendedItem"%>
<%@page import="uit.se.recsys.bean.UserBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Recommender Result</title>

<!-- jquery -->
<script type="text/javascript" src="resources/js/jquery-2.0.0.min.js"></script>

<!-- bootstrap -->
<link rel="stylesheet"
	href="resources/libs/bootstrap-3.3.6-dist/css/bootstrap.min.css">
<script type="text/javascript"
	src="resources/libs/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>

<!-- custom css -->
<link rel="stylesheet" href="resources/css/main.css">

<!-- custom query -->
<script type="text/javascript" src="resources/js/home.js"></script>

<!-- datatable -->
<link
	href="resources/libs/DataTables-1.10.9/css/jquery.dataTables.min.css"
	rel="stylesheet">
<script type="text/javascript"
	src="resources/libs/DataTables-1.10.9/js/jquery.dataTables.min.js"></script>
<script type="text/javascript"
	src="resources/libs/DataTables-1.10.9/js/jquery.dataTables.js"></script>

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

	<div class="container">
		<div class="row" style="margin: 0 auto;">
			<div class="panel-group">
				<div class="panel panel-primary">
					<div class="panel-heading" data-toggle="collapse"
						data-target="#panel-content2">
						<label class="text-uppercase"> Kết quả thực nghiệm thuật
							toán</label>
					</div>
					<div id="panel-content2" class="panel-collapse collapse in">
						<div class="panel-body">
							<div class="row">
								<div class="col-md-6">
									<table class="table table-striped table-bordered table-hover ">
										<tbody>
											<%
												ResourceBundle bundle = ResourceBundle.getBundle("config/experimentType");
												TaskBean task = (TaskBean) request.getAttribute("task");
											%>
											<tr>
												<td>
													<p style="font-weight: bold;">Tên task:</p>
												</td>
												<td><p><%=task.getTaskName()%></p></td>
											</tr>
											<tr>
												<td><p style="font-weight: bold;">Ngày tạo:</p></td>
												<td><p><%=task.getTimeCreate()%></p></td>
											</tr>
											<tr>
												<td><p style="font-weight: bold;">Thuật toán:</p></td>
												<td><p><%=bundle.getString(task.getAlgorithm())%></p></td>
											</tr>
											<%
												Properties configProperties = task.getConfig();
												Enumeration keys = configProperties.keys();
												while (keys.hasMoreElements()) {
													String key = (String) keys.nextElement();
													out.write("<tr>");
													out.write("<td>");
													out.write(bundle.getString(key));
													out.write("</td>");
													out.write("<td>");
													out.println(configProperties.getProperty(key));
													out.write("</td>");
													out.write("</tr>");
												}
											%>
											<tr>
												<td><p style="font-weight: bold;">Dataset:</p></td>
												<td><p>${task.dataset }</p></td>
											</tr>
											<tr>
												<td><p style="font-weight: bold;">Trạng thái:</p></td>
												<td><p>${task.status }</p></td>
											</tr>
											<tr>
												<td><p style="font-weight: bold;">Kết quả:</p></td>
												<td><a
													href="<%=request.getContextPath() %>/ket-qua-thuat-toan.tai-ve?task=${task.taskId }">result.txt</a>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="col-md-6">
									<div class="result">
										<label>Bảng kết quả</label>
										<table id="result"
											class="table table-striped table-bordered table-hover ">
											<thead>
												<tr>
													<th>UserId</th>
													<th>JobId</th>
													<td>Score</td>
												</tr>
											</thead>
											<tbody>
												<%
													List<RecommendedItem> recommendedItems = (List<RecommendedItem>) request.getAttribute("recommendedItems");
													for (RecommendedItem item : recommendedItems) {
														out.write("<tr>");
														out.write("<td>" + item.getUserId() + "</td>");
														out.write("<td>" + item.getItemId() + "</td>");
														out.write("<td>" + item.getScore() + "</td>");
														out.write("</tr>");
													}
												%>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="resources/js/datatable.js"></script>
</body>
</html>