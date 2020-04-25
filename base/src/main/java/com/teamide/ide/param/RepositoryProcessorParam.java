package com.teamide.ide.param;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.teamide.util.IOUtil;
import com.teamide.util.StringUtil;
import com.teamide.client.ClientSession;

public class RepositoryProcessorParam extends SpaceProcessorParam {

	private final String branch;

	private final File branchFolder;

	private final File branchsFolder;

	private final File sourceFolder;

	protected final String sourcePath;

	private final File pomFile;

	public static final String DEFAULT_BRANCH = "master";

	public RepositoryProcessorParam(RepositoryProcessorParam param) {
		this(param.getSession(), param.getSpaceRootFolder(), param.getSpaceFormat(), param.getBranch());
	}

	public RepositoryProcessorParam(ClientSession session, File spaceRootFolder, SpaceFormatParam spaceFormat,
			String branch) {
		super(session, spaceRootFolder, spaceFormat);

		if (StringUtil.isEmpty(branch)) {
			branch = DEFAULT_BRANCH;
		}
		this.branch = branch;
		this.branchsFolder = new File(this.getSpaceFolder(), "branchs");
		File defaultBranchSourceFolder = new File(this.branchsFolder, DEFAULT_BRANCH + "/source");
		if (!defaultBranchSourceFolder.exists()) {
			defaultBranchSourceFolder.mkdirs();
		}
		this.branchFolder = new File(this.branchsFolder, branch);
		this.sourceFolder = new File(this.branchFolder, "source");
		this.sourcePath = sourceFolder.toURI().getPath();
		this.pomFile = new File(this.sourceFolder, "pom.xml");
	}

	public String getMavenHome(String path) {
		return null;
	}

	public String getDeployerLogName(String token) {
		if (StringUtil.isEmpty(token)) {
			throw new RuntimeException("token is null.");
		}

		return getSpaceName() + "." + token;
	}

	public String getLogName() {

		return getSpaceName();
	}

	public File getDeployerLogFolder() {

		return new File(getLogFolder(), "deployer");
	}

	public File getLogFolder() {

		return new File(getBranchFolder(), "log");
	}

	public File getDeployerFolder() {

		return new File(getBranchFolder(), "deployer");

	}

	public File getTokenDeployerFolder(String token) {
		File folder = getDeployerFolder();
		if (!StringUtil.isEmpty(token)) {
			folder = new File(folder, token);
		}
		return folder;
	}

	public Properties loadProperties(String path) {

		return loadProperties(new File(path));
	}

	public Properties loadProperties(File file) {

		Properties properties = new Properties();
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			properties.load(stream);
		} catch (Exception e) {
		} finally {
			IOUtil.close(stream);
		}
		return properties;
	}

	public void saveProperties(String path, Properties properties) {
		File file = new File(path);
		saveProperties(file, properties);
	}

	public void saveProperties(File file, Properties properties) {

		FileOutputStream stream = null;
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			stream = new FileOutputStream(file);
			properties.store(stream, null);
		} catch (Exception e) {
		} finally {
			IOUtil.close(stream);
		}
	}

	public String getBranch() {
		return branch;
	}

	public File getBranchFolder() {
		return branchFolder;
	}

	public File getBranchsFolder() {
		return branchsFolder;
	}

	public File getSourceFolder() {
		return sourceFolder;
	}

	public File getPomFile() {
		return pomFile;
	}

	public String getPath(File file) {
		if (file == null) {
			return null;
		}

		String filePath = file.toURI().getPath();

		String path = filePath.substring(sourcePath.length());
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;

	}

	public File getFile(String path) {
		if (StringUtil.isEmpty(path) || path.equals("/")) {
			return getSourceFolder();
		}
		return new File(getSourceFolder(), path);

	}

	public boolean sourceContains(File file) throws IOException {
		File sourceFolder = getSourceFolder();
		String canonicalParent = sourceFolder.getCanonicalPath();
		String canonicalChild = file.getCanonicalPath();
		if (canonicalParent.equalsIgnoreCase(canonicalChild)) {
			return false;
		}
		return canonicalChild.regionMatches(true, 0, canonicalParent, 0, canonicalParent.length());
	}

	public File getProjectFolder(String projectPath) {

		if (StringUtil.isEmpty(projectPath) || projectPath.equals("/")) {
			return getSourceFolder();
		}
		return new File(getSourceFolder(), projectPath);
	}

	public File getTargetFolder(String projectPath) {
		return new File(getProjectFolder(projectPath), "target");
	}

	public File getJavaFolder(String projectPath) {
		return new File(getProjectFolder(projectPath), "src/main/java/");
	}

	public File getResourcesFolder(String projectPath) {
		return new File(getProjectFolder(projectPath), "src/main/resources/");

	}

	public File getWebappFolder(String projectPath) {
		return new File(getProjectFolder(projectPath), "src/main/webapp/");
	}

	public boolean isAppJava(String projectPath, String path) {
		File file = getFile(path);
		File folder = getJavaFolder(projectPath);
		return file.getAbsolutePath().indexOf(folder.getAbsolutePath()) == 0;
	}

	public boolean isAppWebapp(String projectPath, String path) {
		File file = getFile(path);
		File folder = getWebappFolder(projectPath);
		return file.getAbsolutePath().indexOf(folder.getAbsolutePath()) == 0;
	}

	public boolean isAppResources(String projectPath, String path) {
		File file = getFile(path);
		File folder = getResourcesFolder(projectPath);
		return file.getAbsolutePath().indexOf(folder.getAbsolutePath()) == 0;
	}
}
