/**
 * All rights reserved. This material is confidential and proprietary to 7ROAD SQ team.
 */
package shenzhenuni.com.nio.socket.core.stract;

import java.nio.channels.SocketChannel;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;
import shenzhenuni.com.nio.socket.core.AbstractCmdTaskQueue;
import shenzhenuni.com.nio.socket.core.CmdExecutor;

/**
 * <pre>
 * CmdTaskQueue基本功能实现
 * </pre>
 */
public class SocketCmdTaskQueue extends AbstractCmdTaskQueue<SocketChannel,ObjectMessage> {

	public SocketCmdTaskQueue(CmdExecutor<SocketChannel,ObjectMessage> cmdExecutor) {
		super(cmdExecutor);
	}

}
