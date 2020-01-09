package com.teamide.terminal;

import java.io.File;

public class TerminalParam {

	public boolean backstage;

	public File workFolder;

	public File startShellFile;

	public File stopShellFile;

	public File statusFile;

	public File pidFile;

	public File startShellPidFile;

	public File logFile;

	public File eventFile;

	public File timestampFile;

	public File getWorkFolder() {
		return workFolder;
	}

	public void setWorkFolder(File workFolder) {
		this.workFolder = workFolder;
	}

	public File getStartShellFile() {
		return startShellFile;
	}

	public void setStartShellFile(File startShellFile) {
		this.startShellFile = startShellFile;
	}

	public File getStopShellFile() {
		return stopShellFile;
	}

	public void setStopShellFile(File stopShellFile) {
		this.stopShellFile = stopShellFile;
	}

	public File getStatusFile() {
		return statusFile;
	}

	public void setStatusFile(File statusFile) {
		this.statusFile = statusFile;
	}

	public File getPidFile() {
		return pidFile;
	}

	public void setPidFile(File pidFile) {
		this.pidFile = pidFile;
	}

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	public File getTimestampFile() {
		return timestampFile;
	}

	public void setTimestampFile(File timestampFile) {
		this.timestampFile = timestampFile;
	}

	public File getEventFile() {
		return eventFile;
	}

	public void setEventFile(File eventFile) {
		this.eventFile = eventFile;
	}

	public File getStartShellPidFile() {
		return startShellPidFile;
	}

	public void setStartShellPidFile(File startShellPidFile) {
		this.startShellPidFile = startShellPidFile;
	}

	public boolean isBackstage() {
		return backstage;
	}

	public void setBackstage(boolean backstage) {
		this.backstage = backstage;
	}
}
