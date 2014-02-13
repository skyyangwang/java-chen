package service;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import util.demux.DMXCodecFactory;

public class MinaClient2 {
	public static void main(String[] args) {  
		  
        // Create TCP/IP connector.  
        IoConnector connector = new NioSocketConnector();  
        // 创建接收数据的过滤器  
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();  
        // 设定这个过滤器将以**为单位读取数  
        //json读取
        /*TextLineCodecFactory lineCodec = new TextLineCodecFactory(
				Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
				LineDelimiter.WINDOWS.getValue());
		lineCodec.setDecoderMaxLineLength(2 * 1024 * 1024);
		
        chain.addLast("vestigge", new ProtocolCodecFilter(lineCodec)); */
          
      //对象读取；
  		/*chain.addLast("myChin", new ProtocolCodecFilter(
  				new ObjectSerializationCodecFactory()));*/
        
     // 自定义多路分离解码器  
        chain.addLast("codec", new ProtocolCodecFilter(new DMXCodecFactory(false)));
               
        chain.addLast("logger", new LoggingFilter());
        // 设定客户端的消息处理器:一个ObjectMinaClientHandler对象,  
        connector.setHandler(new UserMinaClientHandler2());  
        
        // 连结到服务器:  
        ConnectFuture cf = connector.connect(new InetSocketAddress("localhost", 6699)); 
        // 等待连接创建完成  
        cf.awaitUninterruptibly();  
        // 等待连接断开  
        cf.getSession().getCloseFuture().awaitUninterruptibly();  
  
        // 客户端断开链接，释放资源  
        connector.dispose();  
    }  
}
