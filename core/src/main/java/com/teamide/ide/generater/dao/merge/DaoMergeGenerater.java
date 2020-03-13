package com.teamide.ide.generater.dao.merge;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.generater.dao.DaoGenerater;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;

public class DaoMergeGenerater extends BaseMergeGenerater {

	protected final List<DaoBean> daos;

	public DaoMergeGenerater(String directory, List<DaoBean> daos, RepositoryProcessorParam param, AppBean app,
			AppContext context) {
		super(directory, daos, param, app, context);
		this.daos = daos;
	}

	public String getPackage() {
		return getDaoPackage();
	}

	@Override
	public void buildData() {

		JSONArray list = new JSONArray();

		for (DaoBean dao : daos) {
			DaoGenerater generater = new DaoGenerater(dao, param, app, context);
			generater.init();
			try {
				String name = dao.getName();
				if (name.indexOf("/") > 0) {
					name = name.substring(name.lastIndexOf("/"));
				}
				String name_ = name.substring(0, 1).toUpperCase() + name.substring(1);
				generater.data.put("$method_name", name);
				generater.data.put("$build_name", "buildSqlParam" + name_);
				generater.data.put("$format_name", "format" + name_);
				generater.data.put("$query_name", "query" + name_);

				generater.data.put("$only_content", true);
				String content = generater.build();
				JSONObject data = generater.data;
				data.put("$content", content);
				list.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		data.put("$list", list);

	}

	@Override
	public String getTemplate() throws Exception {

		return "template/java/merge/dao/default";
	}

}
