/**
 * 
 */

var websocket = null;
var strURL = "ws://127.0.0.1:8081/springboot/mvc/websocket";
if(window.WebSocket) {//"WebSocket" in window
	console.info("Initialising websocket with URL ", strURL);
	websocket = new WebSocket(strURL);
	websocket.onopen = function(event) {
		console.log("Web Socket has established successfully. ", arguments.length);
	};
	websocket.onclose = function(event) {
		console.log("Web Socket has been closed. ", event, arguments.length);
	};
	websocket.onmessage = function(event) {
		console.log("Web Socket received a message. ", arguments.length);
	};
	websocket.onerror = function(event) {
		console.log("Error occured when Web Socket was handling sth. ", event, arguments.length);
	};
}
else {
	console.error("浏览器不支持websocket.");
}

