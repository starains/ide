package com.teamide.app.template.context;

import java.io.IOException;
import java.io.Writer;

import com.alibaba.fastjson.JSONObject;

public abstract class Context {

	protected final TagContext parent;

	public Context(TagContext parent) {
		this.parent = parent;
	}

	public abstract void write(Writer writer, JSONObject data) throws IOException;

	public TagContext getParent() {
		return parent;
	}

}
