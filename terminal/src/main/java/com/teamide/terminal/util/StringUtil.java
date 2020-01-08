package com.teamide.terminal.util;

public class StringUtil {

	public static boolean isEmpty(CharSequence sequence) {
		return sequence == null || sequence.equals("");
	}

	public static boolean isNotEmpty(CharSequence sequence) {
		return !isEmpty(sequence);
	}
}
