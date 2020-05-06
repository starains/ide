package com.teamide.ide.processor.param;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceRepositoryOptionBean;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.param.ProjectProcessorParam;
import com.teamide.ide.service.impl.SpaceRepositoryOptionService;

public class ProjectOption {

	private final ProjectProcessorParam param;

	public ProjectOption(ProjectProcessorParam param) {
		this.param = param;
	}

	public void deleteOption(String name, OptionType type) throws Exception {
		this.deleteOption(name, type.name());
	}

	public void deleteOption(String name, String type) throws Exception {

		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		List<SpaceRepositoryOptionBean> options = service.query(param.getSpaceid(), param.getBranch(),
				param.getProjectPath(), name, type);
		for (SpaceRepositoryOptionBean option : options) {
			service.delete(option.getId());
		}
	}

	public List<SpaceRepositoryOptionBean> getOptions(OptionType type) throws Exception {

		List<SpaceRepositoryOptionBean> options = queryOptions(null, type);
		return options;
	}

	public JSONObject getPluginOption(String name) throws Exception {

		List<SpaceRepositoryOptionBean> options = queryOptions(null, OptionType.PLUGIN);
		if (options.size() > 0) {
			for (SpaceRepositoryOptionBean option : options) {
				if (StringUtil.isEmpty(param.getProjectPath()) && StringUtil.isEmpty(option.getPath())) {
					return option.getJSONOption();
				}
			}
			return options.get(0).getJSONOption();
		}
		return null;
	}

	public JSONObject getOption(String name, OptionType type) throws Exception {

		List<SpaceRepositoryOptionBean> options = queryOptions(name, type);
		if (options.size() > 0) {
			return options.get(0).getJSONOption();
		}
		return null;
	}

	public List<SpaceRepositoryOptionBean> queryOptions(String name, OptionType type) throws Exception {
		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		return service.query(param.getSpaceid(), param.getBranch(), param.getProjectPath(), name, type);
	}

	public List<SpaceRepositoryOptionBean> queryOptions(String name, String type) throws Exception {
		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		return service.query(param.getSpaceid(), param.getBranch(), param.getProjectPath(), name, type);
	}

	public JSONObject saveOption(String name, OptionType type, JSONObject json) throws Exception {
		return saveOption(name, type.name(), json);
	}

	public JSONObject saveOption(String name, String type, JSONObject json) throws Exception {
		if (json == null) {
			json = new JSONObject();
		}

		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();

		List<SpaceRepositoryOptionBean> options = queryOptions(name, type);
		SpaceRepositoryOptionBean option = null;
		if (options.size() > 0) {
			option = options.get(0);
		}
		if (option == null) {
			option = new SpaceRepositoryOptionBean();
		}
		option.setName(name);
		option.setPath(param.getProjectPath());
		option.setJSONOption(json);
		option.setType(type);
		option.setSpaceid(param.getSpaceid());
		option.setBranch(param.getBranch());

		if (StringUtil.isEmpty(option.getId())) {
			service.insert(param.getSession(), option);
		} else {
			service.update(param.getSession(), option);
		}
		return json;
	}

}
