<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
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

<!-- custom css -->
<link rel="stylesheet" href="resources/css/main.css">

<!-- menu script -->
<script type="text/javascript" src="resources/js/menu.js"></script>

</head>
<body>
	<!-- include header file -->
	<jsp:include page="header.jsp"></jsp:include>

	<div class="container">
	
		<div class="row" style="margin: 0 auto;">
			<div class="col-md-3"></div>
			<div class="col-md-6">
				<div class="panel panel-primary ">
					<div class="panel-heading">
						<label class="text-uppercase" style="text-align: center;">Đăng
							nhập</label>
					</div>
					<div class="panel-body">
					<p class="error">${loginError }</p>
						<form:form role="form" action="dang-nhap" method="POST"
							modelAttribute="user">
							<div class="form-group">
								<label for="email">Email:</label>
								<form:input type="email" path="email" required="required"
									class="form-control" id="email" />
								<form:errors path="email" cssClass="error"></form:errors>
							</div>
							<div class="form-group">
								<label for="pwd">Mật khẩu:</label>
								<form:input type="password" path="password" class="form-control"
									id="pwd" />
								<form:errors path="password" cssClass="error"></form:errors>
							</div>
							<button type="submit" class="btn btn-primary">Đăng nhập</button>
						</form:form>
					</div>
					<div class="panel-footer">
						<div>
							<label>Quên mật khẩu?<a
								href="<%=request.getContextPath()%>/quen-mat-khau"> Lấy lại
									mật khẩu</a></label>
						</div>
						<div>
							<label>Chưa có tài khoản?<a
								href="<%=request.getContextPath()%>/dang-ky"> Đăng ký</a></label>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-3"></div>
		</div>
	</div>
</body>
</html>