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
	
	//�ִ���
    private static Analyzer analyzer;
    
    //�������Ŀ¼
    private static Directory indexDirectory;
    
    /** 
     * lucene �����ļ��е�ַ 
     */  
    public static final String LUCENE_INDEX_DIR  = "f://luceneData/luceneTxtIndex"; 
    
	/**
	 * @chen peng
	 * 2013.7.15
	 * lucene������ѯ-�ı��ļ�| ������ѯ��
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
	
	//lucene����
		public void txtSearch() throws IOException{
			
			long startDate = new Date().getTime();
			
			//�����ļ�λ��
			File indexPath = new File("f://luceneData/luceneTxtIndex");
			
			analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
		    indexDirectory = FSDirectory.open(getIndexFile());  
			
			//��ѯ������
			QueryParser queryParser = new QueryParser(Version.LUCENE_36,"body",analyzer);
			
			//��ѯ
			Query query = null;
			try {
				query = queryParser.parse("ƻ��");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//��ȡ���������Ľӿ�,��������
	        IndexReader indexReader  = IndexReader.open(indexDirectory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        
			//ִ�в�ѯ
			TopDocs results = indexSearcher.search(query, null, 10);
			//����ĵ����������
			ScoreDoc[] hits = results.scoreDocs;
			Document document = null;
			if(hits.length > 0){
				System.out.println("�ҵ���ؼ�¼"+hits.length+"��,��λ�ڣ�");
				for(int i=0; i<hits.length; i++){
					document = indexSearcher.doc(hits[i].doc);
					String path = document.get("path");
					System.out.println("'"+path+"'");
				}
			}else{
				System.out.println("δ�ҵ���ؼ�¼��");
			}
			long endDate = new Date().getTime();
			System.out.println("����������ʱ��"+(endDate-startDate)+"���룡");
		}
		
	    //����·�� 
	    private static File getIndexFile() throws IOException {  
	        File indexFile = new File(LUCENE_INDEX_DIR );  
	        if (!indexFile.exists()) {  
	            indexFile.mkdir(); 
	        }  
	        return indexFile;  
	    } 
}
