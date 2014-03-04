package socketUse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import util.CollectionHashMap;
import net.sf.json.JSONObject;

public class SSocketHandler implements Runnable {
	private Socket client;
	private int method;
	
	private String uploadPath="e:/uploadFile/";
	private String downloadPath="e:/downloadFile/";

	private Map<Long, FileLog> datas = new HashMap<Long, FileLog>();// 存放断点数据，最好改为数据库存放

	private static boolean start=true;
	
	//构造方法
	public SSocketHandler(Socket s) throws IOException {
		client = s;
        System.out.println("Client(" + client.getRemoteSocketAddress() + ") come in...");
        
        //此方式取值后，其中的头文件受影响；---后面再取不到头，所以，传递object；
        //解法1   | 中文乱码；
        /*PushbackInputStream in = new PushbackInputStream(
				client.getInputStream());
        String json = StreamTool.readLine(in);
        JSONObject object = JSONObject.fromObject(json);*/
        //解法2
        InputStream inputStream	= client.getInputStream();
        byte[] b = new byte[1024];
        inputStream.read(b, 0, b.length);
        JSONObject object = JSONObject.fromObject(new String(b));
		
		method = Integer.parseInt(object.getJSONObject("head").getString("method"));
		
		switch (method) {
		//盒子连接保持
		case 10042:
			ConnectionsRemain(object);
			break;
		//上传；手机-上传文件
		case 10041:
			upload(object);
			break;
		//上传；盒子返回结果-json串
		case 100410:
			uploadBack(object);
			break;
		//下载；手机请求--json串
			case 10043:
			download(object);
			break;
			//下载；-盒子返回文件
			case 100430:
			downloadBack(object);
			break;
		}

        //new Thread(this).start();
	}

	//下载---手机请求处理
	private void download(JSONObject object) throws IOException {
		// TODO 自动生成的方法存根
		String mobile1 = object.getJSONObject("head").getString("mobileid");
		
    	//保存Socket-手机
		CollectionHashMap.socMap.remove(mobile1);
		CollectionHashMap.socMap.put(mobile1, client);
		
		String jsonStr = object.toString()+"\r\n";
		
		System.out.println("手机下载请求信息"+jsonStr);
		
		//转发请求给盒子
		downloadTwo(object);
	}
	
	private void downloadTwo(JSONObject object) {
		
		System.out.println("服务器转发下载请求到盒子");
		
		try {  
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = " "+jsonString+"\r\n";  
            String boxid1 = object.getJSONObject("user").getString("boxid");
            
            //找出手机socket
            Socket socket = CollectionHashMap.socMap.get(boxid1);
            BufferedOutputStream  bos = new BufferedOutputStream (socket.getOutputStream());
	        bos.write(head.getBytes("UTF-8"));
	        bos.flush();
	        bos.close();    

        } catch (Exception e) {  
            e.printStackTrace();  
        } 
		
	}

