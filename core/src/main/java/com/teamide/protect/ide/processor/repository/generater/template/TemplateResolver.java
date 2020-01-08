package com.teamide.protect.ide.processor.repository.generater.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.jexl.JexlTool;
import com.teamide.util.ObjectUtil;
import com.teamide.util.StringUtil;

public class TemplateResolver {
	protected final JSONObject data;
	protected final String template;

	public TemplateResolver(JSONObject data, String template) {
		this.data = data;
		this.template = template;
	}

	public String build() {
		String content = this.template;
		content = content.replaceAll("\r\n", "#r#n#");

		content = resolveFor(content);
		content = resolveIf(content);
		content = resolveData(content);

		for (int i = 0; i < 10; i++) {
			content = content.replaceAll("(#r#n#\t{0,}#r#n#\t{0,}#r#n#)", "#r#n#");
		}

		content = content.replaceAll("#r#n#", "\r\n");
		return content;
	}

	public String resolveFor(String content) {
		StringBuffer buffer = new StringBuffer();
		Pattern pattern = Pattern.compile("#\\|forStart\\|(.*?)\\|forEnd\\|#");
		Matcher matcher = pattern.matcher(content);
		int index = 0;
		while (matcher.find()) {
			String group = matcher.group();
			buffer.append(content.substring(index, matcher.start()));
			index = matcher.end();
			String oneTemplate = group.replace("#|forStart|", "").replace("|forEnd|#", "");
			int leftIndex = oneTemplate.indexOf("(");
			int rightIndex = oneTemplate.indexOf(")");
			String[] names = oneTemplate.substring(leftIndex + 1, rightIndex).split(",");
			String listName = null;
			if (names.length > 0) {
				listName = names[0].trim();
			}
			String oneName = null;
			if (names.length > 1) {
				oneName = names[1].trim();
			}
			String indexName = null;
			if (names.length > 2) {
				indexName = names[2].trim();
			}
			oneTemplate = oneTemplate.substring(rightIndex + 1);
			if (!StringUtil.isEmpty(listName)) {
				Object listValue = jexlScript(listName);
				StringBuffer listContent = new StringBuffer();
				if (listValue != null) {
					JSONArray array = (JSONArray) JSONArray.toJSON(listValue);
					for (int i = 0; i < array.size(); i++) {
						Object oneValue = array.get(i);
						if (!StringUtil.isEmpty(oneName)) {
							data.put(oneName, oneValue);
						}
						if (!StringUtil.isEmpty(indexName)) {
							data.put(indexName, oneValue);
						}
						String oneContent = resolveIf(oneTemplate);
						oneContent = resolveData(oneContent);
						listContent.append(oneContent);
					}
				}
				buffer.append(listContent);
			}
		}
		if (content.length() > index) {

			buffer.append(content.substring(index));
		}

		return buffer.toString();
	}

	public String resolveIfFor(String content) {
		StringBuffer buffer = new StringBuffer();
		Pattern pattern = Pattern.compile("#\\|ifForStart\\|(.*?)\\|ifForEnd\\|#");
		Matcher matcher = pattern.matcher(content);
		int index = 0;
		while (matcher.find()) {
			String group = matcher.group();
			buffer.append(content.substring(index, matcher.start()));
			index = matcher.end();
			String oneTemplate = group.replace("#|ifForStart|", "").replace("|ifForEnd|#", "");
			int leftIndex = oneTemplate.indexOf("(");
			int rightIndex = oneTemplate.indexOf(")");
			String[] names = oneTemplate.substring(leftIndex + 1, rightIndex).split(",");
			String listName = null;
			if (names.length > 0) {
				listName = names[0].trim();
			}
			String oneName = null;
			if (names.length > 1) {
				oneName = names[1].trim();
			}
			String indexName = null;
			if (names.length > 2) {
				indexName = names[2].trim();
			}
			oneTemplate = oneTemplate.substring(rightIndex + 1);
			if (!StringUtil.isEmpty(listName)) {
				Object listValue = jexlScript(listName);
				StringBuffer listContent = new StringBuffer();
				if (listValue != null) {
					JSONArray array = (JSONArray) JSONArray.toJSON(listValue);
					for (int i = 0; i < array.size(); i++) {
						Object oneValue = array.get(i);
						if (!StringUtil.isEmpty(oneName)) {
							data.put(oneName, oneValue);
						}
						if (!StringUtil.isEmpty(indexName)) {
							data.put(indexName, oneValue);
						}
						String oneContent = resolveData(oneTemplate);
						listContent.append(oneContent);
					}
				}
				buffer.append(listContent);
			}
		}
		if (content.length() > index) {

			buffer.append(content.substring(index));
		}

		return buffer.toString();
	}

	public String resolveIf(String content) {
		StringBuffer buffer = new StringBuffer();
		Pattern pattern = Pattern.compile("#\\|ifStart\\|(.*?)\\|ifEnd\\|#");
		Matcher matcher = pattern.matcher(content);
		int index = 0;
		while (matcher.find()) {
			String group = matcher.group();
			buffer.append(content.substring(index, matcher.start()));
			index = matcher.end();
			String oneTemplate = group.replace("#|ifStart|", "").replace("|ifEnd|#", "");
			int leftIndex = oneTemplate.indexOf("(");
			int rightIndex = oneTemplate.indexOf(")");
			String jexlScript = oneTemplate.substring(leftIndex + 1, rightIndex).trim();
			oneTemplate = oneTemplate.substring(rightIndex + 1);
			if (!StringUtil.isEmpty(jexlScript)) {
				Object value = jexlScript(jexlScript);
				if (ObjectUtil.isTrue(value)) {
					String oneContent = resolveIfFor(oneTemplate);
					oneContent = resolveData(oneContent);
					buffer.append(oneContent);
				}
			}
		}
		if (content.length() > index) {
			buffer.append(content.substring(index));
		}

		return buffer.toString();
	}

	public String resolveData(String content) {
		StringBuffer buffer = new StringBuffer();
		Pattern pattern = Pattern.compile("#\\|\\|(.*?)\\|\\|#");
		Matcher matcher = pattern.matcher(content);
		int index = 0;
		while (matcher.find()) {
			String group = matcher.group();
			buffer.append(content.substring(index, matcher.start()));
			index = matcher.end();
			String jexlScript = group.replace("#||", "").replace("||#", "");
			if (!StringUtil.isEmpty(jexlScript)) {
				Object value = jexlScript(jexlScript);
				if (value != null) {
					buffer.append(String.valueOf(value));
				}
			}
		}
		if (content.length() > index) {

			buffer.append(content.substring(index));
		}

		return buffer.toString();
	}

	public Object jexlScript(String jexlScript) {

		try {
			Object value = JexlTool.invoke(jexlScript, data);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("#r#n#\t\t\t#r#n#".replaceAll("(#r#n#\t{0,}#r#n#)", "#r#n#"));
	}

}
