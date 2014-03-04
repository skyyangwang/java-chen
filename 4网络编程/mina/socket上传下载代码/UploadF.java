package socketUse;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import net.sf.json.JSONObject;

public class UploadF {
	private static boolean start=true;
	
	public static void main(String[] args) {
		
		String filename="e:/nana南岸.txt";
		File uploadFile = new File(filename);
		uploadFile(uploadFile);  
	}

	/** 
     * 上传文件 
     * @param uploadFile 
     */  
    private static void uploadFile(final File uploadFile) {
    	try {  
    		JSONObject jsonObject = new JSONObject();
    		jsonObject.put("filename", uploadFile.getName());
    		
    		JSONObject jsonObject2 = new JSONObject();
    		jsonObject2.put("method", 10041);
    		jsonObject2.put("mobileid", "sss");
    		jsonObject2.put("status", 0);
    		
    		jsonObject.put("head", jsonObject2);
    		jsonObject.put("size", uploadFile.length());
    		jsonObject.put("sourceid", "");
    		
    		JSONObject jsonObject3 = new JSONObject();
    		jsonObject3.put("boxid", "354117215727936");
    		jsonObject3.put("name", "ss");
    		jsonObject3.put("password", "s");
    		
    		jsonObject.put("user", jsonObject3);
    		
            String jsonString=jsonObject.toString();
            
            System.out.println(jsonString);
            
            String head = jsonString+"\r\n";  
            
            Socket socket = new Socket("localhost",9933);  
            OutputStream outStream = socket.getOutputStream();  
            outStream.write(head.getBytes("utf-8"));  
              
            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());      
            String response = StreamTool.readLine(inStream);  
            String[] items = response.split(";");  
            String responseid = items[0].substring(items[0].indexOf("=")+1);  
            String position = items[1].substring(items[1].indexOf("=")+1);  
             
            RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");  
            fileOutStream.seek(Integer.valueOf(position));  
            byte[] buffer = new byte[1024];  
            int len = -1;  
            int length = Integer.valueOf(position);  
            while(start&&(len = fileOutStream.read(buffer)) != -1){  
                outStream.write(buffer, 0, len);   
            }  
            fileOutStream.close();  
            outStream.close();  
            inStream.close();  
            socket.close();  
            if(length==uploadFile.length()) {
            	
            } 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
}
