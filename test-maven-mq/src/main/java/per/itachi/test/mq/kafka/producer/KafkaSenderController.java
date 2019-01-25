package per.itachi.test.mq.kafka.producer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/producer")
public class KafkaSenderController {
	
	private final Logger logger = LoggerFactory.getLogger(KafkaSenderController.class);
	
	@Autowired
	private KafkaTemplate<Object, Object> kafkaTemplate;
	
	@RequestMapping(path="/send")
	@ResponseBody
	public String sendTesting(HttpServletRequest request, HttpServletResponse response) {
		String strMessage = request.getParameter("message");
		logger.info("message is {}. ", strMessage);
		kafkaTemplate.send("mykafka", "testing", strMessage);
		return String.valueOf(response);
	}
}
