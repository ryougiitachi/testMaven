var xmlhttp=new XMLHttpRequest();
var requestData = "{\
	\"user_id\": \"507738\" \
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
xmlhttp.open("POST", "/test-maven-struts2/test/post/payment-list", true);// 
xmlhttp.send(requestData);
