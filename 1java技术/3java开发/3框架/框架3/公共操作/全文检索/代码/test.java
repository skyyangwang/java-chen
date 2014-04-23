package test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import model.Bigtable1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.Version;
import org.apache.struts2.ServletActionContext;
import	org.apache.lucene.search.BooleanClause;

import service.GetBeanBase;
import service.commonality.HandleXml;
import service.commonality.PageStyle;
import service.commonality.HandleXml.DOM4JForXml;

import com.opensymphony.xwork2.ActionContext;

import dao.TestDao;

public class test {
	//�ִ���
    private static Analyzer analyzer;
    
    /** 
     * lucene �����ļ��е�ַ 
     */  
    public static final String LUCENE_INDEX_DIR  = "F://luceneTest"; 
    
    //�������Ŀ¼
    private static Directory indexDirectory;
    
    TestDao testDao = (TestDao) getGetBeanBase().GetBeanAc().getBean("TestDao");
    private String pram; // ���ݲ�ѯ������
    private String pram2; // ȫ������������
    private String pram3; //��¼��

	List<Bigtable1> Bigtable1List = null;
    
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
            
          //���������ã�����������ʽ����������or׷��������| Ĭ��CREATE_OR_APPEND
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
       
	//ȫ������
    public String create() throws InvalidTokenOffsetsException  {
    	long startDate = new Date().getTime();
    	Bigtable1List = testDao.getByParam(pram);

        try {  
            if (Bigtable1List != null) {  
                
            	//IndexWriter
            	IndexWriter indexWriter = null;
            	
            	analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
                indexDirectory = FSDirectory.open(getIndexFile());  
                IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_41, analyzer);  
                
                //���������ã�����������ʽ������������
                iwc.setOpenMode(OpenMode.CREATE);

                //���úϲ�����
                LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
                mergePolicy.setMergeFactor(100);
                iwc.setMergePolicy(mergePolicy);
                
                indexWriter = new IndexWriter(indexDirectory, iwc); 
            	
                //����
                for (Iterator<Bigtable1> iterator = Bigtable1List.iterator(); iterator  
                        .hasNext();) {  
                	Bigtable1 table1 = (Bigtable1) iterator.next();  
                    
                    //ת��Ϊdoc
                    Document doc = getDocumentByTable(table1);  

                    System.out.println("create " + getIndexFile());
                    indexWriter.addDocument(doc);
                }  
                indexWriter.close();  
            }  
        } catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
    	long endDate = new Date().getTime();
		System.out.println("����������ʱ��"+(endDate-startDate)+"���룡");
		return "SUCCESS";
    }
    
    //�޸�---׷�ӡ��޸ġ�ɾ��| �ݡ�����״̬��
    public String update() throws IOException {
    	long startDate = new Date().getTime();
    	pram = "and name like '����%'";
    	Bigtable1List = testDao.getByParam(pram);

        try {  
            if (Bigtable1List != null) {  
                IndexWriter indexWriter = getIndexWriter();  
                for (Iterator<Bigtable1> iterator = Bigtable1List.iterator(); iterator  
                        .hasNext();) {  
                	Bigtable1 table1 = (Bigtable1) iterator.next();  
                    
                    //ת��Ϊdoc
                    Document doc = getDocumentByTable(table1);  
                	
    				int type = 1;  //׷�ӣ�		addDocument
    				//int type = 2;  //�޸ģ�		updateDocument
    				//int type = 3;  //ɾ����		deleteDocuments
    				                 
                	if(type == 1){
                		System.out.println("adding " + getIndexFile());
        	            indexWriter.addDocument(doc);
                	}
                	if(type == 2){
                		System.out.println("updating " + getIndexFile());
        	            indexWriter.updateDocument(new Term("id", table1.getId().toString()), doc);
                	}
                	if(type == 3){
                		Term term = new Term("id","1903344");
                		System.out.println("deleting " + getIndexFile());
                		indexWriter.deleteDocuments(term);
                	}
                }  
                indexWriter.close();  
            }  
        } catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
    	long endDate = new Date().getTime();
		System.out.println("����������ʱ��"+(endDate-startDate)+"���룡");
		return "SUCCESS";
    }
    
    //���
    public String deleteAll() throws IOException {
    	IndexWriter indexWriter = getIndexWriter(); 
    	indexWriter.deleteAll();
    	indexWriter.close(); 
    	System.out.println("clean is run��");
    	return "SUCCESS";
    }
    
    
    
       
  //�����в�ѯ�����б����ݣ�      
    public String select() throws InvalidTokenOffsetsException  {
    	
    	//��ҳ��ʼ------------------------------------
    	HandleXml handleXml = new HandleXml();
		HandleXml.DOM4JForXml dom4j = handleXml.new DOM4JForXml();

		int pageSizeDefault = Integer.parseInt(dom4j
				.readXml("/WebsiteBackstage/xml/BasicSet.xml"));// ��xml�л�ȡ��ҳ��������ô�С��ȫ�����֡�

		int pageIndex = 1;// Ĭ�ϵ�1ҳ��
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request.getParameter("pageIndex") != null) {
			pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
		}

		String pagePram = ""; // ��ҳ��ѯ������
		String pram = ""; // ���ݲ�ѯ������
        
		//���￪ʼ���У�-----
        String queryKeyWord = pram2;
        int recordNums = 0;	//ֻ����ǰ100����¼
        if(pram3!=null){
        	recordNums = Integer.parseInt(pram3);
        }
        
        if (request.getParameter("queryKeyWord1") != null) {	 // 2�ο�ʼ�Ĳ�ѯ������ʹ������ˣ�
			String queryKeyWord2= null;
			try {
				queryKeyWord2 = new String(request.getParameter("queryKeyWord1").getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//������
			pagePram = "queryKeyWord1=" + queryKeyWord2 + "";
			
			queryKeyWord = queryKeyWord2;	//searchBigtable1Index()����������
			pram2 = queryKeyWord2;	//��������������
		}
        else{
        	pagePram = "queryKeyWord1=" + queryKeyWord + "";
			
			pram2 = queryKeyWord;	//��������������
        }
        if (request.getParameter("recordNums1") != null) {	 // 2�ο�ʼ�Ĳ�ѯ������ʹ������ˣ�
			String recordNums2 = request.getParameter("recordNums1");
			pagePram += "&recordNums1=" + recordNums2 + "";
			
			recordNums = Integer.parseInt(recordNums2);	//searchBigtable1Index()����������
			pram3 = String.valueOf(recordNums2);	//��������������
		}
        else{
        	pagePram += "&recordNums1=" + recordNums + "";
			
			pram3 = String.valueOf(recordNums);	//��������������
        }

		int recordcount = Integer.parseInt(pram3) ;
		//int recordcount = 100 ;
		int pageCount = recordcount % pageSizeDefault == 0 ? recordcount
				/ pageSizeDefault : recordcount / pageSizeDefault + 1;
        if (pageCount > 1) {
			pageParam = "�ܼ�¼��" + recordcount + "��ҳ�룺" + pageIndex + "/"
					+ pageCount + "��";
			pageStr = getPageStyle().pageList(pageCount, pageSizeDefault,
					pageIndex, pagePram);// ������ҳ��ʽ��
		}
        
      //��ѯ��ʼ��¼λ��
        int begin = pageSizeDefault * (pageIndex - 1) ;
        //��ѯ��ֹ��¼λ��
        int end = Math.min(begin + pageSizeDefault, recordcount);
		//��ҳ����----------------------------------------
		
        List<Bigtable1> list = null;
		try {
			list = searchBigtable1Index(queryKeyWord,recordNums,begin,end);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ActionContext.getContext().put("lists", list);
        
		return "SUCCESS";
    }
    
    public List<Bigtable1> searchBigtable1Index(String queryKeyWord,int recordNums,int begin,int end) throws IOException, ParseException, InvalidTokenOffsetsException{
        //�����Ĺؼ���
        
        analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
        indexDirectory = FSDirectory.open(getIndexFile());  
        
        //�ڵ���filed��������
        //QueryParser queryParser = new QueryParser(Version.LUCENE_41,"body",analyzer);
        //(�ڶ��Filed������)����---ֻ��һ��ʱ��дһ���ͺ��ˣ����Բ�ʹ�����浥�ֶε�д���ˣ�
        String[] fields = {"title","food","id"};
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_41,fields,analyzer);
        Query q1 = queryParser.parse(queryKeyWord);
        
        QueryParser parser = new QueryParser(Version.LUCENE_41, "name", analyzer);  
        Query q2 = parser.parse("aaa");  
          
        BooleanQuery boolQuery = new BooleanQuery();  //��������ѯ��
        boolQuery.add(q1, BooleanClause.Occur.MUST);  
        boolQuery.add(q2,BooleanClause.Occur.SHOULD); 
         
        //��ȡ���������Ľӿ�,��������
        IndexReader indexReader  = IndexReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        		// ���ֶ�����---Ĭ��false����true����
     			/*SortField sortField = new SortField("title",SortField.Type.STRING,true);
     			Sort sort = new Sort(sortField); */
     			// ���ֶ�����
     			/*SortField sortField2 = new SortField("title",SortField.Type.STRING,true);
     			SortField sortField3 = new SortField("food",SortField.Type.STRING,true);
     			SortField[] sortFieldArr = {sortField2,sortField3};	
     			Sort sort = new Sort(sortFieldArr); */
        		// docID����
				//Sort sort = new Sort(new SortField(null, SortField.Type.DOC, false)); 
     			
        //TopDocs �������صĽ��
        
        TopDocs Hits = indexSearcher.search(boolQuery, recordNums); //��ѯ�����
        List<Bigtable1> list = new ArrayList<Bigtable1>(); 
         
        int totalCount = Hits.totalHits; // �����Ľ��������
        float maxScore = Hits.getMaxScore(); //��ؽ�������÷���ߵġ�
        System.out.println("�������Ľ��������Ϊ��" + totalCount);
        System.out.println("�������Ľ����ߵ÷�Ϊ��" + maxScore);
         
        ScoreDoc[] scoreDocs = Hits.scoreDocs; // �����Ľ���б�
         
        //����������,ʹ�����Ĺؼ���ͻ����ʾ
        Formatter formatter = new SimpleHTMLFormatter("<font color=\"red\">","</font>");
        Scorer fragmentScore = new QueryScorer(boolQuery);
        Highlighter highlighter = new Highlighter(formatter,fragmentScore);
        Fragmenter fragmenter = new SimpleFragmenter(100);
        highlighter.setTextFragmenter(fragmenter);
        
        //���������ȡ�����뵽������
        /*for(ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;//��ǰ������ĵ����
            float score = scoreDoc.score;//��ǰ�������ضȵ÷�
            System.out.println("score is : "+score);
             
            Document document = indexSearcher.doc(docID);
            list.add(getTableByDocument(document,highlighter));
            
        }*/
        if(totalCount<end){end=totalCount;}
        for(int i=begin;i<end;i++) {
        	int docID = scoreDocs[i].doc;//��ǰ������ĵ����
        	float score = scoreDocs[i].score;//��ǰ�������ضȵ÷�
        	System.out.println("score is : "+score);
         
        	Document document = indexSearcher.doc(docID);
        	list.add(getTableByDocument(document,highlighter));
        }
        //�ر�
        //indexSearcher.close();
        indexReader.close();
        indexDirectory.close();
		return list;
        

    }
    
    
    //����·�� 
    private static File getIndexFile() throws IOException {  
        File indexFile = new File(LUCENE_INDEX_DIR );  
        if (!indexFile.exists()) {  
            indexFile.mkdir(); 
        }  
        return indexFile;  
    }  
    
    //�������Ŷ��󣬷���lucene�ĵ����� 
    private static Document getDocumentByTable(Bigtable1 table1) {  
        Document document = new Document();  
        // ID���ý�������  
        //Storeָ��Field�Ƿ���Ҫ�洢,Indexָ��Field�Ƿ���Ҫ�ִ�����
        document.add(new Field("id", table1.getId() + "", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));  
        document.add(new Field("title", table1.getTitle(), Field.Store.YES, Field.Index.ANALYZED));  
        document.add(new Field("food", table1.getFood() + "", Field.Store.YES, Field.Index.ANALYZED)); 
        document.add(new Field("name", table1.getName() + "", Field.Store.YES, Field.Index.ANALYZED)); 
      
        return document;  
    }  
    
    // ���������ĵ���ת��Ϊnews���� 
    private static Bigtable1 getTableByDocument(Document document,Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {  
    	Bigtable1 table1 = new Bigtable1();  
    	table1.setId(Long.parseLong(document.get("id")));  
    	//table1.setTitle(document.get("title"));  
    	
        //������ʾtitle
        String title =  document.get("title");
        String highlighterTitle = highlighter.getBestFragment(analyzer, "title", title);
         
        //���title��û���ҵ��ؼ���
        if(title == null) {
            highlighterTitle = title;
        }
        table1.setTitle(highlighterTitle);
        
        table1.setFood(document.get("food"));   
        table1.setName(document.get("name"));  
        return table1;  
    } 
    
    /// <summary>
    /// �����ؼ��� --��Ҫ��ʾ�����еĹؼ��ּ���ɫ,�ͺ���;
    /// </summary>
    /// <param name="keycontent"></param>
    /// <param name="k"></param>
    /// <returns></returns>
    public static String Highlightkeywords(String keycontent, String k)
    {
    	String resultstr = keycontent;
        if (k.trim().indexOf(" ") > 0)   //�ո�ָ�  �����пո�����Ķ���ؼ���;
        {
        	String[] myArray = k.split(" ");
            for (int i = 0; i < myArray.length; i++)
            {
                resultstr = resultstr.replace(myArray[i].toString(), "<font color=#FF0000>" + myArray[i].toString() + "</font>");  //�滻һ���ַ������е��ַ�;
            }
            return resultstr;
        }
        else
        {
            return resultstr.replace(k, "<font color=#FF0000>" + k + "</font>");
        }
    }
    
	public GetBeanBase getGetBeanBase() {
		return new GetBeanBase();
	}
    public String getPram() {
		return pram;
	}
	public void setPram(String pram) {
		this.pram = pram;
	}
	public String getPram2() {
		return pram2;
	}
	public void setPram2(String pram2) {
		this.pram2 = pram2;
	}
	public String getPram3() {
		return pram3;
	}
	public void setPram3(String pram3) {
		this.pram3 = pram3;
	}
    
	private String pageParam;

	public String getPageParam() {
		return pageParam;
	}

	public void setPageParam(String pageParam) {
		this.pageParam = pageParam;
	}

	private String pageStr;

	public String getPageStr() {
		return pageStr;
	}

	public void setPageStr(String pageStr) {
		this.pageStr = pageStr;
	}
	
	public PageStyle getPageStyle() {
		return new PageStyle();
	}
	
}
