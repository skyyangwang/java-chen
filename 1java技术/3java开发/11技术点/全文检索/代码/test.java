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
	//分词器
    private static Analyzer analyzer;
    
    /** 
     * lucene 索引文件夹地址 
     */  
    public static final String LUCENE_INDEX_DIR  = "F://luceneTest"; 
    
    //索引存放目录
    private static Directory indexDirectory;
    
    TestDao testDao = (TestDao) getGetBeanBase().GetBeanAc().getBean("TestDao");
    private String pram; // 数据查询参数；
    private String pram2; // 全文搜索参数；
    private String pram3; //记录数

	List<Bigtable1> Bigtable1List = null;
    
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
            
          //索引器配置，索引创建方式：创建索引or追加索引；| 默认CREATE_OR_APPEND
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
       
	//全部创建
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
                
                //索引器配置，索引创建方式：创建索引；
                iwc.setOpenMode(OpenMode.CREATE);

                //设置合并因子
                LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
                mergePolicy.setMergeFactor(100);
                iwc.setMergePolicy(mergePolicy);
                
                indexWriter = new IndexWriter(indexDirectory, iwc); 
            	
                //数据
                for (Iterator<Bigtable1> iterator = Bigtable1List.iterator(); iterator  
                        .hasNext();) {  
                	Bigtable1 table1 = (Bigtable1) iterator.next();  
                    
                    //转换为doc
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
		System.out.println("创建索引用时："+(endDate-startDate)+"毫秒！");
		return "SUCCESS";
    }
    
    //修改---追加、修改、删除| 据“索引状态表”
    public String update() throws IOException {
    	long startDate = new Date().getTime();
    	pram = "and name like '陈龙%'";
    	Bigtable1List = testDao.getByParam(pram);

        try {  
            if (Bigtable1List != null) {  
                IndexWriter indexWriter = getIndexWriter();  
                for (Iterator<Bigtable1> iterator = Bigtable1List.iterator(); iterator  
                        .hasNext();) {  
                	Bigtable1 table1 = (Bigtable1) iterator.next();  
                    
                    //转换为doc
                    Document doc = getDocumentByTable(table1);  
                	
    				int type = 1;  //追加；		addDocument
    				//int type = 2;  //修改；		updateDocument
    				//int type = 3;  //删除；		deleteDocuments
    				                 
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
		System.out.println("创建索引用时："+(endDate-startDate)+"毫秒！");
		return "SUCCESS";
    }
    
    //清空
    public String deleteAll() throws IOException {
    	IndexWriter indexWriter = getIndexWriter(); 
    	indexWriter.deleteAll();
    	indexWriter.close(); 
    	System.out.println("clean is run！");
    	return "SUCCESS";
    }
    
    
    
       
  //索引中查询新闻列表内容；      
    public String select() throws InvalidTokenOffsetsException  {
    	
    	//分页开始------------------------------------
    	HandleXml handleXml = new HandleXml();
		HandleXml.DOM4JForXml dom4j = handleXml.new DOM4JForXml();

		int pageSizeDefault = Integer.parseInt(dom4j
				.readXml("/WebsiteBackstage/xml/BasicSet.xml"));// 从xml中获取，页面可以设置大小，全局数字。

		int pageIndex = 1;// 默认第1页；
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request.getParameter("pageIndex") != null) {
			pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
		}

		String pagePram = ""; // 分页查询参数；
		String pram = ""; // 数据查询参数；
        
		//这里开始特有：-----
        String queryKeyWord = pram2;
        int recordNums = 0;	//只返回前100条记录
        if(pram3!=null){
        	recordNums = Integer.parseInt(pram3);
        }
        
        if (request.getParameter("queryKeyWord1") != null) {	 // 2次开始的查询条件都使用这个了；
			String queryKeyWord2= null;
			try {
				queryKeyWord2 = new String(request.getParameter("queryKeyWord1").getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//乱码解决
			pagePram = "queryKeyWord1=" + queryKeyWord2 + "";
			
			queryKeyWord = queryKeyWord2;	//searchBigtable1Index()方法参数；
			pram2 = queryKeyWord2;	//保存搜索条件；
		}
        else{
        	pagePram = "queryKeyWord1=" + queryKeyWord + "";
			
			pram2 = queryKeyWord;	//保存搜索条件；
        }
        if (request.getParameter("recordNums1") != null) {	 // 2次开始的查询条件都使用这个了；
			String recordNums2 = request.getParameter("recordNums1");
			pagePram += "&recordNums1=" + recordNums2 + "";
			
			recordNums = Integer.parseInt(recordNums2);	//searchBigtable1Index()方法参数；
			pram3 = String.valueOf(recordNums2);	//保存搜索条件；
		}
        else{
        	pagePram += "&recordNums1=" + recordNums + "";
			
			pram3 = String.valueOf(recordNums);	//保存搜索条件；
        }

		int recordcount = Integer.parseInt(pram3) ;
		//int recordcount = 100 ;
		int pageCount = recordcount % pageSizeDefault == 0 ? recordcount
				/ pageSizeDefault : recordcount / pageSizeDefault + 1;
        if (pageCount > 1) {
			pageParam = "总记录：" + recordcount + "，页码：" + pageIndex + "/"
					+ pageCount + "；";
			pageStr = getPageStyle().pageList(pageCount, pageSizeDefault,
					pageIndex, pagePram);// 公共分页样式；
		}
        
      //查询起始记录位置
        int begin = pageSizeDefault * (pageIndex - 1) ;
        //查询终止记录位置
        int end = Math.min(begin + pageSizeDefault, recordcount);
		//分页结束----------------------------------------
		
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
        //搜索的关键词
        
        analyzer  = new SmartChineseAnalyzer(Version.LUCENE_41);
        indexDirectory = FSDirectory.open(getIndexFile());  
        
        //在单个filed中搜索；
        //QueryParser queryParser = new QueryParser(Version.LUCENE_41,"body",analyzer);
        //(在多个Filed中搜索)　　---只有一个时，写一个就好了；所以不使用上面单字段的写法了；
        String[] fields = {"title","food","id"};
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_41,fields,analyzer);
        Query q1 = queryParser.parse(queryKeyWord);
        
        QueryParser parser = new QueryParser(Version.LUCENE_41, "name", analyzer);  
        Query q2 = parser.parse("aaa");  
          
        BooleanQuery boolQuery = new BooleanQuery();  //多条件查询；
        boolQuery.add(q1, BooleanClause.Occur.MUST);  
        boolQuery.add(q2,BooleanClause.Occur.SHOULD); 
         
        //获取访问索引的接口,进行搜索
        IndexReader indexReader  = IndexReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        		// 单字段排序---默认false升序，true降序
     			/*SortField sortField = new SortField("title",SortField.Type.STRING,true);
     			Sort sort = new Sort(sortField); */
     			// 多字段排序
     			/*SortField sortField2 = new SortField("title",SortField.Type.STRING,true);
     			SortField sortField3 = new SortField("food",SortField.Type.STRING,true);
     			SortField[] sortFieldArr = {sortField2,sortField3};	
     			Sort sort = new Sort(sortFieldArr); */
        		// docID排序
				//Sort sort = new Sort(new SortField(null, SortField.Type.DOC, false)); 
     			
        //TopDocs 搜索返回的结果
        
        TopDocs Hits = indexSearcher.search(boolQuery, recordNums); //查询结果；
        List<Bigtable1> list = new ArrayList<Bigtable1>(); 
         
        int totalCount = Hits.totalHits; // 搜索的结果总数量
        float maxScore = Hits.getMaxScore(); //相关结果数，得分最高的。
        System.out.println("搜索到的结果总数量为：" + totalCount);
        System.out.println("搜索到的结果最高得分为：" + maxScore);
         
        ScoreDoc[] scoreDocs = Hits.scoreDocs; // 搜索的结果列表
         
        //创建高亮器,使搜索的关键词突出显示
        Formatter formatter = new SimpleHTMLFormatter("<font color=\"red\">","</font>");
        Scorer fragmentScore = new QueryScorer(boolQuery);
        Highlighter highlighter = new Highlighter(formatter,fragmentScore);
        Fragmenter fragmenter = new SimpleFragmenter(100);
        highlighter.setTextFragmenter(fragmenter);
        
        //把搜索结果取出放入到集合中
        /*for(ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;//当前结果的文档编号
            float score = scoreDoc.score;//当前结果的相关度得分
            System.out.println("score is : "+score);
             
            Document document = indexSearcher.doc(docID);
            list.add(getTableByDocument(document,highlighter));
            
        }*/
        if(totalCount<end){end=totalCount;}
        for(int i=begin;i<end;i++) {
        	int docID = scoreDocs[i].doc;//当前结果的文档编号
        	float score = scoreDocs[i].score;//当前结果的相关度得分
        	System.out.println("score is : "+score);
         
        	Document document = indexSearcher.doc(docID);
        	list.add(getTableByDocument(document,highlighter));
        }
        //关闭
        //indexSearcher.close();
        indexReader.close();
        indexDirectory.close();
		return list;
        

    }
    
    
    //索引路径 
    private static File getIndexFile() throws IOException {  
        File indexFile = new File(LUCENE_INDEX_DIR );  
        if (!indexFile.exists()) {  
            indexFile.mkdir(); 
        }  
        return indexFile;  
    }  
    
    //根据新闻对象，返回lucene文档对象 
    private static Document getDocumentByTable(Bigtable1 table1) {  
        Document document = new Document();  
        // ID不用建立索引  
        //Store指定Field是否需要存储,Index指定Field是否需要分词索引
        document.add(new Field("id", table1.getId() + "", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));  
        document.add(new Field("title", table1.getTitle(), Field.Store.YES, Field.Index.ANALYZED));  
        document.add(new Field("food", table1.getFood() + "", Field.Store.YES, Field.Index.ANALYZED)); 
        document.add(new Field("name", table1.getName() + "", Field.Store.YES, Field.Index.ANALYZED)); 
      
        return document;  
    }  
    
    // 根据索引文档，转换为news对象 
    private static Bigtable1 getTableByDocument(Document document,Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {  
    	Bigtable1 table1 = new Bigtable1();  
    	table1.setId(Long.parseLong(document.get("id")));  
    	//table1.setTitle(document.get("title"));  
    	
        //高亮显示title
        String title =  document.get("title");
        String highlighterTitle = highlighter.getBestFragment(analyzer, "title", title);
         
        //如果title中没有找到关键词
        if(title == null) {
            highlighterTitle = title;
        }
        table1.setTitle(highlighterTitle);
        
        table1.setFood(document.get("food"));   
        table1.setName(document.get("name"));  
        return table1;  
    } 
    
    /// <summary>
    /// 高亮关键字 --给要显示内容中的关键字加颜色,就好了;
    /// </summary>
    /// <param name="keycontent"></param>
    /// <param name="k"></param>
    /// <returns></returns>
    public static String Highlightkeywords(String keycontent, String k)
    {
    	String resultstr = keycontent;
        if (k.trim().indexOf(" ") > 0)   //空格分隔  可以有空格相隔的多个关键字;
        {
        	String[] myArray = k.split(" ");
            for (int i = 0; i < myArray.length; i++)
            {
                resultstr = resultstr.replace(myArray[i].toString(), "<font color=#FF0000>" + myArray[i].toString() + "</font>");  //替换一个字符串中有的字符;
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
