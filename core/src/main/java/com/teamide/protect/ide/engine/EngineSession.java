package com.teamide.protect.ide.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.IDGenerateUtil;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.client.ClientHandler;
import com.teamide.protect.ide.handler.SpaceHandler;
import com.teamide.protect.ide.processor.Processor;
import com.teamide.protect.ide.processor.RepositoryProcessor;
import com.teamide.protect.ide.processor.SpaceProcessor;
import com.teamide.protect.ide.processor.param.ProcessorParam;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.param.SpaceProcessorParam;
import com.teamide.protect.ide.util.TokenUtil;

public class EngineSession implements com.teamide.ide.engine.EngineSession {

	public final Session websocketSession;

	public final HttpSession httpSession;

	public final String token;

	public final String spaceid;

	public final String branch;

	public final Processor processor;

	public EngineSession(HttpSession httpSession, Session websocketSession, String token) {
		this.httpSession = httpSession;
		this.websocketSession = websocketSession;
		this.token = token;
		JSONObject json = TokenUtil.getJSON(token);
		Client client = ClientHandler.get(httpSession);
		Processor processor = null;
		String spaceid = null;
		String branch = null;
		if (json != null) {

			spaceid = json.getString("spaceid");
			SpaceBean space = SpaceHandler.get(spaceid);
			if (space != null) {
				if (SpaceHandler.isRepositorys(space)) {
					branch = json.getString("branch");
					if (StringUtil.isEmpty(branch)) {
						branch = "master";
					}
					RepositoryProcessorParam param = new RepositoryProcessorParam(client, spaceid, branch);
					processor = new RepositoryProcessor(this, param);
				} else {
					SpaceProcessorParam param = new SpaceProcessorParam(client, spaceid);
					processor = new SpaceProcessor(this, param);
				}
			}
		}
		if (processor == null) {
			ProcessorParam param = new ProcessorParam(client);
			processor = new Processor(this, param);
		}
		this.spaceid = spaceid;
		this.branch = branch;
		this.processor = processor;
	}

	@Override
	public void onOpen() {
		EngineCache.add(this);
	}

	@Override
	public void onClose() {
		EngineCache.remove(this);
	}

	public final Map<String, String> in_cache = new HashMap<String, String>();

	@Override
	public void onMessage(String message) {

		if (StringUtil.isEmpty(message)) {
			return;
		}
		JSONObject json = JSONObject.parseObject(message);
		if (json == null || json.size() == 0) {
			return;
		}
		if (this.processor != null) {
			String messageID = json.getString("messageID");
			this.processor.on(messageID, json);
		}

	}

	@Override
	public void sendMessage(JSONObject message) {
		if (StringUtil.isEmpty(message.getString("messageID"))) {
			message.put("messageID", IDGenerateUtil.generateShort());
		}
		if (!websocketSession.isOpen()) {
			return;
		}
		try {
			websocketSession.getBasicRemote().sendText(message.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
