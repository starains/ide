package com.teamide.app.template.context;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.template.tag.Tag;
import com.teamide.app.template.tag.TagMatch;

public class TagContext extends Context {

	protected final Tag tag;

	protected String group;

	protected final List<Context> contexts = new ArrayList<Context>();

	public TagContext(Tag tag, TagContext parent) {
		super(parent);
		this.tag = tag;
	}

	public Tag getTag() {
		return tag;
	}

	public void write(Writer writer, JSONObject data) throws IOException {
		tag.write(writer, this, data);
	}

	public TagMatch matchBefore(String str) {
		return tag.matchBefore(str);
	}

	public TagMatch matchAfter(String str) {
		return tag.matchAfter(str);
	}

	public TagContext addContext(Context context) {
		this.contexts.add(context);
		return this;
	}

	public List<Context> getContexts() {
		return contexts;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
