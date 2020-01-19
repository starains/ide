package com.teamide.ide.protect.bean;

public class AppOption {

	private String modelpath;

	private String javapath;

	private String resourcepath;

	private String basepackage;

	private String factorypackage;

	private String dictionarypackage;

	private String controllerpackage;

	private String daopackage;

	private String servicepackage;

	private String jdbcpath;

	private String jdbcdirectorypath;

	private boolean usespringannotation;

	public String getFactorypackage() {
		return factorypackage;
	}

	public void setFactorypackage(String factorypackage) {
		this.factorypackage = factorypackage;
	}

	public String getJdbcpath() {
		return jdbcpath;
	}

	public void setJdbcpath(String jdbcpath) {
		this.jdbcpath = jdbcpath;
	}

	public String getJdbcdirectorypath() {
		return jdbcdirectorypath;
	}

	public void setJdbcdirectorypath(String jdbcdirectorypath) {
		this.jdbcdirectorypath = jdbcdirectorypath;
	}

	public String getDictionarypackage() {
		return dictionarypackage;
	}

	public void setDictionarypackage(String dictionarypackage) {
		this.dictionarypackage = dictionarypackage;
	}

	public boolean isUsespringannotation() {
		return usespringannotation;
	}

	public void setUsespringannotation(boolean usespringannotation) {
		this.usespringannotation = usespringannotation;
	}

	public String getControllerpackage() {
		return controllerpackage;
	}

	public void setControllerpackage(String controllerpackage) {
		this.controllerpackage = controllerpackage;
	}

	public String getDaopackage() {
		return daopackage;
	}

	public void setDaopackage(String daopackage) {
		this.daopackage = daopackage;
	}

	public String getServicepackage() {
		return servicepackage;
	}

	public void setServicepackage(String servicepackage) {
		this.servicepackage = servicepackage;
	}

	public String getModelpath() {
		return modelpath;
	}

	public void setModelpath(String modelpath) {
		this.modelpath = modelpath;
	}

	public String getJavapath() {
		return javapath;
	}

	public void setJavapath(String javapath) {
		this.javapath = javapath;
	}

	public String getResourcepath() {
		return resourcepath;
	}

	public void setResourcepath(String resourcepath) {
		this.resourcepath = resourcepath;
	}

	public String getBasepackage() {
		return basepackage;
	}

	public void setBasepackage(String basepackage) {
		this.basepackage = basepackage;
	}
}
