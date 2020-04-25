package com.teamide.ide.configure;

import com.teamide.ide.service.impl.ConfigureService;

public class IDEConfigure {

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

	private IDEConfigureAccount account = new IDEConfigureAccount();

	private IDEConfigureLogin login = new IDEConfigureLogin();

	private IDEConfigureMailbox mailbox = new IDEConfigureMailbox();

	private IDEConfigureSpace space = new IDEConfigureSpace();

	private IDEConfigureRepository repository = new IDEConfigureRepository();

	public IDEConfigureAccount getAccount() {
		return account;
	}

	public void setAccount(IDEConfigureAccount account) {
		this.account = account;
	}

	public IDEConfigureLogin getLogin() {
		return login;
	}

	public void setLogin(IDEConfigureLogin login) {
		this.login = login;
	}

	public IDEConfigureMailbox getMailbox() {
		return mailbox;
	}

	public void setMailbox(IDEConfigureMailbox mailbox) {
		this.mailbox = mailbox;
	}

	public IDEConfigureSpace getSpace() {
		return space;
	}

	public void setSpace(IDEConfigureSpace space) {
		this.space = space;
	}

	public IDEConfigureRepository getRepository() {
		return repository;
	}

	public void setRepository(IDEConfigureRepository repository) {
		this.repository = repository;
	}

}
