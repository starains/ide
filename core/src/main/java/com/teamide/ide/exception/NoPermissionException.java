package com.teamide.ide.exception;

import com.teamide.exception.BaseException;

public class NoPermissionException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8152460831041674448L;

	public NoPermissionException(String errmsg) {
		super(Errcode.NO_PERMISSION, errmsg);
	}

}
