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
	
	//�鿴�ִʣ�
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String str = "����˵��ȷʵ����";
		sepatateCompare(str);
		
		System.out.println();  
		String dateString = "2012-02-05";
		sepatateCompare(dateString);
	}
	
	//�ִʽ���Ա�
		public static void sepatateCompare(String str) throws IOException{
			StringReader reader1 = new StringReader(str);
			StringReader reader2 = new StringReader(str);
			StringReader reader3 = new StringReader(str);
			StringReader reader4 = new StringReader(str);
			System.out.println("'"+str+"'��2�ִַʽ����");
			luceneSepatate(reader1);
			luceneSepatateCn(reader2);
			
			ikDefaultSeparate(reader3);
			ikIntelligentSeparate(reader4);
		}
	
	private static void luceneSepatate(StringReader reader1) throws IOException{
		//lucene��׼�ִ���
		Analyzer analyzer1 = new StandardAnalyzer(Version.LUCENE_41);
		
		//�ִ�
		TokenStream ts1 = analyzer1.tokenStream("", reader1);		//����������зִ�
		ts1.reset(); //Add this line removes NullPointerException | ����������е�""Ϊ�գ�
		 
		CharTermAttribute cTermAttribute1 = ts1.getAttribute(CharTermAttribute.class);		//����Token��Ӧ��term�ı�
		//�����ִ�����
		try {
			System.out.println("StandardAnalyzerCn�ִʽ����");  
			while (ts1.incrementToken()) {				//�������
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
		//lucene���ı�׼�ִ���
		Analyzer analyzer2 = new SmartChineseAnalyzer(Version.LUCENE_41);
		
		//�ִ�
		TokenStream ts2 = analyzer2.tokenStream("", reader2);		//����������зִ�
		CharTermAttribute cTermAttribute2 = ts2.getAttribute(CharTermAttribute.class);		//����Token��Ӧ��term�ı�
		//�����ִ�����
		try {
			System.out.println("SmartChineseAnalyzer�ִʽ����");  
			while (ts2.incrementToken()) {				//�������
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
	
	//IKAnalyzerĬ����ϸ���ȷִ�
		private static void ikDefaultSeparate(StringReader reader3) throws IOException{
			Analyzer analyzer2 = new IKAnalyzer();		//Ĭ��Ϊ��ϸ���ִ�

			TokenStream ts2 = analyzer2.tokenStream("", reader3);		//����������зִ�
			CharTermAttribute cTermAttribute2 = ts2.getAttribute(CharTermAttribute.class);		//����Token��Ӧ��term�ı�
			
			//�����ִ�����
			try {
				System.out.println("IKAnalyzer��ϸ���ȷִʽ����");  
				while (ts2.incrementToken()) {				//�������
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
		
		//IKAnalyzer���ִܷ�
		private static void ikIntelligentSeparate(StringReader reader4) throws IOException{
			Analyzer analyzer3 = new IKAnalyzer(true);		//'true'����Ϊ���ִܷ�	
			TokenStream ts3 = analyzer3.tokenStream("", reader4);		//����������зִ�
			CharTermAttribute cTermAttribute3 = ts3.getAttribute(CharTermAttribute.class);		//����Token��Ӧ��term�ı�
			
			//�����ִ�����
			try {
				System.out.println("IKAnalyzer���ִܷʽ����");  
				while (ts3.incrementToken()) {				//�������
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
