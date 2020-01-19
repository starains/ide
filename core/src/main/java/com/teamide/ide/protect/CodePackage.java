package com.teamide.ide.protect;

public class CodePackage {

	public static String PACK = CodePackage.class.getPackage().getName();

	public static String PATH = PACK.replaceAll("\\.", "/");
}
