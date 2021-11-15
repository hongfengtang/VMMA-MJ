/**
 * 
 */
package com.mmh.vmma.controlcenter.model.request;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class ReqDeviceInit {
	private String clientId;
	private String datetime;
	private String token;
	private DataField data;
	public DataField getData() {
		if (data == null) {
			data = new DataField();
		}
		return data;
	}
	
	@Data
	public class DataField {
		private String terminalId;
		private String termType;
		private String ipAddress;
		private String macAddress;
	}
}
