package com.teamide.ide.protect.util;

import java.util.Base64;

import com.alibaba.fastjson.JSONObject;

public class TokenUtil {

	static final Base64.Decoder decoder = Base64.getUrlDecoder();
	static final Base64.Encoder encoder = Base64.getUrlEncoder();

	public static String getToken(JSONObject json) {
		try {
			String str = json.toJSONString();
			byte[] bytes = str.getBytes("UTF-8");
			bytes = encoder.encode(bytes);
			String token = new String(bytes, "UTF-8");
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getJSON(String token) {
		try {
			byte[] bytes = token.getBytes("UTF-8");
			bytes = decoder.decode(bytes);
			String str = new String(bytes, "UTF-8");
			return JSONObject.parseObject(str);
		} catch (Exception e) {
		}
		return null;
	}

}
