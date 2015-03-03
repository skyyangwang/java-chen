package com.cxdai.console.cms.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.cxdai.console.cms.constant.CmsConstant;
import com.util.DateUtils;
import com.util.PropertiesUtil;

public class FileLoadUtil {

	public static String ROOTUPLOADPATH = PropertiesUtil.getValue("www_cms_upload");
	public static String ROOTUPLOADPATH2 = PropertiesUtil.getValue("www_ss_upload");

	public static char FENGE = '/';

	public static String upload(FileUploadEvent event, String cmsUploadPath, String fileDic, int fileSizeLimit) throws IOException {
		UploadedFile item = event.getUploadedFile();
		Date date = new Date(System.currentTimeMillis());
		String pixFilePath = cmsUploadPath + File.separator + fileDic;
		String pathFileDic = "";
		String pathDic = "";
		
		//幻灯片，不添加日期目录；
		if(fileDic.equals(CmsConstant.SLIDESHOWFILEDIC1)||fileDic.equals(CmsConstant.SLIDESHOWFILEDIC2)||fileDic.equals(CmsConstant.SLIDESHOWFILEDIC3)){
			pathFileDic = createUploadPath2(date, pixFilePath);
			pathDic = pixFilePath;
		}else{
			pathFileDic = createUploadPath(date, pixFilePath);
			pathDic = pixFilePath + File.separator + pathFileDic;
		}
		
		String newFileName = getNewFileName(date);
		// 解码文件名，当文件命中有中文等其他字符时
		String temp_path = java.net.URLDecoder.decode(item.getName(), "utf-8");

		if (temp_path.lastIndexOf('.') < 0) {
			return "";
		}

		if (item.getSize() > fileSizeLimit) {
			return "";
		}
		// 重命名文件
		newFileName += temp_path.substring(temp_path.lastIndexOf('.'));
		FileOutputStream out = null;
		try {
			String ss = pathDic + File.separator + newFileName;
			out = new FileOutputStream(pathDic + File.separator + newFileName);
			out.write(item.getData());

		} catch (Exception e) {

		} finally {
			out.close();
		}

		String url2 = "";
		//幻灯片
		if(fileDic.equals(CmsConstant.SLIDESHOWFILEDIC1)||fileDic.equals(CmsConstant.SLIDESHOWFILEDIC2)||fileDic.equals(CmsConstant.SLIDESHOWFILEDIC3)){
			url2 = ROOTUPLOADPATH2 + FENGE + fileDic + FENGE + newFileName;
		}else{
			url2 = ROOTUPLOADPATH + FENGE + fileDic + FENGE + pathFileDic + FENGE + newFileName;
		}
	
		return url2;

	}

	private static String getNewFileName(Date date) {
		return DateUtils.getYear() + "" + DateUtils.getMonth() + DateUtils.getDay() + DateUtils.getHour() + DateUtils.getMinute() + DateUtils.getSecond();
	}

	private static String createUploadPath(Date date, String cmsUploadPath2) {
		String pathFileDic = DateUtils.getYear() + "-" + DateUtils.getMonth() + "-" + DateUtils.getDay();
		String pathDic = cmsUploadPath2 + File.separator + pathFileDic;
		File file = new File(pathDic);

		if (!file.exists()) {
			file.mkdirs();
		}

		return pathFileDic;
	}
	
	//路径不添加，年月日
	private static String createUploadPath2(Date date, String cmsUploadPath2) {

		String pathDic = cmsUploadPath2;
		File file = new File(pathDic);

		if (!file.exists()) {
			file.mkdirs();
		}

		return "";
	}

}
