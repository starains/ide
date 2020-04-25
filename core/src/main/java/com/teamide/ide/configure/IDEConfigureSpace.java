package com.teamide.ide.configure;

import java.io.Serializable;

public class IDEConfigureSpace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean prohibitcreate;

	private Integer maxquantity;

	public Boolean getProhibitcreate() {
		return prohibitcreate;
	}

	public void setProhibitcreate(Boolean prohibitcreate) {
		this.prohibitcreate = prohibitcreate;
	}

	public Integer getMaxquantity() {
		return maxquantity;
	}

	public void setMaxquantity(Integer maxquantity) {
		this.maxquantity = maxquantity;
	}
}
