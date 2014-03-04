package socketUse;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSocket {
	public static final int PORT = 9933;//监听的端口号  
	private static ExecutorService executorService;// 线程池
	
	//也可以使用init将业务逻辑分解出来；
	public static void main(String[] args) {    
        System.out.println("服务器启动...\n");    
        try {
        	// 初始化线程池
    		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
    				.availableProcessors() * 50);
    		
        	// 创建一个ServerSocket在端口...监听客户请求
            ServerSocket serverSocket = new ServerSocket(PORT);    
            while (true) {    
            	// 侦听并接受到此Socket的连接,请求到来则产生一个Socket对象，并继续执行
                Socket client = serverSocket.accept();  
                
             // 为支持多用户并发访问，采用线程池管理每一个用户的连接请求
    			executorService.execute(new SSocketHandler(client));// 启动一个线程来处理请求
                       
                //连接处理类
                //new SSocketHandler(client);
            }    
        } catch (Exception e) {    
            System.out.println("服务器异常: " + e);    
        }
    }

}
