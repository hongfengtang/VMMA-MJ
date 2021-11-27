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
public class ReqLogout {
	private String clientId;
	private String datetime;
	private String token;
}
