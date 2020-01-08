package com.teamide.ide.util;

public class PasswordMD5Tool {

	public static String getPasswordMD5(String password) {

		return MD5Util.MD5(password).toUpperCase();
	}
}
