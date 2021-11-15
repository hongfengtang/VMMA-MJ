/**
 * 
 */
package com.mmh.vmma.mina.socket.signage.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.mina.socket.signage.SignageIFClient;
import com.mmh.vmma.mina.socket.signage.flow.FlowFactory;

/**
 * @author hongftan
 * 發送消息到VM隊列
 */
@Component
public class RequestQueue {
	
	private final Logger logger = LoggerFactory.getLogger(RequestQueue.class);
	
	private LinkedList<FlowFactory> flows = new LinkedList<FlowFactory>();
	
	@Autowired
	SignageIFClient signage;
	@Autowired
	ResponseQueue responseQueue;
	
	private ReentrantLock lock = new ReentrantLock(true);
	
	private boolean started = true;
	
	
	@PostConstruct
	private void init() {
		started = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("啟動Request Queue");
				while(started) {
					if(flows.size() <= 0) {
						try {
							responseQueue.clear();
							Thread.sleep(50);
							continue;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					logger.debug("Request Queue發現請求。");
					pop().start(signage, responseQueue);
				}
				logger.info("Request Queue退出");
			}
			
		}).start();
		
	}
	
	@PreDestroy
	private void destory() {
		flows.clear();
		started = false;
	}
	
	public List<FlowFactory> getFlows() {
		if(flows == null) {
			flows = new LinkedList<FlowFactory>();
		}
		return flows;
	}
	
	public FlowFactory getFirst() {
		return flows.getFirst();
	}
	
	public void push(FlowFactory flow) {
		lock.lock();
		flows.addLast(flow);
		lock.unlock();
	}
	
	public FlowFactory pop() {
		
		if(flows.size() <= 0) {
			return null;
		}
		
		lock.lock();
		FlowFactory ff = flows.removeFirst();
		lock.unlock();
		
		return ff;
	}
}
