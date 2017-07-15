var i = 0;
window.onload = function() {
	
	
	$("#postgP").hide(); 
	$("#redisP").hide();
	$("#neoP").hide();
	$("#cassP").hide();
	
	var input = document.getElementById("navbarInput-01");

	$("#search-button").click(function() {
		var param=$("#navbarInput-01").val();
		//console.log("/HelloWeb/movie/"+param);
		window.location="/PolyMovie/search?search="+param;
	});

	$("#navbarInput-01").keypress(function(e) {
		if(e.which == 13) {
			var param=$("#navbarInput-01").val();
			//console.log("/HelloWeb/movie/"+param);
			window.location="/PolyMovie/search?search="+param;}
	});


	
	var i=0;
	$(".btn").click(function () {
		var id = $(this).attr("id");
		if(id == "mongo"){
			console.log("mongo");
			//$(this).attr("autofocus",true).siblings().attr("autofocus",false);
			$("#mongoP").show();
			$("#redisP").hide();
			$("#postgP").hide();
			$("#neoP").hide();
			$("#cassP").hide();

		}
		if(id == "redis"){
			console.log("redis");
			// $(this).attr("autofocus",true).siblings().attr("autofocus",false);
			$("#redisP").show();
			$("#postgP").hide();
			$("#neoP").hide();
			$("#cassP").hide();
			$("#mongoP").hide();
		}
		if(id == "cassandra"){
			console.log("cassandra");
			// $(this).attr("autofocus",true).siblings().attr("autofocus",false);
			$("#cassP").show();
			$("#redisP").hide();
			$("#postgP").hide();
			$("#neoP").hide();
			$("#mongoP").hide();
		}
		if(id == "postg"){
			console.log("postg");
			//$(this).attr("autofocus",true).siblings().attr("autofocus",false);
			$("#postgP").show();
			$("#redisP").hide();
			$("#neoP").hide();
			$("#cassP").hide();
			$("#mongoP").hide();
		}
		if(id == "neo"){
			console.log("neo");
			// $(this).attr("autofocus",true).siblings().attr("autofocus",false);
			$("#neoP").show();
			$("#redisP").hide();
			$("#postgP").hide();
			$("#cassP").hide();
			$("#mongoP").hide();
		}
	});



}