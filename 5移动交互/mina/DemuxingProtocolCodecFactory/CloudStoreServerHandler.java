package handler;

import net.sf.json.JSONObject;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import util.demux.ResultMessage;
import util.demux.SendMessage;
import controller.CloudStoreControllerMina;

public class CloudStoreServerHandler  extends IoHandlerAdapter{
	/** 
     * ���ͻ��� ���� ����Ϣ����ʱ ---1��¼��11ע�᣻12�޸����룻13�һ�����
     */  
    @Override  
    public void messageReceived(IoSession session, Object message) throws Exception {   
    	System.out.println("server�յ���Ϣ��" + message.toString());

/*    	JSONObject object = JSONObject.fromObject(message.toString());
		//int method = object.getInt("method");
    	int method = Integer.parseInt(object.getJSONObject("head").getString("method").toString());
		
		switch (method) {	
		//�ƴ洢
		case 10021: //�б�����---��ת����
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
     * ��һ���ͻ������ӽ���ʱ 
     */  
    @Override  
    public void sessionOpened(IoSession session) throws Exception {  
        System.out.println("incomming client: " + session.getRemoteAddress()); 
    }  
  
    /** 
     * ��һ���ͻ��˹ر�ʱ 
     */  
    @Override  
    public void sessionClosed(IoSession session) throws Exception {  
        System.out.println(session.getRemoteAddress() + " client Disconnect!");  
    }  
  
    /** 
     * �������쳣��ʱ�� 
     */  
    @Override  
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {  
        System.err.println("error!!!!!!!!!!!!!");  
        super.exceptionCaught(session, cause);  
    } 
    
}
