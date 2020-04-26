package com.teamide.ide.bean;

import javax.persistence.Column;
import javax.persistence.Table;

import com.teamide.ide.constant.TableInfoConstant;

@Table(name = TableInfoConstant.CONFIGURE_INFO)
public class ConfigureBean extends BaseBean {

	@Column(name = "accountconfigure", length = 1000)
	private String accountconfigure;

	@Column(name = "mailboxconfigure", length = 1000)
	private String mailboxconfigure;

	@Column(name = "loginconfigure", length = 1000)
	private String loginconfigure;

	@Column(name = "spaceconfigure", length = 1000)
	private String spaceconfigure;

	@Column(name = "repositoryconfigure", length = 1000)
	private String repositoryconfigure;

	@Column(name = "nginxconfigure", length = 1000)
	private String nginxconfigure;

	public String getNginxconfigure() {
		return nginxconfigure;
	}

	public void setNginxconfigure(String nginxconfigure) {
		this.nginxconfigure = nginxconfigure;
	}

	public String getAccountconfigure() {
		return accountconfigure;
	}

	public void setAccountconfigure(String accountconfigure) {
		this.accountconfigure = accountconfigure;
	}

	public String getMailboxconfigure() {
		return mailboxconfigure;
	}

	public void setMailboxconfigure(String mailboxconfigure) {
		this.mailboxconfigure = mailboxconfigure;
	}

	public String getLoginconfigure() {
		return loginconfigure;
	}

	public void setLoginconfigure(String loginconfigure) {
		this.loginconfigure = loginconfigure;
	}

	public String getSpaceconfigure() {
		return spaceconfigure;
	}

	public void setSpaceconfigure(String spaceconfigure) {
		this.spaceconfigure = spaceconfigure;
	}

	public String getRepositoryconfigure() {
		return repositoryconfigure;
	}

	public void setRepositoryconfigure(String repositoryconfigure) {
		this.repositoryconfigure = repositoryconfigure;
	}
}
