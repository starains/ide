package com.teamide.ide.controller.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.teamide.bean.Status;
import com.teamide.client.ClientHandler;
import com.teamide.client.ClientSession;
import com.teamide.http.HttpRequest;
import com.teamide.http.HttpResponse;
import com.teamide.ide.bean.UserLoginBean;
import com.teamide.ide.listen.IDEListen;
import com.teamide.ide.listen.IDEListenHandler;
import com.teamide.ide.service.impl.BaseService;
import com.teamide.ide.service.impl.UserLoginService;
import com.teamide.util.ResponseUtil;
import com.teamide.util.StringUtil;

public class ListenHandler {

	public void listen(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ClientSession session = ClientHandler.getSession(request);
		if (session.get("USER_LOGIN_ID") != null) {
			String USER_LOGIN_ID = String.valueOf(session.get("USER_LOGIN_ID"));

			UserLoginBean loginBean = new UserLoginBean();
			loginBean.setId(USER_LOGIN_ID);
			loginBean.setEndtime(BaseService.PURE_DATETIME_FORMAT.format(new Date()));

			if (session.get("USER_LOGIN_IP") == null) {
				String ip = getIpAddress(request);
				setIP(USER_LOGIN_ID, ip);
				session.set("USER_LOGIN_IP", ip);
			}
			new UserLoginService().update(session, loginBean);
		}

		IDEListen ideListen = IDEListenHandler.get(session);
		Status status = Status.SUCCESS();
		try {
			status.setValue(ideListen.waitListen());
			ResponseUtil.outJSON(response, status);
		} catch (Exception e) {
		}

	}

	private void setIP(String USER_LOGIN_ID, String ip) throws Exception {
		if (StringUtil.isEmpty(USER_LOGIN_ID) || StringUtil.isEmpty(ip)) {
			return;
		}
		UserLoginBean loginBean = new UserLoginBean();
		loginBean.setId(USER_LOGIN_ID);
		loginBean.setIp(ip);

		try {
			String url = "https://api.map.baidu.com/location/ip?ak=26a9d76323f4844c62d222de456a5d31";
			url += "&coor=bd09ll";
			url += "&ip=" + ip;
			HttpRequest request = HttpRequest.get(url);
			request.timeout(1000 * 5);
			HttpResponse response = request.execute();
			String body = response.body();
			JSONObject json = JSONObject.parseObject(body);
			if (json.get("content") != null) {
				JSONObject content = json.getJSONObject("content");

				if (content.get("address_detail") != null) {
					JSONObject address_detail = content.getJSONObject("address_detail");
					loginBean.setProvince(address_detail.getString("province"));
					loginBean.setCity(address_detail.getString("city"));
					loginBean.setDistrict(address_detail.getString("district"));
					loginBean.setStreet(address_detail.getString("street"));
				}
			}
		} catch (Exception e) {
		}
		new UserLoginService().update(null, loginBean);

	}

	private static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}
		return ip;
	}

}
