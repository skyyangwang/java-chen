package socketUse;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.Socket;

import net.sf.json.JSONObject;

public class CSocket {
	public static void main(String[] args) {
        try {
            /** 创建Socket*/
            // 创建一个流套接字并将其连接到指定 IP 地址的指定端口号(本处是本机)
            Socket socket = new Socket("localhost", 9933);
            // 60s超时
            socket.setSoTimeout(600000);
 
            //信息格式1
            /*JSONObject jsonObject = new JSONObject();
	        jsonObject.put("boxid", "354117215727936");
	        jsonObject.put("message", "111");
	        jsonObject.put("method", 10043);
	        jsonObject.put("mobileid", "");
	        jsonObject.put("status", 0);
	        
	        JSONObject jsonObject2 = new JSONObject();
	        jsonObject2.put("head", jsonObject);*/
            
            //信息格式2
            JSONObject jsonObject = new JSONObject();
    		jsonObject.put("filename", "110321152142222.jpg");
    		
    		JSONObject jsonObject2 = new JSONObject();
    		jsonObject2.put("method", 10043);
    		jsonObject2.put("mobileid", "sss");
    		jsonObject2.put("status", 0);
    		
    		jsonObject.put("head", jsonObject2);
    		jsonObject.put("size", 0);
    		jsonObject.put("sourceid", "");
    		
    		JSONObject jsonObject3 = new JSONObject();
    		jsonObject3.put("boxid", "354117215727936");
    		jsonObject3.put("name", "ss");
    		jsonObject3.put("password", "s");
    		
    		jsonObject.put("user", jsonObject3);
    		      
	        String jsonStr = jsonObject.toString()+"\r\n";
	        
	        BufferedOutputStream  bos = new BufferedOutputStream (socket.getOutputStream());
	        bos.write(jsonStr.getBytes());
	        bos.flush();
	        bos.close();
	        
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
    }
}
