package com.teamide.ide.protect.processor.repository.project;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.maven.model.Model;

import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.hanlder.RepositoryHanlder;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class ProjectLoader {

	protected final RepositoryProcessorParam param;

	private final List<ProjectBean> projects = new ArrayList<ProjectBean>();

	public ProjectLoader(RepositoryProcessorParam param) {
		this.param = param;
		init();
	}

	public void init() {
		projects.clear();
		Model model = RepositoryHanlder.getPomModel(param.getPomFile());

		ProjectBean project = createProject("");
		if (project != null) {
			projects.add(project);
		}
		if (model != null) {
			List<String> list = model.getModules();
			if (list != null) {
				for (String path : list) {
					project = createProject(path);
					if (project != null) {
						projects.add(project);
					}

				}
			}
		}

	}

	public ProjectBean createProject(final String path) {
		File projectFolder = new File(this.param.getSourceFolder(), path);
		if (!projectFolder.exists()) {
			return null;
		}
		if (!projectFolder.isDirectory()) {
			return null;
		}
		String projectPath = this.param.getPath(projectFolder);
		ProjectBean project = new ProjectBean();
		project.setName(projectFolder.getName());
		project.setPath(projectPath);
		if (StringUtil.isEmpty(projectPath)) {
			project.setRoot(true);
		} else {
			project.setRoot(false);
		}
		File pomFile = new File(projectFolder, "pom.xml");
		if (pomFile.exists()) {
			project.setMaven(true);
			Model model = RepositoryHanlder.getPomModel(pomFile);
			if (model != null) {
				project.setPackaging(model.getPackaging());
			}
		}
		return project;
	}

	public ProjectBean loadProject(String path) throws Exception {

		ProjectBean projectBean = getProjectByPath(path);
		FileBean folderBean = loadFiles(this.param.getFile(path), projectBean);
		if (folderBean != null) {
			projectBean.setFiles(folderBean.getFiles());

		}
		ProjectAppLoader appLoader = new ProjectAppLoader(param);

		projectBean.setApp(appLoader.loadApp(projectBean.getPath()));
		return projectBean;
	}

	public FileBean loadFiles(File folder, ProjectBean projectBean) {

		String path = this.param.getPath(folder);
		FileBean folderBean = load(path);
		if (folderBean == null) {
			return folderBean;
		}
		List<FileBean> fileBeans = new ArrayList<FileBean>();
		folderBean.setFiles(fileBeans);
		if (folder != null && folder.exists() && folder.isDirectory()) {
			List<File> listFiles = Arrays.asList(folder.listFiles());
			FileUtil.orderByName(listFiles);
			for (int i = 0; i < listFiles.size(); i++) {
				File file = listFiles.get(i);
				path = this.param.getPath(file);
				if (projectBean != null) {
					ProjectBean fileProjectBean = getProjectByPath(path);
					if (fileProjectBean != projectBean) {
						continue;
					}
				}
				if (file.getName().equals(".git")) {
					continue;
				}
				if (file.getName().equals("node_modules")) {
					continue;
				}
				FileBean fileBean = load(path);
				if (fileBean.isDirectory()) {
					FileBean childFolderBean = loadFiles(file, projectBean);
					fileBean.setFiles(childFolderBean.getFiles());
				}
				fileBean.setSequence(i);
				fileBeans.add(fileBean);
			}
		}
		return folderBean;
	}

	public FileBean readFile(final String path) throws Exception {

		FileBean fileBean = load(path);
		try {
			if (fileBean == null) {
				fileBean = new FileBean();
				fileBean.setPath(path);
			}
			String content = getContent(path);
			fileBean.setContent(content);
		} catch (Exception e) {
			fileBean.setErrmsg(e.getMessage());
		}
		return fileBean;

	}

	static long MAX_LENGTH = 1024 * 1024 * 5;

	public static boolean isBinary(File file) {
		boolean isBinary = false;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			long len = file.length();
			for (int j = 0; j < (int) len; j++) {
				int t = fin.read();
				if (t < 32 && t != 9 && t != 10 && t != 13) {
					isBinary = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (Exception e) {
				}
			}
		}
		return isBinary;
	}

	public String getContent(final String path) throws Exception {
		File file = this.param.getFile(path);
		if (file == null || !file.exists()) {
			throw new Exception("文件【" + path + "】不存在.");
		}
		if (!file.isFile()) {
			throw new Exception("路径【" + path + "】非文件，无法打开.");
		}
		long length = file.length();
		if (length > MAX_LENGTH) {
			throw new Exception("无法加载大于" + (MAX_LENGTH / 1024) + "（KB）的文件.");
		}
		if (isBinary(file)) {
			throw new Exception("暂不支持二进制文件的查看.");
		}
		byte[] bytes = FileUtil.read(file);
		String content = null;
		if (bytes != null) {
			if (FileUtil.isImage(file)) {
				bytes = encoder.encode(bytes);
				content = new String(bytes, "UTF-8");
			} else {
				content = new String(bytes, "UTF-8");
			}
		}
		return content;
	}

	static final Base64.Decoder decoder = Base64.getUrlDecoder();
	static final Base64.Encoder encoder = Base64.getUrlEncoder();

	public FileBean load(final String path) {

		File file = new File(this.param.getSourceFolder(), path);
		if (!file.exists()) {
			return null;
		}
		String filePath = this.param.getPath(file);

		FileBean fileBean = new FileBean();

		String name = file.getName();
		fileBean.setName(name);
		fileBean.setFile(file.isFile());
		fileBean.setDirectory(file.isDirectory());
		if (file.isFile()) {
			int lastIndex = name.lastIndexOf(".");
			String type = name;
			if (lastIndex >= 0 && lastIndex < name.length()) {
				type = name.substring(lastIndex + 1);
			}
			fileBean.setType(type);
		}
		fileBean.setPath(filePath);
		if (file.isFile()) {
			fileBean.setLength(file.length());
		}

		return fileBean;
	}

	public ProjectBean getProjectByPath(final String path) {
		File file = new File(this.param.getSourceFolder(), path);

		for (ProjectBean project : projects) {
			if (project.isRoot()) {
				continue;
			}

			File projectFolder = new File(this.param.getSourceFolder(), project.getPath());
			if (isProject(file, projectFolder)) {
				return project;
			}
		}
		for (ProjectBean project : projects) {
			if (project.isRoot()) {
				return project;
			}

		}
		return null;
	}

	public boolean isProject(final File file, final File projectFolder) {
		String filePath = file.toURI().getPath();

		String projectPath = projectFolder.toURI().getPath();
		if (projectPath.equals(filePath)) {
			return true;
		}
		if (!projectPath.endsWith("/")) {
			projectPath += "/";
		}
		if (filePath.startsWith(projectPath)) {
			return true;
		}
		return false;
	}

	public List<ProjectBean> getProjects() {
		return projects;
	}
}
