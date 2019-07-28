package per.itachi.test.springboot.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
public class UploadController {
	
	private final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	/**
	 * 在需要返回页面的时候不要用RestController，这个注解会直接把返回值当做结果而不是ModelAndView；
	 * */
	@RequestMapping(path="/upload-index")
	public String redirect() {
		return "upload-index";
	}
	
	@RequestMapping(path="/upload-file")
	public String upload(@RequestParam MultipartFile fileUpload, HttpServletRequest request) {
		try {
			logger.info("fileUpload is {}", fileUpload);
			logger.info("fileUpload.getBytes()'s length is {}", fileUpload.getBytes().length);
			logger.info("fileUpload.getContentType() is {}", fileUpload.getContentType());
			logger.info("fileUpload.getInputStream() is {}", fileUpload.getInputStream());
			logger.info("fileUpload.getName() is {}", fileUpload.getName());
			logger.info("fileUpload.getOriginalFilename() is {}", fileUpload.getOriginalFilename());
			logger.info("fileUpload.getResource() is {}", fileUpload.getResource());
			logger.info("fileUpload.getSize() is {}", fileUpload.getSize());
			logger.info("fileUpload.isEmpty() is {}", fileUpload.isEmpty());
			logger.info("request content-type is {}", request.getContentType());
		} 
		catch (IOException e) {
			logger.info("", e);
		}
		return "success";
	}
}
