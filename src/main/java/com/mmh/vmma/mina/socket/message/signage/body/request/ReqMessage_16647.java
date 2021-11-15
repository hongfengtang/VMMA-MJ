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
 * 最大货道数的取得(GetData 函数)
 * 用於keep alive
 * 最大货道数是自动售货机最大可设置的货道数(1~127)。
 * 例如： 02 0A 00 01 01 28 06 42 00 01 01 01 22
 */
public class ReqMessage_16647 extends Message{
	private final static short _TYPE  = 0x4107;
	
	private final static byte _ITEM_NO = (byte) 0x00;
	private final static byte _BLOCK_COUNT = (byte) 0x00;
	
	public ReqMessage_16647() {
		setHeader();
		setLength((short) 10);
	}
	
	private void setHeader() {
		getHeader().setDataID(_TYPE);
		getHeader().setMsgType(MESSAGETYPE.GET);
		getHeader().setItemNo(_ITEM_NO);
		getHeader().setBlockCount(_BLOCK_COUNT);
	}
	
	@Override
	public byte[] toBytes() {
		
		return CommonUtils.concatAll(commonBytes());
//		return convertTo(CommonUtils.concatAll(commonBytes()));
		
//		byte[] msg = new byte[commonBytes().length + block.toBytes().length];
//		System.arraycopy(commonBytes(), 0, msg, 0, commonBytes().length);
//		System.arraycopy(block.toBytes(), 0, msg, 0, block.toBytes().length);
//		
//		return msg;
	}
}
