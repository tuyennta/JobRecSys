$(document).ready(function() {
	
	var table = $('#result').DataTable({
		"order" : [ [ 0, "desc" ] ],
		"language" : {
			"lengthMenu" : "Hiển thị _MENU_ dòng",
			"zeroRecords" : "Chưa có dữ liệu",
			"search" : "Tìm kiếm:",
			"info" : "Hiển thị từ _START_ đến _END_ trong tổng số _TOTAL_ dòng",
			"infoEmpty" : "Chưa có dữ liệu",
			"infoFiltered" : "(Lọc từ _MAX_ dòng)",
			"paginate" : {
				"first" : "Trang đầu",
				"last" : "Trang cuối",
				"next" : "Trang sau",
				"previous" : "Trang trước"
			},
			"aria" : {
				"sortAscending" : ": Chọn để sắp xếp tăng dần",
				"sortDescending" : ": Chọn để sắp xếp giảm dần"
			}
		}
	});	
});