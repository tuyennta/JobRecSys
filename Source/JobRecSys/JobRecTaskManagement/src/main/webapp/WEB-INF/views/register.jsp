<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Đăng ký - Recommender</title>

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
							ký tài khoản</label>
					</div>
					<div class="panel-body">
						<form:form role="form" action="dang-ky" method="post"
							modelAttribute="user">
							<form:errors path="*" cssClass="error">
								<div class="error"><spring:message code="error.mysql.exception"/> </div>
							</form:errors>
							<div class="form-group">
								<label for="userName">Tên tài khoản:</label>
								<form:input path="userName" type="text" required="required"
									class="form-control" id="userName" />
								<i class="text-danger" id="noti_user"></i>
								<form:errors path="userName" cssClass="error"></form:errors>
							</div>
							<div class="form-group">
								<label for="email">Email:</label>
								<form:input path="email" type="email" required="required"
									class="form-control" id="email" />
								<i class="text-danger" id="noti_email"></i>
								<form:errors path="email" cssClass="error"></form:errors>
							</div>
							<div class="form-group">
								<label for="pwd">Mật khẩu:</label>
								<form:input path="password" type="password" required="required"
									class="form-control" id="pwd" />
								<form:errors path="password" cssClass="error"></form:errors>
							</div>
							<div class="form-group">
								<label for="pwd">Nhập lại mật khẩu:</label>
								<form:input path="rpassword" type="password" required="required"
									class="form-control" id="rpwd" />
								<i class="text-danger" id="noti_pass"></i>
								<form:errors path="rpassword" cssClass="error"></form:errors>
							</div>
							<button type="submit" class="btn btn-primary">Đăng ký</button>
							<i class="text-success" id="noti_status"></i>
						</form:form>
					</div>
					<div class="panel-footer">
						<label>Đã có tài khoản?<a
							href="<%=request.getContextPath()%>/dang-nhap"> Đăng nhập</a></label>
					</div>
				</div>
			</div>
			<div class="col-md-3"></div>
		</div>
	</div>
</body>
</html>