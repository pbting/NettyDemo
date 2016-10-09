package shenzhenuni.com.nio.socket.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("rawtypes")
public abstract class AbstractCmdTaskQueue<K, M> implements CmdTaskQueue {

	protected Queue<CmdTask<K, M>> queue;
	protected CmdExecutor executor;
	protected ReentrantLock lock = new ReentrantLock();
	public AbstractCmdTaskQueue(CmdExecutor cmdExecutor){
		this.executor = cmdExecutor;
		queue = new LinkedList<CmdTask<K, M>>();
	}
	@Override
	public CmdTaskQueue getCmdTaskQueue() {
		return this;
	}

	@Override
	public void enqueue(CmdTask cmdTask) {
		int queueSize = 0;
		lock.lock();
		try{
			queue.add(cmdTask);
			queueSize = queue.size();
		}finally{
			lock.unlock();
		}

		if (queueSize == 1) {
			executor.execute(cmdTask);
		}
		if (queueSize > 1000) {
			System.err.println(cmdTask.toString() + " queue size : " + queueSize);
		}
	}

	@Override
	public void dequeue(CmdTask cmdTask) {

		CmdTask<K, M> nextCmdTask = null;
		int queueSize = 0;
		String tmpString = null;
		lock.lock();
		try{
			queueSize = queue.size();
			CmdTask<K, M> temp = queue.remove();
			if (temp != cmdTask) {
				tmpString = temp.toString();
				
			}
			if (queueSize != 0) {
				nextCmdTask = queue.peek();
			}
		}finally{
			lock.unlock();
		}

		if (nextCmdTask != null) {
			executor.execute(nextCmdTask);
		}
		if (queueSize == 0) {
			System.err.println("queue.size() is 0.");
		}
		if (tmpString != null) {
			System.err.println("action queue error. temp " + tmpString + ", action : " + cmdTask.toString());
		}
	}

	@Override
	public Queue getQueue() {
		return this.queue;
	}

	@Override
	public void clear() {
		synchronized (queue) {
			queue.clear();
		}
	}
}
