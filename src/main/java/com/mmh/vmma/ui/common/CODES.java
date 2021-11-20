/**
 * 
 */
package com.mmh.vmma.ui.common;

/**
 * @author hongftan
 *
 */
public class CODES {
	
	public final static byte OBJECT_ID = (byte) 0x26;
	
	public final static int SUCCESS = 0;
	public final static int ERR_NO_CABINET = 1;
	public final static int ERR_CONNECT_CABINET = 2;
	public final static int ERR_NO_CENTER_CONTROL = 3;
	public final static int ERR_CONNECT_CENTER_CONTROL=4;

	public final static int ERR_CONNECT_VM=5;

	//販賣機取藥處理結果碼
	public final static int PROV_VM_START = 600;
	public final static int PROV_VM_PROCESS = 601;
	public final static int ERR_VM_STATUS = 501;
	public final static int ERR_VM_NO_GOOD = 502;
	public final static int ERR_VM_NO_RESP = 503;
	public final static int ERR_VM_COMMON = 504;
	
	public final static String TERM_TYPE_NORMAL = "0";
	public final static String TERM_TYPE_VM = "1";
	
	public final static int CARDID = 0;
	public final static int USERID = 1;
	public final static int FACEID = 2;
	public final static int FIGNERID = 3;
	public final static int MAKEY = 4;
	
	public final static String LOGIN_TYPE_MK = "id";
	public final static String LOGIN_TYPE_ID = "id";
	public final static String LOGIN_TYPE_CARD = "card";
	
	public enum TCP_CLIENT_STATUS{CONNECTING, CONNECTED, RECORRECTING, DISPOSING, DISPOSED, UNKNOW};
	
	public enum MESSAGE_LEVEL{NOTICATION, WARNING, ERROR};
}
