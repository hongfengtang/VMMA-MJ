/**
 * 
 */
package com.mmh.vmma.controlcenter.model.request;

import java.util.ArrayList;
import java.util.List;


import lombok.Data;

/**
 * @author hongftan
 * 
 * 中控接口：SYS015_tmedicines
 *
 */
@Data
public class ReqTMedicines {
	private String clientId;
	private String datetime;
	private String token;		//user token
	private DataField data;
//	private List<DataField> data;
	public DataField getData() {
		if (data == null) {
			data = new DataField();
		}
		return data;
	}

	@Data
	public static class DataField {
		private String terminalId;
		private String medicineId;
		private boolean photo;
	}

}
