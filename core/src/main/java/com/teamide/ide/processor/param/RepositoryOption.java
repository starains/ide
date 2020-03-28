package com.teamide.ide.processor.param;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.ide.bean.SpaceRepositoryOptionBean;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.param.RepositoryProcessorParam;
import com.teamide.ide.service.impl.SpaceRepositoryOptionService;

public class RepositoryOption {

	private final RepositoryProcessorParam param;

	public RepositoryOption(RepositoryProcessorParam param) {
		this.param = param;
	}

	public void deleteOption(String name, OptionType type) throws Exception {

		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		List<SpaceRepositoryOptionBean> options = service.query(param.getSession(), param.getSpaceid(),
				param.getBranch(), null, name, type);
		for (SpaceRepositoryOptionBean option : options) {
			service.delete(option.getId());
		}
	}

	public List<SpaceRepositoryOptionBean> getOptions(OptionType type) throws Exception {

		List<SpaceRepositoryOptionBean> options = queryOptions(null, type);
		return options;
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
		return service.query(param.getSession(), param.getSpaceid(), param.getBranch(), null, name, type);
	}

	public List<SpaceRepositoryOptionBean> queryOptions(String name, String type) throws Exception {
		SpaceRepositoryOptionService service = new SpaceRepositoryOptionService();
		return service.query(param.getSession(), param.getSpaceid(), param.getBranch(), null, name, type);
	}

	public JSONObject saveOption(String name, OptionType type, JSONObject json) throws Exception {
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
		if (param.getSession() != null && param.getSession().getUser() != null) {
			option.setUserid(param.getSession().getUser().getId());
		}
		option.setName(name);
		option.setPath(null);
		option.setJSONOption(json);
		option.setType(type.name());
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
