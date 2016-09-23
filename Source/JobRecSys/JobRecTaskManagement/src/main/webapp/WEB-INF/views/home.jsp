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
<title>Recommender</title>

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
<script type="text/javascript" src="resources/js/ajax-home.js"></script>

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
	<div class="container">
		<div class="row" style="margin: 0 auto;">
			<div class="panel-group">
				<div class="panel panel-primary">
					<div class="panel-heading" data-toggle="collapse"
						data-target="#panel-content">
						<label class="text-uppercase">Trạng thái tasks</label>
					</div>
					<div id="panel-content" class="panel-collapse collapse in">
						<div class="panel-body">
							<table class="table table-hover">
								<thead>
									<tr>
										<th>STT</th>
										<th>Tên task</th>
										<th>Ngày tạo</th>
										<th>Thuật toán</th>
										<th>Dataset</th>
										<th>Trạng thái</th>
									</tr>
								</thead>
								<tbody>
									<%
										List<TaskBean> listTask = (List<TaskBean>) request.getAttribute("listTask");
										int count = 1;
										for (TaskBean task : listTask) {
											out.write("<tr>");
											out.write("<td>" + count++ + "</td>");
											out.write("<td><a href='" + request.getContextPath() + "/ket-qua-thuat-toan?taskid= " + task.getTaskId() + "'>"
													+ task.getTaskName() + "</a></td>");
											out.write("<td>" + task.getTimeCreate() + "</td>");
											String tooltip = "";
											for (String key : task.getConfig().stringPropertyNames()) {
												tooltip += key + "=" + task.getConfig().getProperty(key) + "\n";
											}
											out.write("<td><a href='#' title='" + tooltip);
											out.write("' data-toggle='tooltip'>" + task.getAlgorithm() + "</a></td>");
											out.write("<td>" + task.getDataset() + "</td>");
											out.write("<td>" + "<p class='status' id='t" + task.getTaskId() + "'>" + task.getStatus() + "</p></td>");
										}
									%>									
								</tbody>
							</table>
						</div>
					</div>
				</div>


				<div class="panel panel-primary">
					<div class="panel-heading" data-toggle="collapse"
						data-target="#panel-content2">
						<label class="text-uppercase">Tạo task mới</label>
					</div>
					<div id="panel-content2" class="panel-collapse collapse in">
						<div class="panel-body">
							<div class="row">
								<div class="col-md-3"></div>
								<div class="col-md-6">
									<form:form class="form" enctype="multipart/form-data"
										action="trang-chu" modelAttribute="task" method="POST">										
										<div class="form-group">
											<label for="algorithm">Chọn thuật toán</label>
											<form:select class="form-control" id="algorithm"
												path="algorithm">
												<option value="cf">Collaborative Filtering</option>
												<option value="cb">Content Base</option>
												<option value="hb">Hybrid</option>
											</form:select>
										</div>
										<div class="form-group">
											<label for="algorithm">Chọn hoặc <a
												href="<%=request.getContextPath()%>/quan-ly-dataset">nhập</a>
												dataset
											</label>
											<form:select class="form-control" required="required"
												id="dataset" path="dataset">
												<%
													String[] datasets = (String[]) request.getAttribute("datasets");
															if (datasets != null) {
																for (int i = 0; i < datasets.length; i++) {
																	out.write("<option value='" + datasets[i] + "'>" + datasets[i] + "</option>");
																}
															}
												%>
											</form:select>
										</div>
										<div class="form-group">
											<label for="db">Chọn File cấu hình cho thuật toán</label> <input
												type="file" class="filestyle form-control"
												data-buttonName="btn-primary" name="config"
												required="required" data-buttonText="Chọn file"
												data-buttonBefore="true" id="config">
										</div>
										<button type="submit" class="btn btn-primary">Xử lý</button>
									</form:form>
								</div>
								<div class="col-md-3"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>