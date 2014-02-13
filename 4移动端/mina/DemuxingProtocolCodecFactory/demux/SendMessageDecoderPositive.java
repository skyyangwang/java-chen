package util.demux;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public class SendMessageDecoderPositive  implements MessageDecoder{

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// TODO 自动生成的方法存根
		if (in.remaining() < 2)    
			return MessageDecoderResult.NEED_DATA;   
		else {    
			char symbol = in.getChar();    
			if (symbol == '+') {      
				return MessageDecoderResult.OK;       
			} 
			else { 
				return MessageDecoderResult.NOT_OK;     
	        }    
		} 
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// TODO 自动生成的方法存根
		SendMessage sm = new SendMessage();   
		sm.setSymbol(in.getChar());   
		sm.setI(in.getInt());   
		sm.setJ(in.getInt());   
		out.write(sm);    
		return MessageDecoderResult.OK; 
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		// TODO 自动生成的方法存根
		
	}
	
}
