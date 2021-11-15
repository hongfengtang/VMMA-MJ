/**
 * 
 */
package com.mmh.vmma.mina.socket.signage.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mmh.vmma.mina.socket.message.signage.Message;


/**
 * @author hongftan
 * VM返回消息隊列
 */
@Component
public class ResponseQueue {
	private final Logger logger = LoggerFactory.getLogger(ResponseQueue.class);
	
	private LinkedList<Message> messages = new LinkedList<Message>();
	private ReentrantLock lock = new ReentrantLock(true);
	
	@PreDestroy
	private void destory() {
		messages.clear();
		logger.debug("系統退出，清空返回消息隊列。");
	}
	
	public void clear() {
		lock.lock();
		messages.clear();
		lock.unlock();
		
	}
	public List<Message> getMessages() {
		if(messages == null) {
			messages = new LinkedList<Message>();
		}
		return messages;
	}
	
	public Message getFirst() {
		return messages.getFirst();
	}
	
	public void push(Message flow) {
		lock.lock();
		messages.addLast(flow);
		lock.unlock();
	}
	
	public Message pop() {
		
		if(messages.size() <= 0) {
			return null;
		}
		lock.lock();
		Message ff = messages.removeFirst();
		lock.unlock();
		
		return ff;
	}
}
