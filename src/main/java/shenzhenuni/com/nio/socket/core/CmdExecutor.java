package shenzhenuni.com.nio.socket.core;

import java.nio.channels.SocketChannel;

import shenzhenuni.com.globalgrow.netty.ObjectMessage;

public interface CmdExecutor<K,M> {

	public AbstractCmdTaskQueue<K,M> getDefaultQueue();
	
	public void enDefaultQueue(CmdTask<K,M> cmdTask);
	
	public void execute(CmdTask<SocketChannel,ObjectMessage> cmdTask);
	
	public void stop();
}
