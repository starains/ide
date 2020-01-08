package com.teamide.protect.ide.processor.repository;
//package com.coospro.protect.ide.workspace.repository;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.List;
//import java.util.Properties;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.coospro.ide.bean.SpaceRepositoryOptionBean;
//import com.coospro.ide.bean.WorkParamBean;
//import com.coospro.ide.constant.IDEConstant;
//import com.coospro.protect.ide.enums.OptionType;
//import com.coospro.protect.ide.service.SpaceRepositoryOptionService;
//import com.coospro.util.IoUtil;
//import com.coospro.util.StringUtil;
//
//public class RepositoryParam {
//
//	public final WorkParamBean param;
//
//	private final String rootFolder;
//
//	private final String branchsFolder;
//
//	private final String branchFolder;
//
//	private final String sourceFolder;
//
//	private final String pom;
//
//	public RepositoryParam(WorkParamBean param) {
//
//		if (param == null) {
//			throw new RuntimeException("参数为空！");
//		}
//		if (StringUtil.isEmpty(param.getFolder())) {
//			throw new RuntimeException("空间目录为空！");
//		}
//
//		this.param = param;
//
//		rootFolder = IDEConstant.SPACE_FOLDER + param.getFolder();
//
//		branchsFolder = rootFolder + "branchs/";
//		branchFolder = branchsFolder + param.getBranch() + "/";
//
//		sourceFolder = branchFolder + "source/";
//
//		pom = sourceFolder + "pom.xml";
//
//	}
//
//	public String getPath(String path) {
//		String root = sourceFolder;
//		if (!root.endsWith("/")) {
//			root = root + "/";
//		}
//
//		path = path.replaceAll("\\\\", "/").replaceAll("//", "/");
//		if ((path + "/").indexOf(root) == 0) {
//			return path;
//		}
//		if (path.startsWith("/")) {
//			path = path.substring(1);
//		}
//		path = root + path;
//		return path;
//
//	}
//
//	public boolean isAppSource(String path) {
//
//		return path.indexOf(getSourceFolder()) == 0;
//	}
//
//	public String getProjectFolader(String projectPath) {
//
//		projectPath = projectPath == null ? "" : projectPath;
//		if (!StringUtil.isEmpty(projectPath) && !projectPath.endsWith("/")) {
//			projectPath += "/";
//		}
//		return sourceFolder + projectPath;
//	}
//
//	public String getTargetFolder(String projectPath) {
//		String targetFolder = getProjectFolader(projectPath) + "target/";
//		return targetFolder;
//	}
//
//	public String getJavaFolder(String projectPath) {
//		String targetFolder = getProjectFolader(projectPath) + "src/main/java/";
//		return targetFolder;
//	}
//
//	public String getResourcesFolder(String projectPath) {
//		String targetFolder = getProjectFolader(projectPath) + "src/main/resources/";
//		return targetFolder;
//	}
//
//	public String getWebappFolder(String projectPath) {
//		String targetFolder = getProjectFolader(projectPath) + "src/main/webapp/";
//		return targetFolder;
//	}
//
//	public boolean isAppJava(String projectPath, String path) {
//
//		return path.indexOf(getJavaFolder(projectPath)) == 0;
//	}
//
//	public boolean isAppWebapp(String projectPath, String path) {
//
//		return path.indexOf(getWebappFolder(projectPath)) == 0;
//	}
//
//	public boolean isAppResources(String projectPath, String path) {
//
//		return path.indexOf(getResourcesFolder(projectPath)) == 0;
//	}
//
//	
//
//	
//
//
//	
//
//	
//
//	public String getBranch() {
//
//		return this.param.getBranch();
//	}
//
//	public String getRootFolder() {
//
//		return rootFolder;
//	}
//
//	public String getBranchsFolder() {
//
//		return branchsFolder;
//	}
//
//	public String getBranchFolder() {
//
//		return branchFolder;
//	}
//
//	public String getSourceFolder() {
//
//		return sourceFolder;
//	}
//
//	public WorkParamBean getParam() {
//		return param;
//	}
//
//	public String getPom() {
//
//		return pom;
//	}
//
//}
