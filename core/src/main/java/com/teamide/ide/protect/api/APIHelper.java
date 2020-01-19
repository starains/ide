package com.teamide.ide.protect.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.util.StringUtil;

public class APIHelper {

	private final String server = "http://open.coos.top";

	public static APIHelper get() {

		return new APIHelper();
	}

	public APIHelper() {

	}

	public JSONObject updateCheck(String name) {

		String url = server + "/update/check?name=" + name;
		JSONObject param = new JSONObject();

		JSONObject result = (JSONObject) post(url, param);

		return result;
	}

	public byte[] updateDownload(String name, String path) {

		String url = server + "/update/download";
		JSONObject param = new JSONObject();
		param.put("name", name);
		param.put("path", path);
		HttpRequest request = HttpRequest.post(url);
		request.body(param);
		HttpResponse response = request.execute();

		return response.bodyBytes();
	}

	public static void main(String[] args) {

		byte[] bytes = APIHelper.get().updateDownload("coospro.ide", "/bin/startup.bat");

		System.out.println(new String(bytes));
	}

	private JSON post(String url, JSON data) {

		HttpRequest request = HttpRequest.post(url);
		request.body(data);
		HttpResponse response = request.execute();
		return getResult(url, response);
	}

	private JSON getResult(String url, HttpResponse response) {

		String body = response.body();
		if (!StringUtil.isEmpty(body)) {
			try {
				JSONObject result = JSON.parseObject(body);
				if (result.get("errcode") != null && result.getInteger("errcode") == 0) {
					return (JSON) JSON.toJSON(result.get("value"));
				}
			} catch (Exception e) {

			}
		}
		return null;
	}
}
