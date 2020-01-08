package com.teamide;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class IDEShare {

	private static final Map<String, Object> CACHE = new HashMap<String, Object>();

	private static final Queue<String> EVENTS = new ArrayDeque<String>();

	public static void put(String key, Object value) {
		CACHE.put(key, value);
	}

	public static Object get(String key) {
		return CACHE.get(key);
	}

	public static void addEvent(String... events) {
		if (events != null) {
			for (String event : events) {
				EVENTS.add(event);
				IDEEventListener.get().doListener();
			}
		}
	}

	public static String getEvent() {
		String poll = EVENTS.poll();
		return poll;
	}
}
