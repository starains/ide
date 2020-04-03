package com.teamide.app.generater.dao.merge;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.generater.BaseMergeGenerater;
import com.teamide.app.generater.dao.DaoGenerater;
import com.teamide.app.generater.dao.DaoImplGenerater;
import com.teamide.app.plugin.AppBean;

public class DaoMergeGenerater extends BaseMergeGenerater {

	protected final List<DaoBean> daos;

	public DaoMergeGenerater(String directory, List<DaoBean> daos, File sourceFolder, AppBean app, AppContext context) {
		super(directory, daos, sourceFolder, app, context);
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
				generater = new DaoImplGenerater(dao, sourceFolder, app, context);
			} else {
				generater = new DaoGenerater(dao, sourceFolder, app, context);
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
		if (isUsemybatis()) {
			return "template/java/merge/dao/mapper/default";
		}
		return "template/java/merge/dao/default";
	}

}
