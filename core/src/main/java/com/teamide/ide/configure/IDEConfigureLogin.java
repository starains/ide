package com.teamide.ide.configure;

import java.io.Serializable;

public class IDEConfigureLogin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean openlock;

	private Integer faillimit;

	private Boolean openlockip;

	private Integer unlockminute;

	private Integer unlockipminute;

	public Boolean getOpenlock() {
		return openlock;
	}

	public void setOpenlock(Boolean openlock) {
		this.openlock = openlock;
	}

	public Integer getUnlockipminute() {
		return unlockipminute;
	}

	public void setUnlockipminute(Integer unlockipminute) {
		this.unlockipminute = unlockipminute;
	}

	public Boolean getOpenlockip() {
		return openlockip;
	}

	public void setOpenlockip(Boolean openlockip) {
		this.openlockip = openlockip;
	}

	public Integer getFaillimit() {
		return faillimit;
	}

	public void setFaillimit(Integer faillimit) {
		this.faillimit = faillimit;
	}

	public Integer getUnlockminute() {
		return unlockminute;
	}

	public void setUnlockminute(Integer unlockminute) {
		this.unlockminute = unlockminute;
	}

}
