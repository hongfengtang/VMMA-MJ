/**
 * 
 */
package com.mmh.vmma.ui.common;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public class ResponseMessage {
	private int code = -1;	//返回狀態碼
	private int loop = 0;	//取第幾顆藥報錯
	private String message = "";	//錯誤消息
}
