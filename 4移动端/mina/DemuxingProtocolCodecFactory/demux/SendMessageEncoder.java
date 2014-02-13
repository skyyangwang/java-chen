package util.demux;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

public class SendMessageEncoder implements MessageEncoder<SendMessage> {

	@Override
	public void encode(IoSession session, SendMessage message,
			ProtocolEncoderOutput out) throws Exception {
		// TODO 自动生成的方法存根
		IoBuffer buffer = IoBuffer.allocate(10);   
		buffer.putChar(message.getSymbol());   
		buffer.putInt(message.getI());   
		buffer.putInt(message.getJ());   
		buffer.flip();   
		out.write(buffer); 
	}

}
