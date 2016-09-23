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
<title>Thống kê dữ liệu - Recommender</title>

<!-- jquery -->
<script type="text/javascript" src="resources/js/jquery-2.0.0.min.js"></script>

<!-- bootstrap -->
<link rel="stylesheet"
	href="resources/libs/bootstrap-3.3.6-dist/css/bootstrap.min.css">
<script type="text/javascript"
	src="resources/libs/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>

<!-- custom css -->
<link rel="stylesheet" href="resources/css/main.css">

<script type="text/javascript" src="resources/js/canvasjs.min.js"></script>

<!-- menu script -->
<script type="text/javascript" src="resources/js/menu.js"></script>

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
			<h1 class="text-uppercase" style="text-align: center;">THỐNG KÊ
				DATASET</h1>
		</div>
		<!-- Metadata -->
		<div class="row" style="margin: 0 auto;">
			<div class="panel panel-primary">
				<div class="panel-heading">Thông tin tổng quan về dataset</div>
				<div class="panel-body">
					<table border="0" style="text-align: center;" width="100%">
						<tr>
							<td>
								<p>
									<b>Tên dataset:</b> DATASET_01
								</p>
								<p>
									<b>Kích thước:</b> 10000 KB
								</p>
								<p>
									<b>Số lượng công việc:</b> 400000
								</p>
							</td>
							<td>
								<p>
									<b>Số lượng CV:</b> 200 CV
								</p>
								<p>
									<b>Số lượng dữ liệu rating:</b> 5000
								</p>
								<p>
									<b>Số ngành nghề:</b> 12
								</p>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<!-- Bieu do thong ke  -->
			<div class="panel panel-primary">
				<div class="panel-heading">Thông tin tổng quan về dataset</div>
				<div class="panel-body">
					<!-- Tỉ lệ các giá trị rating  -->
					<script type="text/javascript">
						window.onload = function() {
							var chart = new CanvasJS.Chart(
									"chartContainer",
									{
										title : {
											text : "Biểu đồ thống kế tỉ lệ % người dùng theo ngành nghề"
										},
										legend : {
											maxWidth : 850,
											itemWidth : 120
										},
										data : [ {
											type : "pie",
											showInLegend : true,
											legendText : "{indexLabel}",
											toolTipContent : "{y} người - tỉ lệ #percent %",
											click : function(e) {
												alert("Legend item clicked with type : "
														+ e.dataPoint.lable);
											},
											dataPoints : [
													{
														y : 4181563,
														lable : "haha",
														indexLabel : "Ngành IT-Phần mềm"
													},
													{
														y : 2175498,
														lable : "haha",
														indexLabel : "Ngành Dược sĩ"
													},
													{
														y : 3125844,
														lable : "haha",
														indexLabel : "Ngành kế toán kiểm toán"
													},
													{
														y : 1176121,
														lable : "haha",
														indexLabel : "IT-Phần cứng"
													}, {
														y : 1727161,
														lable : "haha",
														indexLabel : "Hóa dược"
													}, {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													}, {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													} , {
														y : 1727161,
														lable : "haha",
														indexLabel : "Nhân sự"
													}  ]
										} ]
									});
							chart.render();

							var chart1 = new CanvasJS.Chart(
									"chartContainer1",
									{

										title : {
											text : "Biểu đồ thống kế tỉ lệ % các giá trị rating"
										},
										legend : {
											maxWidth : 350,
											itemWidth : 120
										},
										data : [ {
											type : "pie",
											showInLegend : true,
											legendText : "{indexLabel}",
											toolTipContent : "{y} người - tỉ lệ #percent %",
											dataPoints : [ {
												y : 4181563,
												indexLabel : "1"
											}, {
												y : 2175498,
												indexLabel : "2"
											}, {
												y : 3125844,
												indexLabel : "3"
											}, {
												y : 1176121,
												indexLabel : "4"
											}, {
												y : 1727161,
												indexLabel : "5"
											}, ]
										} ]
									});
							chart1.render();
						}
					</script>
					<div id="chartContainer1"
						style="height: 500px; width: 100%; margin: 0;"></div>
					<hr>

					<div id="chartContainer"
						style="height: 500px; width: 100%; margin: 0;"></div>



				</div>
			</div>
		</div>
		<br>

	</div>
</body>
</html>