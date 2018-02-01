package per.itachi.test.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFilter implements Filter {
	
	private final Logger logger = LoggerFactory.getLogger(TestFilter.class);

	/**
	 * For tomcat 8 or below, init and destroy are required to override explicitly. 
	 * Because some jar packages in tomcat 8 are compiled by Java 1.7.
	 * 
	 * */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.debug("The remote {} comes in. ", request.getRemoteAddr());
		chain.doFilter(request, response);//DO NOT FORGET TO ADD THIS STATEMENT.
	}

	@Override
	public void destroy() {
	}
}
