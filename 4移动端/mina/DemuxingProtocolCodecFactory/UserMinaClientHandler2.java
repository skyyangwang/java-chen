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
	
	// 当一个服务端连结进入时  
    @Override  
    public void sessionOpened(IoSession session) throws Exception {  
    	System.out.println("client发送消息："); 
	
    	//获取云存储列表---手机连接
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
    	sm.setSymbol('-'); //修改+－进行测试；  
    	session.write(sm); 
		
    	//用户管理测试
    	//注册：
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
    	
    	
    	//修改
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
    	
    	
    	//登录
    	/*MobieMessageJson<MobileHeadJson,SysUser> appJson = new MobieMessageJson<MobileHeadJson,SysUser>();
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		SysUser sysUser = new SysUser();
		
		mobileHeadJson.setMethod(10002);	//用户登录
		
		sysUser.setName("android");
		sysUser.setPassword("android");
		
		appJson.setHead(mobileHeadJson);
		
		appJson.setBody(sysUser);
		
		session.write(new Gson().toJson(appJson));*/
		
		//app下载测试
		/*MobieMessageJson<MobileHeadJson,AppStore> appJson = new MobieMessageJson<MobileHeadJson,AppStore>();
		
		//AppStore appStore = new AppStore();
		//appStore.setName("微信");
		
		//appJson.setBody(appStore);
		
		MobileHeadJson mobileHeadJson = new MobileHeadJson();
		mobileHeadJson.setMethod(10011);
		
		appJson.setHead(mobileHeadJson);
		
		session.write(new Gson().toJson(appJson));*/
    }  
  
    // 当一个服务端关闭时  
    @Override  
    public void sessionClosed(IoSession session) {  
        System.out.println(session.getRemoteAddress() + " server Disconnect !");  
    }  
  
    // 当服务器发送的消息到达时:  
    @Override  
    public void messageReceived(IoSession session, Object message) throws Exception {  
       
    	System.out.println("client收到消息："); 
    	
        System.out.println("这里是客户端(" + session.getLocalAddress() + ")\t服务器(" + session.getRemoteAddress() + ")发来的消息: " + message.toString());  
        
        ResultMessage rs = (ResultMessage) message;   
        System.out.println(String.valueOf(rs.getResult())); 
        
        // 发送到服务端  
        //session.write(u);  
    }  
}
