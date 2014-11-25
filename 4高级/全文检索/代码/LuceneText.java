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
	 * lucene索引查询-文本文件 | 创建索引；
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
	
	//创建索引文件
		public void createTxtIndexFile() throws IOException{
			
			long startDate = new Date().getTime();
			
			//数据源文件位置
			File dataPath = new File("f://luceneData/luceneTxtData");

			//写索引器
			IndexWriter writer = getIndexWriter();  
			
			indexDocs(writer,dataPath);
			
			//关闭索引
			writer.close();
			long endDate = new Date().getTime();
			System.out.println("创建索引用时："+(endDate-startDate)+"毫秒！");
		}
		
		//遍历目录中的文件
		static void indexDocs(IndexWriter writer, File dataPath) throws IOException{
			//获得文本文件
			File[] textFiles = dataPath.listFiles();
			int textFilesLength = textFiles.length;
			//将文档写入写索引器
			if(textFilesLength>0){
				for(int i=0; i<textFilesLength; i++){
					//txt文件，
					if(textFiles[i].isFile() && textFiles[i].getName().endsWith(".txt")){
						//文本文件标准路径
						String file = textFiles[i].getCanonicalPath();
						System.out.println(file+"的内容：");
						//文本文件输入流
						String tempString = fileReaderString(file,"GBK");
						System.out.println(tempString);
						System.out.println("正在被索引...");
						
						//索引文档
						Document doc1 = new Document();
						//索引字段
						Field f1 = new Field("path",textFiles[i].getPath(),Field.Store.YES,Field.Index.NO);
						Field f2 = new Field("body",tempString,Field.Store.YES,Field.Index.ANALYZED);
						Field f3 = new Field("name",textFiles[i].getName(),Field.Store.YES,Field.Index.ANALYZED);
						doc1.add(f1);
						doc1.add(f2);
						doc1.add(f3);
						//将文当写入写索引器
						writer.addDocument(doc1);
					}
					//目录
					if (textFiles[i].isDirectory()) { 
						// 获取file目录下的所有文件(包括目录文件)File对象，放到数组files里
						String[] files = textFiles[i].list(); 
						if (files != null) {// 如果files!=null
							// 对files数组里面的File对象递归索引，通过广度遍历
						    indexDocs(writer, textFiles[i]);
						 }
					}	
				}
			}else{
				System.out.println("未找到文本文件！");
			}
		}
		
	    //创建IndexWriter索引器对象 
	    private static IndexWriter getIndexWriter() {  
	 
	        IndexWriter indexWriter = null;
	        boolean create = false;
	        try {  
	            // 分词器  
	            analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
	            indexDirectory = FSDirectory.open(getIndexFile());  
	            IndexWriterConfig iwc = new IndexWriterConfig(  
	                    Version.LUCENE_41, analyzer);  
	            
	          //索引器配置，索引创建方式：创建索引or追加索引；
	            if (create) {
	            	// Create a new index in the directory, removing any
	            	// previously indexed documents:
	            	iwc.setOpenMode(OpenMode.CREATE);
	            } else {
	            	// Add new documents to an existing index:
	            	iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	            }
	            
	          //设置合并因子
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
	
	//文档转换为输入流
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
		
	    //索引路径 
	    private static File getIndexFile() throws IOException {  
	        File indexFile = new File(LUCENE_INDEX_DIR );  
	        if (!indexFile.exists()) {  
	            indexFile.mkdir(); 
	        }  
	        return indexFile;  
	    } 
  	
}
