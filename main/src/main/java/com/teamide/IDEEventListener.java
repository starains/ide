package com.teamide;

public class IDEEventListener extends Thread {

	private static final IDEEventListener LISTENER = new IDEEventListener();

	private final IDEServer server = new IDEServer();
	private final long wait = 1000 * 5;

	public static IDEEventListener get() {
		return LISTENER;
	}

	private IDEEventListener() {

	}

	@Override
	public void run() {
		server.startServer();
		while (true) {
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doListener();
		}
	}

	static boolean LISTENER_ING = false;

	public void doListener() {
		if (LISTENER_ING) {
			return;
		}
		LISTENER_ING = true;
		String eventStr = IDEShare.getEvent();
		if (eventStr != null) {

			try {
				IDEEvent event = IDEEvent.valueOf(eventStr);
				doEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LISTENER_ING = false;
	}

	public void doEvent(IDEEvent event) {
		if (event == null) {
			return;
		}
		switch (event) {
		case RESTART:
			server.restartServer();
			break;
		case START:
			server.startServer();
			break;
		case STOP:
			server.stopServer();
			break;
		case UPDATE:
			server.updateFile();
			break;
		}
	}

}
