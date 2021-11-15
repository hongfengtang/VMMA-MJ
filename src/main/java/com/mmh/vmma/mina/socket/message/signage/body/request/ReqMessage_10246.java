/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage.body.request;

import com.mmh.vmma.mina.socket.message.signage.BLOCKDATATYPE;
import com.mmh.vmma.mina.socket.message.signage.Block;
import com.mmh.vmma.mina.socket.message.signage.MESSAGETYPE;
import com.mmh.vmma.mina.socket.message.signage.Message;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 * 选择货道号码信息的通知(PushData 函数)
 * 把被选中的货道 No.向自动售货机通知。 
 * 当货道的贩卖状态为可结算时,搬出由货道 No.指定的货道中的商品。 
 * 当自动贩卖机在待机中收到此事件的通知,该货道 No.的对应货道作为先选择商品。
 * 例如： 02 0A 00 01 01 28 06 42 00 01 01 01 22
 */
public class ReqMessage_10246 extends Message{
	private final static short _TYPE  = 0x2806;
	private final static byte _ITEM_NO = (byte) 0x00;
	private final static byte _BLOCK_COUNT = (byte) 0x01;
	
	private Block block;
	
	public ReqMessage_10246() {
		setHeader();
		setLength((short) 10);
		
		block = new Block(BLOCKDATATYPE.BYTE, (byte)0x01);
//		block.setData((byte)0xFF);
	}
	
	private void setHeader() {
		getHeader().setDataID(_TYPE);
		getHeader().setMsgType(MESSAGETYPE.PUSH);
		getHeader().setItemNo(_ITEM_NO);
		getHeader().setBlockCount(_BLOCK_COUNT);
	}
	
	public void setColumnNo(byte column) {
		block.setData(column);
	}

	@Override
	public byte[] toBytes() {
		
		return CommonUtils.concatAll(commonBytes(), block.toBytes());
//		return convertTo(CommonUtils.concatAll(commonBytes(), block.toBytes()));
		
//		byte[] msg = new byte[commonBytes().length + block.toBytes().length];
//		System.arraycopy(commonBytes(), 0, msg, 0, commonBytes().length);
//		System.arraycopy(block.toBytes(), 0, msg, 0, block.toBytes().length);
//		
//		return msg;
	}
}
