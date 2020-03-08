package com.teamide.ide.protect.processor.repository.generater;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.AppContext;
import com.teamide.app.bean.BeanBean;
import com.teamide.app.bean.BeanPropertyBean;
import com.teamide.ide.protect.processor.param.RepositoryProcessorParam;
import com.teamide.ide.protect.processor.repository.project.AppBean;
import com.teamide.util.StringUtil;

public class BeanGenerater extends CodeGenerater {

	protected final BeanBean bean;

	public BeanGenerater(BeanBean bean, RepositoryProcessorParam param, AppBean app, AppContext context) {
		super(bean, param, app, context);
		this.bean = bean;
	}

	public void generate() throws Exception {
		super.generate();
	}

	public String getPackage() {
		return getBeanPackage();
	}

	public String getClassName() {
		return super.getClassName() + "Bean";
	}

	@Override
	public void buildData() {
		List<BeanPropertyBean> propertys = bean.getPropertys();
		JSONArray $propertys = new JSONArray();
		this.data.put("$propertys", $propertys);
		if (propertys != null) {
			for (BeanPropertyBean property : propertys) {
				String $name = property.getName();
				String $type = property.getType();
				String $columnname = property.getColumnname();
				if (StringUtil.isEmpty($name)) {
					continue;
				}

				JSONObject $property = (JSONObject) JSON.toJSON(property);
				$propertys.add($property);
				$property.put("$name", $name);
				$property.put("$type", $type);
				$property.put("$columnname", $columnname);
				String $getname = "get" + ($name.substring(0, 1)).toUpperCase() + ($name.substring(1));
				String $setname = "set" + ($name.substring(0, 1)).toUpperCase() + ($name.substring(1));
				$property.put("$getname", $getname);
				$property.put("$setname", $setname);
			}
		}

	}

	@Override
	public String getTemplate() throws Exception {
		return "template/java/bean/bean";
	}

}
