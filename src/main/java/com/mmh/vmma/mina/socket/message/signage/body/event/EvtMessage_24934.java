/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage.body.event;

import java.util.ArrayList;
import java.util.List;

import com.mmh.vmma.mina.socket.message.signage.Block;
import com.mmh.vmma.mina.socket.message.signage.Message;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 * 出货结果的事件(事件)
 * 自动售货机在商品出货时通知出货状态(出货成功/出货异常)。 出货状态如下。
 * 贩卖状态 值 
 * 出货成功 1 
 * 出货异常 0
 * 例如： 02 0A 00 FF FF 28 67 00 00 01 01 01 01
 */
public class EvtMessage_24934 extends Message{
	private List<Block> blocks = new ArrayList<Block>();
	
	public EvtMessage_24934(byte[] bytes){
		short dataId = (byte)bytes[6];
		dataId += (byte)bytes[5] << 8;
		
		getHeader().setObjId(bytes[3]);
		getHeader().setIndexId(bytes[4]); 
		getHeader().setDataID(dataId);
		getHeader().setMsgType(bytes[7]);
		getHeader().setItemNo(bytes[8]);
		getHeader().setBlockCount(bytes[9]);

		Block block00 = new Block();
		block00.setType(bytes[10]);
		block00.setCount(bytes[11]);
		block00.setData(bytes[12]);
		block00.setData(bytes[13]);
		blocks.add(block00);
		
		Block block01 = new Block();
		block01.setType(bytes[14]);
		block01.setCount(bytes[15]);
		byte[] cashAmountBytes = {bytes[16], bytes[17]};
		short cashAmount = CommonUtils.bytesToShortBE(cashAmountBytes);
		block01.setData(cashAmount);
		byte[] cardAmountBytes = {bytes[18], bytes[19]};
		short cardAmount = CommonUtils.bytesToShortBE(cardAmountBytes);
		block01.setData(cardAmount);
		blocks.add(block01);
		
		Block block02 = new Block();
		block02.setType(bytes[20]);
		block02.setCount(bytes[21]);
		int id = 0;
		byte[] idBytes = {bytes[22], bytes[23], bytes[24], bytes[25]};
		id = CommonUtils.bytesToIntBE(idBytes);
		block02.setData(id);
		blocks.add(block02);
		
		Block block03 = new Block();
		block03.setType(bytes[26]);
		block03.setCount(bytes[27]);
		block03.setData(bytes[28]);
		blocks.add(block03);
		
		Block block04 = new Block();
		block04.setType(bytes[29]);
		block04.setCount(bytes[30]);
		block04.setData(bytes[31]);
		blocks.add(block04);
		
		Block block05 = new Block();
		block05.setType(bytes[32]);
		block05.setCount(bytes[33]);
		block05.setData(bytes[34]);
		blocks.add(block05);
		
		Block block06 = new Block();
		block06.setType(bytes[35]);
		block06.setCount(bytes[36]);
		int cardBlance = 0;
		byte[] icardBlanceBytes = {bytes[37], bytes[38], bytes[39], bytes[40]};
		cardBlance = CommonUtils.bytesToIntBE(icardBlanceBytes);
		block06.setData(cardBlance);
		blocks.add(block06);
		
		Block block07 = new Block();
		block07.setType(bytes[41]);
		block07.setCount(bytes[42]);
		int cardBfBlance = 0;
		byte[] cardBfBlanceBytes = {bytes[43], bytes[44], bytes[45], bytes[46]};
		cardBfBlance = CommonUtils.bytesToIntBE(cardBfBlanceBytes);
		block07.setData(cardBfBlance);
		blocks.add(block07);
		
		Block block08 = new Block();
		block08.setType(bytes[47]);
		block08.setCount(bytes[48]);
		int cardAfBlance = 0;
		byte[] cardAfBlanceBytes = {bytes[49], bytes[50], bytes[51], bytes[52]};
		cardAfBlance = CommonUtils.bytesToIntBE(cardAfBlanceBytes);
		block08.setData(cardAfBlance);
		blocks.add(block08);
		
		Block block09 = new Block();
		block09.setType(bytes[53]);
		block09.setCount(bytes[54]);
		for(int i = 55; i < 71; i++) {
			block09.setData(bytes[i]);
		}
		blocks.add(block09);
	}
	
	public Block getBlock(int index) {
		if(blocks == null) {
			blocks = new ArrayList<Block>();
			return null;
		}
		
		if(blocks.size() <= index) {
			return null;
		}
		
		return blocks.get(index);
	}

	@Override
	public byte[] toBytes() {
		
		
		return CommonUtils.concatAll(commonBytes(), blocksBytes(blocks.size() - 1));// block.toBytes());
		
	}
	
	private byte[] blocksBytes(int index) {
		if(index == 0)
			return blocks.get(0).toBytes();
		
		return CommonUtils.concatAll(blocksBytes(index - 1), blocks.get(index).toBytes());
	}
}
