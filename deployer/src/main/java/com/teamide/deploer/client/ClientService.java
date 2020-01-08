package com.teamide.deploer.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;

public class ClientService {

	public final ClientListener listener;

	public ClientService(ClientListener listener) {
		this.listener = listener;
	}

	public JSONObject listen(JSONObject data) throws Exception {
		String path = getUrl("/runner/listen");
		byte[] bytes = post(path, data);
		if (bytes != null) {
			return JSONObject.parseObject(new String(bytes));
		}
		return null;
	}

	public byte[] download(JSONObject data) throws Exception {
		String path = getUrl("/runner/download");
		byte[] bytes = post(path, data);
		return bytes;
	}

	public JSONObject task(JSONObject data) throws Exception {
		String path = getUrl("/runner/task");
		byte[] bytes = post(path, data);
		if (bytes != null) {
			return JSONObject.parseObject(new String(bytes));
		}
		return null;
	}

	public JSONObject notice(JSONObject data) throws Exception {
		String path = getUrl("/runner/notice");
		byte[] bytes = post(path, data);
		if (bytes != null) {
			return JSONObject.parseObject(new String(bytes));
		}
		return null;
	}

	public String getUrl(String path) throws Exception {
		String server = listener.client.getServer();
		if (!server.endsWith("/")) {
			server += "/";
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return server + path;
	}

	public byte[] post(String path, JSONObject data) throws Exception {
		JSONObject json = new JSONObject();
		json.put("client", listener.client);
		json.put("name", listener.name);
		json.put("token", listener.token);
		json.put("data", data);

		HttpURLConnection conn = null;
		InputStream stream = null;
		try {
			URL url = new URL(path);
			// 打开和url之间的连接
			conn = (HttpURLConnection) url.openConnection();

			/** 设置URLConnection的参数和普通的请求属性****start ***/

			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

			/** 设置URLConnection的参数和普通的请求属性****end ***/

			// 设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
			// 最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
			// post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setRequestMethod("POST");// GET和POST必须全大写
			/** GET方法请求*****start */
			/**
			 * 如果只是发送GET方式请求，使用connet方法建立和远程资源之间的实际连接即可；
			 * 如果发送POST方式的请求，需要获取URLConnection实例对应的输出流来发送请求参数。
			 */
			conn.connect();

			/** GET方法请求*****end */

			/*** POST方法请求****start */
			if (data != null) {
				PrintWriter out = new PrintWriter(conn.getOutputStream());// 获取URLConnection对象对应的输出流

				out.print(data.toJSONString());// 发送请求参数即数据

				out.flush();// 缓冲数据
			}

			/*** POST方法请求****end */

			// 获取URLConnection对象对应的输入流
			stream = conn.getInputStream();

			// 构造一个字符流缓存
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			int b = -1;
			while ((b = stream.read()) != -1) {
				baos.write(b);
			}

			return baos.toByteArray();
		} finally {
			if (stream != null) {
				try {
					// 关闭流
					stream.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					// 断开连接，最好写上，disconnect是在底层tcp
					// socket链接空闲时才切断。如果正在被其他线程使用就不切断。
					// 固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
					conn.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}
}
