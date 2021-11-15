/**
 * 
 */
package com.mmh.vmma.controlcenter.model;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class SqlEntity {
	private String userid;
	private String datetime;
	private String terminalid;
	private String action;
	private String token;
}
