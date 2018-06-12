package per.itachi.test.struts2.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHttpServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3631022623029534498L;
	
	private Logger logger = LoggerFactory.getLogger(TestHttpServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("TestHttpServlet GET");
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("TestHttpServlet POST");
		//这个指的是servlet匹配的路径，即url-pattern；
		String strServletPath = req.getServletPath();
		logger.info("TestHttpServlet {}", strServletPath);
	}

}
