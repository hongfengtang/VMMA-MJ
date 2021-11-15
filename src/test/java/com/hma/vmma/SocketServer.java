package com.hma.vmma;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class SocketServer extends IoHandlerAdapter {
	/** 30秒后超时 */
	private static final int IDELTIMEOUT = 15;
	/** 15秒发送一次心跳包 */
	private static final int HEARTBEATRATE = 15;
	/** 心跳包内容 */
	private static final String HEARTBEATREQUEST = "0x11";
	private static final String HEARTBEATRESPONSE = "0x12";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getSessionConfig().setReadBufferSize(1024);
//		acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE,
//				IDELTIMEOUT);
        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
//        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "ascii" ))));
        
        
//		KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
//		//下面注释掉的是自定义Handler方式
//		KeepAliveRequestTimeoutHandler heartBeatHandler = new 
//								KeepAliveRequestTimeoutHandlerImpl();
//		KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
//				IdleStatus.WRITER_IDLE, heartBeatHandler);
//		
////		KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
////				IdleStatus.WRITER_IDLE);
// 
//		//设置是否forward到下一个filter
//		heartBeat.setForwardEvent(false);
//		//设置心跳频率
//		heartBeat.setRequestInterval(5);
//		heartBeat.setRequestTimeout(5);
// 
//		acceptor.getFilterChain().addLast("heartbeat", heartBeat);
        
        
        acceptor.setHandler(  new SocketServer() );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.WRITER_IDLE, 5 );
        acceptor.bind( new InetSocketAddress(20000) );

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("sessionCreated" + session.getId());
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("sessionOpened" + session.getId());
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] b1 = {0x02, 0x0A, 0x00, (byte) 0xFF, (byte) 0xFF, 0x41, 0x65, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01};
				byte[] b2 = {0x02, 0x44, 0x00, (byte) 0xFF, (byte) 0xFF, 0x61, 0x66, 0x00, 0x00, 0x0A, 0x01, 0x7D, 0x22, 0x22, 0x17, 0x7D, 
						0x22, 0x7D, 0x22, 0x00, 0x00, 0x00, 0x00, 0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 
						0x01, 0x22, 0x01, 0x01, 0x01, 0x04, 0x01, 0x01, 0x00, 0x00, 0x00, 0x04, 0x01, 0x01, 0x00, 0x00, 0x00, 
						0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x01, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
				byte[] b3 = {0x02, 0x41, 0x00, (byte) 0xFF, (byte) 0xFF, 0x28, 0x7B, 0x00, 0x00, 0x01, 0x01, 0x38, 0x00, 0x00, 0x00, 0x00, 
						0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x00, 
						0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x01, 0x01, 
						0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 
						0x00};
				
				int mod = 0;
				for(;;) {
					switch(mod % 3) {
					case 0:
						session.write(IoBuffer.wrap(b1));
						break;
					case 1:
						session.write(IoBuffer.wrap(b2));
						break;
					default:
						session.write(IoBuffer.wrap(b3));
						break;
					}
					mod++;
					if(mod >= 3) {
						mod = 0;
					}
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		super.sessionOpened(session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("sessionClosed" + session.getId());
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("sessionIdle" + session.getId());
		super.sessionIdle(session, status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("exceptionCaught" + session.getId());
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("messageReceived");
	       IoBuffer ioBuffer = (IoBuffer)message; 
	       byte[] b = new byte[ioBuffer.limit()]; 
	       ioBuffer.get(b);
		System.out.println("messageReceived" + session.getId());
		for(int i = 0; i < b.length; i++)
			System.out.print(b[i] + " ");
		System.out.println("");
//		super.messageReceived(session, message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("messageSent" + session.getId());
		super.messageSent(session, message);
	}
	
	
	@Override
	public void event(IoSession session, FilterEvent event) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("event" + session.getId() + event);
		super.event(session, event);
	}


	/**
	 * @ClassName KeepAliveMessageFactoryImpl
	 * @Description 内部类，实现KeepAliveMessageFactory（心跳工厂）
	 * @author cruise
	 *
	 */
	private static class KeepAliveMessageFactoryImpl implements
			KeepAliveMessageFactory {
 
		@Override
		public boolean isRequest(IoSession session, Object message) {
			System.out.println("请求心跳包信息: " + message);
			if (message.equals(HEARTBEATREQUEST))
				return true;
			return false;
		}
 
		@Override
		public boolean isResponse(IoSession session, Object message) {
//			LOG.info("响应心跳包信息: " + message);
//			if(message.equals(HEARTBEATRESPONSE))
//				return true;
			return false;
		}
 
		@Override
		public Object getRequest(IoSession session) {
			System.out.println("请求预设信息: " + HEARTBEATREQUEST);
			/** 返回预设语句 */
			return HEARTBEATREQUEST;
		}
 
		@Override
		public Object getResponse(IoSession session, Object request) {
			System.out.println("响应预设信息: " + HEARTBEATRESPONSE);
			/** 返回预设语句 */
			return HEARTBEATRESPONSE;
//			return null;
		}
 
	}
	
	/**
	 * 对应上面的注释
	 * KeepAliveFilter(heartBeatFactory,IdleStatus.BOTH_IDLE,heartBeatHandler)
	 * 心跳超时处理
	 * KeepAliveFilter 在没有收到心跳消息的响应时，会报告给的KeepAliveRequestTimeoutHandler。
	 * 默认的处理是 KeepAliveRequestTimeoutHandler.CLOSE
	 * （即如果不给handler参数，则会使用默认的从而Close这个Session）
	 * @author cruise
	 *
	 */
 
	private static class KeepAliveRequestTimeoutHandlerImpl implements
			KeepAliveRequestTimeoutHandler {

	
		@Override
		public void keepAliveRequestTimedOut(KeepAliveFilter filter,
				IoSession session) throws Exception {
			System.out.println("心跳超时！");
		}

	}


}
