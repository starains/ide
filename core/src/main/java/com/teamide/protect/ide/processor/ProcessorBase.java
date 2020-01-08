package com.teamide.protect.ide.processor;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.protect.ide.engine.EngineCache;
import com.teamide.protect.ide.engine.EngineSession;
import com.teamide.protect.ide.processor.enums.MessageLevel;
import com.teamide.protect.ide.processor.enums.ProcessorType;
import com.teamide.protect.ide.processor.param.ProcessorParam;
import com.teamide.protect.ide.service.SpaceEventService;

public class ProcessorBase {
	protected final EngineSession session;

	protected final ProcessorParam param;

	public ProcessorBase(EngineSession session, ProcessorParam param) {
		this.session = session;
		this.param = param;
	}

	public void appendEvent(SpaceEventBean spaceEventBean) {
		new Thread() {

			@Override
			public void run() {
				try {
					new SpaceEventService().insert(param.getClient(), spaceEventBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	public void outData(String messageID, String model, Object value) {
		JSONObject data = new JSONObject();
		data.put("model", model);
		data.put("value", value);

		out(messageID, createDataMessage(model, value));
	}

	public JSONObject createDataMessage(String model, Object value) {
		JSONObject data = new JSONObject();
		data.put("model", model);
		data.put("value", value);

		JSONObject message = new JSONObject();
		message.put("type", ProcessorType.DATA.getValue());
		message.put("data", data);
		return message;
	}

	public void outMessage(MessageLevel level, String msg) {
		JSONObject data = new JSONObject();
		data.put("level", level.getValue());
		data.put("message", msg);

		JSONObject message = new JSONObject();
		message.put("type", ProcessorType.MESSAGE.getValue());
		message.put("data", data);

		sendMessage(null, message);
	}

	public void out(String messageID, String type, Object data) {

		JSONObject message = new JSONObject();
		message.put("type", type);
		message.put("data", data);

		sendMessage(messageID, message);
	}

	public void out(String messageID, JSONObject message) {

		sendMessage(messageID, message);
	}

	public void outBySessionid(JSONObject message) {
		Set<EngineSession> sessions = EngineCache.getBySessionid(session.httpSession.getId());
		for (EngineSession session : sessions) {
			session.sendMessage(message);
		}
	}

	public void outToAll(JSONObject message) {
		Set<EngineSession> sessions = EngineCache.getSessions();
		for (EngineSession session : sessions) {
			session.sendMessage(message);
		}
	}

	public void sendMessage(String messageID, JSONObject message) {
		message.put("messageID", messageID);
		session.sendMessage(message);
	}
}
