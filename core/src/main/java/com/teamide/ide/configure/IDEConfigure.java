package com.teamide.ide.configure;

import javax.persistence.Column;

import com.teamide.ide.bean.BaseBean;
import com.teamide.protect.ide.service.ConfigureService;

public class IDEConfigure extends BaseBean {

	private static IDEConfigure CONFIGURE;

	public static IDEConfigure get() {

		if (CONFIGURE == null) {
			loadConfigure();
		}
		return CONFIGURE;
	}

	public static final IDEConfigure loadConfigure() {

		CONFIGURE = new ConfigureService().get();
		return CONFIGURE;
	}

	@Column(name = "openregister")
	private Boolean openregister;

	@Column(name = "openaccountlock")
	private Boolean openaccountlock;

	@Column(name = "passworderrorlimit")
	private Integer passworderrorlimit;

	@Column(name = "defaultpassword")
	private String defaultpassword;

	public Boolean getOpenregister() {

		return openregister;
	}

	public void setOpenregister(Boolean openregister) {

		this.openregister = openregister;
	}

	public Boolean getOpenaccountlock() {

		return openaccountlock;
	}

	public void setOpenaccountlock(Boolean openaccountlock) {

		this.openaccountlock = openaccountlock;
	}

	public Integer getPassworderrorlimit() {

		return passworderrorlimit;
	}

	public void setPassworderrorlimit(Integer passworderrorlimit) {

		this.passworderrorlimit = passworderrorlimit;
	}

	public String getDefaultpassword() {

		return defaultpassword;
	}

	public void setDefaultpassword(String defaultpassword) {

		this.defaultpassword = defaultpassword;
	}

}
