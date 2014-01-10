package com.cares.ynt.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class WriteCommExcel {
	//--最后要输入的流 --
	private OutputStream out;
	//每个sheet最大行数
	protected final int SHEETROWS=60000;
	//标题列
	private String[] attributes;
	//excel对象
	private WritableWorkbook workBook;
	//当前sheet
	private WritableSheet currentSheet;
	// 汇总sheet 
	private  WritableSheet sumSheet;
	
	
	//当前行数
	private int currentRows=0;
	
	//当前行数
	private int currentSumRows=0;
	
	
	//样式封装
	private CellStyle style;
	private String title;
	//总行数 
	private int rowsCount=0;
	private int rowsSumCount=0;
	//sheet3当前行数
	private int currentSumThreeRows=0;
	//sheet3 
	private  WritableSheet sumSheet3;
	private int rowsSumThreeCount=0;
	
 	//当前sheet
	private WritableSheet[] currentSheets;
	
	private jxl.write.DateFormat df = new jxl.write.DateFormat("yyyyMMdd");
	private jxl.write.WritableCellFormat wcfDF = new jxl.write.WritableCellFormat(df);   
	
	/**
	 * 
	 * @param out 文件输出流
	 * @param attributes 行标题数组 比如: 姓名，年龄，地址 。。。。
	 * ---------------------------------
	 *    姓名		|年龄		|地址
	 *    name1     |12			|address1
	 *    name1     |12			|address1
	 *    name1     |12			|address1 
	 */
	public WriteCommExcel(OutputStream out,String[] attributes){
	   this.attributes=attributes;
	   this.out= new BufferedOutputStream(out);
	   this.style=new CellStyle();
	   this.init();
	}
	
	/**
	 * 
	 * @param out 文件输出流
	 * @param title Excel 文件标题，比如：XXXX报表
	 * @param attributes 行标题数组 比如: 姓名，年龄，地址 。。。。
	 *               XXXX报表
	 *    ---------------------------------
	 *    姓名		|年龄		|地址
	 *    name1     |12			|address1
	 *    name1     |12			|address1
	 *    name1     |12			|address1   
	 */
	public WriteCommExcel(OutputStream out,String title,String[] attributes){
		   this.attributes=attributes;
		   this.out= new BufferedOutputStream(out);
		   this.title = title;
		   this.style=new CellStyle();
		   this.init();
		}
	
	public WriteCommExcel(OutputStream out,String title[],String[][] attributes,String sheeatName[] ){
		this.out= new BufferedOutputStream(out);
		this.style=new CellStyle();
		   try{
			   workBook=Workbook.createWorkbook(out);
			   createSumSheet( sheeatName,title,attributes);
		   }catch(Exception exc)
		   {
			   exc.printStackTrace();
		   }
		 
		}
	public WriteCommExcel(OutputStream out,String title[],String[][] attributes,String sheeatName[] ,boolean hasTitle,boolean hasHeaders){
		this.out= new BufferedOutputStream(out);
		this.style=new CellStyle();
		try{
			workBook=Workbook.createWorkbook(out);
			createSumSheet( sheeatName,title,attributes,hasTitle,hasHeaders);
		}catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
	}
	public WriteCommExcel(OutputStream out,String title[],String[][] attributes,String sheeatName[], String s){
		this.out= new BufferedOutputStream(out);
		this.style=new CellStyle();
		   try{
			   workBook=Workbook.createWorkbook(out);
			   createSumSheetThree( sheeatName,title,attributes);
		   }catch(Exception exc)
		   {
			   exc.printStackTrace();
		   }
		 
		}
	private synchronized void  addLine(List data) {
		
		System.out.println("");
		WritableCellFormat dashedRight = style.getCentreStyle();
//		jxl.write.DateFormat df = new jxl.write.DateFormat("yyyyMMdd");
//		jxl.write.WritableCellFormat wcfDF = new jxl.write.WritableCellFormat(df);   
		if (currentRows > SHEETROWS) {creatNewSheet();
		}
//		for (int i = 0; i < attributes.length; i++) {
//			String content = (data.get(i) != null ? data.get(i).toString() : "");
//			Label label = new Label(i, currentRows, content, dashedRight);
//			try {
//				currentSheet.addCell(label);
//			} catch (RowsExceededException e) {
//				e.printStackTrace();
//			} catch (WriteException e) {
//				e.printStackTrace();
//			}
//		}
		//modify  by xuliang --------for new data-type  cell
		for (int i = 0; i < attributes.length; i++) {
			Object obj = data.get(i);
			if(obj instanceof  Integer){
				Integer content = (data.get(i) == null ? 0 : Integer.parseInt(data.get(i)+""));
				jxl.write.Number nr = new jxl.write.Number(i, currentRows, content, dashedRight);
				try {
					currentSheet.addCell(nr);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}else if(obj instanceof  Date){
				Date d = (Date)data.get(i);
				jxl.write.DateTime dt = new  jxl.write.DateTime(i, currentRows, d, wcfDF);
				try {
					currentSheet.addCell(dt);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}else{
				String content = (data.get(i) != null ? data.get(i).toString() : "");
				Label label = new Label(i, currentRows, content, dashedRight);
				try {
					currentSheet.addCell(label);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
		}
		rowsCount++;
		currentRows++;
	}
	 
	/**
	 * 将数据写入汇总sheet  currentSumRows
	 * @param data
	 */
private synchronized void  addSumLine(List data,String  att[]) {
		
		WritableCellFormat dashedRight = style.getCentreStyle();
		for (int i = 0; i < att.length; i++) {
			String content = (data.get(i) != null ? data.get(i).toString() : "");
			Label label = new Label(i, currentSumRows, content, dashedRight);
			try {
				sumSheet.addCell(label);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
		rowsSumCount++;
		currentSumRows++;
	}

	private void close(){
		
		try {
			workBook.write();
			workBook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//--------------创建 ---
	private void init()  {
		try {
			 workBook=Workbook.createWorkbook(out);
			 creatNewSheet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private  void creatNewSheet()  {
		currentSheet= workBook.createSheet("Sheet"+(workBook.getNumberOfSheets()+1), workBook.getNumberOfSheets());
		currentRows=0;
		if(title != null){
			try {
			    //currentSheet.setRowView(0,20);
			      Label  label = new Label(0, currentRows, title, style.getTitleFormat());
			      currentSheet.addCell(label);
			      currentSheet.mergeCells(0, 0,  attributes.length-1,0);
			      currentRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<attributes.length;i++){
		      String content=(attributes[i]!=null?attributes[i].toString():"");
		      Label  label = new Label(i, currentRows, content, style.getAttributFormat());
		      try {
		    	  //currentSheet.setRowView(1,18);
		    	  currentSheet.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
			    }
		}
		currentRows++;
		rowsCount++;
	}
	
	

	public  WriteCommExcel(OutputStream out)
	{
		initMutSheet(out);
	}
	
	private void initMutSheet(OutputStream out)
	{
		try {
			this.out= new BufferedOutputStream(out);
			 workBook=Workbook.createWorkbook(out);
			 
			 this.style=new CellStyle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void createSheetByNames(String[]  sheetName,String  title[],String  attributes[][],List dataList)
	{
		if(sheetName==null||sheetName.length<=0) return ;
	//	currentSheets=new WritableSheet[sheetName.length];
		for(int i=0;i<sheetName.length;i++)
		{
			currentSheet=  workBook.createSheet(sheetName[i], i);
			 
			currentRows=0;
			if(title[i] != null){
				try {
				    //currentSheet.setRowView(0,20);
				      Label  label = new Label(0, currentRows, title[i], style.getTitleFormat());
				      currentSheet.addCell(label);
				      currentSheet.mergeCells(0, 0,  attributes[i].length-1,0);
				      currentRows++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 写列名。
			for(int n=0;n<attributes[i].length;n++){
			      String content=(attributes[i][n]!=null?attributes[i][n].toString():"");
			      Label  label = new Label(n, currentRows, content, style.getAttributFormat());
			      try {
			    	  currentSheet.addCell(label);
					} catch (Exception e) {
						e.printStackTrace();
				    }
			}
		  currentRows++;
			
			// 写数据。
		  
			List contentList = (List) dataList.get(i);
			
			for(int r=0;r<contentList.size();r++)
			{
				List rowList = (List) contentList.get(r);
				this.addLineBySheet(rowList, currentSheet,attributes[i]);
			}
			
		}
		
		close();
		
	}
	
	
private synchronized void  addLineBySheet(List data,WritableSheet sheet,String[] attr) {
		
		WritableCellFormat dashedRight = style.getCentreStyle();
		
		if (currentRows > SHEETROWS) {creatNewSheet();
		}
		for (int i = 0; i < attr.length; i++) {
			String content = (data.get(i) != null ? data.get(i).toString() : "");
			Label label = new Label(i, currentRows, content, dashedRight);
			try {
				currentSheet.addCell(label);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
		currentRows++;
		
	}
	
	
	
	/**
	 * 根据 sheetName创建 sheet  
	 * @param sheetName
	 * @param title
	 * @param attributes
	 */
	private  void createSumSheet(String[]  sheetName,String  title[],String  attributes[][])  {
		this.attributes=attributes[0];
		
		currentSheet= workBook.createSheet(sheetName[0], workBook.getNumberOfSheets());    //费用明细sheet
		currentRows=0;
		if(title[0] != null && !title[0].equals("")){
			try {
			    //currentSheet.setRowView(0,20);
			      Label  label = new Label(0, currentRows, title[0], style.getTitleFormat());
			      currentSheet.addCell(label);
			      currentSheet.mergeCells(0, 0,  attributes[0].length-1,0);
			      currentRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			currentRows++;
		}
		for(int i=0;i<attributes[0].length;i++){
		      String content=(attributes[0][i]!=null?attributes[0][i].toString():"");
		      Label  label = new Label(i, currentRows, content, style.getAttributFormat());
		      try {
		    	  //currentSheet.setRowView(1,18);
		    	  currentSheet.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
			    }
		}
		currentRows++;
		rowsCount++;
		
		if(sheetName.length<2) return ;
		sumSheet= workBook.createSheet(sheetName[1], workBook.getNumberOfSheets());    //费用明细sheet
		currentSumRows=0;
		if(title[1] != null){
			try {
			    //currentSheet.setRowView(0,20);
			      Label  label = new Label(0, currentSumRows, title[1], style.getTitleFormat());
			      sumSheet.addCell(label);
			      sumSheet.mergeCells(0, 0,  attributes[1].length-1,0);
			      currentSumRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<attributes[1].length;i++){
		      String content=(attributes[1][i]!=null?attributes[1][i].toString():"");
		      Label  label = new Label(i, currentSumRows, content, style.getAttributFormat());
		      try {
		    	  //currentSheet.setRowView(1,18);
		    	  sumSheet.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
			    }
		}
		
		currentSumRows++;
		rowsSumCount++;
	}
	
	/**
	 * 根据 sheetName创建 sheet  
	 * @param sheetName 表名
	 * @param title 标题
	 * @param attributes 列名
	 * @param hasTitle  是否写标题
	 * @param hasHeaders 是否写列名
	 */
	private  void createSumSheet(String[]  sheetName,String  title[],String  attributes[][],boolean hasTitle,boolean hasHeaders)  {
		this.attributes=attributes[0];
		
		currentSheet= workBook.createSheet(sheetName[0], workBook.getNumberOfSheets());    //费用明细sheet
		currentRows=0;
		if(hasTitle){
			if(title[0] != null && !title[0].equals("")){
				try {
					//currentSheet.setRowView(0,20);
					Label  label = new Label(0, currentRows, title[0], style.getTitleFormat());
					currentSheet.addCell(label);
					currentSheet.mergeCells(0, 0,  attributes[0].length-1,0);
					currentRows++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				currentRows++;
			}
		}
		if(hasHeaders){
			for(int i=0;i<attributes[0].length;i++){
				String content=(attributes[0][i]!=null?attributes[0][i].toString():"");
				Label  label = new Label(i, currentRows, content, style.getAttributFormat());
				try {
					//currentSheet.setRowView(1,18);
					currentSheet.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			currentRows++;
			rowsCount++;
		}
		
		if(sheetName.length<2) return ;
		sumSheet= workBook.createSheet(sheetName[1], workBook.getNumberOfSheets());    //费用明细sheet
		currentSumRows=0;
		if(title[1] != null){
			try {
				//currentSheet.setRowView(0,20);
				Label  label = new Label(0, currentSumRows, title[1], style.getTitleFormat());
				sumSheet.addCell(label);
				sumSheet.mergeCells(0, 0,  attributes[1].length-1,0);
				currentSumRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<attributes[1].length;i++){
			String content=(attributes[1][i]!=null?attributes[1][i].toString():"");
			Label  label = new Label(i, currentSumRows, content, style.getAttributFormat());
			try {
				//currentSheet.setRowView(1,18);
				sumSheet.addCell(label);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		currentSumRows++;
		rowsSumCount++;
	}
	/**
	 * 根据 sheetName创建 sheet  
	 * @param sheetName
	 * @param title
	 * @param attributes
	 */
	private  void createSumSheetThree(String[]  sheetName,String  title[],String  attributes[][])  {
		this.attributes=attributes[0];
		
		currentSheet= workBook.createSheet(sheetName[0], workBook.getNumberOfSheets());    //费用明细sheet
		currentRows=0;
		if(title[0] != null){
			try {
			    //currentSheet.setRowView(0,20);
			      Label  label = new Label(0, currentRows, title[0], style.getTitleFormat());
			      currentSheet.addCell(label);
			      currentSheet.mergeCells(0, 0,  attributes[0].length-1,0);
			      currentRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<attributes[0].length;i++){
		      String content=(attributes[0][i]!=null?attributes[0][i].toString():"");
		      Label  label = new Label(i, currentRows, content, style.getAttributFormat());
		      try {
		    	  //currentSheet.setRowView(1,18);
		    	  currentSheet.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
			    }
		}
		currentRows++;
		rowsCount++;
		
		if(sheetName.length<2) return ;
		sumSheet= workBook.createSheet(sheetName[1], workBook.getNumberOfSheets());    //费用明细sheet
		currentSumRows=0;
		if(title[1] != null){
			try {
			    //currentSheet.setRowView(0,20);
			      Label  label = new Label(0, currentSumRows, title[1], style.getTitleFormat());
			      sumSheet.addCell(label);
			      sumSheet.mergeCells(0, 0,  attributes[1].length-1,0);
			      currentSumRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<attributes[1].length;i++){
		      String content=(attributes[1][i]!=null?attributes[1][i].toString():"");
		      Label  label = new Label(i, currentSumRows, content, style.getAttributFormat());
		      try {
		    	  //currentSheet.setRowView(1,18);
		    	  sumSheet.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
			    }
		}
		
		currentSumRows++;
		rowsSumCount++;
		if(sheetName.length<3) return ;
		sumSheet3= workBook.createSheet(sheetName[2], workBook.getNumberOfSheets());    //费用明细sheet
		currentSumRows=0;
		if(title[2] != null){
			try {
			    //currentSheet.setRowView(0,20);
			      Label  label = new Label(0, currentSumRows, title[2], style.getTitleFormat());
			      sumSheet3.addCell(label);
			      sumSheet3.mergeCells(0, 0,  attributes[2].length-1,0);
			      currentSumRows++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<attributes[2].length;i++){
		      String content=(attributes[2][i]!=null?attributes[2][i].toString():"");
		      Label  label = new Label(i, currentSumRows, content, style.getAttributFormat());
		      try {
		    	
		    	  sumSheet3.addCell(label);
				} catch (Exception e) {
					e.printStackTrace();
			    }
		}
		
		currentSumThreeRows++;
		rowsSumThreeCount++;
	}
	/**
	 * 将汇总数据写入Excel
	 * @param list
	 * @param attr
	 */
	 public void  writeSumExcel(List list,String attr[])
	 {
		 for(int i=0;i<list.size();i++)
		 {
			 List rowList=(List)list.get(i);
			 this.addSumLine(rowList,attr);
		 }
	 }
	
	 /**
	  * 将数据写入Excel
	  * @param list
	  */
	public void writeExcel(List list){
		for(int r =0;r<list.size();r++){
			List rowList = (List) list.get(r);
			this.addLine(rowList);
		}
	 	this.close();
	}
	public void writeExcelForSheets(List<List> lists){
		for(int i=0;i<lists.size();i++){
			List list = lists.get(i);
			currentSheet = workBook.getSheet(i);
			currentRows =  currentSheet.getRows();
			for(int r =0;r<list.size();r++){
				List rowList = (List) list.get(r);
				this.addLine(rowList);
			}
		}
		this.close();
	}
	/**
	  * 将数据写入Excel
	  * @param list
	  */
	public void writeExcelThree(List list){
		for(int r =0;r<list.size();r++){
			List rowList = (List) list.get(r);
			this.addLine(rowList);
		}
		this.close();
	}
	/**
	 * 将汇总数据写入sheet3
	 * @param list
	 * @param attr
	 */
	 public void  writeSumExcelThree(List list,String attr[])
	 {
		 for(int i=0;i<list.size();i++)
		 {
			 List rowList=(List)list.get(i);
			 this.addSumLineThree(rowList,attr);
		 }
	 }
		/**
		 * 将数据写入sheet3
		 * @param data
		 */
	private synchronized void  addSumLineThree(List data,String  att[]) {
			
			WritableCellFormat dashedRight = style.getCentreStyle();
			for (int i = 0; i < att.length; i++) {
				String content = (data.get(i) != null ? data.get(i).toString() : "");
				Label label = new Label(i, currentSumThreeRows, content, dashedRight);
				try {
					sumSheet3.addCell(label);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
			rowsSumThreeCount++;
			currentSumThreeRows++;
		}
	
	/**
	 * 测试主函数
	 */
	public static void main(String[] args ) throws Exception{
		int cols = 10;
		String rowTitle = "DATA";
		String[] lines = new String[cols];
		for(int i=0;i<cols;i++){
			lines[i] = rowTitle + (i + 1);
		}
		File file=new File("d:/out.xls");
		if(file.exists())file.delete();
		file.createNewFile();
		FileOutputStream out=new FileOutputStream(file);
		List data=new ArrayList();
		List rowList = null;
		for(int i=0;i<500;i++){
			rowList = new ArrayList();
			for(int j=0;j<cols;j++){
				rowList.add(j,i+"_"+j+"_data");	
			}
			data.add(rowList);
		}
//		SimpleExcel exl=new SimpleExcel(out,lines);
		WriteCommExcel exl=new WriteCommExcel(out,"测试数据",lines);
		exl.writeExcel(data);	
		out.close();
		System.out.println("OK ");
	}
}