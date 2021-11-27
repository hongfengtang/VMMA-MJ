/**
 * 
 */
package com.mmh.vmma.mina.socket.signage.flow;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmh.vmma.mina.socket.message.signage.MESSAGETYPE;
import com.mmh.vmma.mina.socket.message.signage.Message;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_10343;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_16741;
import com.mmh.vmma.mina.socket.message.signage.body.request.ReqMessage_10246;
import com.mmh.vmma.mina.socket.message.signage.body.request.ReqMessage_16650;
import com.mmh.vmma.mina.socket.message.signage.body.request.ReqMessage_25089;
import com.mmh.vmma.mina.socket.message.signage.body.request.ReqMessage_25090;
import com.mmh.vmma.mina.socket.message.signage.body.response.ResMessage_16650;
import com.mmh.vmma.mina.socket.signage.ISignageCallBack;
import com.mmh.vmma.mina.socket.signage.SignageIFClient;
import com.mmh.vmma.mina.socket.signage.queue.ResponseQueue;
import com.mmh.vmma.ui.common.CODES;
import com.mmh.vmma.ui.common.ResponseMessage;

/**
 * @author hongftan
 * 出貨流程
 */
public class Vendout extends FlowFactory{
	private final static Logger logger = LoggerFactory.getLogger(Vendout.class);
	private final static int RETRYTIMES = 2;
	private enum VENDOUT_STATUS {
		SUCCESS, FAILED, RETRY
	}
	
	private final static int STEPS = 4;
	
	public Vendout(String id, int column, int count, ISignageCallBack callBack) {
		
		setId(id);
		setCallBack(callBack);
		setType(ACTION_TYPE.VENDOUT);
		setCount(count);
		setSteps(STEPS);
		setStatus(ACTION_STATUS.READY);
		
		initSteps(column);
	}
	
	private void initSteps(int column) {
		Action vmState = new Action();
		vmState.setAction(new ReqMessage_16650(MESSAGETYPE.GET));
		vmState.setStatus(ACTION_STATUS.READY);
		getActions().add(vmState);

		Action vmRefund = new Action();
		vmRefund.setAction(new ReqMessage_25089());
		vmRefund.setStatus(ACTION_STATUS.READY);
		getActions().add(vmRefund);
		
		Action vmSaleSetting = new Action();
		ReqMessage_25090 saleSeting= new ReqMessage_25090();
		saleSeting.setStatus((byte) 0x03);
		saleSeting.setColumns(new int[] {column});
		saleSeting.setBalance(0);
		saleSeting.setCardNo(String.format("%016d", 0));
		
		vmSaleSetting.setAction(saleSeting);
		vmSaleSetting.setStatus(ACTION_STATUS.READY);
		getActions().add(vmSaleSetting);

		Action vmVendout = new Action();
		ReqMessage_10246 vendout= new ReqMessage_10246();
		vendout.setColumnNo((byte)column);
		
		vmVendout.setAction(vendout);
		vmVendout.setStatus(ACTION_STATUS.READY);
		getActions().add(vmVendout);
	}

	public ACTION_STATUS getCurrentStatus() {
		return getActions().get(this.getCurrentStep()).getStatus();
	}


	@Override
	public boolean start(SignageIFClient signage, ResponseQueue responseQ) {
		updateCallback(CODES.PROV_VM_START, 0, getId());
		for (int i = 0; i < getCount(); i++) {	//共取藥次數
			updateCallback(CODES.PROV_VM_PROCESS, i+1, "");
			int retryTimes = 0;
//			while(retryTimes < RETRYTIMES) {
			while(true) {
				retryTimes++;
				logger.debug("取第[{}]包藥的，第[{}]次嘗試取貨", i + 1, retryTimes);
				
				VENDOUT_STATUS status = vendoutOneLoop(signage, responseQ, i);
				if(status == VENDOUT_STATUS.SUCCESS) {	//取貨成功，無需重試
					break;
				}else if(status == VENDOUT_STATUS.FAILED) {	//取貨失敗，終止取貨
					return false;
				}else if((status == VENDOUT_STATUS.RETRY) && (retryTimes >= RETRYTIMES)) {  //重試取貨失敗
					updateCallback(CODES.ERR_VM_NO_RESP, i+1, "販賣機取貨失敗，已達到重試次數。");
					return false;
				}else {
					updateCallback(CODES.ERR_VM_NO_RESP, i+1, "販賣機取貨失敗，無法識別返回碼。");
					return false;
				}
			}
		}
		updateCallback(CODES.SUCCESS, getCount(), "");
		return true;
	}
	