	//保存盒子传回的下载文件；
	private void downloadBack(JSONObject object) {
		// TODO 自动生成的方法存根
		//服务器保存；
				try {
					System.out.println("盒子回传下载文件 "
							+ client.getInetAddress() + " @ " + client.getPort());
					PushbackInputStream inStream = new PushbackInputStream(
							client.getInputStream());
					// 得到客户端发来的第一行协议数据：Content-Length=143253434;filename=xxx.3gp;sourceid=
					// 如果用户初次上传文件，sourceid的值为空。
					//String json = StreamTool.readLine(inStream);
					//System.out.println(json);
					if (object != null) {
						// 下面从协议数据中读取各种参数值
						/*String[] items = json.split(";");
						String filelength = items[0].substring(items[0].indexOf("=") + 1);
						String filename = items[1].substring(items[1].indexOf("=") + 1);
						String sourceid = items[2].substring(items[2].indexOf("=") + 1);*/
						
						//JSONObject object = JSONObject.fromObject(new String(json));
						String filelength = object.getString("size");
						String filename = object.getString("filename");
						String sourceid = object.getString("sourceid");
						
						Long id = System.currentTimeMillis();
						FileLog log = null;
						if (null != sourceid && !"".equals(sourceid)) {
							id = Long.valueOf(sourceid);
							log = find(id);//查找上传的文件是否存在上传记录
						}
						File file = null;
						int position = 0;
						if(log==null){//如果上传的文件不存在上传记录,为文件添加跟踪记录
							//String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
							File dir = new File(downloadPath);
							if(!dir.exists()) dir.mkdirs();
							file = new File(dir, filename);
							if(file.exists()){//如果上传的文件发生重名，然后进行改名
								filename = filename.substring(0, filename.indexOf(".")-1)+ dir.listFiles().length+ filename.substring(filename.indexOf("."));
								file = new File(dir, filename);
							}
							save(id, file);
						}else{// 如果上传的文件存在上传记录,读取上次的断点位置
							file = new File(log.getPath());//从上传记录中得到文件的路径
							if(file.exists()){
								File logFile = new File(file.getParentFile(), file.getName()+".log");
								if(logFile.exists()){
									Properties properties = new Properties();
									properties.load(new FileInputStream(logFile));
									position = Integer.valueOf(properties.getProperty("length"));//读取断点位置
								}
							}
						}
						
						OutputStream outStream = client.getOutputStream();
						String response = "sourceid="+ id+ ";position="+ position+ "\r\n";
						//服务器收到客户端的请求信息后，给客户端返回响应信息：sourceid=1274773833264;position=0
						//sourceid由服务生成，唯一标识上传的文件，position指示客户端从文件的什么位置开始上传
						outStream.write(response.getBytes("utf-8"));
						
						RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
						if(position==0) fileOutStream.setLength(Integer.valueOf(filelength));//设置文件长度
						fileOutStream.seek(position);//移动文件指定的位置开始写入数据
						byte[] buffer = new byte[1024*100];
						int len = -1;
						int length = position;
						while( (len=inStream.read(buffer)) != -1){//从输入流中读取数据写入到文件中
							fileOutStream.write(buffer, 0, len);
							length += len;
							Properties properties = new Properties();
							properties.put("length", String.valueOf(length));
							FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName()+".log"));
							properties.store(logFile, null);//实时记录文件的最后保存位置
							logFile.close();
						}
						if(length==fileOutStream.length()) {
							delete(id);
						}
						fileOutStream.close();					
						inStream.close();
						outStream.close();
						file = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
		                if(client != null && !client.isClosed()) client.close();
		            } catch (IOException e) {}
				}
				
				//转发下载文件给手机
				downloadBackTwo(object);
	}

	private void downloadBackTwo(JSONObject object) {
		// TODO 自动生成的方法存根
		System.out.println("服务器转发下载文件给手机");
		/*try {  	
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            //找到socket-手机id
            String mobile1 = object.getJSONObject("head").getString("mobileid");
            Socket socket = CollectionHashMap.socMap.get(mobile1);
            
            OutputStream outStream = socket.getOutputStream();  
            //outStream.write(head.getBytes("utf-8"));  
                      		
    		String filename = object.getString("filename");
    		File uploadFile2 = new File(downloadPath,filename);
    		
            RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile2, "r");  
            fileOutStream.seek(Integer.valueOf(0));  
            byte[] buffer = new byte[1024];  
            int len = -1;   
            while(start&&(len = fileOutStream.read(buffer)) != -1){  
                outStream.write(buffer, 0, len);   
            }  
            fileOutStream.close();  
            outStream.close();  
            socket.close();
 
        } catch (Exception e) {  
            e.printStackTrace();  
        }*/ 
		
		try {
				String mobile1 = object.getJSONObject("head").getString("mobileid");
		        Socket socket = CollectionHashMap.socMap.get(mobile1);
		            
		        String filename = object.getString("filename");
		        File uploadFile2 = new File(downloadPath,filename);
		            
				DataInputStream fis = new DataInputStream(
						new BufferedInputStream(new FileInputStream(uploadFile2)));
				DataOutputStream ps = new DataOutputStream(socket.getOutputStream());
				// 将文件名及长度传给客户端。这里要真正适用所有平台，例如中文名的处理，还需要加工，具体可以参见Think In Java
				// 4th里有现成的代码。
				ps.writeUTF(filename);
				ps.flush();
				ps.writeLong((long) uploadFile2.length());
				ps.flush();

				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];

				while (true) {
					int read = 0;
					if (fis != null) {
						read = fis.read(buf);
						// 从包含的输入流中读取一定数量的字节，并将它们存储到缓冲区数组 b
						// 中。以整数形式返回实际读取的字节数。在输入数据可用、检测到文件末尾 (end of file)
						// 或抛出异常之前，此方法将一直阻塞。
					}

					if (read == -1) {
						break;
					}
					ps.write(buf, 0, read);
				}
				ps.flush();
				// 注意关闭socket链接哦，不然客户端会等待server的数据过来，
				// 直到socket超时，导致数据不完整。
				fis.close();
				socket.close();  
				System.out.println("下载文件传输完成");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		
	}
	
	public void ConnectionsRemain(JSONObject object) throws IOException{
		//直接取流-普通写法；
		/*InputStream in = client.getInputStream();
		byte[] c = new byte[1024];
		in.read(c, 0, c.length);
		
        System.out.println("客户端发送内容："+new String(c));
        
        JSONObject object = JSONObject.fromObject(new String(c));*/
        
    	String boxid1 = object.getJSONObject("head").getString("boxid");
    	
    	//保存Socket-盒子
		CollectionHashMap.socMap.remove(boxid1);
		CollectionHashMap.socMap.put(boxid1, client);

		System.out.println(object.toString());
		System.out.println("盒子id："+boxid1);
		
	}
	
	public void upload(JSONObject object) throws IOException{
		System.out.println("手机上传内容到服务器");
		//354117215727936
		//再从手机头信息中拆出盒子id；
		//Socket s2 = CollectionHashMap.socMap.get("354117215727936");
		
		String mobile1 = object.getJSONObject("head").getString("mobileid");

    	//保存Socket-手机
		CollectionHashMap.socMap.remove(mobile1);
		CollectionHashMap.socMap.put(mobile1, client);
		
		System.out.println(object.toString());
		
		//直接转发流；---有问题，待议；
		/*PushbackInputStream in = new PushbackInputStream(
				client.getInputStream());
		try {  
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            OutputStream outStream = s2.getOutputStream();  
            outStream.write(head.getBytes());  
            
            byte[] buffer = new byte[1024];  
            int len = -1;  
            while((len = in.read(buffer)) != -1){  
                outStream.write(buffer, 0, len);   
            }  
            outStream.close();  
            in.close();  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }*/
		
		//服务器保存；
		try {
			System.out.println("accepted connenction from "
					+ client.getInetAddress() + " @ " + client.getPort());
			PushbackInputStream inStream = new PushbackInputStream(
					client.getInputStream());
			// 得到客户端发来的第一行协议数据：Content-Length=143253434;filename=xxx.3gp;sourceid=
			// 如果用户初次上传文件，sourceid的值为空。
			//String json = StreamTool.readLine(inStream);
			//System.out.println(json);
			if (object != null) {
				// 下面从协议数据中读取各种参数值
				/*String[] items = json.split(";");
				String filelength = items[0].substring(items[0].indexOf("=") + 1);
				String filename = items[1].substring(items[1].indexOf("=") + 1);
				String sourceid = items[2].substring(items[2].indexOf("=") + 1);*/
				
				//JSONObject object = JSONObject.fromObject(new String(json));
				String filelength = object.getString("size");
				String filename = object.getString("filename");
				String sourceid = object.getString("sourceid");
				
				Long id = System.currentTimeMillis();
				FileLog log = null;
				if (null != sourceid && !"".equals(sourceid)) {
					id = Long.valueOf(sourceid);
					log = find(id);//查找上传的文件是否存在上传记录
				}
				File file = null;
				int position = 0;
				if(log==null){//如果上传的文件不存在上传记录,为文件添加跟踪记录
					//String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
					File dir = new File(uploadPath);
					if(!dir.exists()) dir.mkdirs();
					file = new File(dir, filename);
					if(file.exists()){//如果上传的文件发生重名，然后进行改名
						filename = filename.substring(0, filename.indexOf(".")-1)+ dir.listFiles().length+ filename.substring(filename.indexOf("."));
						file = new File(dir, filename);
					}
					save(id, file);
				}else{// 如果上传的文件存在上传记录,读取上次的断点位置
					file = new File(log.getPath());//从上传记录中得到文件的路径
					if(file.exists()){
						File logFile = new File(file.getParentFile(), file.getName()+".log");
						if(logFile.exists()){
							Properties properties = new Properties();
							properties.load(new FileInputStream(logFile));
							position = Integer.valueOf(properties.getProperty("length"));//读取断点位置
						}
					}
				}
				
				OutputStream outStream = client.getOutputStream();
				String response = "sourceid="+ id+ ";position="+ position+ "\r\n";
				//服务器收到客户端的请求信息后，给客户端返回响应信息：sourceid=1274773833264;position=0
				//sourceid由服务生成，唯一标识上传的文件，position指示客户端从文件的什么位置开始上传
				outStream.write(response.getBytes("utf-8"));
				
				RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
				if(position==0) fileOutStream.setLength(Integer.valueOf(filelength));//设置文件长度
				fileOutStream.seek(position);//移动文件指定的位置开始写入数据
				byte[] buffer = new byte[1024*100];
				int len = -1;
				int length = position;
				while( (len=inStream.read(buffer)) != -1){//从输入流中读取数据写入到文件中
					fileOutStream.write(buffer, 0, len);
					length += len;
					Properties properties = new Properties();
					properties.put("length", String.valueOf(length));
					FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName()+".log"));
					properties.store(logFile, null);//实时记录文件的最后保存位置
					logFile.close();
				}
				if(length==fileOutStream.length()) delete(id);
				fileOutStream.close();					
				inStream.close();
				outStream.close();
				file = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
                if(client != null && !client.isClosed()) client.close();
            } catch (IOException e) {}
		}
		
		//转发文件：
		//uploadTwo(object);
	}
	
	private void uploadTwo(JSONObject object) {
		System.out.println("服务器转发上传文件到盒子");
		//354117215727936

		/*String filename="e:/chen/sshWeb.rar";
		File uploadFile = new File(filename);*/
		uploadFile(object);
	}
	 
    private void uploadFile(JSONObject object) {
    	try {  
		
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = " "+jsonString+"\r\n";  
            
            //找到盒子id； 
            String boxid1 = object.getJSONObject("user").getString("boxid");
            Socket socket = CollectionHashMap.socMap.get(boxid1);

            OutputStream outStream = socket.getOutputStream();  
            outStream.write(head.getBytes("UTF-8"));
            
            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());      
            String response = StreamTool.readLine(inStream);  
            String[] items = response.split(";");  
            String responseid = items[0].substring(items[0].indexOf("=")+1);  
            String position = items[1].substring(items[1].indexOf("=")+1);  
            
            String filename = object.getString("filename");
    		File uploadFile2 = new File(uploadPath,filename);
             
            RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile2, "r");  
            fileOutStream.seek(Integer.valueOf(position));  
            byte[] buffer = new byte[1024*100];  
            int len = -1;  
            while(start&&(len = fileOutStream.read(buffer)) != -1){  
                outStream.write(buffer, 0, len);   
            }  
            fileOutStream.close();  
            outStream.close();   
            System.out.println("上传文件传输完成");
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    private void uploadBack(JSONObject object) {
		System.out.println("盒子返回上传结果到服务器-服务器转发给手机");
		
		try {  
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            //找出手机socket
            String mobile1 = object.getJSONObject("head").getString("mobileid");
            Socket socket = CollectionHashMap.socMap.get(mobile1);
            BufferedOutputStream  bos = new BufferedOutputStream (socket.getOutputStream());
	        bos.write(head.getBytes("UTF-8"));
	        bos.flush();
	        bos.close();    
	        socket.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
	}
    

	public FileLog find(Long sourceid) {
		return datas.get(sourceid);
	}

	// 保存上传记录
	public void save(Long id, File saveFile) {
		// 日后可以改成通过数据库存放
		datas.put(id, new FileLog(id, saveFile.getAbsolutePath()));
	}

	// 当文件上传完毕，删除记录
	public void delete(long sourceid) {
		if (datas.containsKey(sourceid))
			datas.remove(sourceid);
	}

	private class FileLog {
		private Long id;
		private String path;
		
		public FileLog(Long id, String path) {
			super();
			this.id = id;
			this.path = path;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}
	
}
