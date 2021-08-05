package com.touchealth.platform.processengine.controller;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;

public class BaseController {

	private HttpSession getSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return session;
	}

	public Object getSessionName(HttpServletRequest request, String key) {
		HttpSession session = getSession(request);
		return session.getAttribute(key);
	}

	public void addSession(HttpServletRequest request, String key, Object value) {
		HttpSession session = getSession(request);
		session.setAttribute(key, value);
		session.setMaxInactiveInterval(2 * 60 * 60);
	}

	public void removeSession(HttpServletRequest request, String key) {
		HttpSession session = getSession(request);
		session.removeAttribute(key);
	}

	public void destorySession(HttpServletRequest request) {
		HttpSession session = getSession(request);
		session.invalidate();
	}

	public String getRemoteIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (Exception e) {

				}
				ip = inet.getHostAddress();
			}
		}
		if (ip != null && ip.length() > 15) {
			if (ip.indexOf(",") > 0) {
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}

}
