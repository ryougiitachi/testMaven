package per.itachi.test.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/api")//千万别当成RequestMapping；
@RequestMapping("/api")
public class TestController {
	
	private final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	/**
	 * RequestMapping的path如果是/开头可以无视Controller中的value？当然不是这样，之前把controller给当成RequestMapping；
	 * */
	@RequestMapping(path="/status", method={RequestMethod.GET})
	@ResponseBody
	public String checkStatus() {
		try {
			logger.info("Start handling. ");
			Thread.sleep(3000l);
		} 
		catch (InterruptedException e) {
			logger.error("", e);
		}
		return "OK";
	}
}
