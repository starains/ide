package com.teamide.ide.processor;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.deployer.Deploy;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.handler.DeployHandler;
import com.teamide.ide.handler.SpacePermissionHandler;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.processor.enums.RepositoryModelType;
import com.teamide.ide.processor.enums.RepositoryProcessorType;
import com.teamide.ide.processor.param.RepositoryOption;
import com.teamide.ide.processor.repository.RepositoryBase;
import com.teamide.ide.processor.repository.RepositoryCreate;
import com.teamide.ide.processor.repository.RepositoryGit;
import com.teamide.ide.processor.repository.RepositoryLoad;
import com.teamide.ide.processor.repository.RepositoryStarter;
import com.teamide.ide.processor.repository.project.ProjectLoader;

public class RepositoryProcessor extends SpaceProcessor {
	protected final RepositoryProcessorParam param;

	public RepositoryProcessor(RepositoryProcessorParam param) {
		super(param);
		this.param = param;
	}

	public Object onDo(String type, JSONObject data) throws Exception {
		RepositoryProcessorType processorType = RepositoryProcessorType.get(type);
		if (processorType == null) {
			return super.onDo(type, data);
		}
		return onDo(processorType, data);
	}

	protected Object onDo(RepositoryProcessorType processorType, JSONObject data) throws Exception {
		if (processorType == null) {
			return null;
		}
		SpacePermissionHandler.checkPermission(processorType, param.getPermission());
		SpaceEventBean spaceEventBean = new SpaceEventBean();
		spaceEventBean.setType(processorType.getValue());
		spaceEventBean.setName(processorType.getText());
		spaceEventBean.setSpaceid(param.getSpaceid());
		Object value = null;
		switch (processorType) {

		case BRANCH_CREATE:
			String branch = data.getString("branch");
			String frombranch = data.getString("frombranch");
			value = new RepositoryCreate(param).createBranch(branch, frombranch);

			spaceEventBean.set("branch", branch);
			spaceEventBean.set("frombranch", frombranch);
			appendEvent(spaceEventBean);
			break;
		case GIT_BRANCH_CREATE:
			String gitBranchName = data.getString("gitBranchName");
			String startPoint = data.getString("startPoint");
			value = new RepositoryGit(param).branchCreate(gitBranchName, startPoint);

			spaceEventBean.set("branch", gitBranchName);
			spaceEventBean.set("startpoint", startPoint);
			appendEvent(spaceEventBean);

			break;
		case GIT_BRANCH_DELETE:
			gitBranchName = data.getString("gitBranchName");
			value = new RepositoryGit(param).branchDelete(gitBranchName);

			spaceEventBean.set("branch", gitBranchName);
			appendEvent(spaceEventBean);

			break;
		case GIT_BRANCH_RENAME:
			String oldGitBranchName = data.getString("oldGitBranchName");
			String newGitBranchName = data.getString("newGitBranchName");
			value = new RepositoryGit(param).branchRename(oldGitBranchName, newGitBranchName);

			spaceEventBean.set("oldbranch", oldGitBranchName);
			spaceEventBean.set("newbranch", newGitBranchName);
			appendEvent(spaceEventBean);

			break;
		case GIT_CHECKOUT:
			String gitBranch = data.getString("gitBranch");
			startPoint = data.getString("startPoint");
			value = new RepositoryGit(param).checkout(gitBranch, startPoint);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case GIT_CLONE:
			String url = data.getString("url");
			gitBranch = data.getString("gitBranch");
			String gitRemoteName = data.getString("gitRemoteName");
			String certificateid = data.getString("certificateid");
			value = new RepositoryGit(param).clone(url, gitBranch, gitRemoteName, certificateid);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case GIT_INIT:
			value = new RepositoryGit(param).init();

			appendEvent(spaceEventBean);

			break;
		case GIT_PULL:

			gitRemoteName = data.getString("gitRemoteName");
			String gitRemoteBranch = data.getString("gitRemoteBranch");

			certificateid = data.getString("certificateid");

			new RepositoryGit(param).pull(gitRemoteName, gitRemoteBranch, certificateid);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case GIT_REVERT:
			JSONArray paths = data.getJSONArray("paths");
			RepositoryGit repositoryGit = new RepositoryGit(param);
			ProjectLoader loader = new ProjectLoader(param);
			JSONArray files = new JSONArray();
			for (Object one : paths) {
				repositoryGit.checkout(String.valueOf(one));
				files.add(loader.load(String.valueOf(one)));
			}

			if (data.get("deletes") != null) {
				JSONArray deletes = data.getJSONArray("deletes");
				for (Object one : deletes) {
					param.getFile(String.valueOf(one)).delete();
				}
			}

			appendEvent(spaceEventBean);

			break;
		case GIT_PUSH:
			gitRemoteName = data.getString("gitRemoteName");
			String branchName = data.getString("branchName");
			gitRemoteBranch = data.getString("gitRemoteBranch");

			repositoryGit = new RepositoryGit(param);

			String message = data.getString("message");

			paths = data.getJSONArray("paths");

			certificateid = data.getString("certificateid");
			new RepositoryGit(param).push(paths, message, gitRemoteName, branchName, gitRemoteBranch, certificateid);
			data.remove("paths");
			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case GIT_INDEX_ADD:
			repositoryGit = new RepositoryGit(param);

			paths = data.getJSONArray("paths");

			repositoryGit.add(paths);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case GIT_INDEX_REMOVE:

			break;
		case GIT_REMOTE_ADD:
			gitRemoteName = data.getString("gitRemoteName");
			gitRemoteBranch = data.getString("gitRemoteBranch");
			url = data.getString("url");

			JSONObject option = new JSONObject();
			option.put("gitRemoteName", gitRemoteName);
			option.put("gitRemoteBranch", gitRemoteBranch);
			option.put("url", url);

			new RepositoryOption(this.param).saveOption(null, OptionType.GIT, option);

			if (data.getBooleanValue("needclean")) {
				File sourceFolder = param.getSourceFolder();
				if (sourceFolder.exists()) {
					File[] fs = sourceFolder.listFiles();
					for (File f : fs) {
						if (f.isFile()) {
							FileUtils.forceDelete(f);
						} else {
							FileUtils.deleteDirectory(f);
						}
					}

				}
			}

			value = new RepositoryGit(param).remoteAdd(gitRemoteName, url);

			spaceEventBean.set("remote", gitRemoteName);
			appendEvent(spaceEventBean);

			onDo(RepositoryProcessorType.GIT_PULL.getValue(), data);
			break;
		case GIT_REMOTE_REMOVE:
			gitRemoteName = data.getString("gitRemoteName");
			value = new RepositoryGit(param).remoteRemove(gitRemoteName);

			spaceEventBean.set("remote", gitRemoteName);
			appendEvent(spaceEventBean);

			break;
		case GIT_REMOTE_SETURL:
			gitRemoteName = data.getString("gitRemoteName");
			url = data.getString("url");
			gitRemoteBranch = data.getString("gitRemoteBranch");

			option = new JSONObject();
			option.put("gitRemoteName", gitRemoteName);
			option.put("gitRemoteBranch", gitRemoteBranch);
			option.put("url", url);

			new RepositoryOption(this.param).saveOption(null, OptionType.GIT, option);

			value = new RepositoryGit(param).remoteSetUrl(gitRemoteName, url);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case STARTER_DEPLOY:
			String token = data.getString("token");
			if (!StringUtil.isEmpty(token)) {
				new RepositoryStarter(param).deploy(token);
			} else {
				String path = data.getString("path");
				option = data.getJSONObject("option");
				new RepositoryStarter(param).deploy(path, option);

				spaceEventBean.set("path", path);
				spaceEventBean.set("option", option);
			}

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);
			break;
		case STARTER_START:
			token = data.getString("token");
			new RepositoryStarter(param).start(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;
		case STARTER_STOP:
			token = data.getString("token");
			new RepositoryStarter(param).stop(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;
		case STARTER_REMOVE:
			token = data.getString("token");
			new RepositoryStarter(param).remove(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;
		case STARTER_LOG_CLEAN:
			token = data.getString("token");
			if (!StringUtil.isEmpty(token) && !"0".equals(token)) {
				new RepositoryStarter(param).logClean(token);
			} else {
				new RepositoryBase(param).getLog().clean();
			}

			value = 0;

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;
		case SET_STARTER_OPTION:
			option = data.getJSONObject("option");
			String name = option.getString("name");
			value = new RepositoryOption(this.param).saveOption(name, OptionType.STARTER, option);

			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);

			break;
		case DELETE_STARTER_OPTION:
			option = data.getJSONObject("option");
			name = option.getString("name");
			new RepositoryOption(this.param).deleteOption(name, OptionType.STARTER);

			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);

			break;
		}

		return value;

	}

