package com.teamide.ide.protect.processor.param;

import com.teamide.client.ClientSession;

public class ProcessorParam {

	private final ClientSession session;

	public ProcessorParam(ClientSession session) {
		this.session = session;
	}

	public ClientSession getSession() {
		return session;
	}

}
