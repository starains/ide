package com.teamide.app.template.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.app.template.context.Context;
import com.teamide.app.template.context.TagContext;
import com.teamide.app.template.util.JexlTool;
import com.teamide.util.StringUtil;

public class ForTag extends Tag {

	public ForTag() {
		super(Pattern.compile("#forStart\\((.*?)\\)#"), Pattern.compile("#forEnd#"));
	}

	@Override
	public void write(Writer writer, TagContext context, JSONObject data) throws IOException {
		if (context == null || context.getGroup() == null) {
			return;
		}
		String group = context.getGroup().trim();
		if (StringUtil.isEmpty(group)) {
			return;
		}
		String[] keys = group.split(",");
		if (keys.length == 0) {
			return;
		}
		String listName = keys[0].trim();

		if (StringUtil.isEmpty(listName)) {
			return;
		}

		Object res = JexlTool.invoke(listName, data);
		if (res == null) {
			return;
		}
		String oneName = null;
		if (keys.length > 1) {
			oneName = keys[1].trim();
		}
		String indexName = null;
		if (keys.length > 2) {
			indexName = keys[2].trim();
		}
		JSONArray array = (JSONArray) JSON.toJSON(res);
		for (int i = 0; i < array.size(); i++) {
			if (StringUtil.isNotEmpty(indexName)) {
				data.put(indexName, i);
			}
			if (StringUtil.isNotEmpty(oneName)) {
				data.put(oneName, array.get(i));
			}
			for (Context one_context : context.getContexts()) {
				one_context.write(writer, data);
			}
		}
	}
}
