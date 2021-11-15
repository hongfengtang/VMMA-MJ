/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage.body.event;

import java.util.ArrayList;
import java.util.List;

import com.mmh.vmma.mina.socket.message.signage.BLOCKDATATYPE;
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
public class EvtMessage_10363 extends Message{
	private Block block = new Block();
	
	public EvtMessage_10363(byte[] bytes){
		short dataId = (byte)bytes[6];
		dataId += (byte)bytes[5] << 8;
		getHeader().setObjId(bytes[3]);
		getHeader().setIndexId(bytes[4]); 
		getHeader().setDataID(dataId);
		getHeader().setMsgType(bytes[7]);
		getHeader().setItemNo(bytes[8]);
		getHeader().setBlockCount(bytes[9]);

		block.setType(bytes[10]);
		block.setCount(bytes[11]);
		int columns = bytes[11];
		List<Number> datas = new ArrayList<Number>();
		for(int i = 12; i < columns; i++) {
			datas.add(bytes[i]);
		}
		block.setData(datas);
	}
	
	public Block getBlock() {
		if(block == null) {
			block = new Block(BLOCKDATATYPE.BYTE, (byte)0x01);
		}
		return block;
	}

	@Override
	public byte[] toBytes() {
		
		return CommonUtils.concatAll(commonBytes(), block.toBytes());
		
	}
}
