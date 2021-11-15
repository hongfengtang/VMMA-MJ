/**
 * 
 */
package com.mmh.vmma.controlcenter.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hongftan
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ResDeviceInit extends ResEntity {
	private int reccount;
	private DataField data;
	
	@Data
	public class DataField {
		private String id;
		private String token;
		private long expireddate;
	}
}

