/**
 * Function set active menu
 */
$(document).ready(function(){
	var path = window.location.pathname;
    path = path.replace(/\/$/, "");
    path = decodeURIComponent(path);       
    $(".nav a").each(function () {    	
    	$(this).closest('li').removeClass('active');
    	var href = $(this).attr('href');    	       
        if (path === href) {
            $(this).closest('li').addClass('active');
        }
    });
});

$(document).ready(function(){
	$(".status").each(function(){
		console.log($(this).text);
	});
//	setInterval(() => {
//		updateTask();
//	}, 3000);
});

function updateTask(){
	$.ajax({
		type:'POST',
		contentType:'application/json',
		url:'trang-chu/updateTask',
		data:JSON.stringify('data'),
		dataType:'json',
		success:function(task){
			$.each(task, function(index, curTask){
				$(".t"+index).html(curTask.status);
			});
		}
	});
}

function changeEvalType() {
	var evalType = $("#evaluationType").val();
	if (evalType == "cross") {
		showCross();
	} else {
		if (evalType == "custom") {
			showCustom();
		} else {
			showPartitioning();
		}
	}
}
function showCross() {
	$('#evaluationParamG').hide();
	$('#evaluationParamG1').show();
	$('#evaluationParamG2').hide();	
	//	$('#evaluationParamG')
//			.html(
//					"<label for='evaluationParam'>Số fold</label><form:input type='number' required='required' class='form-control' path='evaluationParam' id='evaluationParam' />");
}
function showPartitioning() {
	$('#evaluationParamG').show();
	$('#evaluationParamG1').hide();
	$('#evaluationParamG2').hide();
//	$('#evaluationParamG')
//			.html(
//					"<label for='evaluationParam'>Tỉ lệ % tập test</label><form:input type='number' required='required' class='form-control' path='evaluationParam' id='evaluationParam' />");
}
function showCustom() {
	$('#evaluationParamG').hide();
	$('#evaluationParamG1').hide();
	$('#evaluationParamG2').show();
//	$('#evaluationParamG')
//			.html(
//					"<label for='evaluationParam'>Chọn file test</label> <input type='file' class='filestyle form-control' data-buttonName='btn-primary' name='test' data-buttonText='Chọn file' data-buttonBefore='true' id='test'> <label for='evaluationParam'>Chọn file train</label> <input type='file' class='filestyle form-control'	data-buttonName='btn-primary' name='train'	data-buttonText='Chọn file' data-buttonBefore='true' id='train'>");
}