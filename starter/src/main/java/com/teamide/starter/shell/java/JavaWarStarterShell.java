package com.teamide.starter.shell.java;

import com.teamide.starter.StarterParam;

public class JavaWarStarterShell extends JavaInternalTomcatStarterShell {

	public JavaWarStarterShell(StarterParam param) {
		super(param);
	}

	@Override
	public void copyWorkFolder() throws Exception {
		outAppWar(param.starterServerFolder);
	}

}
