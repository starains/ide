package com.teamide.protect.ide.processor.repository.starter;

public class StarterOption {
	private String name;
	private String language;
	private String mode;
	private String jar;
	private String main;
	private String environmentid;
	private String mavenenvp;
	private String javaenvp;
	private String javaenvironmentid;
	private String mavenenvironmentid;
	private boolean useinternal;
	private String internaltomcat;
	private String hostname;
	private int port;
	private String contextpath;
	private String nodeenvironmentid;
	private String nodecommand;
	private String compilecommand;
	private String startcommand;
	private String stopcommand;
	private String pidfile;

	public String getStopcommand() {
		return stopcommand;
	}

	public void setStopcommand(String stopcommand) {
		this.stopcommand = stopcommand;
	}

	public String getPidfile() {
		return pidfile;
	}

	public void setPidfile(String pidfile) {
		this.pidfile = pidfile;
	}

	public String getCompilecommand() {
		return compilecommand;
	}

	public void setCompilecommand(String compilecommand) {
		this.compilecommand = compilecommand;
	}

	public String getStartcommand() {
		return startcommand;
	}

	public void setStartcommand(String startcommand) {
		this.startcommand = startcommand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getEnvironmentid() {
		return environmentid;
	}

	public void setEnvironmentid(String environmentid) {
		this.environmentid = environmentid;
	}

	public String getMavenenvp() {
		return mavenenvp;
	}

	public void setMavenenvp(String mavenenvp) {
		this.mavenenvp = mavenenvp;
	}

	public String getJavaenvp() {
		return javaenvp;
	}

	public void setJavaenvp(String javaenvp) {
		this.javaenvp = javaenvp;
	}

	public String getJavaenvironmentid() {
		return javaenvironmentid;
	}

	public void setJavaenvironmentid(String javaenvironmentid) {
		this.javaenvironmentid = javaenvironmentid;
	}

	public String getMavenenvironmentid() {
		return mavenenvironmentid;
	}

	public void setMavenenvironmentid(String mavenenvironmentid) {
		this.mavenenvironmentid = mavenenvironmentid;
	}

	public boolean isUseinternal() {
		return useinternal;
	}

	public void setUseinternal(boolean useinternal) {
		this.useinternal = useinternal;
	}

	public String getInternaltomcat() {
		return internaltomcat;
	}

	public void setInternaltomcat(String internaltomcat) {
		this.internaltomcat = internaltomcat;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getContextpath() {
		return contextpath;
	}

	public void setContextpath(String contextpath) {
		this.contextpath = contextpath;
	}

	public String getNodeenvironmentid() {
		return nodeenvironmentid;
	}

	public void setNodeenvironmentid(String nodeenvironmentid) {
		this.nodeenvironmentid = nodeenvironmentid;
	}

	public String getNodecommand() {
		return nodecommand;
	}

	public void setNodecommand(String nodecommand) {
		this.nodecommand = nodecommand;
	}

}
