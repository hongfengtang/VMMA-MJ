/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage.body.request;

import com.mmh.vmma.mina.socket.message.signage.MESSAGETYPE;
import com.mmh.vmma.mina.socket.message.signage.Message;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 * 退币要求指示的通知(PushData 函数)
 * 从映像控制器指示退币。
 * 02 07 00 01 01 62 01 42 00 00
 */
public class ReqMessage_25089 extends Message{
	private final static short _TYPE  = (short)0x6201;
	private final static byte _ITEM_NO = (byte) 0x00;
	private final static byte _BLOCK_COUNT = (byte) 0x00;
	
	public ReqMessage_25089() {
		setHeader();
		setLength((short) 0x07);
	}
	
	private void setHeader() {
		getHeader().setDataID(_TYPE);
		getHeader().setMsgType(MESSAGETYPE.PUSH);
		getHeader().setItemNo(_ITEM_NO);
		getHeader().setBlockCount(_BLOCK_COUNT);
	}
	
	@Override
	public byte[] toBytes() {
		return CommonUtils.concatAll(commonBytes());
//		return convertTo(CommonUtils.concatAll(commonBytes()));
	}

}
