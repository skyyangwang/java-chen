package socketUse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
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

	private Map<Long, FileLog> datas = new HashMap<Long, FileLog>();// ��Ŷϵ����ݣ���ø�Ϊ���ݿ���

	private static boolean start=true;
	
	//���췽��
	public SSocketHandler(Socket s) throws IOException {
		client = s;
        System.out.println("Client(" + client.getRemoteSocketAddress() + ") come in...");
        
        //�˷�ʽȡֵ�����е�ͷ�ļ���Ӱ�죻---������ȡ����ͷ�����ԣ�����object��
        PushbackInputStream in = new PushbackInputStream(
				client.getInputStream());
          
        String json = StreamTool.readLine(in);
        /*byte[] b = new byte[1024];
		in.read(b, 0, b.length);*/
		JSONObject object = JSONObject.fromObject(json);
		
		method = Integer.parseInt(object.getJSONObject("head").getString("method"));
		
		switch (method) {
		//�������ӱ���
		case 10042:
			ConnectionsRemain(object);
			break;
		//�ϴ���-�ֻ�
		case 10041:
			upload(object);
			break;
		//�ϴ���-���ӷ��ؽ��
		case 100410:
			uploadBack(object);
			break;
		//���أ�
			case 10043:
			download(object);
			break;
			//���أ�-���ӷ���
			case 100430:
			downloadBack(object);
			break;
		}

        //new Thread(this).start();
	}

	//����---�ֻ�������
	private void download(JSONObject object) throws IOException {
		// TODO �Զ����ɵķ������
		String mobile1 = object.getJSONObject("head").getString("mobileid");
		String boxid1 = object.getJSONObject("user").getString("boxid");
 	
    	//����Socket-�ֻ�
		CollectionHashMap.socMap.remove(mobile1);
		CollectionHashMap.socMap.put(mobile1, client);
		
		String jsonStr = object.toString()+"\r\n";
		
		System.out.println("�ֻ�����������Ϣ"+jsonStr);
		
		/*BufferedOutputStream  bos = new BufferedOutputStream (sd.getOutputStream());
        bos.write(jsonStr.getBytes());
        bos.flush();
        bos.close();*/
		
		//ת�����������
		downloadTwo(object,boxid1);
	}
	
	private void downloadTwo(JSONObject object,String boxid1) {
		
		System.out.println("������ת���������󵽺���");
		
		try {  
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            //�ҳ��ֻ�socket
            Socket socket = CollectionHashMap.socMap.get(boxid1);
            BufferedOutputStream  bos = new BufferedOutputStream (socket.getOutputStream());
	        bos.write(head.getBytes("UTF-8"));
	        bos.flush();
	        bos.close();    

        } catch (Exception e) {  
            e.printStackTrace();  
        } 
		
	}

	//������Ӵ��ص������ļ���
	private void downloadBack(JSONObject object) {
		// TODO �Զ����ɵķ������
		//���������棻
				try {
					System.out.println("���ӻش������ļ� "
							+ client.getInetAddress() + " @ " + client.getPort());
					PushbackInputStream inStream = new PushbackInputStream(
							client.getInputStream());
					// �õ��ͻ��˷����ĵ�һ��Э�����ݣ�Content-Length=143253434;filename=xxx.3gp;sourceid=
					// ����û������ϴ��ļ���sourceid��ֵΪ�ա�
					//String json = StreamTool.readLine(inStream);
					//System.out.println(json);
					if (object != null) {
						// �����Э�������ж�ȡ���ֲ���ֵ
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
							log = find(id);//�����ϴ����ļ��Ƿ�����ϴ���¼
						}
						File file = null;
						int position = 0;
						if(log==null){//����ϴ����ļ��������ϴ���¼,Ϊ�ļ���Ӹ��ټ�¼
							//String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
							File dir = new File(downloadPath);
							if(!dir.exists()) dir.mkdirs();
							file = new File(dir, filename);
							if(file.exists()){//����ϴ����ļ�����������Ȼ����и���
								filename = filename.substring(0, filename.indexOf(".")-1)+ dir.listFiles().length+ filename.substring(filename.indexOf("."));
								file = new File(dir, filename);
							}
							save(id, file);
						}else{// ����ϴ����ļ������ϴ���¼,��ȡ�ϴεĶϵ�λ��
							file = new File(log.getPath());//���ϴ���¼�еõ��ļ���·��
							if(file.exists()){
								File logFile = new File(file.getParentFile(), file.getName()+".log");
								if(logFile.exists()){
									Properties properties = new Properties();
									properties.load(new FileInputStream(logFile));
									position = Integer.valueOf(properties.getProperty("length"));//��ȡ�ϵ�λ��
								}
							}
						}
						
						OutputStream outStream = client.getOutputStream();
						String response = "sourceid="+ id+ ";position="+ position+ "\r\n";
						//�������յ��ͻ��˵�������Ϣ�󣬸��ͻ��˷�����Ӧ��Ϣ��sourceid=1274773833264;position=0
						//sourceid�ɷ������ɣ�Ψһ��ʶ�ϴ����ļ���positionָʾ�ͻ��˴��ļ���ʲôλ�ÿ�ʼ�ϴ�
						outStream.write(response.getBytes("utf-8"));
						
						RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
						if(position==0) fileOutStream.setLength(Integer.valueOf(filelength));//�����ļ�����
						fileOutStream.seek(position);//�ƶ��ļ�ָ����λ�ÿ�ʼд������
						byte[] buffer = new byte[1024];
						int len = -1;
						int length = position;
						while( (len=inStream.read(buffer)) != -1){//���������ж�ȡ����д�뵽�ļ���
							fileOutStream.write(buffer, 0, len);
							length += len;
							Properties properties = new Properties();
							properties.put("length", String.valueOf(length));
							FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName()+".log"));
							properties.store(logFile, null);//ʵʱ��¼�ļ�����󱣴�λ��
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
				
				//ת�������ļ����ֻ�
				downloadBackTwo(object);
	}

	private void downloadBackTwo(JSONObject object) {
		// TODO �Զ����ɵķ������
		try {  	
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            //�ҵ�socket-�ֻ�id
            String mobile1 = object.getJSONObject("head").getString("mobileid");
            Socket socket = CollectionHashMap.socMap.get(mobile1);
            
            OutputStream outStream = socket.getOutputStream();  
            outStream.write(head.getBytes("utf-8"));  
                      		
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
        } 
	}

	@Override
	public void run() {
		
	}
	
	public void ConnectionsRemain(JSONObject object) throws IOException{
		//ֱ��ȡ��-��ͨд����
		/*InputStream in = client.getInputStream();
		byte[] c = new byte[1024];
		in.read(c, 0, c.length);
		
        System.out.println("�ͻ��˷������ݣ�"+new String(c));
        
        JSONObject object = JSONObject.fromObject(new String(c));*/
        
    	String boxid1 = object.getJSONObject("head").getString("boxid");
    	
    	//����Socket-����
		CollectionHashMap.socMap.remove(boxid1);
		CollectionHashMap.socMap.put(boxid1, client);

		System.out.println(object.toString());
		System.out.println("����id��"+boxid1);
		
	}
	
	public void upload(JSONObject object) throws IOException{
		System.out.println("�ֻ��ϴ����ݵ�������");
		//354117215727936
		//�ٴ��ֻ�ͷ��Ϣ�в������id��
		//Socket s2 = CollectionHashMap.socMap.get("354117215727936");
		
		String mobile1 = object.getJSONObject("head").getString("mobileid");
		String boxid1 = object.getJSONObject("user").getString("boxid");
    	
    	//����Socket-�ֻ�
		CollectionHashMap.socMap.remove(mobile1);
		CollectionHashMap.socMap.put(mobile1, client);
		
		System.out.println(object.toString());
		
		//ֱ��ת������---�����⣬���飻
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
		
		//���������棻
		try {
			System.out.println("accepted connenction from "
					+ client.getInetAddress() + " @ " + client.getPort());
			PushbackInputStream inStream = new PushbackInputStream(
					client.getInputStream());
			// �õ��ͻ��˷����ĵ�һ��Э�����ݣ�Content-Length=143253434;filename=xxx.3gp;sourceid=
			// ����û������ϴ��ļ���sourceid��ֵΪ�ա�
			//String json = StreamTool.readLine(inStream);
			//System.out.println(json);
			if (object != null) {
				// �����Э�������ж�ȡ���ֲ���ֵ
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
					log = find(id);//�����ϴ����ļ��Ƿ�����ϴ���¼
				}
				File file = null;
				int position = 0;
				if(log==null){//����ϴ����ļ��������ϴ���¼,Ϊ�ļ���Ӹ��ټ�¼
					//String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
					File dir = new File(uploadPath);
					if(!dir.exists()) dir.mkdirs();
					file = new File(dir, filename);
					if(file.exists()){//����ϴ����ļ�����������Ȼ����и���
						filename = filename.substring(0, filename.indexOf(".")-1)+ dir.listFiles().length+ filename.substring(filename.indexOf("."));
						file = new File(dir, filename);
					}
					save(id, file);
				}else{// ����ϴ����ļ������ϴ���¼,��ȡ�ϴεĶϵ�λ��
					file = new File(log.getPath());//���ϴ���¼�еõ��ļ���·��
					if(file.exists()){
						File logFile = new File(file.getParentFile(), file.getName()+".log");
						if(logFile.exists()){
							Properties properties = new Properties();
							properties.load(new FileInputStream(logFile));
							position = Integer.valueOf(properties.getProperty("length"));//��ȡ�ϵ�λ��
						}
					}
				}
				
				OutputStream outStream = client.getOutputStream();
				String response = "sourceid="+ id+ ";position="+ position+ "\r\n";
				//�������յ��ͻ��˵�������Ϣ�󣬸��ͻ��˷�����Ӧ��Ϣ��sourceid=1274773833264;position=0
				//sourceid�ɷ������ɣ�Ψһ��ʶ�ϴ����ļ���positionָʾ�ͻ��˴��ļ���ʲôλ�ÿ�ʼ�ϴ�
				outStream.write(response.getBytes("utf-8"));
				
				RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
				if(position==0) fileOutStream.setLength(Integer.valueOf(filelength));//�����ļ�����
				fileOutStream.seek(position);//�ƶ��ļ�ָ����λ�ÿ�ʼд������
				byte[] buffer = new byte[1024];
				int len = -1;
				int length = position;
				while( (len=inStream.read(buffer)) != -1){//���������ж�ȡ����д�뵽�ļ���
					fileOutStream.write(buffer, 0, len);
					length += len;
					Properties properties = new Properties();
					properties.put("length", String.valueOf(length));
					FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName()+".log"));
					properties.store(logFile, null);//ʵʱ��¼�ļ�����󱣴�λ��
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
		
		//ת���ļ���
		uploadTwo(boxid1,object);
	}
	
	private void uploadTwo(String boxid1,JSONObject object) {
		System.out.println("������ת���ϴ��ļ�������");
		//354117215727936

		/*String filename="e:/chen/sshWeb.rar";
		File uploadFile = new File(filename);*/
		uploadFile(boxid1,object);
	}
	 
    private void uploadFile(String boxid1,JSONObject object) {
    	try {  
		
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            //�ҵ�����id��
            Socket socket = CollectionHashMap.socMap.get(boxid1);
            OutputStream outStream = socket.getOutputStream();  
            outStream.write(head.getBytes("UTF-8"));  
                      
            String filename = object.getString("filename");
    		File uploadFile2 = new File(uploadPath,filename);
    		
            RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile2, "r");  
            fileOutStream.seek(Integer.valueOf(0));  
            byte[] buffer = new byte[1024];  
            int len = -1;   
            while(start&&(len = fileOutStream.read(buffer)) != -1){  
                outStream.write(buffer, 0, len);   
            }  
            fileOutStream.close();  
            outStream.close();  
                        
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    private void uploadBack(JSONObject object) {
		System.out.println("���ӷ����ϴ������������-������ת�����ֻ�");
		
		try {  
            String jsonString=object.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            //�ҳ��ֻ�socket
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

	// �����ϴ���¼
	public void save(Long id, File saveFile) {
		// �պ���Ըĳ�ͨ�����ݿ���
		datas.put(id, new FileLog(id, saveFile.getAbsolutePath()));
	}

	// ���ļ��ϴ���ϣ�ɾ����¼
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
