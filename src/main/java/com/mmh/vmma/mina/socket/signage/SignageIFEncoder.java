package com.mmh.vmma.mina.socket.signage;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignageIFEncoder implements ProtocolEncoder {

	private final static Logger logger = LoggerFactory.getLogger(SignageIFEncoder.class);
	public SignageIFEncoder() {
		
	}
	
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		
		if(message == null) {
			throw new IllegalArgumentException("You send the message is null.");
		}
		
		IoBuffer buffer = IoBuffer.allocate(10, false);
		buffer.setAutoExpand(true);
		byte[] msg = (byte[]) message;
		
		if(msg.length < 10) {
			throw new IllegalArgumentException("You send the message is not a Signage format message.");
		}
		buffer.put(msg[0]);
		for (int i = 1; i < msg.length; i++) {
			if(msg[i] == (byte)0x02) {
				buffer.put((byte)0x7D);
				buffer.put((byte)0x22);
			}else if(msg[i] == (byte)0x7D) {
				buffer.put((byte)0x7D);
				buffer.put((byte)0x5D);
			}else {
				buffer.put(msg[i]);
			}
		}
		
		buffer.flip();
		
		logger.debug("Send message [{}]", buffer.getHexDump());
		out.write(buffer);
	}
	
	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}   

}
