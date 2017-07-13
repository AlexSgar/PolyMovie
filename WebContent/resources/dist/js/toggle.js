var i = 0;
window.onload = function() {
	var row = document.getElementById("appari");
	var filter = document.getElementById("filtra");
	$store = $("#appari").detach();

	console.log(i)
	i = 1;
	console.log("sono qui")
	$("#filtra").click(function() {
		if (i % 2 == 0) {
			console.log("provvo a cancellare");
			$store = $("#appari").detach();
			i++;
		} else {
			console.log("qui" + i)
			$("#primafiltro").after($store);
			i++;
		}
	});
}