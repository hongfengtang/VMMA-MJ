/**
 * 
 */
package com.mmh.vmma.mina.socket.signage;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author hongftan
 *
 */
public class SignageCodecFactory implements ProtocolCodecFactory {
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		
		return new SignageIFEncoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new SignageIFDecoder();
	}

}
