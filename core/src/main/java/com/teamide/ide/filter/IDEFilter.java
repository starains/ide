package com.teamide.ide.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.util.RequestUtil;

@WebFilter(urlPatterns = "/*")
public class IDEFilter implements Filter {

	public static boolean ignore(String servletpath) {
		if (servletpath.startsWith("/coos/file/") || servletpath.startsWith("/resources/")) {

			return true;
		}
		return false;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");

		String servletpath = RequestUtil.getServletpath(request);
		if (servletpath.lastIndexOf("$ROOT$") > 0) {
			int index = servletpath.lastIndexOf("$ROOT$");
			servletpath = servletpath.substring(index + ("$ROOT$").length() + 1);

		}

		if (ignore(servletpath)) {
			chain.doFilter(req, res);
			return;
		}

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
