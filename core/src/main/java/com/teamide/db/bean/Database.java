package com.teamide.db.bean;

import javax.persistence.Column;

/**
 * 数据库配置
 * 
 * @author ZhuLiang
 *
 */
public class Database {

	@Column(name = "driver")
	private String driver;

	@Column(name = "url")
	private String url;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "initializeclass")
	private String initializeclass;

	@Column(name = "validationquery")
	private String validationquery;

	@Column(name = "initialsize")
	private Integer initialsize;

	@Column(name = "maxtotal")
	private Integer maxtotal;

	@Column(name = "minidle")
	private Integer minidle;

	@Column(name = "maxidle")
	private Integer maxidle;

	@Column(name = "maxwaitmillis")
	private Integer maxwaitmillis;

	@Column(name = "showsql")
	private Boolean showsql;

	@Column(name = "formatsql")
	private Boolean formatsql;

	@Column(name = "maxactive")
	private Integer maxactive;

	@Column(name = "maxwait")
	private Integer maxwait;

	public String getDriver() {

		return driver;
	}

	public void setDriver(String driver) {

		this.driver = driver;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public String getUsername() {

		return username;
	}

	public void setUsername(String username) {

		this.username = username;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public String getInitializeclass() {

		return initializeclass;
	}

	public void setInitializeclass(String initializeclass) {

		this.initializeclass = initializeclass;
	}

	public String getValidationquery() {

		return validationquery;
	}

	public void setValidationquery(String validationquery) {

		this.validationquery = validationquery;
	}

	public Integer getInitialsize() {

		return initialsize;
	}

	public void setInitialsize(Integer initialsize) {

		this.initialsize = initialsize;
	}

	public Integer getMaxtotal() {

		return maxtotal;
	}

	public void setMaxtotal(Integer maxtotal) {

		this.maxtotal = maxtotal;
	}

	public Integer getMinidle() {

		return minidle;
	}

	public void setMinidle(Integer minidle) {

		this.minidle = minidle;
	}

	public Integer getMaxidle() {

		return maxidle;
	}

	public void setMaxidle(Integer maxidle) {

		this.maxidle = maxidle;
	}

	public Integer getMaxwaitmillis() {

		return maxwaitmillis;
	}

	public void setMaxwaitmillis(Integer maxwaitmillis) {

		this.maxwaitmillis = maxwaitmillis;
	}

	public Boolean getShowsql() {
		return showsql;
	}

	public void setShowsql(Boolean showsql) {
		this.showsql = showsql;
	}

	public Boolean getFormatsql() {
		return formatsql;
	}

	public void setFormatsql(Boolean formatsql) {
		this.formatsql = formatsql;
	}

	public Integer getMaxactive() {

		return maxactive;
	}

	public void setMaxactive(Integer maxactive) {

		this.maxactive = maxactive;
	}

	public Integer getMaxwait() {

		return maxwait;
	}

	public void setMaxwait(Integer maxwait) {

		this.maxwait = maxwait;
	}

}
