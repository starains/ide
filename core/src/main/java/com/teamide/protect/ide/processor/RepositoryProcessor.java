package com.teamide.protect.ide.processor;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.RunnerClientBean;
import com.teamide.ide.bean.RunnerServerBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.SpaceRepositoryOpenBean;
import com.teamide.protect.ide.engine.EngineSession;
import com.teamide.protect.ide.enums.OptionType;
import com.teamide.protect.ide.processor.enums.MessageLevel;
import com.teamide.protect.ide.processor.enums.RepositoryModelType;
import com.teamide.protect.ide.processor.enums.RepositoryProcessorType;
import com.teamide.protect.ide.processor.param.RepositoryProcessorParam;
import com.teamide.protect.ide.processor.repository.RepositoryCreate;
import com.teamide.protect.ide.processor.repository.RepositoryFile;
import com.teamide.protect.ide.processor.repository.RepositoryGenerateSourceCode;
import com.teamide.protect.ide.processor.repository.RepositoryGit;
import com.teamide.protect.ide.processor.repository.RepositoryLoad;
import com.teamide.protect.ide.processor.repository.RepositoryLog;
import com.teamide.protect.ide.processor.repository.RepositoryMaven;
import com.teamide.protect.ide.processor.repository.RepositoryRunner;
import com.teamide.protect.ide.processor.repository.RepositoryStarter;
import com.teamide.protect.ide.processor.repository.project.FileBean;
import com.teamide.protect.ide.processor.repository.project.ProjectAppLoader;
import com.teamide.protect.ide.processor.repository.project.ProjectLoader;
import com.teamide.protect.ide.processor.repository.starter.StarterHandler;
import com.teamide.protect.ide.service.RunnerClientService;
import com.teamide.protect.ide.service.RunnerServerService;
import com.teamide.protect.ide.service.SpaceRepositoryOpenService;

public class RepositoryProcessor extends SpaceProcessor {
	protected final RepositoryProcessorParam param;

	public RepositoryProcessor(EngineSession session, RepositoryProcessorParam param) {
		super(session, param);
		this.param = param;
	}

