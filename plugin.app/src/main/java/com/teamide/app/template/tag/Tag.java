package com.teamide.app.template.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.template.context.TagContext;
import com.teamide.util.StringUtil;

public abstract class Tag {

	/** 前置包装符号 */
	private final Pattern before;
	/** 后置包装符号 */
	private final Pattern after;

	public Tag(Pattern before, Pattern after) {
		this.before = before;
		this.after = after;
	}

	public TagContext newTagContext(TagContext parent) {

		return new TagContext(this, parent);
	}

	public abstract void write(Writer writer, TagContext context, JSONObject data) throws IOException;

	public TagMatch matchBefore(String str) {
		if (StringUtil.isEmpty(str) || before == null) {
			return null;
		}

		Matcher matcher = before.matcher(str);
		TagMatch tagMatch = null;
		while (matcher.find()) {
			tagMatch = new TagMatch();
			tagMatch.setStart(matcher.start());
			tagMatch.setEnd(matcher.end());
			tagMatch.setGroup(matcher.group(1));
			break;
		}
		return tagMatch;
	}

	public TagMatch matchAfter(String str) {
		if (StringUtil.isEmpty(str) || after == null) {
			return null;
		}

		Matcher matcher = after.matcher(str);
		TagMatch tagMatch = null;
		while (matcher.find()) {
			tagMatch = new TagMatch();
			tagMatch.setGroup(matcher.group());
			tagMatch.setStart(matcher.start());
			tagMatch.setEnd(matcher.end());
			break;
		}
		return tagMatch;
	}

}
