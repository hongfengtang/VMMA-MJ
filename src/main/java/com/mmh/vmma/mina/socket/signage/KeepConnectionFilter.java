/**
 * 
 */
package com.mmh.vmma.mina.socket.signage;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IoSession;

/**
 * @author hongftan
 *
 */
public class KeepConnectionFilter extends IoFilterAdapter {
	private final byte[] keepConnection = {0x02, 0x07, 0x00, 0x01, 0x01, 0x41, 0x11, 0x01, 0x00, 0x00};
	private volatile int requestInterval;
	
	private Thread thread = null;
	
	public KeepConnectionFilter(int keepConnectionRequestInterval) {
        if (keepConnectionRequestInterval <= 0) {
            throw new IllegalArgumentException("keepAliveRequestInterval must be a positive integer: "
                    + keepConnectionRequestInterval);
        }
        
        requestInterval = keepConnectionRequestInterval;

	}

	public int getRequestInterval() {
		return requestInterval;
	}
	
	public void setRequestInterval(int keepConnectionRequestInterval) {
		if(keepConnectionRequestInterval <= 0) {
			throw new IllegalArgumentException("keepConnectionRequestInterval must be a positive integer: "
                    + keepConnectionRequestInterval);
		}
		
		requestInterval = keepConnectionRequestInterval;
	}
	
	@Override
	public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
		if(thread == null) {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					for(;;) {
						session.write(IoBuffer.wrap(keepConnection));
						try {
							Thread.sleep(requestInterval * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			});
		}
		
		if(!thread.isAlive()) {
			thread.start();
		}
		super.sessionOpened(nextFilter, session);
	}

	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
		if((thread != null) && (thread.getState() == Thread.State.RUNNABLE)) {
			thread.interrupt();
		}
		super.sessionClosed(nextFilter, session);
	}

	@Override
	public void onPreAdd(IoFilterChain parent, String name, NextFilter nextFilter) throws Exception {
		if(parent.contains(this)) {
			throw new IllegalArgumentException("You can't add the same filter instance more than once. "
                    + "Create another instance and add it.");
		}
		
		super.onPreAdd(parent, name, nextFilter);
	}

	@Override
	public void onPostRemove(IoFilterChain parent, String name, NextFilter nextFilter) throws Exception {
		if((thread != null) && (thread.getState() == Thread.State.RUNNABLE)) {
			thread.interrupt();
		}
		super.onPostRemove(parent, name, nextFilter);
	}
	
	
}
