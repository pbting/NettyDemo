package shenzhenuni.com.nio.socket.core.stract;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import shenzhenuni.com.globalgrow.netty.ObjectMessage;
import shenzhenuni.com.nio.socket.core.AbstractCmdExecutor;

/**
 * <pre>
 * 执行action队列的线程池
 * 延迟执行的action，先放置到delay action队列中，延迟时间后再加入执行队列
 * </pre>
 */
public class SocketCmdExecutor extends AbstractCmdExecutor<SocketChannel, ObjectMessage>{

	/**
	 * 执行action队列的线程池
	 * @param corePoolSize 最小线程数，包括空闲线程
	 * @param maxPoolSize 最大线程数 
	 * @param keepAliveTime 当线程数大于核心时，终止多余的空闲线程等待新任务的最长时间
	 * @param cacheSize 执行队列大小
	 * @param prefix 线程池前缀名称
	 */
	public SocketCmdExecutor() {
		int corePoolSize = Runtime.getRuntime().availableProcessors()*2 + 1 ;
		int maxPoolSize = corePoolSize * 2 + 1 ;
		TimeUnit unit = TimeUnit.MINUTES;
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
		shenzhenuni.com.nio.socket.core.AbstractCmdExecutor.Threads threadFactory = new Threads("socket-cmd-executor");
		super.pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 1, unit, workQueue, threadFactory, handler);
		super.defaultQueue = new SocketCmdTaskQueue(this);
	}

}


