package per.itachi.test.web.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServlet extends HttpServlet {
	
	private final Logger logger = LoggerFactory.getLogger(TestServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7934784435589448822L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.debug("Servlet {} has initialised with {}. ", getClass().getSimpleName(), config);
	}

	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)req;
		logger.debug("Servlet {} gets a new {} request {}. ", getClass().getSimpleName(), request.getMethod(), req);
		super.service(req, res);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String strHeaderName = null;
		String strHeaderValue = null;
		Enumeration<String> enumHeaderName = req.getHeaderNames();
		Enumeration<String> enumHeaderValue = null;
		logger.debug("The remote user {} sent the following request headers using session {}: ", 
				req.getRemoteUser(), req.getRequestedSessionId());
		while (enumHeaderName.hasMoreElements()) {
			strHeaderName = enumHeaderName.nextElement();
			enumHeaderValue = req.getHeaders(strHeaderName);
			while (enumHeaderValue.hasMoreElements()) {
				strHeaderValue = enumHeaderValue.nextElement();
				logger.debug("Header - {}: {}", strHeaderName, strHeaderValue);
			}
		}
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			logger.debug("The remote user {} sent the following cookies using session {}: ", 
					req.getRemoteUser(), req.getRequestedSessionId());
			for (Cookie cookie : req.getCookies()) {
				logger.debug("Cookie - {}", getCookieString(cookie));
			}
		}
		else {
			logger.debug("The remote user {} has no cookie: ", req.getRemoteUser());
		}
		logger.debug("");
		RequestDispatcher dispatcher = req.getRequestDispatcher("/pages/index.jsp");
		dispatcher.forward(req, resp);
	}
	
	private String getCookieString(Cookie cookie) {
		StringBuilder builder = new StringBuilder();
		builder.append("Name=").append(cookie.getName()).append("; ");
		builder.append("Value=").append(cookie.getValue()).append("; ");
		builder.append("Comment=").append(cookie.getComment()).append("; ");
		builder.append("Domain=").append(cookie.getDomain()).append("; ");
		builder.append("MaxAge=").append(cookie.getMaxAge()).append("; ");
		builder.append("Path=").append(cookie.getPath()).append("; ");
		builder.append("Secure=").append(cookie.getSecure()).append("; ");
		builder.append("Version=").append(cookie.getVersion()).append("; ");
		builder.append("isHttpOnly=").append(cookie.isHttpOnly()).append("; ");
		return builder.toString();
	}
}
