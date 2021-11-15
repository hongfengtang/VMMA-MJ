/**
 * 
 */
package com.mmh.vmma.systemvalue;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class VMConfiguration {

	private String customer;
	private String terminalid;
	
	private List<PortMode> portModes;
	
	public List<PortMode> getPortModes(){
		if(portModes == null) {
			portModes = new ArrayList<PortMode>();
		}
		return portModes;
	}
}
