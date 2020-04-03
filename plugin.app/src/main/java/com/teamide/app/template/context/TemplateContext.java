package com.teamide.app.template.context;

import java.io.IOException;
import java.io.Writer;

import com.alibaba.fastjson.JSONObject;

public class TemplateContext extends TagContext {

	public TemplateContext() {
		super(null, null);
	}

	public void write(Writer writer, JSONObject data) throws IOException {
		for (Context context : contexts) {
			context.write(writer, data);
		}
	}
}
