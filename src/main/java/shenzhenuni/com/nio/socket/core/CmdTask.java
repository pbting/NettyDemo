/**
 * All rights reserved. This material is confidential and proprietary to 7ROAD SQ team.
 */
package shenzhenuni.com.nio.socket.core;

import java.nio.channels.SocketChannel;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;

/**
 * <pre>
 * 执行cmd
 * </pre>
 */
public class CmdTask implements Runnable {

	private CmdTaskQueue queue;
	private SocketChannel socket;
	private Command cmd;
	protected Long createTime;
	protected ObjectMessage objectMessage ;
	public CmdTask(Command cmd,SocketChannel socket,ObjectMessage message,CmdTaskQueue queue) {
		this.cmd = cmd;
		this.queue = queue;
		this.socket = socket;
		this.objectMessage = message;
		createTime = System.currentTimeMillis();
	}

	public void run() {
		if (queue != null) {
			long start = System.currentTimeMillis();
			try {
				cmd.execute(this.socket,this.objectMessage);
				long end = System.currentTimeMillis();
				long interval = end - start;
				long leftTime = end - createTime;
				if (interval >= 1000) {
					System.err.println("execute action : " + this.toString() + ", interval : " + interval + ", leftTime : " + leftTime + ", size : " + queue.getQueue().size());
				}
			} catch (Exception e) {
				System.err.println("执行 command 异常, command : " + cmd.toString() + ", packet : " + this.objectMessage.toString()+e.getMessage());
			} finally {
				queue.dequeue(this);
			}
		}
	}

	@Override
	public String toString() {
		return cmd.toString() + ", packet : " + this.objectMessage.toString();
	}
}
