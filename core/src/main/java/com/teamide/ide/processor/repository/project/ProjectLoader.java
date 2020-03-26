package com.teamide.ide.processor.repository.project;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;

import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.hanlder.RepositoryHanlder;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class ProjectLoader {

	protected final RepositoryProcessorParam param;

	private final List<ProjectBean> project_caches = new ArrayList<ProjectBean>();

	private final Map<String, ProjectBean> project_map = new HashMap<String, ProjectBean>();

	public ProjectLoader(RepositoryProcessorParam param) {
		this.param = param;
		init();
	}

	public void appendProjects(File folder) {

		if (folder != null && folder.isFile()) {
			return;
		}
		File pomFile = new File(folder, "pom.xml");
		if (pomFile.exists()) {
			String path = param.getPath(folder);
			if (project_map.get(path) == null) {
				ProjectBean project = createProject(path);
				if (project != null) {
					project_map.put(path, project);
					project_caches.add(project);
				}
			}
		}

		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				appendProjects(file);
			}
		}
	}

	public void init() {
		project_caches.clear();
		project_map.clear();

		ProjectBean project = createProject("");
		if (project != null) {
			project_caches.add(project);
			project_map.put("", project);
		}
		appendProjects(param.getSourceFolder());

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
		if (StringUtil.isEmpty(projectPath)) {
			project.setName(this.param.getSpace().getName());
		} else {
			project.setName(projectFolder.getName());
		}
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

		ProjectBean projectBean = getProject(path);
		if (projectBean != null) {
			FileBean folderBean = loadFiles(this.param.getFile(path), projectBean);
			if (folderBean != null) {
				projectBean.setFiles(folderBean.getFiles());

			}
			ProjectAppLoader appLoader = new ProjectAppLoader(param);

			projectBean.setApp(appLoader.loadApp(projectBean.getPath()));
		}
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

				if (file.isDirectory()) {
					if (file.getName().equals(".git")) {
						continue;
					} else if (file.getName().equals("node_modules")) {
						continue;
					}
				}

				path = this.param.getPath(file);
				if (projectBean != null) {
					if (!isProjectFile(projectBean, path)) {
						continue;
					}
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

	public boolean isProjectFile(ProjectBean project, final String path) {

		ProjectBean lastP = null;
		int lastLength = 0;
		for (ProjectBean p : project_caches) {
			if (StringUtil.isNotEmpty(p.getPath())) {
				if (path.startsWith(p.getPath() + "/") || path.equals(p.getPath())) {
					int length = p.getPath().length();
					if (length > lastLength) {
						lastLength = length;
						lastP = p;
					}
				}

			}
		}
		if (lastP == null) {
			lastP = getProject("");
		}
		boolean flag = lastP == project;

		return flag;
	}

	public ProjectBean getProject(String key) {
		if (StringUtil.isEmpty(key)) {
			return project_map.get("");
		}
		return project_map.get(key);
	}

	public List<ProjectBean> getProjects() {
		return project_caches;
	}
}
