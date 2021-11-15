/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage;

import java.util.ArrayList;
import java.util.List;

import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.utils.CommonUtils;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public abstract class Message {
	public final static byte STX = 0x02;
	private static int indexId = 0;
	
	private short length = 0;
	
	private Header header;
	
	public Message(byte[] bytes){
		short msgType = (short) ((byte)bytes[5] + (byte)bytes[6] << 8);
		getHeader().setObjId(bytes[3]);
		getHeader().setIndexId(bytes[4]); 
		getHeader().setDataID(msgType);
		getHeader().setMsgType(bytes[7]);
		getHeader().setItemNo(bytes[8]);
		getHeader().setBlockCount(bytes[9]);
	}

	public Message() {
		header = new Header();
		header.setObjId(CODES.OBJECT_ID);
		indexId = indexId > 0x7F ? 1 : indexId + 1;
		header.setIndexId((byte)indexId);
	}
	
	public void updateIndexId() {
		indexId = indexId > 0x7F ? 1 : indexId + 1;
		header.setIndexId((byte)indexId);
	}
	
	public byte[] commonBytes() {
		
		byte[] common = new byte[3 + header.toBytes().length];
		common[0] = STX;
		byte[] len = CommonUtils.shortToByteArray(length);
		common[1] = len[1];
		common[2] = len[0];
		int i = 3;
		for(byte b : header.toBytes()) {
			common[i++] = b;
		}
		return common;
	}
	
	public static byte[] convertTo(byte[] msg) {
		if(msg.length <= 0) {
			return null;
		}
		List<Byte> convertMsg= new ArrayList<Byte>();
		convertMsg.add(STX);
		for(int i = 1; i < msg.length; i++) {
			if(msg[i] == 0x02) {
				convertMsg.add((byte) 0x7D);
				convertMsg.add((byte) 0x22);
				continue;
			}
			
			if(msg[i] == 0x7D) {
				convertMsg.add((byte) 0x7D);
				convertMsg.add((byte) 0x5D);
				continue;
			}
			convertMsg.add(msg[i]);
		}
		byte[] convertBytes = new byte[convertMsg.size()];

		for(int j = 0; j < convertMsg.size(); j++) {
			convertBytes[j] = convertMsg.get(j);
		}
		return convertBytes;
	}
	
	public static byte[] convertFrom(byte[] msg) {
		if(msg.length <= 0) {
			return null;
		}
		List<Byte> convertMsg= new ArrayList<Byte>();
		convertMsg.add(STX);
		for(int i = 1; i < msg.length; i++) {
			if((msg[i] == 0x7D) && (msg[i+1] == 0x22)) {
				convertMsg.add((byte) 0x02);
				i++;
				continue;
			}
			
			if((msg[i] == 0x7D) && (msg[i+1] == 0x5D)){
				convertMsg.add((byte) 0x7D);
				i++;
				continue;
			}
			convertMsg.add(msg[i]);
		}
		byte[] convertBytes = new byte[convertMsg.size()];

		for(int j = 0; j < convertMsg.size(); j++) {
			convertBytes[j] = convertMsg.get(j);
		}
		return convertBytes;

	}
	public abstract byte[] toBytes();
}