	private VENDOUT_STATUS vendoutOneLoop(SignageIFClient signage, ResponseQueue responseQ, int loop) {
		Message msg = null;
		boolean runCompleted = false;
		setStatus(ACTION_STATUS.PROCESSING);
		Date sendMsgTime = null;
		try {
			//查詢販賣機狀態
			logger.info("STEP1. 查詢販賣機狀態。");
			setCurrentStep(0);
			Action vmState = getActions().get(0);
			vmState.setStatus(ACTION_STATUS.PROCESSING);
			//發送消息
			msg = (Message)vmState.getAction();
			msg.updateIndexId();
			logger.trace("發送消息:[{}]", msg);
			signage.send(msg.toBytes());
			sendMsgTime = new Date();
			//等待返回消息
			while (!runCompleted) {
				if((new Date()).getTime() - sendMsgTime.getTime() > 10000){
					setStatus(ACTION_STATUS.FAILED);
					vmState.setStatus(ACTION_STATUS.FAILED);
					logger.error("STEP1. 查詢販賣機狀態時，超過[10]秒沒有返回。");
					updateCallback(CODES.ERR_VM_NO_RESP, loop+1, "查詢販賣機狀態無迴應");
					return VENDOUT_STATUS.FAILED;
				}
				
				Message response = responseQ.pop();
				//沒有近回消息
				if(response == null)
				{
					Thread.sleep(100);
					continue;
				}
				//返回消息不是16650迴應信息
				if(response.getHeader().getDataID() != 16650) {
					continue;
				}
				
				//返回消息不是當前請求迴應
				if(response.getHeader().getIndexId() != msg.getHeader().getIndexId()) {
					continue;
				}
				
				byte saleState = (byte)((ResMessage_16650)response).getBlock().getDataAll();
				if((saleState & 0x03) == 0x00) {
					logger.info("販賣機當前狀態為不可銷售狀態[{}], 將終止取貨。", saleState);
					updateCallback(CODES.ERR_VM_STATUS, loop+1, "藥品櫃當前狀態為不可出貨狀態");
					return VENDOUT_STATUS.FAILED;
				}
				
				vmState.setStatus(ACTION_STATUS.COMPLETION);
				runCompleted = true;
			}
			
			//設置退幣動作
			runCompleted = false;
			logger.info("STEP2. 設置退幣動作。");
			setCurrentStep(1);
			Action vmRefund = getActions().get(1);
			vmRefund.setStatus(ACTION_STATUS.PROCESSING);
			//發送消息
			msg = (Message)vmRefund.getAction();
			msg.updateIndexId();
			logger.trace("發送消息:[{}]", msg);
			signage.send(msg.toBytes());
			sendMsgTime = new Date();
			
			//等待返回消息
			byte resRefundCode = 0x00; //返回消息確認，00000011: 從低到高：發送命令返回+確認結果返回
			while (!runCompleted) {
				if((new Date()).getTime() - sendMsgTime.getTime() > 10000){
					setStatus(ACTION_STATUS.FAILED);
					vmRefund.setStatus(ACTION_STATUS.FAILED);
					logger.error("STEP2. 設置退幣動作時，超過[10]秒沒有返回。");
					updateCallback(CODES.ERR_VM_NO_RESP, loop+1, "設置退幣動作無迴應");
					return VENDOUT_STATUS.FAILED;
				}
				Message response = responseQ.pop();
				//沒有近回消息
				if(response == null)
				{
					Thread.sleep(100);
					continue;
				}
				
				//請求消息返回
				if((response.getHeader().getDataID() == 25089) && (response.getHeader().getIndexId() == msg.getHeader().getIndexId())) {
					sendMsgTime = new Date();
					resRefundCode = (byte) (resRefundCode | 0x01);
				}
				
				//退幣事件返回
				if(response.getHeader().getDataID() == 24939) {
					sendMsgTime = new Date();
					resRefundCode = (byte) (resRefundCode | 0x02);
				}
				
				//判斷兩個事件是否都已經返回
				if((resRefundCode & 0x03) != 0x03) { //兩個事件沒有全部返回，繼續查找
					continue;
				}

				vmRefund.setStatus(ACTION_STATUS.COMPLETION);
				runCompleted = true;
			}
			
			//設置取貨動作
			runCompleted = false;
			logger.info("STEP3. 設置取貨動作。");
			setCurrentStep(2);
			Action vmSaleSetting = getActions().get(2);
			vmSaleSetting.setStatus(ACTION_STATUS.PROCESSING);
			//發送消息
			msg = (Message)vmSaleSetting.getAction();
			msg.updateIndexId();
			logger.trace("發送消息:[{}]", msg);
			signage.send(msg.toBytes());
			sendMsgTime = new Date();
		
			//等待返回消息
			byte resSaleSettingCode = 0x00; //返回消息確認，00000011: 從低到高：發送命令返回+確認結果返回
			while (!runCompleted) {
				if((new Date()).getTime() - sendMsgTime.getTime() > 10000){
					setStatus(ACTION_STATUS.FAILED);
					vmSaleSetting.setStatus(ACTION_STATUS.FAILED);
					logger.error("STEP3. 設置取貨動作時，超過[10]秒沒有返回。");
					updateCallback(CODES.ERR_VM_NO_RESP, loop+1, "設置取貨動作無迴應");
					return VENDOUT_STATUS.FAILED;
				}
				Message response = responseQ.pop();
				//沒有近回消息
				if(response == null)
				{
					Thread.sleep(100);
					continue;
				}
				
				//請求消息返回
				if((response.getHeader().getDataID() == 25090) &&(response.getHeader().getIndexId() == msg.getHeader().getIndexId())) {
					sendMsgTime = new Date();
					resSaleSettingCode = (byte) (resSaleSettingCode | 0x01);
				}
				
				//退幣事件返回
				if(response.getHeader().getDataID() == 16741) {
					byte saleState = (byte)((EvtMessage_16741)response).getBlock().getDataAll();
					if((saleState & 0x02) == 0x00) {
						logger.debug("販賣機不可取貨，狀態為: [{}].", saleState);
						continue;
					}
					sendMsgTime = new Date();
					resSaleSettingCode = (byte) (resSaleSettingCode | 0x02);
				}
				
				//判斷兩個事件是否都已經返回
				if((resSaleSettingCode & 0x03) != 0x03) { //兩個事件沒有全部返回，繼續查找
					continue;
				}

				vmSaleSetting.setStatus(ACTION_STATUS.COMPLETION);
				runCompleted = true;
			}
			

			//取貨
			runCompleted = false;
			logger.info("STEP4. 開始取貨。");
			
			//2021-11-27 設置取貨動作與開始取貨動作之間增加1000ms延遲 - start
			Thread.sleep(1000);
			// - end
			
			setCurrentStep(3);
			Action vmVendout = getActions().get(3);
			vmVendout.setStatus(ACTION_STATUS.PROCESSING);
			//發送消息
			msg = (Message)vmVendout.getAction();
			msg.updateIndexId();
			logger.trace("發送消息:[{}]", msg);
			signage.send(msg.toBytes());
			sendMsgTime = new Date();
		
			//等待返回消息
//			int reSend = 0;
			int countVendoutStatus = 0;
			byte resVendoutgCode = 0x00; //返回消息確認，00000011: 從低到高：發送命令返回，販賣報告，結算結果，出貨結果，狀態恢復
			while (!runCompleted) {
				if((new Date()).getTime() - sendMsgTime.getTime() > 30000){
					setStatus(ACTION_STATUS.FAILED);
					vmVendout.setStatus(ACTION_STATUS.FAILED);
					logger.error("STEP4. 取貨時，超過[30]秒沒有返回。");
					updateCallback(CODES.ERR_VM_NO_RESP, loop+1, "取貨動作無迴應");
					return VENDOUT_STATUS.FAILED;
				}
				Message response = responseQ.pop();
				//沒有近回消息
				if(response == null)
				{
					Thread.sleep(100);
					continue;
				}
				
				//請求消息返回
				if((response.getHeader().getDataID() == 10246) &&(response.getHeader().getIndexId() == msg.getHeader().getIndexId())) {
					if((resVendoutgCode & 0x01) == 0x00) {
						sendMsgTime = new Date();
					}
					resVendoutgCode = (byte) (resVendoutgCode | 0x01);
				}
				
				//販賣機報告事件返回
				if(response.getHeader().getDataID() == 10363) {
//					byte vmReport = (byte)((EvtMessage_10363)response).getBlock().getDataAll();
//					if((vmReport & 0x02) == 0x00) {
//						logger.debug("販賣機不可取貨，狀態為: [{}].", vmReport);
//						continue;
//					}
					if((resVendoutgCode & 0x01) == 0x02) {
						sendMsgTime = new Date();
					}
					resVendoutgCode = (byte) (resVendoutgCode | 0x02);
				}

				//結算結果事件返回
				if(response.getHeader().getDataID() == 24934) {
//					byte statementResult = (byte)((EvtMessage_24934)response).getBlock().getDataAll();
//					if((statementResult & 0x02) == 0x00) {
//						logger.debug("販賣機不可取貨，狀態為: [{}].", statementResult);
//						continue;
//					}
					if((resVendoutgCode & 0x04) == 0x00) {
						sendMsgTime = new Date();
					}
					resVendoutgCode = (byte) (resVendoutgCode | 0x04);
				}
				
				//出貨結果事件返回
				if(response.getHeader().getDataID() == 10343) {
					byte vendoutResult = (byte)((EvtMessage_10343)response).getBlock().getDataAll();
					if((vendoutResult & 0x01) == 0x00) {
						logger.debug("販賣機出貨失敗，狀態為: [{}].", vendoutResult);
						//出貨失敗處理
						setStatus(ACTION_STATUS.FAILED);
						vmSaleSetting.setStatus(ACTION_STATUS.FAILED);
						runCompleted = true;
						updateCallback(CODES.ERR_VM_STATUS, loop+1, "販賣機出貨失敗，狀態為: [" + vendoutResult + "].");
						return VENDOUT_STATUS.FAILED;
//						continue;
					}
					if((resVendoutgCode & 0x08) == 0x00) {
						sendMsgTime = new Date();
					}
					resVendoutgCode = (byte) (resVendoutgCode | 0x08);
				}
				
				//狀態恢復事件返回
				if(response.getHeader().getDataID() == 16741) {
					byte vmCurrentState = (byte)((EvtMessage_16741)response).getBlock().getDataAll();
					if((vmCurrentState & 0x01) == 0x00) {
						logger.debug("販賣機狀態未恢復，狀態為: [{}].", vmCurrentState);
						if(vmCurrentState == 0x02) {
							countVendoutStatus++;
						}
						//re-send message
						if(countVendoutStatus >= 3) {
							countVendoutStatus = 0;
							return VENDOUT_STATUS.RETRY;
						}
						continue;
					}
					if((resVendoutgCode & 0x10) == 0x00) {
						sendMsgTime = new Date();
					}
					resVendoutgCode = (byte) (resVendoutgCode | 0x10);
				}
				
				logger.debug("resVendoutgCode = [{}]", resVendoutgCode);
				//判斷兩個事件是否都已經返回
				if((resVendoutgCode & 0x1F) != 0x1F) { //兩個事件沒有全部返回，繼續查找
					continue;
				}

				vmSaleSetting.setStatus(ACTION_STATUS.COMPLETION);
				runCompleted = true;
			}

		}catch(Exception e) {
			logger.error("執行命令[{}]報錯", msg, e);
			updateCallback(CODES.ERR_VM_NO_RESP, loop+1, "系統執行命令報錯");
			return VENDOUT_STATUS.FAILED;
		}
		return VENDOUT_STATUS.SUCCESS;
	}
	
	
	private void updateCallback(int code, int loop, String info) {
		if(getCallBack() != null) {
			ResponseMessage message = new ResponseMessage();
			message.setCode(code);
			message.setLoop(loop);
			message.setMessage(info);
			getCallBack().update(message);
		}


	}
}
