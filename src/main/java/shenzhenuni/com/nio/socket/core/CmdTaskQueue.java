/**
 * All rights reserved. This material is confidential and proprietary to 7ROAD SQ team.
 */
package shenzhenuni.com.nio.socket.core;

import java.util.Queue;

/**
 * <pre>
 * CmdTask命令处理队列
 * </pre>
 */
public interface CmdTaskQueue<K,M> {
	
	public CmdTaskQueue<K,M> getCmdTaskQueue();
	
	public void enqueue(CmdTask<K,M> cmdTask);
	
	public void dequeue(CmdTask<K,M> cmdTask);
	
	public Queue<CmdTask<K,M>> getQueue();
	
	public void clear();
}
