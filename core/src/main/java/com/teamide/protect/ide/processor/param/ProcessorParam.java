package com.teamide.protect.ide.processor.param;

import com.teamide.ide.client.Client;

public class ProcessorParam {

	private final Client client;

	public ProcessorParam(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

}
