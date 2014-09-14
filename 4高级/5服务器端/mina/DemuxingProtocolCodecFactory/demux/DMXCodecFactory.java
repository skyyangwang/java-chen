package util.demux;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

public class DMXCodecFactory extends DemuxingProtocolCodecFactory {

	public DMXCodecFactory(boolean server) {
		if (server) {
			//±àÂë£ºÊµÌåÀà£¬±àÂëÆ÷£»
			super.addMessageEncoder(ResultMessage.class,
					ResultMessageEncoder.class);
			super.addMessageDecoder(SendMessageDecoderPositive.class);
			super.addMessageDecoder(SendMessageDecoderNegative.class);
		} else {
			super.addMessageEncoder(SendMessage.class, SendMessageEncoder.class);
			super.addMessageDecoder(ResultMessageDecoder.class);
		}
	}
	
	

}




