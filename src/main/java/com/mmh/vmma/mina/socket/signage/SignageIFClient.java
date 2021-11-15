/**
 * 
 */
package com.mmh.vmma.mina.socket.signage;

import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mmh.vmma.mina.socket.message.signage.Message;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_10343;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_10363;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_16741;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_24934;
import com.mmh.vmma.mina.socket.message.signage.body.event.EvtMessage_24939;
import com.mmh.vmma.mina.socket.message.signage.body.response.ResMessage_10246;
import com.mmh.vmma.mina.socket.message.signage.body.response.ResMessage_16650;
import com.mmh.vmma.mina.socket.message.signage.body.response.ResMessage_25089;
import com.mmh.vmma.mina.socket.message.signage.body.response.ResMessage_25090;
import com.mmh.vmma.mina.socket.signage.queue.ResponseQueue;
import com.mmh.vmma.ui.common.CODES;

/**
 * 实现ignage I/F贩卖机接口连接功能
 * 
 * 通过Socket Client方式实现
 * 
 * @author hongftan
 *
 */
@Component
public class SignageIFClient extends IoHandlerAdapter{
	private final Logger logger = LoggerFactory.getLogger(SignageIFClient.class);
	private final int KEEP_CONNECTION_INTERVAL = 15;
	private final int CONNECT_TIMEOUT = 30000;
	private final int IDEL_TIMEOUT = 30000;	//空閒時間30秒
	private final int INTERVAL_START=1;
	private final int RECEIVE_BUFFER_SIZE = 1024;
	private final int SEND_BUFFER_SIZE = 1024;
	
	@Autowired
	ResponseQueue responseQueue;
	
	private NioSocketConnector ioConnector = null;
	private int reconnectInterval = INTERVAL_START;
	private CODES.TCP_CLIENT_STATUS status = CODES.TCP_CLIENT_STATUS.UNKNOW;
	
	public SignageIFClient() {
		
	}
	
