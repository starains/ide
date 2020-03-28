package com.teamide.ide.param;

import com.teamide.client.ClientSession;

public class ProcessorParam {

	private final ClientSession session;

	public ProcessorParam(ProcessorParam param) {
		this(param.getSession());
	}

	public ProcessorParam(ClientSession session) {
		this.session = session;
	}

	public ClientSession getSession() {
		return session;
	}

}
