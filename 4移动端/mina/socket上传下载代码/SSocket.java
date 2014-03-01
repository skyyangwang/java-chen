package socketUse;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSocket {
	public static final int PORT = 9933;//�����Ķ˿ں�  
	private static ExecutorService executorService;// �̳߳�
	
	//Ҳ����ʹ��init��ҵ���߼��ֽ������
	public static void main(String[] args) {    
        System.out.println("����������...\n");    
        try {
        	// ��ʼ���̳߳�
    		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
    				.availableProcessors() * 50);
    		
        	// ����һ��ServerSocket�ڶ˿�...�����ͻ�����
            ServerSocket serverSocket = new ServerSocket(PORT);    
            while (true) {    
            	// ���������ܵ���Socket������,�����������һ��Socket���󣬲�����ִ��
                Socket client = serverSocket.accept();  
                
             // Ϊ֧�ֶ��û��������ʣ������̳߳ع���ÿһ���û�����������
    			executorService.execute(new SSocketHandler(client));// ����һ���߳�����������
                       
                //���Ӵ�����
                //new SSocketHandler(client);
                
    			
            }    
        } catch (Exception e) {    
            System.out.println("�������쳣: " + e);    
        }
    }

 

}
