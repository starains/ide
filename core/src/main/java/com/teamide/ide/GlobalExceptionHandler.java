//package com.teamide.ide;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.teamide.bean.Status;
//import com.teamide.exception.BaseException;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//	@ExceptionHandler(Exception.class)
//	@ResponseBody // 在返回自定义相应类的情况下必须有，这是@ControllerAdvice注解的规定
//	public Status exceptionHandler(Exception e, HttpServletResponse response) {
//		e.printStackTrace();
//		Status status = Status.FAIL();
//		if (e instanceof NullPointerException) {
//			status.setErrcode(-1);
//			status.setErrmsg("NullPointerException");
//		} else if (e instanceof BaseException) {
//			BaseException baseException = (BaseException) e;
//			if (baseException.getErrcode() != null) {
//				status.setErrcode(baseException.getErrcode());
//				status.setErrmsg(baseException.getErrmsg());
//			} else {
//				status.setErrcode(-1);
//				status.setErrmsg(e.getMessage());
//			}
//		} else {
//			status.setErrcode(-1);
//			status.setErrmsg(e.getMessage());
//		}
//		return status;
//	}
//}