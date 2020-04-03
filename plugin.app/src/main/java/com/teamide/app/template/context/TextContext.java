package com.teamide.app.template.context;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.teamide.app.template.util.JexlTool;
import com.teamide.util.StringUtil;

public class TextContext extends Context {

	private final String text;

	public TextContext(StringBuffer text, TagContext parent) {
		this(text.toString(), parent);
	}

	public TextContext(String text, TagContext parent) {
		super(parent);
		this.text = text;
	}

	@Override
	public void write(Writer writer, JSONObject data) throws IOException {
		if (text != null) {
			writer.write(resolveText(data));
		}
	}

	public String resolveText(JSONObject data) {
		StringBuffer buffer = new StringBuffer();
		Pattern pattern = Pattern.compile("#\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while (matcher.find()) {
			buffer.append(text.substring(index, matcher.start()));
			index = matcher.end();
			String jexlScript = matcher.group(1);
			if (!StringUtil.isEmpty(jexlScript)) {
				Object value = JexlTool.invoke(jexlScript, data);
				if (value != null) {
					buffer.append(String.valueOf(value));
				}
			}
		}
		if (text.length() > index) {

			buffer.append(text.substring(index));
		}

		return buffer.toString();
	}

	public String getText() {
		return text;
	}

}
