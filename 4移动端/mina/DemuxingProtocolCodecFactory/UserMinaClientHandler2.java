package service;

import model.bean.MobieMessageJson;
import model.bean.MobileHeadJson;
import model.bean.SysUser;
import object.JsonBean;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import util.demux.ResultMessage;
import util.demux.SendMessage;

import com.google.gson.Gson;

public class UserMinaClientHandler2 extends IoHandlerAdapter{
	
	private static JsonBean jsonBean;
	
	// ��һ��������������ʱ  
    @Override  
    public void sessionOpened(IoSession session) throws Exception {  
    	System.out.println("client������Ϣ��"); 
	
    	//��ȡ�ƴ洢�б�---�ֻ�����
    	/*MobieMessageJson<MobileHeadJson,SysUser> appJson = new MobieMessageJson<MobileHeadJson,SysUser>();
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		
		mobileHeadJson.setMethod(10021);
		mobileHeadJson.setBoxid("354114915724936");
		mobileHeadJson.setMobileid("aaa111");
		
		appJson.setHead(mobileHeadJson);

		//session.write(new Gson().toJson(appJson));
		jsonBean = new JsonBean();
		jsonBean.setJsonStr(new Gson().toJson(appJson));
		
		session.write(jsonBean);*/
    	
    	SendMessage sm = new SendMessage();   
    	sm.setI(100);   
    	sm.setJ(99);   
    	sm.setSymbol('-'); //�޸�+�����в��ԣ�  
    	session.write(sm); 
		
    	//�û��������
    	//ע�᣺
    	/*MobieMessageJson<MobileHeadJson,SysUser> appJson = new MobieMessageJson<MobileHeadJson,SysUser>();
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		SysUser sysUser = new SysUser();
		
		mobileHeadJson.setMethod(10001);
		
		sysUser.setName("android");
		sysUser.setPassword("android3");
		sysUser.setBoxid("bbb");
		
		appJson.setHead(mobileHeadJson);
		
		appJson.setBody(sysUser);
		
		session.write(new Gson().toJson(appJson));*/
    	
    	
    	//�޸�
    	/*MobieMessageJson<MobileHeadJson,SysUser> appJson = new MobieMessageJson<MobileHeadJson,SysUser>();
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		SysUser sysUser = new SysUser();
		
		mobileHeadJson.setMethod(10003);
		
		sysUser.setName("android");
		sysUser.setPassword("android2");
		sysUser.setBoxid("bbb");
		
		appJson.setHead(mobileHeadJson);
		
		appJson.setBody(sysUser);
		
		session.write(new Gson().toJson(appJson));*/
    	
    	
    	//��¼
    	/*MobieMessageJson<MobileHeadJson,SysUser> appJson = new MobieMessageJson<MobileHeadJson,SysUser>();
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		SysUser sysUser = new SysUser();
		
		mobileHeadJson.setMethod(10002);	//�û���¼
		
		sysUser.setName("android");
		sysUser.setPassword("android");
		
		appJson.setHead(mobileHeadJson);
		
		appJson.setBody(sysUser);
		
		session.write(new Gson().toJson(appJson));*/
		
		//app���ز���
		/*MobieMessageJson<MobileHeadJson,AppStore> appJson = new MobieMessageJson<MobileHeadJson,AppStore>();
		
		//AppStore appStore = new AppStore();
		//appStore.setName("΢��");
		
		//appJson.setBody(appStore);
		
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		mobileHeadJson.setMethod(10011);
		
		appJson.setHead(mobileHeadJson);
		
		session.write(new Gson().toJson(appJson));*/
    }  
  
    // ��һ������˹ر�ʱ  
    @Override  
    public void sessionClosed(IoSession session) {  
        System.out.println(session.getRemoteAddress() + " server Disconnect !");  
    }  
  
    // �����������͵���Ϣ����ʱ:  
    @Override  
    public void messageReceived(IoSession session, Object message) throws Exception {  
       
    	System.out.println("client�յ���Ϣ��"); 
    	
        System.out.println("�����ǿͻ���(" + session.getLocalAddress() + ")\t������(" + session.getRemoteAddress() + ")��������Ϣ: " + message.toString());  
        
        ResultMessage rs = (ResultMessage) message;   
        System.out.println(String.valueOf(rs.getResult())); 
        
        // ���͵������  
        //session.write(u);  
    }  
}
