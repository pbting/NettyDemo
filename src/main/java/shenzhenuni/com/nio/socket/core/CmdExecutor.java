package shenzhenuni.com.nio.socket.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * 执行action队列的线程池
 * 延迟执行的action，先放置到delay action队列中，延迟时间后再加入执行队列
 * </pre>
 */
public class CmdExecutor {

	private AbstractCmdTaskQueue defaultQueue;
	private ThreadPoolExecutor pool;

	/**
	 * 执行action队列的线程池
	 * @param corePoolSize 最小线程数，包括空闲线程
	 * @param maxPoolSize 最大线程数 
	 * @param keepAliveTime 当线程数大于核心时，终止多余的空闲线程等待新任务的最长时间
	 * @param cacheSize 执行队列大小
	 * @param prefix 线程池前缀名称
	 */
	public CmdExecutor(int corePoolSize, int maxPoolSize, int keepAliveTime, int cacheSize, String prefix) {
		TimeUnit unit = TimeUnit.MINUTES;
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
		if (prefix == null) {
			prefix = "";
		}
		ThreadFactory threadFactory = new Threads(prefix);
		pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		defaultQueue = new AbstractCmdTaskQueue(this);
	}

	public AbstractCmdTaskQueue getDefaultQueue() {
		return defaultQueue;
	}

	public void enDefaultQueue(CmdTask cmdTask) {
		defaultQueue.enqueue(cmdTask);
	}

	public void execute(CmdTask cmdTask) {
		pool.execute(cmdTask);
	}
	
	public void stop() {
		if (!pool.isShutdown()) {
			pool.shutdown();
		}
	}

	static class Threads implements ThreadFactory {

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

		Threads(String prefix) {
			SecurityManager securitymanager = System.getSecurityManager();
			group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
			namePrefix = (new StringBuilder()).append("pool-").append(poolNumber.getAndIncrement()).append("-").append(prefix).append("-thread-").toString();
		}
	}
}


