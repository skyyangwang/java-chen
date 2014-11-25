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
	//手机、盒子交互使用；
		public static void main(String[] args) throws IOException {  
	        // 创建一个非阻塞的Server端 Socket,用NIO  
	        IoAcceptor acceptor = new NioSocketAcceptor(); 
	        // 创建接收数据的过滤器  
	        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();  
	        
	        // 自定义多路分离解码器  
	        chain.addLast("codec", new ProtocolCodecFilter(new DMXCodecFactory(true)));
			 
	        chain.addLast("logger", new LoggingFilter());
	  
	        // 设定服务器端的消息处理器:一个ObjectMinaServerHandler对象,   
	        acceptor.setHandler(new CloudStoreServerHandler()); 
	  
	        // 服务器端绑定的端口  
	        int bindPort = 6699;  
	        // 绑定端口,启动服务器  
	        acceptor.bind(new InetSocketAddress(bindPort));  
	        System.out.println("RUserMina Server is Listing on:= " + bindPort);  
	    }  

}
