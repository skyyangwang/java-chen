package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class LuceneText {
	
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
	 * lucene������ѯ-�ı��ļ� | ����������
	 */
	public static void main(String[] args)   {
		// TODO Auto-generated method stub
		LuceneText luceneText = new LuceneText();
		try {
			luceneText.createTxtIndexFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}
	
	//���������ļ�
		public void createTxtIndexFile() throws IOException{
			
			long startDate = new Date().getTime();
			
			//����Դ�ļ�λ��
			File dataPath = new File("f://luceneData/luceneTxtData");

			//д������
			IndexWriter writer = getIndexWriter();  
			
			indexDocs(writer,dataPath);
			
			//�ر�����
			writer.close();
			long endDate = new Date().getTime();
			System.out.println("����������ʱ��"+(endDate-startDate)+"���룡");
		}
		
		//����Ŀ¼�е��ļ�
		static void indexDocs(IndexWriter writer, File dataPath) throws IOException{
			//����ı��ļ�
			File[] textFiles = dataPath.listFiles();
			int textFilesLength = textFiles.length;
			//���ĵ�д��д������
			if(textFilesLength>0){
				for(int i=0; i<textFilesLength; i++){
					//txt�ļ���
					if(textFiles[i].isFile() && textFiles[i].getName().endsWith(".txt")){
						//�ı��ļ���׼·��
						String file = textFiles[i].getCanonicalPath();
						System.out.println(file+"�����ݣ�");
						//�ı��ļ�������
						String tempString = fileReaderString(file,"GBK");
						System.out.println(tempString);
						System.out.println("���ڱ�����...");
						
						//�����ĵ�
						Document doc1 = new Document();
						//�����ֶ�
						Field f1 = new Field("path",textFiles[i].getPath(),Field.Store.YES,Field.Index.NO);
						Field f2 = new Field("body",tempString,Field.Store.YES,Field.Index.ANALYZED);
						Field f3 = new Field("name",textFiles[i].getName(),Field.Store.YES,Field.Index.ANALYZED);
						doc1.add(f1);
						doc1.add(f2);
						doc1.add(f3);
						//���ĵ�д��д������
						writer.addDocument(doc1);
					}
					//Ŀ¼
					if (textFiles[i].isDirectory()) { 
						// ��ȡfileĿ¼�µ������ļ�(����Ŀ¼�ļ�)File���󣬷ŵ�����files��
						String[] files = textFiles[i].list(); 
						if (files != null) {// ���files!=null
							// ��files���������File����ݹ�������ͨ����ȱ���
						    indexDocs(writer, textFiles[i]);
						 }
					}	
				}
			}else{
				System.out.println("δ�ҵ��ı��ļ���");
			}
		}
		
	    //����IndexWriter���������� 
	    private static IndexWriter getIndexWriter() {  
	 
	        IndexWriter indexWriter = null;
	        boolean create = false;
	        try {  
	            // �ִ���  
	            analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
	            indexDirectory = FSDirectory.open(getIndexFile());  
	            IndexWriterConfig iwc = new IndexWriterConfig(  
	                    Version.LUCENE_41, analyzer);  
	            
	          //���������ã�����������ʽ����������or׷��������
	            if (create) {
	            	// Create a new index in the directory, removing any
	            	// previously indexed documents:
	            	iwc.setOpenMode(OpenMode.CREATE);
	            } else {
	            	// Add new documents to an existing index:
	            	iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	            }
	            
	          //���úϲ�����
	            LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
	            mergePolicy.setMergeFactor(100);
	            iwc.setMergePolicy(mergePolicy);
	            
	            indexWriter = new IndexWriter(indexDirectory, iwc); 
	            
	        } catch (CorruptIndexException e) {  
	            e.printStackTrace();  
	        } catch (LockObtainFailedException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return indexWriter;  
	    } 
	
	//�ĵ�ת��Ϊ������
		private static String fileReaderString(String file, String charset) throws IOException{
			InputStream iStream = new FileInputStream(file);
			InputStreamReader iStreamReader = new InputStreamReader(iStream,charset);
			
			BufferedReader reader = new BufferedReader(iStreamReader);
			String line = new String();
			StringBuffer tempString  = new StringBuffer();
			
			while((line = reader.readLine()) != null){
				tempString.append(line);
			}
			return tempString.toString();
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
