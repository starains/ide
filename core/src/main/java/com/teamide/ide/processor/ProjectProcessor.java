package com.teamide.ide.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.SpaceRepositoryOpenBean;
import com.teamide.ide.deployer.Deploy;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.handler.DeployHandler;
import com.teamide.ide.param.ProjectParam;
import com.teamide.ide.processor.enums.ProjectModelType;
import com.teamide.ide.processor.enums.ProjectProcessorType;
import com.teamide.ide.processor.param.ProjectOption;
import com.teamide.ide.processor.repository.RepositoryBase;
import com.teamide.ide.processor.repository.RepositoryFile;
import com.teamide.ide.processor.repository.RepositoryMaven;
import com.teamide.ide.processor.repository.RepositoryStarter;
import com.teamide.ide.processor.repository.project.ProjectLoader;
import com.teamide.ide.service.impl.SpaceRepositoryOpenService;

public class ProjectProcessor extends RepositoryProcessor {
	protected final ProjectParam param;

	public ProjectProcessor(ProjectParam param) {
		super(param);
		this.param = param;
	}

	public Object onDo(String type, JSONObject data) throws Exception {
		ProjectProcessorType processorType = ProjectProcessorType.get(type);
		if (processorType == null) {
			return super.onDo(type, data);
		}
		return onDo(processorType, data);
	}

	protected Object onDo(ProjectProcessorType processorType, JSONObject data) throws Exception {
		if (processorType == null) {
			return null;
		}
		SpaceEventBean spaceEventBean = new SpaceEventBean();
		spaceEventBean.setType(processorType.getValue());
		spaceEventBean.setName(processorType.getText());
		spaceEventBean.setSpaceid(param.getSpaceid());
		Object value = null;
		switch (processorType) {

		case FILE_CREATE:
			String parentPath = data.getString("parentPath");
			String name = data.getString("name");
			String content = data.getString("content");
			boolean isFile = data.getBooleanValue("isFile");
			new RepositoryFile(param).create(parentPath, name, isFile, content);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			value = 0;

			break;
		case FILE_MOVE:
			String path = data.getString("path");
			String to = data.getString("to");
			JSONObject model = null;
			if (data.get("model") != null) {
				model = data.getJSONObject("model");
			}
			new RepositoryFile(param).move(path, to, model);

			spaceEventBean.set("path", path);
			spaceEventBean.set("to", to);
			appendEvent(spaceEventBean);

			value = 0;

			break;
		case FILE_DELETE:
			path = data.getString("path");
			new RepositoryFile(param).delete(path);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			value = 0;

			break;
		case FILE_PASTE:
			path = data.getString("path");
			String source = data.getString("source");
			new RepositoryFile(param).paste(path, source);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			break;
		case FILE_SAVE:
			path = data.getString("path");
			content = data.getString("content");
			new RepositoryFile(param).save(path, content);

			spaceEventBean.set("path", path);
			appendEvent(spaceEventBean);

			break;
		case FILE_RENAME:
			path = data.getString("path");
			name = data.getString("name");
			model = null;
			if (data.get("model") != null) {
				model = data.getJSONObject("model");
			}
			new RepositoryFile(param).rename(path, name, model);

			spaceEventBean.set("path", path);
			spaceEventBean.set("name", name);
			appendEvent(spaceEventBean);

			value = 0;

			break;
		case DOWNLOAD:
			new RepositoryFile(param).download(data);
			break;
		case UPLOAD:
			new RepositoryFile(param).upload(data);
			break;
		case FILE_OPEN:
			path = data.getString("path");
			if (!StringUtil.isEmpty(path)) {
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("spaceid", param.getSpaceid());
				p.put("userid", param.getSession().getUser().getId());
				p.put("path", path);
				p.put("branch", param.getBranch());
				SpaceRepositoryOpenService spaceRepositoryOpenService = new SpaceRepositoryOpenService();
				List<SpaceRepositoryOpenBean> list = spaceRepositoryOpenService.queryList(p);
				if (list.size() == 0) {
					SpaceRepositoryOpenBean spaceRepositoryOpenBean = new SpaceRepositoryOpenBean();
					spaceRepositoryOpenBean.setSpaceid(param.getSpaceid());
					spaceRepositoryOpenBean.setPath(path);
					spaceRepositoryOpenBean.setUserid(param.getSession().getUser().getId());
					spaceRepositoryOpenBean.setOpentime(new Date().getTime());
					spaceRepositoryOpenBean.setBranch(param.getBranch());
					spaceRepositoryOpenService.insert(param.getSession(), spaceRepositoryOpenBean);
				} else {
					for (SpaceRepositoryOpenBean one : list) {
						SpaceRepositoryOpenBean up = new SpaceRepositoryOpenBean();
						up.setId(one.getId());
						up.setOpentime(new Date().getTime());
						spaceRepositoryOpenService.update(param.getSession(), up);
					}
				}
			}

			break;

		case FILE_CLOSE:

			path = data.getString("path");
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("spaceid", param.getSpaceid());
			p.put("userid", param.getSession().getUser().getId());
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

		case SET_PLUGIN_OPTION:
			String type = data.getString("type");
			JSONObject option = data.getJSONObject("option");
			value = new ProjectOption(this.param).saveOption(null, type, option);

			spaceEventBean.set("option", option);
			appendEvent(spaceEventBean);

			break;

		case DELETE_PLUGIN_OPTION:
			type = data.getString("type");
			new ProjectOption(this.param).deleteOption(null, type);

			appendEvent(spaceEventBean);

			break;
		}

		return value;

	}

	public Object onLoad(String type, JSONObject data) throws Exception {
		ProjectModelType modelType = ProjectModelType.get(type);
		if (modelType == null) {
			return super.onLoad(type, data);
		}
		return onLoad(modelType, data);
	}

	public Object onLoad(ProjectModelType modelType, JSONObject data) throws Exception {
		if (modelType == null) {
			return null;
		}

		Object value = null;
		switch (modelType) {
		case PROJECT:
			ProjectLoader loader = new ProjectLoader(param);
			value = loader.loadProject(param.getProjectPath());

			break;
		case FILE:
			String path = data.getString("path");
			value = new ProjectLoader(param).readFile(path);

			break;
		case FILES:
			path = data.getString("path");
			value = new ProjectLoader(param).loadFiles(param.getFile(path), null);

			break;
		case STARTER_OPTIONS:
			value = new ProjectOption(this.param).getOptions(OptionType.STARTER);
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
			JSONObject res = null;
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

		case PLUGIN_OPTION:
			String type = data.getString("type");
			value = new ProjectOption(this.param).getOptionByType(type);

			break;
		}
		return value;
	}

}
