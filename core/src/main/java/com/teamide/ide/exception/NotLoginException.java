package com.teamide.ide.exception;

import com.teamide.exception.BaseException;

public class NotLoginException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8152460831041674448L;

	public NotLoginException() {
		this("登录信息丢失，请先登录！");
	}

	public NotLoginException(String errmsg) {
		super(Errcode.NOT_LOGIN, errmsg);
	}

}
