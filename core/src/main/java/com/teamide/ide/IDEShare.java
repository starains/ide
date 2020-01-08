package com.teamide.ide;

import java.lang.reflect.Method;

public class IDEShare {

	public static Class<?> getIDEShareClass() {
		try {
			Class<?> mainClass = ClassLoader.getSystemClassLoader().loadClass("com.coospro.IDEShare");
			return mainClass;
		} catch (ClassNotFoundException e) {

		}
		return null;
	}

	public static void put(String key, Object value) {
		try {
			Class<?> clazz = getIDEShareClass();
			if (clazz != null) {
				Method method = clazz.getMethod("put", new Class[] { String.class, Object.class });
				method.invoke(null, new Object[] { key, value });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object get(String key) {
		try {
			Class<?> clazz = getIDEShareClass();
			if (clazz != null) {
				Method method = clazz.getMethod("get", new Class[] { String.class });
				return method.invoke(null, new Object[] { key });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addEvent(String... events) {
		try {
			Class<?> clazz = getIDEShareClass();
			if (clazz != null) {
				Method method = clazz.getMethod("addEvent", new Class[] { String[].class });
				method.invoke(null, new Object[] { events });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
