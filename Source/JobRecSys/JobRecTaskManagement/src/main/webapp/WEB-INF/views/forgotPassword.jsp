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
<title>Quên mật khẩu - Recommender</title>

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
				<div class="panel panel-primary ${user.changePasswordCode != 0 ? 'hiden' : '' }">
					<div class="panel-heading">
						<label class="text-uppercase" style="text-align: center;">Đổi mật khẩu</label>
					</div>
					<div class="panel-body">		
					<label>Vui lòng nhập email cần đổi mật khẩu vào ô bên dưới để lấy mã xác thực đổi mật khẩu</label>			
						<form:form role="form" action="doi-mat-khau.getCode" method="post"
							modelAttribute="user">
							<form:errors path="*" cssClass="error">
								<div class="error"><spring:message code="error.mysql.exception"/> </div>
							</form:errors>							
							<div class="form-group">
								<label for="email">Email:</label>
								<form:input path="email" type="email" required="required"
									class="form-control" id="email" />
								<i class="text-danger" id="noti_email"></i>
								<form:errors path="email" cssClass="error"></form:errors>
							</div>
							
							<button type="submit" class="btn btn-primary">Lấy mã xác thực</button>
							<i class="text-success" id="noti_status"></i>
						</form:form>
					</div>
					<div class="panel-footer">
						<label>Đã có tài khoản?<a
							href="<%=request.getContextPath()%>/dang-nhap"> Đăng nhập</a></label>
					</div>
				</div>
				<div class="panel panel-primary ${user.changePasswordCode == 0 ? 'hiden' : '' }">
					<div class="panel-heading">
						<label class="text-uppercase" style="text-align: center;">Đổi mật khẩu</label>
					</div>
					<div class="panel-body">		
					<label>Chúng tôi đã gửi cho bạn một mã xác thực qua email vui lòng nhập mã vào ô bên dưới</label>			
						<form:form role="form" action="doi-mat-khau.changePass" method="post"
							modelAttribute="user">
							<form:errors path="*" cssClass="error">
								<div class="error"><spring:message code="error.mysql.exception"/> </div>
							</form:errors>							
							<div class="form-group">
								<label for="email">Email:</label>
								<form:input path="email" type="email" required="required"
									class="form-control" id="email" />
								<i class="text-danger" id="noti_email"></i>
								<form:errors path="email" cssClass="error"></form:errors>
							</div>
							<div class="form-group">
								<label for="changePasswordCode">Mã xác thực:</label>
								<form:input path="changePasswordCode" type="text" required="required"
									class="form-control" id="changePasswordCode" />
								<i class="text-danger" id="noti_changePasswordCode"></i>
								<form:errors path="changePasswordCode" cssClass="error"></form:errors>
							</div>						
							<div class="form-group">
								<label for="pwd">Mật khẩu mới:</label>
								<form:input path="password" type="password" required="required"
									class="form-control" id="pwd" />
								<form:errors path="password" cssClass="error"></form:errors>
							</div>
							<div class="form-group">
								<label for="pwd">Nhập lại mật khẩu mới:</label>
								<form:input path="rpassword" type="password" required="required"
									class="form-control" id="rpwd" />
								<i class="text-danger" id="noti_pass"></i>
								<form:errors path="rpassword" cssClass="error"></form:errors>
							</div>
							<button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
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