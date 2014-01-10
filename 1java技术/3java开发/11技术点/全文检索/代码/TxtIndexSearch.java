package test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @param args
 */
public class TxtIndexSearch {
	
	//分词器
    private static Analyzer analyzer;
    
    //索引存放目录
    private static Directory indexDirectory;
    
    /** 
     * lucene 索引文件夹地址 
     */  
    public static final String LUCENE_INDEX_DIR  = "f://luceneData/luceneTxtIndex"; 
    
	/**
	 * @chen peng
	 * 2013.7.15
	 * lucene索引查询-文本文件| 索引查询；
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TxtIndexSearch txtIndexSearch = new TxtIndexSearch();
		try {
			txtIndexSearch.txtSearch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//lucene搜索
		public void txtSearch() throws IOException{
			
			long startDate = new Date().getTime();
			
			//索引文件位置
			File indexPath = new File("f://luceneData/luceneTxtIndex");
			
			analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
		    indexDirectory = FSDirectory.open(getIndexFile());  
			
			//查询解析器
			QueryParser queryParser = new QueryParser(Version.LUCENE_36,"body",analyzer);
			
			//查询
			Query query = null;
			try {
				query = queryParser.parse("苹果");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//获取访问索引的接口,进行搜索
	        IndexReader indexReader  = IndexReader.open(indexDirectory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        
			//执行查询
			TopDocs results = indexSearcher.search(query, null, 10);
			//获得文档，获得数据
			ScoreDoc[] hits = results.scoreDocs;
			Document document = null;
			if(hits.length > 0){
				System.out.println("找到相关记录"+hits.length+"个,它位于：");
				for(int i=0; i<hits.length; i++){
					document = indexSearcher.doc(hits[i].doc);
					String path = document.get("path");
					System.out.println("'"+path+"'");
				}
			}else{
				System.out.println("未找到相关记录！");
			}
			long endDate = new Date().getTime();
			System.out.println("创建索引用时："+(endDate-startDate)+"毫秒！");
		}
		
	    //索引路径 
	    private static File getIndexFile() throws IOException {  
	        File indexFile = new File(LUCENE_INDEX_DIR );  
	        if (!indexFile.exists()) {  
	            indexFile.mkdir(); 
	        }  
	        return indexFile;  
	    } 
}
