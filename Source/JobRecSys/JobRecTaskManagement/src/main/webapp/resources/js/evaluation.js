/**
 * change evaluation type
 * @returns
 */
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
}
function showPartitioning() {
	$('#evaluationParamG').show();
	$('#evaluationParamG1').hide();
	$('#evaluationParamG2').hide();
}
function showCustom() {
	$('#evaluationParamG').hide();
	$('#evaluationParamG1').hide();
	$('#evaluationParamG2').show();
}