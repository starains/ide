package com.teamide.ide.generater.dao.merge;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.ide.generater.BaseMergeGenerater;
import com.teamide.ide.generater.dao.DaoGenerater;
import com.teamide.ide.generater.dao.DaoImplGenerater;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.ide.processor.repository.project.AppBean;

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
	public String getClassName() {
		return "I" + super.getClassName();
	}

	public boolean isImpl() {
		return false;
	}

	@Override
	public void buildData() {

		JSONArray list = new JSONArray();

		for (DaoBean dao : daos) {
			DaoGenerater generater;
			if (isImpl()) {
				generater = new DaoImplGenerater(dao, param, app, context);
			} else {
				generater = new DaoGenerater(dao, param, app, context);
			}
			generater.init();
			try {
				String name = dao.getName();
				if (name.indexOf("/") > 0) {
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				generater.data.put("$method_name", name);
				generater.data.put("$build_name", name + "BuildSqlParam");
				generater.data.put("$format_name", name + "Format");
				generater.data.put("$query_name", name + "Query");

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
