/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage.body.response;

import com.mmh.vmma.mina.socket.message.signage.Message;
import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 * 选择货道号码信息的通知(PushData 函数)
 * 把被选中的货道 No.向自动售货机通知。 
 * 当货道的贩卖状态为可结算时,搬出由货道 No.指定的货道中的商品。 
 * 当自动贩卖机在待机中收到此事件的通知,该货道 No.的对应货道作为先选择商品。
 * 例如： 02 07 00 01 01 62 01 42 00 00
 */
public class ResMessage_25089 extends Message{
	
	public ResMessage_25089(byte[] bytes){
		short dataId = (byte)bytes[6];
		dataId += (byte)bytes[5] << 8;
		getHeader().setObjId(bytes[3]);
		getHeader().setIndexId(bytes[4]); 
		getHeader().setDataID(dataId);
		getHeader().setMsgType(bytes[7]);
		getHeader().setItemNo(bytes[8]);
		getHeader().setBlockCount(bytes[9]);
	}
	@Override
	public byte[] toBytes() {
		
		return CommonUtils.concatAll(commonBytes());
		
//		byte[] msg = new byte[commonBytes().length + block.toBytes().length];
//		System.arraycopy(commonBytes(), 0, msg, 0, commonBytes().length);
//		System.arraycopy(block.toBytes(), 0, msg, 0, block.toBytes().length);
//		
//		return msg;
	}
}
