var i = 0;
window.onload = function() {
	//var row = document.getElementById("appari");
	//var filter = document.getElementById("search-button");
	//$store = $("#search-button").detach();

	//console.log(i)
	//i = 1;
	var input = document.getElementById("navbarInput-01");
	//console.log("il click è qui")
	$("#search-button").click(function() {
		var param=$("#navbarInput-01").val();
		//console.log("/HelloWeb/movie/"+param);
		window.location="/HelloWeb/search?search="+param;
		
		
	});
}