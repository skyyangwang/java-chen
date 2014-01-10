package test;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class AnalyzerResult {
	
	//查看分词；
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String str = "张三说的确实在理";
		sepatateCompare(str);
		
		System.out.println();  
		String dateString = "2012-02-05";
		sepatateCompare(dateString);
	}
	
	//分词结果对比
		public static void sepatateCompare(String str) throws IOException{
			StringReader reader1 = new StringReader(str);
			StringReader reader2 = new StringReader(str);
			StringReader reader3 = new StringReader(str);
			StringReader reader4 = new StringReader(str);
			System.out.println("'"+str+"'，2种分词结果：");
			luceneSepatate(reader1);
			luceneSepatateCn(reader2);
			
			ikDefaultSeparate(reader3);
			ikIntelligentSeparate(reader4);
		}
	
	private static void luceneSepatate(StringReader reader1) throws IOException{
		//lucene标准分词器
		Analyzer analyzer1 = new StandardAnalyzer(Version.LUCENE_41);
		
		//分词
		TokenStream ts1 = analyzer1.tokenStream("", reader1);		//标记流，进行分词
		ts1.reset(); //Add this line removes NullPointerException | 即，上面句中的""为空；
		 
		CharTermAttribute cTermAttribute1 = ts1.getAttribute(CharTermAttribute.class);		//保存Token对应的term文本
		//遍历分词数据
		try {
			System.out.println("StandardAnalyzerCn分词结果：");  
			while (ts1.incrementToken()) {				//增量标记
				System.out.print(cTermAttribute1.toString()+"|");  
			}
			System.out.println();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(reader1 != null){
				reader1.close();
			}
			if(analyzer1 != null){
				analyzer1.close();
			}
		}
	}
	
	private static void luceneSepatateCn(StringReader reader2) throws IOException{
		//lucene中文标准分词器
		Analyzer analyzer2 = new SmartChineseAnalyzer(Version.LUCENE_41);
		
		//分词
		TokenStream ts2 = analyzer2.tokenStream("", reader2);		//标记流，进行分词
		CharTermAttribute cTermAttribute2 = ts2.getAttribute(CharTermAttribute.class);		//保存Token对应的term文本
		//遍历分词数据
		try {
			System.out.println("SmartChineseAnalyzer分词结果：");  
			while (ts2.incrementToken()) {				//增量标记
				System.out.print(cTermAttribute2.toString()+"|");  
			}
			System.out.println();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(reader2 != null){
				reader2.close();
			}
			if(analyzer2 != null){
				analyzer2.close();
			}
		}
	}
	
	//IKAnalyzer默认最细粒度分词
		private static void ikDefaultSeparate(StringReader reader3) throws IOException{
			Analyzer analyzer2 = new IKAnalyzer();		//默认为最细化分词

			TokenStream ts2 = analyzer2.tokenStream("", reader3);		//标记流，进行分词
			CharTermAttribute cTermAttribute2 = ts2.getAttribute(CharTermAttribute.class);		//保存Token对应的term文本
			
			//遍历分词数据
			try {
				System.out.println("IKAnalyzer最细粒度分词结果：");  
				while (ts2.incrementToken()) {				//增量标记
					System.out.print(cTermAttribute2.toString()+"|");  
				}
				System.out.println();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(reader3 != null){
					reader3.close();
				}
				if(analyzer2 != null){
					analyzer2.close();
				}
			}
		}
		
		//IKAnalyzer智能分词
		private static void ikIntelligentSeparate(StringReader reader4) throws IOException{
			Analyzer analyzer3 = new IKAnalyzer(true);		//'true'参数为智能分词	
			TokenStream ts3 = analyzer3.tokenStream("", reader4);		//标记流，进行分词
			CharTermAttribute cTermAttribute3 = ts3.getAttribute(CharTermAttribute.class);		//保存Token对应的term文本
			
			//遍历分词数据
			try {
				System.out.println("IKAnalyzer智能分词结果：");  
				while (ts3.incrementToken()) {				//增量标记
					System.out.print(cTermAttribute3.toString()+"|");  
				}
				System.out.println();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(reader4 != null){
					reader4.close();
				}
				if(analyzer3 != null){
					analyzer3.close();
				}
			}
		}
	
}
