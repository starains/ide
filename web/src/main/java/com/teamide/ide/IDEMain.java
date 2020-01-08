package com.teamide.ide;

public class IDEMain {

	public static void main(String[] args) {
		IDEServer server = new IDEServer(args);
		new Thread(server).start();
	}
}
