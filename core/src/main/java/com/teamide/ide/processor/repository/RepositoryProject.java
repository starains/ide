package com.teamide.ide.processor.repository;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.ProjectSetting;
import com.teamide.util.FileUtil;
import com.teamide.util.StringUtil;

public class RepositoryProject extends RepositoryBase {

	public RepositoryProject(RepositoryProcessorParam param) {

		super(param);
	}

	static String setting = "project.setting";
	static String git = "project.git";
	static String starter = "starter";
	static String plugin = "plugin";

	public JSONObject readGit() {
		File file = getGitFile();
		try {
			if (file.exists()) {

				String content = new String(FileUtil.read(file));
				JSONObject option = JSONObject.parseObject(content);
				return option;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveGit(JSONObject option) throws Exception {
		File file = getGitFile();
		if (option == null) {
			if (file.exists()) {
				file.delete();
			}
		}
		FileUtil.write(option.toJSONString().getBytes(), file);
	}

	public JSONObject readPlugin(String projectPath, String name) {
		File file = getPluginFile(projectPath, name);
		try {
			if (file.exists()) {

				String content = new String(FileUtil.read(file));
				JSONObject option = JSONObject.parseObject(content);
				return option;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void savePlugin(String projectPath, String name, JSONObject option) throws Exception {
		File file = getPluginFile(projectPath, name);
		if (option == null) {
			if (file.exists()) {
				file.delete();
			}
		}
		FileUtil.write(option.toJSONString().getBytes(), file);
	}

	public JSONObject readStarter(String projectPath, String name) {
		File starterFile = getStarterFile(projectPath, name);
		try {
			if (starterFile.exists()) {

				String content = new String(FileUtil.read(starterFile));
				JSONObject option = JSONObject.parseObject(content);
				return option;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getStarters(String projectPath) {
		File starterFolder = getStarterFolder(projectPath);
		JSONArray res = new JSONArray();
		try {
			if (starterFolder.exists()) {
				File[] files = starterFolder.listFiles();
				for (File file : files) {
					JSONObject one = readStarter(projectPath, file.getName());
					if (one != null) {
						res.add(one);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public void saveStarter(String projectPath, String name, JSONObject option) throws Exception {
		File starterFile = getStarterFile(projectPath, name);
		if (option == null) {
			if (starterFile.exists()) {
				starterFile.delete();
			}
		}
		FileUtil.write(option.toJSONString().getBytes(), starterFile);
	}

	public ProjectSetting readSettingByName(String project) {
		File settingFile = getSettingFileByName(project);
		ProjectSetting setting = readSetting(settingFile);
		try {
			if (settingFile.exists()) {
				String xml = new String(FileUtil.read(settingFile));
				setting = xmlToBean(xml, ProjectSetting.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (setting == null && StringUtil.isEmpty(project)) {
				setting = new ProjectSetting();
				setting.setName(param.getSpaceName());
				setting.setPath("");
			}
		}
		return setting;
	}

	public ProjectSetting readSetting(File settingFile) {
		ProjectSetting setting = null;
		try {
			if (settingFile.exists()) {
				String xml = new String(FileUtil.read(settingFile));
				setting = xmlToBean(xml, ProjectSetting.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setting;
	}

	public void saveSetting(String projectPath, ProjectSetting setting) throws Exception {
		File settingFile = getSettingFileByPath(projectPath);
		if (setting == null || StringUtil.isEmpty(setting.getName())) {
			if (settingFile.exists()) {
				settingFile.delete();
			}
		}
		String xml = beanToXml(setting);
		FileUtil.write(xml.getBytes(), settingFile);
	}

	public File getStarterFile(String projectPath, String name) {
		File folder = getStarterFolder(projectPath);
		return new File(folder, name);
	}

	public File getPluginFile(String projectPath, String name) {
		File folder = getPluginFolder(projectPath);
		return new File(folder, name);
	}

	public File getGitFile() {
		File folder = param.getTeamideFolder();
		return new File(folder, git);
	}

	public File getSettingFileByName(String project) {
		File folder = getFolderByName(project);
		return new File(folder, setting);
	}

	public File getSettingFileByPath(String projectPath) {
		File folder = getFolderByPath(projectPath);
		return new File(folder, setting);
	}

	public File getStarterFolder(String projectPath) {
		File folder = getFolderByPath(projectPath);
		return new File(folder, starter);
	}

	public File getPluginFolder(String projectPath) {
		File folder = getFolderByPath(projectPath);
		return new File(folder, plugin);
	}

	public File getFolderByPath(String projectPath) {
		File folder = param.getTeamideFolder();
		if (StringUtil.isNotEmpty(projectPath)) {
			File projectsFolder = new File(param.getTeamideFolder(), "projects");
			if (projectsFolder.exists()) {
				File[] folders = projectsFolder.listFiles();
				for (File f : folders) {

					ProjectSetting setting = readSettingByName(f.getName());
					if (setting != null && projectPath.equals(setting.getPath())) {
						return f;
					}
				}
			}
		}
		return folder;
	}

	public File getFolderByName(String project) {
		File folder = param.getTeamideFolder();
		if (StringUtil.isNotEmpty(project)) {

			File projectsFolder = new File(param.getTeamideFolder(), "projects");
			folder = new File(projectsFolder, project);
		}
		return folder;
	}

	/**
	 * JavaBean装换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @return
	 */
	public static String beanToXml(Object obj) {
		return beanToXml(obj, "UTF-8");

	}

	/**
	 * JavaBean装换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	private static String beanToXml(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * xml装换成JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlToBean(String xml, Class<T> c) {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;

	}

}
