/**
 * 
 */
package com.mmh.vmma.mina.socket.signage;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hongftan
 *
 */
public class SignageIFDecoder extends CumulativeProtocolDecoder {

	private final static Logger logger = LoggerFactory.getLogger(SignageIFDecoder.class);
	public SignageIFDecoder() {
		
	}
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		
		logger.debug("-----------------------------------");
		logger.debug("in = [{}]", in.getHexDump());
		logger.debug("-----------------------------------");

		if(in.remaining() >= 10) {
			IoBuffer buffer = IoBuffer.allocate(10, false);
			buffer.setAutoExpand(true);
			
			in.mark();
			
			buffer.put(in.get());
			while(in.remaining() > 0) {
				byte b = in.get();
				if(b == 0x02) {
					in.position(in.position() - 1);
					break;
				}
				
				if(b == 0x7D) {
					b = in.get();
					if(b == 0x22) {
						buffer.put((byte)0x02);
					}
					else if(b == 0x5D) {
						buffer.put((byte)0x7D);
					}else {
						in.reset();
						return false;
					}
					
					continue;
				}
				buffer.put(b);
			}
			
			buffer.flip();
			buffer.mark();
			//取得字符串長度
			if(buffer.limit() >= 10) {
				int msgLength = buffer.get(1);
				msgLength += buffer.get(2) << 8;
//				logger.debug("$$$$$$$$$$$$$$$$$-------{}-------$$$$$$$$$$$$$$$$$", msgLength);
				
				if ((msgLength <= 0) || (msgLength +3 != buffer.limit())) {
					in.reset();
					return false;
				}
					
			}else {
				in.reset();
				return false;
			}
			buffer.reset();
//			logger.debug("**********************************");
//			logger.debug("[{}]", buffer.getHexDump());
//			logger.debug("**********************************");
//			in.position(in.limit());
			out.write(buffer);
			return true;
		}
		
		return false;
	}

}
