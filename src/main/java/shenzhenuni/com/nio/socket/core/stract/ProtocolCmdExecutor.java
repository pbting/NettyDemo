package shenzhenuni.com.nio.socket.core.stract;

import java.nio.channels.SelectionKey;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import shenzhenuni.com.nio.socket.Protocol;
import shenzhenuni.com.nio.socket.core.AbstractCmdExecutor;

public class ProtocolCmdExecutor extends AbstractCmdExecutor<Protocol,SelectionKey> {

	public ProtocolCmdExecutor() {
		int corePoolSize = Runtime.getRuntime().availableProcessors()*2 + 1 ;
		int maxPoolSize = corePoolSize * 2 + 1 ;
		TimeUnit unit = TimeUnit.MINUTES;
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
		shenzhenuni.com.nio.socket.core.AbstractCmdExecutor.Threads threadFactory = new Threads("protocol-cmd-executor");
		super.pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 1, unit, workQueue, threadFactory, handler);
		super.defaultQueue = new ProtocolCmdTaskQuence(this);
	}
}
