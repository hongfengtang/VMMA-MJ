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
 * 自动售货机贩卖状态的取得(GetData 函数)/设定(SetData 函数)
 * 贩卖状态
 * 例如： 
 */
public class ReqMessage_16650 extends Message{
	private final static short _TYPE  = 0x410A;
	
	private final static byte _ITEM_NO = (byte) 0x00;
	
	private Block block1;	//販賣停止指示
	private Block block2;	//自動解除時的秒數
	
	public ReqMessage_16650(MESSAGETYPE msgType) {
		getHeader().setDataID(_TYPE);
		getHeader().setMsgType(msgType);
		getHeader().setItemNo(_ITEM_NO);
		
		if (msgType == MESSAGETYPE.GET) {
			getHeader().setBlockCount((byte) 0x00);
			setLength((short) 7);
		}else if(msgType == MESSAGETYPE.SET) {
			getHeader().setBlockCount((byte) 0x02);
			setLength((short) 14);
			setBlocks();
		}
	}
	
	//僅用于set動作
	private void setBlocks() {
		block1 = new Block(BLOCKDATATYPE.BYTE, (byte) 0x01);
		block2 = new Block(BLOCKDATATYPE.SHORT, (byte) 0x01);
	}
	
	//僅用于set動作
	public void setStop(byte stop) {
		if(block1 != null)
			block1.setData(stop);
	}

	//僅用于set動作
	public void setRlsSeconds(short second) {
		if(block2 != null)
			block2.setData(second);
	}

	@Override
	public byte[] toBytes() {
		
		switch(getHeader().getMsgType()) {
		case GET:
			return CommonUtils.concatAll(commonBytes());
//			return convertTo(CommonUtils.concatAll(commonBytes()));
		case SET:
			return CommonUtils.concatAll(commonBytes(), block1.toBytes(), block2.toBytes());
//			return convertTo(CommonUtils.concatAll(commonBytes(), block1.toBytes(), 
//					block2.toBytes()));
		default:
			return CommonUtils.concatAll(commonBytes());
//			return convertTo(CommonUtils.concatAll(commonBytes()));
		}
	}
}
