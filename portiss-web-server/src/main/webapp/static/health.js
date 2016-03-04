(function($){
	$(document).ready(function() {
		$.get("/portiss-web-server/loadHealth",function(result) {
			if(result.data) {
				var $table = $(".portissHealth");
				$.each(result.data,function(key,value){
					$table.append("<tr><td>"+key+"</td><td>"+value+"</td></tr>");
				});
				$(".start-time").text(result.startTime);
				$(".health-container").show();
				$("#startTime").show();
			}else {
				$("#startTime").hide();
				$(".server-not-healthy").show();
			}
		});
	});
	
})(jQuery);