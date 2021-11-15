/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage;

import com.mmh.vmma.utils.CommonUtils;

import lombok.Data;

/**
 * @author hongftan
 * 
 */
@Data
public class Header {
	private final static byte _NONE = (byte) 0xFF;
	private final static byte _EVENT = (byte) 0x00;
	private final static byte _GET = (byte) 0x01;
	private final static byte _SET = (byte) 0x02;
	private final static byte _SEND = (byte) 0x04;
	private final static byte _CLEAR = (byte) 0x08;
	private final static byte _PUSH = (byte) 0x42;
	
	private byte objId = 0x01;		//對象ID
	private byte indexId = 0x01;	//索引ID
	private short dataID = 0;		//自動收貨機對象ID (數據ID)
	private byte msgType = -1;		//消息類型
	private byte itemNo = 0x00;		//列表項目編號
	private byte blockCount = 0x00;	//塊數
	
	public void setMsgType(byte type) {
		msgType = type;
	}
	public void setMsgType(MESSAGETYPE type) {
		switch (type) {
		case EVENT:
			msgType = _EVENT;
			break;
		case GET:
			msgType = _GET;
			break;
		case SET:
			msgType = _SET;
			break;
		case SEND:
			msgType = _SEND;
			break;
		case CLEAR:
			msgType = _CLEAR;
			break;
		case PUSH:
			msgType = _PUSH;
			break;
		default:
			msgType = _NONE;
			break;
		}
	}
	
	public MESSAGETYPE getMsgType() {
		switch (msgType) {
		case _EVENT:
			return MESSAGETYPE.EVENT;
		case _GET:
			return MESSAGETYPE.GET;
		case _SET:
			return MESSAGETYPE.SET;
		case _SEND:
			return MESSAGETYPE.SEND;
		case _CLEAR:
			return MESSAGETYPE.CLEAR;
		case _PUSH:
			return MESSAGETYPE.PUSH;
		default:
			return MESSAGETYPE.NONE;
		}
	}
	
	public byte[] toBytes() {
		byte[] dataId = CommonUtils.shortToByteArray(dataID);
		return new byte[] {objId, indexId, dataId[0], dataId[1], msgType, itemNo, blockCount};
	}
	
}
