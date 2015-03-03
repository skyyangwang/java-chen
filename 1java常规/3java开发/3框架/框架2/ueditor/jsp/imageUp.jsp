<%@page import="com.util.PropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.util.Uploader"%>
	
<%
	request.setCharacterEncoding("utf-8");
	response.setCharacterEncoding("utf-8");
	Uploader up = new Uploader(request);
	up.setSavePath(PropertiesUtil.getValue("cms_upload_path"));
	up.setAllowFiles(Uploader.default_img_allowFiles);
	up.setMaxSize(2048); //单位KB
	up.upload();

	String callback = request.getParameter("callback");
	
	String result = "{\"name\":\"" + up.getFileName()
					+ "\",\"originalName\":\"" + up.getOriginalName()
					+ "\",\"size\":\"" + up.getSize()
					+ "\",\"state\":\"" + up.getState()
					+ "\",\"type\":\"" + up.getType()
					+ "\",\"url\":\"" + up.getUrl() + "\"}";

	result = result.replaceAll("\\\\", "\\\\");

	if (callback == null) {
		response.getWriter().print(result);
	} else {
		response.getWriter().print("<script>" + callback + "(" + result + ")</script>");
	}
%>
