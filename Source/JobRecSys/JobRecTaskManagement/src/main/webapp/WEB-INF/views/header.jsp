<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<nav class="navbar navbar-default navbar-static-top custom_navbar_color" >
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar3">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="/trang-chu"><img
				src="<%out.print(request.getContextPath() + "/resources/images/logo.png");%>"
				class="img-responsive" alt="Dispute Bills"> </a>
		</div>
		<div id="navbar3" class="navbar-collapse collapse text-uppercase">
			<ul class="nav navbar-nav navbar-right">				
				<li class="active"><a href="<%=request.getContextPath()%>">Chạy thuật toán</a></li>
				<li><a href="<%=request.getContextPath()%>/quan-ly-dataset">Quản lý dataset</a></li>
				<li><a href="<%=request.getContextPath()%>/danh-gia-thuat-toan">Đánh giá thuật toán</a></li>
				<li><a href="<%=request.getContextPath()%>/thong-ke-du-lieu">Thống kê dữ liệu</a></li>
				<li><a href="<%=request.getContextPath()%>/dang-nhap"><span class="glyphicon glyphicon-log-in"></span>
						Đăng nhập</a></li>
				<li><a href="<%=request.getContextPath()%>/dang-ky"><span class="glyphicon glyphicon-user"></span>
						Đăng ký</a></li>
			</ul>
		</div>
		<!--/.nav-collapse -->
	</div>
	<!--/.container-fluid -->
</nav>