	public Object onLoad(String type, JSONObject data) throws Exception {
		RepositoryModelType modelType = RepositoryModelType.get(type);
		if (modelType == null) {
			return super.onLoad(type, data);
		}
		return onLoad(modelType, data);
	}

	public Object onLoad(RepositoryModelType modelType, JSONObject data) throws Exception {
		if (modelType == null) {
			return null;
		}

		Object value = null;
		switch (modelType) {
		case REPOSITORY_STATUS:
			break;
		case REPOSITORY_PERMISSIONS:
			value = SpacePermissionHandler.getRepositoryPermissions(param.getPermission());
			break;
		case PROJECT_PERMISSIONS:
			value = SpacePermissionHandler.getProjectPermissions(param.getPermission());
			break;
		case REPOSITORY:
			RepositoryLoad repositoryLoad = new RepositoryLoad(this.param);
			value = repositoryLoad.loadRepository();
			break;
		case BRANCHS:
			repositoryLoad = new RepositoryLoad(this.param);
			value = repositoryLoad.loadBranchs();
			break;
		case GIT:
			value = new RepositoryGit(param).load();
			break;
		case GIT_BRANCH_LIST:
			value = new RepositoryGit(param).branchList();
			break;
		case GIT_LOG:
			String maxCount = data.getString("maxCount");
			if (StringUtil.isEmpty(maxCount)) {
				maxCount = "10";
			}
			value = new RepositoryGit(param).log(Integer.valueOf(maxCount));
			break;
		case GIT_REMOTE_LIST:
			value = new RepositoryGit(param).remoteList();
			break;
		case GIT_STATUS:
			String path = data.getString("path");
			value = new RepositoryGit(param).status(path);
			break;
		case GIT_WORK_STATUS:
			JSONObject res = new JSONObject();
			res.put("status", new RepositoryGit(param).getGitWorkStatus());
			res.put("message", new RepositoryGit(param).getGitWorkMessage());
			value = res;
			break;
		case STARTERS:
			value = new RepositoryLoad(param).loadStarters();
			break;
		case STARTER_OPTIONS:
			value = new RepositoryOption(this.param).getOptions(OptionType.STARTER);
			break;
		case STARTER_STATUS:
			String token = data.getString("token");
			value = new RepositoryStarter(param).status(token);
			break;
		case STARTER_LOG:
			token = data.getString("token");
			int start = data.getIntValue("start");
			int end = data.getIntValue("end");
			String timestamp = data.getString("timestamp");
			boolean isloadold = data.getBooleanValue("isloadold");
			res = null;
			if (!StringUtil.isEmpty(token) && !token.equals("0")) {
				Deploy deploy = DeployHandler.get(token);
				if (deploy != null) {
					res = deploy.read(start, end, timestamp);
				} else {
					res = new JSONObject();
				}
			} else {
				res = new RepositoryBase(param).getLog().read(start, end, timestamp);
			}
			res.put("token", token);
			res.put("start", start);
			res.put("end", end);
			res.put("isloadold", isloadold);
			value = res;
			break;

		}
		return value;
	}

}
