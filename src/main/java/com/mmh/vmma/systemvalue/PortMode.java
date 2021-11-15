/**
 * 
 */
package com.mmh.vmma.systemvalue;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class PortMode {
	//連接模式 0 ﹣ 串口；1 ﹣ TCP/IP Client; 2 - TCP/IP Server
	private int mode;
	
	//串品設置 ﹣ mode = 1時有效
	private String name;
	private String baudRate;
	private String dataBites;
	private String parity;
	private String startBits;
	private String stopBits;
	private String flowControl;
	
	//TCP/IP時有效
	private String address;
	private String portNumber;	
}