	protected void process(String messageID, String type, JSONObject data) throws Exception {
		RepositoryProcessorType processorType = RepositoryProcessorType.get(type);
		if (processorType == null) {
			super.process(messageID, type, data);
			return;
		}
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
		case FILE_CREATE:
			String parentPath = data.getString("parentPath");
			String name = data.getString("name");
			boolean isFile = data.getBooleanValue("isFile");
			File folder = new RepositoryFile(param).create(parentPath, name, isFile);

			// changeFolder(folder);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			value = 0;

			reloadGit();
			break;
		case FILE_MOVE:
			String path = data.getString("path");
			String to = data.getString("to");
			folder = new RepositoryFile(param).move(path, to);

			spaceEventBean.set("path", path);
			spaceEventBean.set("to", to);
			appendEvent(spaceEventBean);

			value = 0;

			reloadGit();
			break;
		case FILE_DELETE:
			path = data.getString("path");
			folder = new RepositoryFile(param).delete(path);

			// changeFolder(folder);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			value = 0;

			reloadGit();
			break;
		case FILE_PASTE:
			path = data.getString("path");
			String source = data.getString("source");
			folder = new RepositoryFile(param).paste(path, source);

			changeFolder(folder);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case FILE_SAVE:
			path = data.getString("path");
			String content = data.getString("content");
			new RepositoryFile(param).save(path, content);

			changeFile(param.getFile(path));

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case FILE_RENAME:
			path = data.getString("path");
			name = data.getString("name");
			folder = new RepositoryFile(param).rename(path, name);

			// changeFolder(folder);

			spaceEventBean.set("path", path);
			spaceEventBean.set("name", name);
			appendEvent(spaceEventBean);

			value = 0;

			reloadGit();
			break;
		case FILE_DOWNLOAD:
			path = data.getString("path");
			value = new RepositoryFile(param).download(path);
			break;
		case FILE_OPEN:
			path = data.getString("path");
			if (!StringUtil.isEmpty(path)) {
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("spaceid", param.getSpaceid());
				p.put("userid", param.getClient().getUser().getId());
				p.put("path", path);
				p.put("branch", param.getBranch());
				SpaceRepositoryOpenService spaceRepositoryOpenService = new SpaceRepositoryOpenService();
				List<SpaceRepositoryOpenBean> list = spaceRepositoryOpenService.queryList(p);
				if (list.size() == 0) {
					SpaceRepositoryOpenBean spaceRepositoryOpenBean = new SpaceRepositoryOpenBean();
					spaceRepositoryOpenBean.setSpaceid(param.getSpaceid());
					spaceRepositoryOpenBean.setPath(path);
					spaceRepositoryOpenBean.setUserid(param.getClient().getUser().getId());
					spaceRepositoryOpenBean.setOpentime(new Date().getTime());
					spaceRepositoryOpenBean.setBranch(param.getBranch());
					spaceRepositoryOpenService.insert(param.getClient(), spaceRepositoryOpenBean);
				} else {
					for (SpaceRepositoryOpenBean one : list) {
						SpaceRepositoryOpenBean up = new SpaceRepositoryOpenBean();
						up.setId(one.getId());
						up.setOpentime(new Date().getTime());
						spaceRepositoryOpenService.update(param.getClient(), up);
					}
				}
			}

			break;

		case FILE_CLOSE:

			path = data.getString("path");
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("spaceid", param.getSpaceid());
			p.put("userid", param.getClient().getUser().getId());
			p.put("path", path);

			SpaceRepositoryOpenService spaceRepositoryOpenService = new SpaceRepositoryOpenService();

			value = spaceRepositoryOpenService.delete(p);
			break;
		case MAVEN_CLEAN:
			path = data.getString("path");
			new RepositoryMaven(param).clean(path);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);
			break;
		case MAVEN_DEPLOY:
			path = data.getString("path");
			new RepositoryMaven(param).deploy(path);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);
			break;
		case MAVEN_INSTALL:
			path = data.getString("path");
			new RepositoryMaven(param).install(path);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);
			break;
		case MAVEN_PACKAGE:
			path = data.getString("path");
			new RepositoryMaven(param).doPackage(path);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);
			break;
		case MAVEN_COMPILE:
			path = data.getString("path");
			new RepositoryMaven(param).doCompile(path);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);
			break;
		case GIT_BRANCH_CREATE:
			String gitBranchName = data.getString("gitBranchName");
			String startPoint = data.getString("startPoint");
			value = new RepositoryGit(param).branchCreate(gitBranchName, startPoint);

			spaceEventBean.set("branch", gitBranchName);
			spaceEventBean.set("startpoint", startPoint);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_BRANCH_DELETE:
			gitBranchName = data.getString("gitBranchName");
			value = new RepositoryGit(param).branchDelete(gitBranchName);

			spaceEventBean.set("branch", gitBranchName);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_BRANCH_RENAME:
			String oldGitBranchName = data.getString("oldGitBranchName");
			String newGitBranchName = data.getString("newGitBranchName");
			value = new RepositoryGit(param).branchRename(oldGitBranchName, newGitBranchName);

			spaceEventBean.set("oldbranch", oldGitBranchName);
			spaceEventBean.set("newbranch", newGitBranchName);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_CHECKOUT:
			String gitBranch = data.getString("gitBranch");
			startPoint = data.getString("startPoint");
			value = new RepositoryGit(param).checkout(gitBranch, startPoint);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_CLONE:
			String url = data.getString("url");
			gitBranch = data.getString("gitBranch");
			String gitRemoteName = data.getString("gitRemoteName");
			String username = null;
			String password = null;
			JSONObject certificate = data.getJSONObject("certificate");
			if (certificate != null) {
				username = certificate.getString("username");
				password = certificate.getString("password");
			}
			value = new RepositoryGit(param).clone(url, gitBranch, gitRemoteName, username, password);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_INIT:
			value = new RepositoryGit(param).init();

			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_PULL:

			gitRemoteName = data.getString("gitRemoteName");
			String gitRemoteBranch = data.getString("gitRemoteBranch");

			outRepositoryStatus("Git pull [" + gitRemoteName + "] [" + gitRemoteBranch + "] ...");

			try {
				certificate = data.getJSONObject("certificate");
				username = null;
				password = null;
				if (certificate != null) {
					username = certificate.getString("username");
					password = certificate.getString("password");
				}
				new RepositoryGit(param).pull(gitRemoteName, gitRemoteBranch, username, password);

				spaceEventBean.set(data);
				appendEvent(spaceEventBean);

				onData(null, RepositoryModelType.REPOSITORY.getValue(), new JSONObject());
			} finally {
				outRepositoryStatus(null);
			}

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
			value = files;

			spaceEventBean.set("paths", paths);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_PUSH:
			gitRemoteName = data.getString("gitRemoteName");
			String branchName = data.getString("branchName");
			gitRemoteBranch = data.getString("gitRemoteBranch");

			outRepositoryStatus("Git push [" + gitRemoteName + "] [" + gitRemoteBranch + "] ...");

			try {
				repositoryGit = new RepositoryGit(param);

				String message = data.getString("message");

				paths = data.getJSONArray("paths");
				repositoryGit.add(paths);

				repositoryGit.commit(message);

				certificate = data.getJSONObject("certificate");
				username = null;
				password = null;
				if (certificate != null) {
					username = certificate.getString("username");
					password = certificate.getString("password");
				}

				new RepositoryGit(param).push(gitRemoteName, branchName, gitRemoteBranch, username, password);

				spaceEventBean.set(data);
				appendEvent(spaceEventBean);

				reloadGit();
			} finally {
				outRepositoryStatus(null);
			}

			break;
		case GIT_INDEX_ADD:
			repositoryGit = new RepositoryGit(param);

			paths = data.getJSONArray("paths");

			repositoryGit.add(paths);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_INDEX_REMOVE:

			reloadGit();
			break;
		case GIT_REMOTE_ADD:
			gitRemoteName = data.getString("gitRemoteName");
			url = data.getString("url");
			param.saveOption(null, null, OptionType.GIT, data);
			value = new RepositoryGit(param).remoteAdd(gitRemoteName, url);

			spaceEventBean.set("remote", gitRemoteName);
			appendEvent(spaceEventBean);

			process(messageID, RepositoryProcessorType.GIT_PULL.getValue(), data);
			break;
		case GIT_REMOTE_REMOVE:
			gitRemoteName = data.getString("gitRemoteName");
			value = new RepositoryGit(param).remoteRemove(gitRemoteName);

			spaceEventBean.set("remote", gitRemoteName);
			appendEvent(spaceEventBean);

			reloadGit();
			break;
		case GIT_REMOTE_SETURL:
			gitRemoteName = data.getString("gitRemoteName");
			url = data.getString("url");
			gitRemoteBranch = data.getString("gitRemoteBranch");

			param.saveOption(null, null, OptionType.GIT, data);

			value = new RepositoryGit(param).remoteSetUrl(gitRemoteName, url);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			process(messageID, RepositoryProcessorType.GIT_PULL.getValue(), data);
			break;
		case SET_STARTER_OPTION:
			path = data.getString("path");
			JSONObject option = data.getJSONObject("option");
			name = option.getString("name");
			value = param.saveOption(path, name, OptionType.STARTER, option);

			spaceEventBean.set("path", path);
			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "保存成功.");

			onData(null, RepositoryModelType.STARTER_OPTIONS.getValue(), data);
			break;
		case DELETE_STARTER_OPTION:
			path = data.getString("path");
			option = data.getJSONObject("option");
			name = option.getString("name");
			param.deleteOption(path, name, OptionType.STARTER);

			spaceEventBean.set("path", path);
			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "删除成功.");

			onData(null, RepositoryModelType.STARTER_OPTIONS.getValue(), data);
			break;
		case STARTER_START:
			String token = data.getString("token");
			if (!StringUtil.isEmpty(token)) {
				new RepositoryStarter(param).start(token);
			} else {
				path = data.getString("path");
				option = data.getJSONObject("option");
				new RepositoryStarter(param).start(path, option);

				spaceEventBean.set("path", path);
				spaceEventBean.set("option", option);
			}

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			onData(null, RepositoryModelType.STARTERS.getValue(), data);
			break;
		case STARTER_STOP:
			token = data.getString("token");
			new RepositoryStarter(param).stop(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;
		case STARTER_DESTROY:
			token = data.getString("token");
			new RepositoryStarter(param).destroy(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;
		case STARTER_REMOVE:
			token = data.getString("token");
			new RepositoryStarter(param).remove(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			onData(null, RepositoryModelType.STARTERS.getValue(), data);
			break;
		case STARTER_LOG_CLEAN:
			token = data.getString("token");
			if (!StringUtil.isEmpty(token) && !"0".equals(token)) {
				new RepositoryStarter(param).logClean(token);
			} else {
				param.getLog().clean();
			}

			value = 0;

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);

			break;

		case RUNNER_LOG_CLEAN:
			token = data.getString("token");
			value = new RepositoryRunner(param).logClean(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_REMOVE:
			token = data.getString("token");
			value = new RepositoryRunner(param).remove(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_DEPLOY:

			token = data.getString("token");
			if (!StringUtil.isEmpty(token)) {
				value = new RepositoryRunner(param).deploy(token);
			} else {
				path = data.getString("path");
				option = data.getJSONObject("option");
				value = new RepositoryRunner(param).deploy(path, option);

				spaceEventBean.set("path", path);
				spaceEventBean.set("option", option);

			}

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_START:

			token = data.getString("token");
			value = new RepositoryRunner(param).start(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_STOP:
			token = data.getString("token");
			value = new RepositoryRunner(param).stop(token);

			spaceEventBean.set("token", token);
			appendEvent(spaceEventBean);
			break;
		case SET_RUNNER_OPTION:
			path = data.getString("path");
			option = data.getJSONObject("option");
			name = option.getString("name");
			value = param.saveOption(path, name, OptionType.DEPLOYER, option);

			spaceEventBean.set("path", path);
			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);
			break;
		case DELETE_RUNNER_OPTION:
			path = data.getString("path");
			option = data.getJSONObject("option");
			name = option.getString("name");
			param.deleteOption(path, name, OptionType.DEPLOYER);

			spaceEventBean.set("path", path);
			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_SERVER_DELETE:

			String id = data.getString("id");
			if (!StringUtil.isEmpty(id)) {
				new RunnerServerService().delete(id);
			}

			spaceEventBean.set("id", id);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_SERVER_SAVE:
			RunnerServerBean runnerServerBean = data.toJavaObject(RunnerServerBean.class);
			if (param.getClient().getUser() != null) {
				runnerServerBean.setUserid(param.getClient().getUser().getId());
			}
			value = new RunnerServerService().save(param.getClient(), runnerServerBean);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_CLIENT_DELETE:
			id = data.getString("id");
			if (!StringUtil.isEmpty(id)) {
				new RunnerClientService().delete(id);
			}

			spaceEventBean.set("id", id);
			appendEvent(spaceEventBean);
			break;
		case RUNNER_CLIENT_SAVE:
			RunnerClientBean runnerClientBean = data.toJavaObject(RunnerClientBean.class);
			if (param.getClient().getUser() != null) {
				runnerClientBean.setUserid(param.getClient().getUser().getId());
			}
			value = new RunnerClientService().save(param.getClient(), runnerClientBean);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);
			break;
		case APP_SET_OPTION:
			path = data.getString("path");
			option = data.getJSONObject("option");
			value = param.saveOption(path, null, OptionType.APP, option);

			spaceEventBean.set("path", path);
			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "保存成功.");

			onData(null, RepositoryModelType.PROJECT.getValue(), data);
			break;

		case APP_DELETE_OPTION:
			path = data.getString("path");
			param.deleteOption(path, null, OptionType.APP);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "删除成功.");

			onData(null, RepositoryModelType.PROJECT.getValue(), data);
			break;
		case APP_GENERATE_SOURCE_CODE:
			path = data.getString("path");
			new RepositoryGenerateSourceCode(param).generate(path);
			value = 0;

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "生成成功.");

			onData(null, RepositoryModelType.PROJECT.getValue(), data);

			break;
		}

		out(messageID, processorType.getValue(), value);

	}

	public void changeFolder(File folder) throws Exception {
		JSONObject data = new JSONObject();
		JSONObject message = createDataMessage(RepositoryModelType.FILE.getValue(), data);
		outByThisSpaceBranch(message);
	}

	public void changeFile(File file) throws Exception {

		String path = this.param.getPath(file);
		FileBean fileBean = new ProjectLoader(param).readFile(path);

		JSONObject message = createDataMessage(RepositoryModelType.FILE.getValue(), fileBean);

		outByThisSpaceBranch(message);
	}

	public void reloadGit() throws Exception {

		onData(null, RepositoryModelType.GIT.getValue(), new JSONObject());
	}

	public void onData(String messageID, String model, JSONObject data) throws Exception {
		RepositoryModelType modelType = RepositoryModelType.get(model);
		if (modelType == null) {
			super.onData(messageID, model, data);
			return;
		}
		Object value = null;
		switch (modelType) {
		case REPOSITORY_STATUS:
			break;
		case REPOSITORY:
			RepositoryLoad repositoryLoad = new RepositoryLoad(this.param);
			if (!this.param.getSpaceFolder().exists()) {
				RepositoryCreate repositoryCreate = new RepositoryCreate(this.param);
				repositoryCreate.create();
			}
			value = repositoryLoad.loadRepository();
			break;
		case BRANCHS:
			repositoryLoad = new RepositoryLoad(this.param);
			value = repositoryLoad.loadBranchs();
			break;
		case PROJECT:
			String path = data.getString("path");
			ProjectLoader loader = new ProjectLoader(param);
			value = loader.loadProject(path);

			break;
		case FILE:
			path = data.getString("path");
			value = new ProjectLoader(param).readFile(path);

			break;
		case FILES:
			path = data.getString("path");
			value = new ProjectLoader(param).loadFiles(param.getFile(path), null);

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
			path = data.getString("path");
			value = new RepositoryGit(param).status(path);
			break;
		case STARTER_OPTIONS:
			path = data.getString("path");
			value = param.getOptions(path, OptionType.STARTER);
			break;
		case STARTERS:
			value = new RepositoryStarter(param).loadStarters();
			break;
		case STARTER_STATUS:
			String token = data.getString("token");
			value = new RepositoryStarter(param).status(token);
			break;
		case STARTER_LOG:
			token = data.getString("token");

			int lastIndex = -1;
			if (StringUtil.isNotEmpty(data.getString("lastIndex"))) {
				lastIndex = data.getIntValue("lastIndex");
			}
			JSONObject res = null;
			if (!StringUtil.isEmpty(token) && !token.equals("0")) {

				RepositoryLog repositoryLog = StarterHandler.getStarterLog(token);
				if (repositoryLog != null) {
					res = repositoryLog.read(lastIndex);
				} else {
					res = new JSONObject();
				}
			} else {
				res = param.getLog().read(lastIndex);
			}
			res.put("token", token);
			value = res;
			break;

		case RUNNER_SERVERS:

			Map<String, Object> p = new HashMap<String, Object>();
			p.put("userid", this.param.getClient().getUser().getId());

			RunnerServerService runnerServerService = new RunnerServerService();

			value = runnerServerService.queryList(p);
			break;
		case RUNNER_CLIENTS:

			p = new HashMap<String, Object>();
			p.put("userid", this.param.getClient().getUser().getId());

			RunnerClientService runnerClientService = new RunnerClientService();

			value = runnerClientService.queryList(p);
			break;
		case RUNNER_LOG:
			token = data.getString("token");

			lastIndex = -1;
			if (StringUtil.isNotEmpty(data.getString("lastIndex"))) {
				lastIndex = data.getIntValue("lastIndex");
			}
			value = param.getRunnerLog(token).read(lastIndex);
			break;
		case RUNNER_STATUS:
			token = data.getString("token");
			value = new RepositoryRunner(param).status(token);
			break;
		case RUNNER_OPTIONS:
			path = data.getString("path");
			value = param.getOptions(path, OptionType.DEPLOYER);
			break;
		case RUNNERS:
			value = new RepositoryRunner(param).loadRunners();
			break;
		case APP:
			path = data.getString("path");
			ProjectAppLoader appLoader = new ProjectAppLoader(param);
			res = new JSONObject();
			res.put("path", path);
			res.put("app", appLoader.loadApp(path));
			value = res;
			break;

		}
		outData(messageID, modelType.getValue(), value);
	}

	public void outRepositoryStatus(String message) {

		outData(null, RepositoryModelType.REPOSITORY_STATUS.getValue(), message);
	}
}
