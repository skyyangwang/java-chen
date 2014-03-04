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
        // �����������ݵĹ�����  
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();  
        // �趨�������������**Ϊ��λ��ȡ��  
        //json��ȡ
        /*TextLineCodecFactory lineCodec = new TextLineCodecFactory(
				Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
				LineDelimiter.WINDOWS.getValue());
		lineCodec.setDecoderMaxLineLength(2 * 1024 * 1024);
		
        chain.addLast("vestigge", new ProtocolCodecFilter(lineCodec)); */
          
      //�����ȡ��
  		/*chain.addLast("myChin", new ProtocolCodecFilter(
  				new ObjectSerializationCodecFactory()));*/
        
     // �Զ����·���������  
        chain.addLast("codec", new ProtocolCodecFilter(new DMXCodecFactory(false)));
               
        chain.addLast("logger", new LoggingFilter());
        // �趨�ͻ��˵���Ϣ������:һ��ObjectMinaClientHandler����,  
        connector.setHandler(new UserMinaClientHandler2());  
        
        // ���ᵽ������:  
        ConnectFuture cf = connector.connect(new InetSocketAddress("localhost", 6699)); 
        // �ȴ����Ӵ������  
        cf.awaitUninterruptibly();  
        // �ȴ����ӶϿ�  
        cf.getSession().getCloseFuture().awaitUninterruptibly();  
  
        // �ͻ��˶Ͽ����ӣ��ͷ���Դ  
        connector.dispose();  
    }  
}
