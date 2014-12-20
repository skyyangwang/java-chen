package com.common.report;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Description:报表数据模板<br />
 * </p>
 * @title ReportData.java
 * @package com.common.report 
 * @author justin.xu
 * @version 0.1 2014年6月10日
 */
public class ReportData {
	/**
	 * 报表文件路径
	 */
	private String reportFilePath;

	/**
	 * 报表参数对象
	 */
	private Map parametersDto;

	/**
	 * 报表集合对象
	 */
	private List fieldsList;

	public ReportData() {
	}

	/**
	 * 构造函数
	 * 
	 * @param pReportFilePath
	 *            报表文件路径
	 * @param pParametersDto
	 *            报表参数集
	 * @param pFieldsList
	 *            报表字段列表集合
	 */
	public ReportData(String pReportFilePath, Map pParametersDto,
			List pFieldsList) {
		reportFilePath = pReportFilePath;
		parametersDto = pParametersDto;
		fieldsList = pFieldsList;
	}

	public String getReportFilePath() {
		return reportFilePath;
	}

	/**
	 * 设置报表文件路径
	 * 
	 * @param reportFilePath
	 */
	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public Map getParametersDto() {
		return parametersDto;
	}

	/**
	 * 设置报表参数集
	 * 
	 * @param parametersDto
	 */
	public void setParametersDto(Map parametersDto) {
		this.parametersDto = parametersDto;
	}

	public List getFieldsList() {
		return fieldsList;
	}

	/**
	 * 设置报表字段列表集合
	 * 
	 * @param fieldsList
	 */
	public void setFieldsList(List fieldsList) {
		this.fieldsList = fieldsList;
	}

}
