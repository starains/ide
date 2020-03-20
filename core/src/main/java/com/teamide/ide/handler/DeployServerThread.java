package com.teamide.ide.handler;

import com.alibaba.fastjson.JSONObject;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.ide.bean.DeployServerBean;
import com.teamide.ide.enums.DeployServerMode;
import com.teamide.ide.enums.DeployServerStatus;
import com.teamide.util.StringUtil;

public class DeployServerThread extends Thread {

	private final DeployServerBean server;

	private final long WAIT = 5 * 1000;

	private boolean stop = false;

	public DeployServerThread(DeployServerBean server) {
		this.server = server;
		this.start();
	}

	public void destroy() {
		this.stop = true;
	}

	@Override
	public void run() {

		while (true) {
			if (stop) {
				break;
			}

			check();

			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void check() {
		try {
			if (!DeployServerMode.SERVER.getValue().equalsIgnoreCase(this.server.getMode())) {
				return;
			}

			String url = DeployServerHandler.getCheckUrl(this.server);
			HttpRequest request = HttpRequest.get(url);
			HttpResponse response = request.execute();
			String res = response.body();
			if (StringUtil.isNotEmpty(res)) {
				JSONObject json = JSONObject.parseObject(res);
				if (json.getIntValue("errcode") != 0) {
					throw new Exception(json.getString("errmsg"));
				}
			} else {
				throw new Exception("response is null.");
			}
			this.server.setErrmsg(null);
			this.server.setStatus(DeployServerStatus.ONLINE);
		} catch (Exception e) {
			this.server.setStatus(DeployServerStatus.OFFLINE);
			this.server.setErrmsg(e.getMessage());
		}
	}

	public DeployServerBean getServer() {
		return server;
	}
}