	@PostConstruct
	private void init() {
		logger.debug("SignageIFClient - init");
		ioConnector = new NioSocketConnector();
		ioConnector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		
		//TODO 增加自定義filter, 用於處理消息包
		ioConnector.getFilterChain().addLast( "logger", new LoggingFilter() );
		ioConnector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new SignageCodecFactory()));
		ioConnector.getFilterChain().addLast( "keepconnection", new KeepConnectionFilter(KEEP_CONNECTION_INTERVAL));
		
		ioConnector.getSessionConfig().setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
		ioConnector.getSessionConfig().setSendBufferSize(SEND_BUFFER_SIZE);
		ioConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDEL_TIMEOUT);  //读写都空闲时间

		ioConnector.setHandler(this);
		
	}
	@PreDestroy
	private void destory() {
		logger.debug("destory");
		if(ioConnector.isActive()) {
			ioConnector.dispose();
		}
	}
	
	public void connect(String host, int port) {
		status = CODES.TCP_CLIENT_STATUS.CONNECTING;
		new Thread(new Runnable() {
			public void run() {
				logger.debug("設置signage連接主機:[{}], 端口:[{}].", host, port);
				ioConnector.setDefaultRemoteAddress(new InetSocketAddress(host, port));
				for (;;) {
					try {
						if(ioConnector.isActive()) {
							ioConnector.getManagedSessions().forEach((id, session) -> {
								session.closeOnFlush();
							});
							ioConnector.dispose();
						}
						logger.info("signage連接主機:[{}], 端口:[{}].", host, port);
						status = CODES.TCP_CLIENT_STATUS.CONNECTING;
						ConnectFuture future = ioConnector.connect();
						// 等待连接创建成功
						future.awaitUninterruptibly();
						
						
//						logger.debug("session: [{}]", future.getSession().isConnected());
//						// 获取会话
//						future.getSession().getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
//							
//							@Override
//							public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause)
//									throws Exception {
//								// TODO Auto-generated method stub
//								logger.error("**************exceptionCaught for [{}]", cause.getMessage());
//								super.exceptionCaught(nextFilter, session, cause);
//							}
//
//							@Override
//							public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {
//								logger.debug("*************************debug1");
//								status = CODES.TCP_CLIENT_STATUS.RECORRECTING;
//								for(;;){
//									try{
//										Thread.sleep(reconnectInterval * 1000);
//										reconnectInterval = reconnectInterval >= 30 ? reconnectInterval : reconnectInterval * 2;
//										logger.info("signage連接主機:[{}], 端口:[{}].", 
//												ioConnector.getDefaultRemoteAddress().getHostName(),
//												ioConnector.getDefaultRemoteAddress().getPort());
//										ConnectFuture future = ioConnector.connect();
//										future.awaitUninterruptibly();// 等待连接创建成功
//										if(future.getSession().isConnected()){
//											status = CODES.TCP_CLIENT_STATUS.CONNECTED;
//											logger.info("斷線重連[{}:{}]成功！", 
//													ioConnector.getDefaultRemoteAddress().getHostName(),
//													ioConnector.getDefaultRemoteAddress().getPort());
//											break;
//										}
//									}catch(Exception ex){
//										logger.info("重連服務器[{}:{}]失敗,{} 秒后重連......", 
//												ioConnector.getDefaultRemoteAddress().getHostName(),
//												ioConnector.getDefaultRemoteAddress().getPort(), reconnectInterval, ex);
//									}
//								}
//							}
//						});
						if(future.getSession().isConnected()) {
							logger.info("連接服務[{}:{}]成功。", host, port);
							status = CODES.TCP_CLIENT_STATUS.CONNECTED;
						}
						break;
					} catch (RuntimeIoException e) {
						logger.error("連接服務[{}:{}]失敗, {} 秒后重試......。", host, port, reconnectInterval, e);
						try {
							Thread.sleep(reconnectInterval * 1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						reconnectInterval = reconnectInterval >= 30 ? reconnectInterval : reconnectInterval * 2;
					}
				}
				
			}
		}).start();

	}
	
	public void send(Object obj) {
		if(ioConnector.isActive() && ioConnector.getManagedSessionCount() == 1) {
			ioConnector.getManagedSessions().forEach((id, session) -> {
				session.write(obj);
			});
		}
	}

	public void Close(){
		
		if(ioConnector.isActive()) {
			ioConnector.getManagedSessions().forEach((id, session) -> {
				session.closeOnFlush();
			});
			ioConnector.dispose();
		}
	}
	
	public CODES.TCP_CLIENT_STATUS getStatus() {
		if(ioConnector.isActive()) {
			return CODES.TCP_CLIENT_STATUS.CONNECTED;
		}else if(ioConnector.isDisposing()) {
			return CODES.TCP_CLIENT_STATUS.DISPOSING;
		}else if(ioConnector.isDisposed()){
			return CODES.TCP_CLIENT_STATUS.DISPOSED;
		}
		return status;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("sessionCreated");
		reconnectInterval = INTERVAL_START;
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^sessionOpened [{}]", ioConnector.getManagedSessions().size());
		// 获取会话
		session.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
			
			@Override
			public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause)
					throws Exception {
				// TODO Auto-generated method stub
				logger.error("**************exceptionCaught for [{}]", cause.getMessage());
				super.exceptionCaught(nextFilter, session, cause);
			}

			@Override
			public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {
				logger.debug("*************************debug1");
				status = CODES.TCP_CLIENT_STATUS.RECORRECTING;
				for(;;){
					try{
						Thread.sleep(reconnectInterval * 1000);
						reconnectInterval = reconnectInterval >= 30 ? reconnectInterval : reconnectInterval * 2;
						logger.info("signage連接主機:[{}], 端口:[{}].", 
								ioConnector.getDefaultRemoteAddress().getHostName(),
								ioConnector.getDefaultRemoteAddress().getPort());
						ConnectFuture future = ioConnector.connect();
						future.awaitUninterruptibly();// 等待连接创建成功
						if(future.getSession().isConnected()){
							status = CODES.TCP_CLIENT_STATUS.CONNECTED;
							logger.info("斷線重連[{}:{}]成功！", 
									ioConnector.getDefaultRemoteAddress().getHostName(),
									ioConnector.getDefaultRemoteAddress().getPort());
							break;
						}
					}catch(Exception ex){
						logger.info("重連服務器[{}:{}]失敗,{} 秒后重連......", 
								ioConnector.getDefaultRemoteAddress().getHostName(),
								ioConnector.getDefaultRemoteAddress().getPort(), reconnectInterval, ex);
					}
				}
			}
		});
		super.sessionOpened(session);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		logger.info("messageReceived");
		logger.info("receive message [{}]", message);
		pushResponseMessage(message);
		super.messageReceived(session, message);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("exceptionCaught for [{}]", cause.getMessage());
		super.exceptionCaught(session, cause);
	}
	
	private void pushResponseMessage(Object message) {
		
		byte[] bytes;
		if(message instanceof IoBuffer) {
			bytes = new byte[((IoBuffer) message).limit()];
			((IoBuffer) message).get(bytes);
		}else {
			bytes = (byte[]) message;
		}
		
		if(bytes.length < 10) {
			logger.error("收到消息錯誤。");
			return;
		}
		Message response;
		
		short dataId = (byte)bytes[6];
		dataId += (byte)bytes[5] << 8;
		
		
		switch(dataId) {
			case 10246:
				response = new ResMessage_10246(bytes);
				break;
			case 16650:
				response = new ResMessage_16650(bytes);
				break;
			case 25089:
				response = new ResMessage_25089(bytes);
				break;
			case 25090:
				response = new ResMessage_25090(bytes);
				break;
			case 10343:
				response = new EvtMessage_10343(bytes);
				break;
			case 10363:
				response = new EvtMessage_10363(bytes);
				break;
			case 16741:
				response = new EvtMessage_16741(bytes);
				break;
			case 24934:
				response = new EvtMessage_24934(bytes);
				break;
			case 24939:
				response = new EvtMessage_24939(bytes);
				break;
			default:
				return;
//				response = new ResMessage_00000(bytes);
//				break;
		}
		
		responseQueue.push(response);
	}
}
