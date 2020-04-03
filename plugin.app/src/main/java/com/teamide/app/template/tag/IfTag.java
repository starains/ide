package com.teamide.app.template.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.template.context.Context;
import com.teamide.app.template.context.TagContext;
import com.teamide.app.template.util.JexlTool;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class IfTag extends Tag {

	public IfTag() {
		super(Pattern.compile("#ifStart\\((.*?)\\)#"), Pattern.compile("#ifEnd#"));
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

		Object res = JexlTool.invoke(group, data);
		if (ObjectUtil.isTrue(res)) {
			for (Context one_context : context.getContexts()) {
				one_context.write(writer, data);
			}
		}
	}

}
