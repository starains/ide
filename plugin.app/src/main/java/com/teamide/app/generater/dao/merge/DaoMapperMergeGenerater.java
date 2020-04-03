package com.teamide.app.generater.dao.merge;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.DaoBean;
import com.teamide.app.generater.dao.DaoGenerater;
import com.teamide.app.generater.dao.DaoMapperGenerater;
import com.teamide.app.plugin.AppBean;
import com.teamide.util.StringUtil;

public class DaoMapperMergeGenerater extends DaoMergeGenerater {

	public DaoMapperMergeGenerater(String directory, List<DaoBean> daos, File sourceFolder, AppBean app,
			AppContext context) {
		super(directory, daos, sourceFolder, app, context);
	}

	@Override
	public File getFile() {

		File resourcesFolder = getResourcesFolder();
		String codepackage = getFolderPackage(directory);
		String folder = "";
		if (!StringUtil.isEmpty(codepackage)) {
			folder = codepackage.replaceAll("\\.", "/");
		}
		File file = new File(resourcesFolder, "mybatis/mappers/" + folder + "/" + getClassName() + "Mapper.xml");
		return file;
	}

	@Override
	public void buildData() {

		JSONArray list = new JSONArray();

		for (DaoBean dao : daos) {
			try {
				DaoGenerater generater = new DaoMapperGenerater(dao, sourceFolder, app, context);
				generater.init();
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
				System.out.println(directory + ":" + dao);
				e.printStackTrace();
			}
		}
		data.put("$list", list);

	}

	@Override
	public String getTemplate() throws Exception {

		return "template/java/merge/mapper/default.xml";
	}

}
