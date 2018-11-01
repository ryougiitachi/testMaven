package per.itachi.test.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/api")
public class TestController {
	
	@RequestMapping(path="/status", method={RequestMethod.GET})
	@ResponseBody
	public String checkStatus() {
		return "OK";
	}
}
