package com.teamide.ide.listen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class IDEListen {

	private JSONArray events = new JSONArray();

	private static final long WAIT_TIMEOUT = 1000 * 60 * 5;

	enum Status {
		NONE, WAITING
	}

	private Status status = Status.NONE;

	public JSONObject waitListen() throws InterruptedException {
		JSONObject result = new JSONObject();
		try {
			if (this.events.size() > 0) {

			} else {
				synchronized (this) {
					this.status = Status.WAITING;
					this.wait(WAIT_TIMEOUT);
				}
			}
		} finally {
			result.put("events", this.events.clone());
			this.events.clear();
			this.status = Status.NONE;
		}
		return result;
	}

	public void setEvent(JSONObject event) {
		this.events.add(event);
		if (this.status == Status.WAITING) {
			synchronized (this) {
				this.notify();
			}
		}
	}

}
