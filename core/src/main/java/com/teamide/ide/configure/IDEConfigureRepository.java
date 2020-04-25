package com.teamide.ide.configure;

import java.io.Serializable;

public class IDEConfigureRepository implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean prohibitstarter;

	public Boolean getProhibitstarter() {
		return prohibitstarter;
	}

	public void setProhibitstarter(Boolean prohibitstarter) {
		this.prohibitstarter = prohibitstarter;
	}
}
