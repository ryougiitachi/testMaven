/**
 * 
 */

var xmlhttp=new XMLHttpRequest();
var requestData = "{\
	\"homer_id\": \"5u7027\", \
	\"payment_id\": 1704879, \
	\"payment_status\": \"S\" \
}";
xmlhttp.addEventListener(
		"readystatechange", 
		function(e) {
			var source = e.currentTarget;
			if (source.readyState==4 && source.status==200)
			{
				console.log(source.responseText);
				console.log("arguments.length = ", arguments.length);
				console.log(e);
			}
			else {
				console.log("readyState=", source.readyState, "status=", source.status);
			}
		}, 
		false);
xmlhttp.open("POST", "/test-maven-struts2/ajax/default", true);// 
xmlhttp.send(requestData);

//