/**
 * 
 */
package com.mmh.vmma.ui.common;

import org.springframework.stereotype.Component;

import com.mmh.vmma.controlcenter.model.response.ResDeviceInit;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Component
@Data
public class GlobalData {
	private String custoerName;
	private String terminalId;
	private String ccBaseUrl;
	
	private ResDeviceInit deviceInit;
	private String systemToken;
	
	public ResDeviceInit getDeviceInit() {
		if (deviceInit == null) {
			deviceInit = new ResDeviceInit();
		}
		
		return deviceInit;
	}
}
