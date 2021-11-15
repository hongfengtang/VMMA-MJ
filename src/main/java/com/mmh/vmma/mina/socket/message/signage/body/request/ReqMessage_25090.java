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
 * 下回贩卖指示的通知(个别)(PushData 函数)
 * 向自动售货机指示下回贩卖形式。被指定的货道作为指示对象。
 * 02 25 00 01 01 62 7D 22 42 00 04 01 01 03 01 01 22 04 01 01 00 00 00 01 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
 */
public class ReqMessage_25090  extends Message {
	private final static short _TYPE  = 0x6202;
	private final static byte _ITEM_NO = (byte) 0x00;
	private final static byte _BLOCK_COUNT = (byte) 0x04;
	
	private final static int CARD_LEN = 16;
	
//	private final static byte _STATUS_FREE = (byte) 0x01;
//	private final static byte _STATUS_DRAW = (byte) 0x02;
//	private final static byte _STATUS_CARD = (byte) 0x03;
	
	private Block blockStatus;
	private Block blockColumns;
	private Block blockBalance;
	private Block blockCardNo;
	
	public ReqMessage_25090() {
		setHeader();
		setLength((short) 37);
		
		blockStatus = new Block(BLOCKDATATYPE.BYTE, (byte)0x01);
		blockColumns = new Block(BLOCKDATATYPE.BYTE, (byte)0x01);
		blockBalance = new Block(BLOCKDATATYPE.INT, (byte)0x01);
		blockCardNo = new Block(BLOCKDATATYPE.BYTE, (byte)0x10);
	}
	
	private void setHeader() {
		getHeader().setDataID(_TYPE);
		getHeader().setMsgType(MESSAGETYPE.PUSH);
		getHeader().setItemNo(_ITEM_NO);
		getHeader().setBlockCount(_BLOCK_COUNT);
	}
	
	public void setStatus(byte status) {
		blockStatus.setData(status);
	}
	
	public void setColumns(int[] columns) {
		
		if(columns.length <= 0) {
			blockColumns.setData((byte) 0xFF);
			blockColumns.setCount((byte) 0x01);
			return;
		}
		
		blockColumns.setCount((byte) columns.length);
		
		for(int column : columns) {
			blockColumns.setData((byte) column);
		}
	}
	
	public void setBalance(int balance) {
		blockBalance.setData(balance);
	}
	
	public void setCardNo(String cardNo) {
		if(cardNo.length() != CARD_LEN) {
			for (int i = 0; i < CARD_LEN; i++) {
				blockCardNo.setData((byte) 0x00);
			}
			return;
		}
		
		for (byte b : cardNo.getBytes()) {
			blockCardNo.setData(b);
		}
	}

	@Override
	public byte[] toBytes() {
		
		return CommonUtils.concatAll(commonBytes(), blockStatus.toBytes(), 
				blockColumns.toBytes(), blockBalance.toBytes(), blockCardNo.toBytes());
//		return convertTo(CommonUtils.concatAll(commonBytes(), blockStatus.toBytes(), 
//				blockColumns.toBytes(), blockBalance.toBytes(), blockCardNo.toBytes()));
		
//		byte[] msg = new byte[commonBytes().length + block.toBytes().length];
//		System.arraycopy(commonBytes(), 0, msg, 0, commonBytes().length);
//		System.arraycopy(block.toBytes(), 0, msg, 0, block.toBytes().length);
//		
//		return msg;
	}

}
