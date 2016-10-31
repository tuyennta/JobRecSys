<%@page import="uit.se.recsys.bean.RowInfoBean"%>
<%@page import="uit.se.recsys.bean.TaskCFBean"%>
<%@page import="uit.se.recsys.bean.TaskBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Evaluation Summarization</title>
<!-- jquery -->
<script type="text/javascript" src="resources/js/jquery-2.0.0.min.js"></script>

<!-- bootstrap -->
<link rel="stylesheet"
	href="resources/libs/bootstrap-3.3.6-dist/css/bootstrap.min.css">
<script type="text/javascript"
	src="resources/libs/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
</head>
<body>
<a href="<%=request.getContextPath() %>/tong-hop.tai-ve">Tải file kết quả</a>
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
			for(String key : rowInfos.keySet()){
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
</body>
</html>