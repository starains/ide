package com.teamide.ide.processor.repository;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.deployer.Deploy;
import com.teamide.ide.handler.DeployHandler;
import com.teamide.ide.param.ProjectParam;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.plugin.PluginHandler;
import com.teamide.ide.processor.repository.project.ProjectBean;
import com.teamide.ide.processor.repository.project.ProjectLoader;
import com.teamide.ide.service.impl.SpaceRepositoryOpenService;

public class RepositoryLoad extends RepositoryBase {

	public RepositoryLoad(RepositoryProcessorParam param) {

		super(param);
	}

	public JSONArray loadBranchs() {

		JSONArray branchs = new JSONArray();
		File rootFolder = this.param.getBranchsFolder();
		if (rootFolder.isDirectory()) {
			File[] fs = rootFolder.listFiles();
			for (File f : fs) {
				if (f.isDirectory()) {
					JSONObject info = new JSONObject();
					String name = f.getName();
					info.put("name", name);
					branchs.add(info);
				}
			}
		}
		return branchs;
	}

	public JSONObject loadRepository() throws Exception {

		this.param.getLog().info("load " + param.getSpaceName() + "]版本[ " + this.param.getBranch() + "] workspace");
		if (!this.param.getBranchFolder().exists()) {
			this.param.getLog().error("库[" + param.getSpaceName() + "]版本[ " + this.param.getBranch() + "]未创建!");
			return null;
		}
		if (!this.param.getSourceFolder().exists()) {
			this.param.getSourceFolder().mkdirs();
		}
		JSONObject result = new JSONObject();

		ProjectLoader projectLoader = new ProjectLoader(param);
		List<ProjectBean> projects = projectLoader.getProjects();

		for (ProjectBean project : projects) {
			ProjectParam projectParam = new ProjectParam(param, project.getPath());

			PluginHandler.loadProject(projectParam, project);
		}

		result.put("projects", projects);
		result.put("branch", this.param.getBranch());
		// JSONObject runner = (JSONObject)
		// JSONObject.toJSON(BaseWork.getRunner(getTomcatContext()));
		// result.put("runner", runner);

		result.put("branchs", loadBranchs());

		result.put("git", new RepositoryGit(param).load());

		SpaceRepositoryOpenService spaceRepositoryOpenService = new SpaceRepositoryOpenService();

		String userid = null;
		String spaceid = param.getSpaceid();
		String branch = param.getBranch();
		if (param.getSession().getUser() != null) {
			userid = param.getSession().getUser().getId();
		}
		result.put("opens", spaceRepositoryOpenService.queryOpens(userid, spaceid, branch));
		return result;
	}

	public JSONArray loadStarters() throws Exception {
		JSONArray res = new JSONArray();
		List<Deploy> deploys = DeployHandler.getStarters(this.param);
		for (Deploy deploy : deploys) {
			res.add(deploy.getStatus());
		}
		return res;
	}
}
