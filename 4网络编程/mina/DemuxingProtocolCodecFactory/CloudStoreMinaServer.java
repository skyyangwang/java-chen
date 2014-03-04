package server;

import handler.CloudStoreServerHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import util.demux.DMXCodecFactory;

public class CloudStoreMinaServer {
	//�ֻ������ӽ���ʹ�ã�
		public static void main(String[] args) throws IOException {  
	        // ����һ����������Server�� Socket,��NIO  
	        IoAcceptor acceptor = new NioSocketAcceptor(); 
	        // �����������ݵĹ�����  
	        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();  
	        
	        // �Զ����·���������  
	        chain.addLast("codec", new ProtocolCodecFilter(new DMXCodecFactory(true)));
			 
	        chain.addLast("logger", new LoggingFilter());
	  
	        // �趨�������˵���Ϣ������:һ��ObjectMinaServerHandler����,   
	        acceptor.setHandler(new CloudStoreServerHandler()); 
	  
	        // �������˰󶨵Ķ˿�  
	        int bindPort = 6699;  
	        // �󶨶˿�,����������  
	        acceptor.bind(new InetSocketAddress(bindPort));  
	        System.out.println("RUserMina Server is Listing on:= " + bindPort);  
	    }  

}
