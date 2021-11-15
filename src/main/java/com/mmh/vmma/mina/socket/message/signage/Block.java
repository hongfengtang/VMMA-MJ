/**
 * 
 */
package com.mmh.vmma.mina.socket.message.signage;

import java.util.ArrayList;
import java.util.List;

import com.mmh.vmma.utils.CommonUtils;

/**
 * @author hongftan
 *
 */
public class Block {
	private final static byte _BYTE = (byte) 0x01;
	private final static byte _SHORT = (byte) 0x02;
	private final static byte _INT = (byte) 0x04;
	private final static byte _STRING = (byte) 0x08;
	private final static byte _BCD = (byte) 0x10;
	
	private byte type;
	private byte count;
	private List<Number> data;
	
	public Block() {
		data = new ArrayList<Number>();
	}
	
	public Block(BLOCKDATATYPE type, byte count) {
		data = new ArrayList<Number>();
		setType(type);
		this.count = count;
	}
	
	public void setType(byte type) {
		this.type = type;
	}

	public void setType(BLOCKDATATYPE type) {
		switch(type) {
		case BYTE:
			this.type = _BYTE;
			break;
		case SHORT:
			this.type = _SHORT;
			break;
		case INT:
			this.type = _INT;
			break;
		case STRING:
			this.type = _STRING;
			break;
		case BCD:
			this.type = _BCD;
			break;
		default:
			this.type = _BYTE;
		}
	}
	
	public BLOCKDATATYPE getType() {
		switch(type) {
		case _BYTE:
			return BLOCKDATATYPE.BYTE;
		case _SHORT:
			return BLOCKDATATYPE.SHORT;
		case _INT:
			return BLOCKDATATYPE.INT;
		case _STRING:
			return BLOCKDATATYPE.STRING;
		case _BCD:
			return BLOCKDATATYPE.BCD;
		default:
			return BLOCKDATATYPE.NONE;
		}
		
	}
	
	public void setCount(byte count) {
		this.count = count;
	}
	
	public void setCount(Number count) {
		this.count = count.byteValue();
	}
	
	public byte getCount() {
		return count;
	}
	
	public void setData(Number e) {
		this.data.add(e);
	}
	
	public void setData(List<Number> numbers) {
		this.data.addAll(numbers);
	}
	
	public Object getDataAll() {
		
		if((data == null) || (data.size() <= 0)) {
			return null;
		}
		
		if (count == 1) {
			return data.get(0);
		}
		return data;
	}
	
	public Number getData(int index) {
		if((data == null) || (Integer.valueOf(count) <= index) || (data.size() <= index)) {
			return null;
		}

		return data.get(index);
	}
	
	private int length() {
		int len = 2;
		switch(type) {
		case _BYTE:
			len = len + Integer.valueOf(count);
			break;
		case _SHORT:
			len = len + Integer.valueOf(count) * 2;
			break;
		case _INT:
			len = len + Integer.valueOf(count) * 4;
			break;
		case _STRING:
			len = len + Integer.valueOf(count);
			break;
		case _BCD:
			len = len + Integer.valueOf(count) * 4;
			break;
		default:
			len = len + Integer.valueOf(count) * 0;
			break;
		}
		return len;

	}
	
	public byte[] toBytes() {
		int i = 2;
		byte[] self = new byte[length()];
		
		self[0] = type;
		self[1] = count;
		
		switch(type) {
		case _BYTE:
			for(Number b : data) {
				self[i++] = b.byteValue();
			}
			break;
		case _SHORT:
			for(Number b : data) {
				byte[] d = CommonUtils.shortToByteArray(b.shortValue());
				self[i++] = d[0];
				self[i++] = d[1];
			}
		case _INT:
			for(Number b : data) {
				byte[] d = CommonUtils.intByteArray(b.intValue());
				self[i++] = d[0];
				self[i++] = d[1];
				self[i++] = d[2];
				self[i++] = d[3];
			}
			break;
		case _STRING:
			for(Number b : data) {
				self[i++] = b.byteValue();
			}
			break;
		case _BCD:
			for(Number b : data) {
				byte[] d = CommonUtils.intByteArray(b.intValue());
				self[i++] = d[0];
				self[i++] = d[1];
				self[i++] = d[2];
				self[i++] = d[3];
			}
			break;
		default:
			for(Number b : data) {
				self[i++] = b.byteValue();
			}
		}
		return self;


	}
	
}
