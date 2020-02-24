package com.teamide.ide.listen;

import com.teamide.client.ClientSession;

public class IDEListenHandler {

	public static String KEY = "IDE_LISTEN";

	public static IDEListen get(ClientSession session) {
		Object object = session.getCache(IDEListenHandler.KEY);
		if (object == null) {
			object = new IDEListen();
			session.setCache(IDEListenHandler.KEY, object);
		}
		return (IDEListen) object;
	}

}
