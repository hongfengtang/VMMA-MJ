/**
 * 
 */
package com.mmh.vmma.mina.socket.signage.flow;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class Action {
	private Object action;
	private ACTION_STATUS status = ACTION_STATUS.NONE;
}
