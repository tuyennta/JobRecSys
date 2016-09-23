/**
 * check task status every 3 second
 */
$(document).ready(function(){		
	setInterval(() => {
		if($("p").text().toLowerCase().indexOf("running") >= 0)
			updateTask();
	}, 3000);		
});

function updateTask(){
	$.ajax({
		type:'POST',
		contentType:'application/json',
		url:'danh-gia-thuat-toan/updateTask',
		data:JSON.stringify('data'),
		dataType:'json',
		success:function(task){
			for(var key in task){
				$("#t"+key).text(task[key]);
			}
		}
	});
}