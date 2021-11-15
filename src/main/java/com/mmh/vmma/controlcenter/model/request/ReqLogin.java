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
public class ReqLogin {
	private String clientId;
	private String datetime;
	private DataField data;
	public DataField getData() {
		if (data == null) {
			data = new DataField();
		}
		return data;
	}
	@Data
	public class DataField {
		private String loginType;
		private String userName;
		private String encPassword;
	}
}
