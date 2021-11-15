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
 */
@Data
public class ReqActions {
	private String terminalId;
	private String datetime;
	private String token;		//user token
	private List<DataField> data;
	
	public List<DataField> getData() {
		if(data == null) {
			data = new ArrayList<DataField>();
		}
		
		return data;
	}
	@Data
	public class DataField {
		private String ActionId;
		private String ActionResult;
	}
}
