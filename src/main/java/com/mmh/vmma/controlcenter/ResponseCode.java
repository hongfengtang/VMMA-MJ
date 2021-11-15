/**
 * 
 */
package com.mmh.vmma.controlcenter;

/**
 * @author hongftan
 *
 */
public class ResponseCode {
	public final static int OK = 20000;
	public final static int ERR_MISSPARAM = 40000;
	public final static int ERR_NOAUTH = 40100;
	public final static int ERR_NOACCOUNT = 40101;
	public final static int ERR_NOPASSWORD = 40102;
	public final static int ERR_NOTERMID = 40102;
	public final static int ERR_NOAPI = 40400;
	public final static int ERR_API = 42200;
	public final static int ERR_TOKEN = 50008;
	public final static int ERR_SYSTEM = 50000;
	
	public final static int ERR_UNIQUE = 20001;
	public final static int ERR_NOTABLE = 20002;
	public final static int ERR_INSERT = 20003;
	public final static int ERR_QUERY = 20004;
	public final static int ERR_SQL = 20004;
	
}
