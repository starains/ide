package com.teamide.ide.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.teamide.app.servlet.DefaultServlet;
import com.teamide.app.servlet.annotation.RequestMapping;
import com.teamide.bean.Status;
import com.teamide.http.HttpStatus;

@WebServlet("/*")
public class IndexController extends DefaultServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6261365188077899003L;

	@RequestMapping(value = "")
	public String toIndex(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.HTTP_OK);
		System.out.println(1);
		return "/html/index.html";
	}

	@RequestMapping(value = "api/validate")
	public void validate(HttpServletRequest request, HttpServletResponse response) {

		response.setStatus(HttpStatus.HTTP_OK);

		outJSON(response, Status.SUCCESS());
	}
}
