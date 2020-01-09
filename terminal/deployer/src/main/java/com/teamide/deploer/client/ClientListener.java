package com.teamide.deploer.client;

import com.alibaba.fastjson.JSONObject;
import com.teamide.deploer.Listener;
import com.teamide.deploer.IDEOption.Client;
import com.teamide.deploer.processor.TaskBean;
import com.teamide.deploer.util.StringUtil;

public class ClientListener extends Listener {

	public final Client client;

	protected ClientService service;

	public ClientListener(String name, String token, Client client) throws Exception {
		super(name, token);

		this.client = client;

		if (client == null) {
			throw new Exception("client is null.");
		}
		if (StringUtil.isEmpty(client.getServer())) {
			throw new Exception("client.server is null.");
		}

		this.service = new ClientService(this);
	}

	@Override
	public void listen() throws Exception {
		JSONObject json = new JSONObject();
		JSONObject result = service.listen(json);
		if (result != null) {
			TaskBean task = result.toJavaObject(TaskBean.class);
			onTask(task);
		}
	}

}
