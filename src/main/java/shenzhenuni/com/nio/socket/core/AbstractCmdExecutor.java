package shenzhenuni.com.nio.socket.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("rawtypes")
public abstract class AbstractCmdExecutor<K, M> implements CmdExecutor{

	protected AbstractCmdTaskQueue defaultQueue;
	protected ThreadPoolExecutor pool;
	@SuppressWarnings("unchecked")
	public AbstractCmdTaskQueue<K, M> getDefaultQueue() {
		return defaultQueue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void enDefaultQueue(CmdTask cmdTask) {
		if(cmdTask == null){
			return ;
		}
		
		if(cmdTask.getCmdTaskQuence() == null){
			cmdTask.setCmdTaskQueue(defaultQueue);
		}
		defaultQueue.enqueue(cmdTask);		
	}
	
	@Override
	public void execute(CmdTask cmdTask) {
		if(cmdTask==null){
			return ;
		}
		pool.execute(cmdTask);
	}
	
	@Override
	public void stop() {
		if (!pool.isShutdown()) {
			pool.shutdown();
		}
	}
	
	public static class Threads implements ThreadFactory {

		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(group, runnable, (new StringBuilder()).append(namePrefix).append(threadNumber.getAndIncrement()).toString(), 0L);
			if (thread.isDaemon())
				thread.setDaemon(false);
			if (thread.getPriority() != 5)
				thread.setPriority(5);
			return thread;
		}

		public Threads(String prefix) {
			SecurityManager securitymanager = System.getSecurityManager();
			group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
			namePrefix = (new StringBuilder()).append("pool-").append(poolNumber.getAndIncrement()).append("-").append(prefix).append("-thread-").toString();
		}
	}
}
