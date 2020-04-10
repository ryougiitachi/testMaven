package per.itachi.test.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestSecurityController {
	
	private final Logger logger = LoggerFactory.getLogger(TestSecurityController.class);

	@GetMapping("/product/info")
	public String accessProductInfo() {
		logger.info("Authentication is {}. ", SecurityContextHolder.getContext().getAuthentication());
		return "/product/info";
	}

	@GetMapping("/admin/info")
	public String accessAdminInfo() {
		logger.info("Authentication is {}. ", SecurityContextHolder.getContext().getAuthentication());
		return "/admin/info";
	}
}
