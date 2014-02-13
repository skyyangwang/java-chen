package handler;

import net.sf.json.JSONObject;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import util.demux.ResultMessage;
import util.demux.SendMessage;
import controller.CloudStoreControllerMina;

public class CloudStoreServerHandler  extends IoHandlerAdapter{
	/** 
     * 当客户端 发送 的消息到达时 ---1登录；11注册；12修改密码；13找回密码
     */  
    @Override  
    public void messageReceived(IoSession session, Object message) throws Exception {   
    	System.out.println("server收到消息：" + message.toString());

/*    	JSONObject object = JSONObject.fromObject(message.toString());
		//int method = object.getInt("method");
    	int method = Integer.parseInt(object.getJSONObject("head").getString("method").toString());
		
		switch (method) {	
		//云存储
		case 10021: //列表请求---中转处理
			CloudStoreControllerMina.BTransitDeal(session, object);
			break;
		}*/
		
		SendMessage sm = (SendMessage) message;    
		System.out.println("The message received is [ " + sm.getI() + " "       + sm.getSymbol() + " " + sm.getJ() + " ]"
				);      
		ResultMessage rm = new ResultMessage(); 	      
		rm.setResult(sm.getI() + sm.getJ());   
		session.write(rm); 
		
    }  
  
    /** 
     * 当一个客户端连接进入时 
     */  
    @Override  
    public void sessionOpened(IoSession session) throws Exception {  
        System.out.println("incomming client: " + session.getRemoteAddress()); 
    }  
  
    /** 
     * 当一个客户端关闭时 
     */  
    @Override  
    public void sessionClosed(IoSession session) throws Exception {  
        System.out.println(session.getRemoteAddress() + " client Disconnect!");  
    }  
  
    /** 
     * 当捕获到异常的时候 
     */  
    @Override  
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {  
        System.err.println("error!!!!!!!!!!!!!");  
        super.exceptionCaught(session, cause);  
    } 
    
}
