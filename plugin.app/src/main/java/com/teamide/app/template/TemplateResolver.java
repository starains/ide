package com.teamide.app.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.teamide.app.template.context.TagContext;
import com.teamide.app.template.context.TemplateContext;
import com.teamide.app.template.context.TextContext;
import com.teamide.app.template.tag.ForTag;
import com.teamide.app.template.tag.IfTag;
import com.teamide.app.template.tag.Tag;
import com.teamide.app.template.tag.TagMatch;
import com.teamide.util.ResourceUtil;

public class TemplateResolver {
	public static String NEW_LINE = System.getProperty("line.separator");

	private final Reader reader;

	private int lineIndex = -1;

	private final Tag[] tags = new Tag[] { new ForTag(), new IfTag() };

	public TemplateResolver(Reader reader) {
		this.reader = reader;
	}

	private List<TagContext> tag_context_cache = new ArrayList<TagContext>();

	public TemplateContext resolve() throws IOException {
		TemplateContext context = new TemplateContext();
		String readLine = null;

		BufferedReader reader = new BufferedReader(this.reader);
		while ((readLine = reader.readLine()) != null) {
			readLine += NEW_LINE;
			boolean matched = false;
			// 匹配结尾
			if (tag_context_cache.size() > 0) {
				TagContext last_tag_context = tag_context_cache.get(tag_context_cache.size() - 1);
				TagMatch tagMatch = last_tag_context.matchAfter(readLine);
				if (tagMatch != null) {
					tag_context_cache.remove(last_tag_context);
					matched = true;
				}
			}

			for (Tag tag : tags) {
				// 匹配开始
				TagMatch tagMatch = tag.matchBefore(readLine);
				if (tagMatch != null) {
					matched = true;
					TagContext tag_context;
					if (tag_context_cache.size() > 0) {
						TagContext last_tag_context = tag_context_cache.get(tag_context_cache.size() - 1);

						tag_context = tag.newTagContext(last_tag_context);
						last_tag_context.addContext(tag_context);
					} else {
						tag_context = tag.newTagContext(context);
						context.addContext(tag_context);
					}
					tag_context.setGroup(tagMatch.getGroup());
					tag_context_cache.add(tag_context);
					break;
				}
			}
			if (!matched) {
				if (tag_context_cache.size() > 0) {
					TagContext last_tag_context = tag_context_cache.get(tag_context_cache.size() - 1);

					TextContext text_context = new TextContext(readLine, last_tag_context);
					last_tag_context.addContext(text_context);
				} else {
					TextContext text_context = new TextContext(readLine, context);
					context.addContext(text_context);
				}
			}

		}
		return context;
	}

	public static void main(String[] args) throws IOException {
		InputStream stream = ResourceUtil.load("template/test/template");

		InputStreamReader reader = new InputStreamReader(stream);
		TemplateResolver resolver = new TemplateResolver(reader);
		TemplateContext context = resolver.resolve();
		System.out.println(resolver.lineIndex);

		List<SerializerFeature> features = new ArrayList<SerializerFeature>();
		features.add(SerializerFeature.WriteNullListAsEmpty);
		features.add(SerializerFeature.WriteNullStringAsEmpty);
		features.add(SerializerFeature.WriteNullNumberAsZero);
		features.add(SerializerFeature.WriteNullBooleanAsFalse);
		features.add(SerializerFeature.WriteMapNullValue);
		features.add(SerializerFeature.PrettyFormat);
		SerializerFeature[] sfs = new SerializerFeature[features.size()];
		System.out.println("-----------context-----------");
		System.out.println(JSON.toJSONString(context, features.toArray(sfs)));
		StringWriter writer = new StringWriter();
		JSONObject data = new JSONObject();
		JSONArray $list = new JSONArray();
		$list.add("111");
		$list.add("111");
		$list.add("111");
		data.put("$list", $list);
		context.write(writer, data);
		System.out.println("-----------out-----------");
		System.out.println(writer.toString());

	}

}
