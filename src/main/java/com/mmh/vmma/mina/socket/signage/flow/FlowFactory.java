/**
 * 
 */
package com.mmh.vmma.mina.socket.signage.flow;

import java.util.ArrayList;
import java.util.List;

import com.mmh.vmma.mina.socket.signage.ISignageCallBack;
import com.mmh.vmma.mina.socket.signage.SignageIFClient;
import com.mmh.vmma.mina.socket.signage.queue.ResponseQueue;

import lombok.Data;

/**
 * @author hongftan
 *
 */
@Data
public abstract class FlowFactory {
	private ACTION_TYPE type = ACTION_TYPE.NONE;			//流程類型，可能有：出貨，心跳，查詢動作
	private int count = 1;									//執行次數
	private int steps = 0;									//共幾步
	private int currentStep = 0;							//當前執行步驟
	private List<Action> actions;							//步驟動作
	private ACTION_STATUS status = ACTION_STATUS.NONE;		//當前執行狀態
	
	private ISignageCallBack callBack = null;				//囘調函數，用於運行結果返回
	
	public List<Action> getActions(){
		if(actions == null) {
			actions = new ArrayList<Action>();
		}
		return actions;
	}
	
	public abstract boolean start(SignageIFClient signage, ResponseQueue responseQ);
}
