package com.teamide.terminal;

public interface TerminalProcessListener {

	public void onStart(long pid);

	public void onStop();

	public void onLog(String line);

}
