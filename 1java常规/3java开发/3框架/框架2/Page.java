package com.common.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

/**
 * 封装分页和排序参数及分页查询结果.
 */
public class Page extends RowBounds implements Serializable {
	private static final long serialVersionUID = -1993051712091479583L;

	private static final int DEFAULT_PAGESIZE = 10;
	
    public static final Page DEFAULT = new Page();
    
	private int pageNo = 1;							// 页码(当前页)
	private int pageSize = DEFAULT_PAGESIZE;		// 每页的记录数量
	private int totalPage = 0;						// 总页数
	private int totalCount = 0;						// 总记录数
	private List<?> result = Collections.emptyList();// 数据列

	public Page() {
	}

	public Page(int pageSize) {
		setPageSize(pageSize);
	}

	public Page(int pageNo, int pageSize) {
		setPageNo(pageNo);
		setPageSize(pageSize);
	}

	public Page(int pageNo, int pageSize, int totalCount, List<?> result) {
		setPageNo(pageNo);
		setPageSize(pageSize);
		setTotalCount(totalCount);
		setResult(result);
	}

	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = (pageNo < 1) ? 1 : pageNo;
	}

	/**
	 * 获得每页的记录数量,默认为DEFAULT_PAGESIZE.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量,低于1时自动调整为DEFAULT_PAGESIZE.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = (pageSize < 1) ? DEFAULT_PAGESIZE : pageSize;
	}

	/**
	 * 取得总记录数, 默认值为0.
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数, 默认值为0.
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = (totalCount < 0) ? 0 : totalCount;
	}
	
	/**
	 * 总页数.
	 */
	public int getTotalPage() {
		totalPage = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			totalPage++;
		}
		return totalPage;
	}

	/**
	 * 页内的数据列表.
	 */
	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean getHasPre() {
		return pageNo - 1 >= 1;
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean getHasNext() {
		return pageNo + 1 <= getTotalPage();
	}

	/**
	 * 取得上页的页号,序号从1开始.
	 */
	public int getPrePage() {
		if (getHasPre())
			return pageNo - 1;
		else
			return pageNo;
	}

	/**
	 * 取得下页的页号,序号从1开始.
	 */
	public int getNextPage() {
		if (getHasNext())
			return pageNo + 1;
		else
			return pageNo;
	}
	
	/**=====================================================================================*/
	
	/**
	 * 偏移量,用于sql分页查询(从0开始)
	 */
	public int getOffset() {
		return ((pageNo - 1) * pageSize);
	}

	public int getLimit() {
		return pageSize;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " [pageNo=" + getPageNo() + ",pageSize=" + getPageSize() + ",totalPage=" + getTotalPage() + ",totalCount=" + getTotalCount() + ",resultSize=" + getResult().size() + "]";
	}
}
