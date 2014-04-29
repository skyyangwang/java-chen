package util.demux;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public class ResultMessageDecoder implements MessageDecoder{

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// TODO �Զ����ɵķ������
		if (in.remaining() < 4) 
		       return MessageDecoderResult.NEED_DATA;   
		else if (in.remaining() == 4)    
			return MessageDecoderResult.OK;   
		else      
			return MessageDecoderResult.NOT_OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		// TODO �Զ����ɵķ������
		ResultMessage rm = new ResultMessage();   
		rm.setResult(in.getInt());   
		out.write(rm);    
		return MessageDecoderResult.OK; 
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
		// TODO �Զ����ɵķ������
		
	}

}
