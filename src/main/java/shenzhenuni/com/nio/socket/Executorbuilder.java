package shenzhenuni.com.nio.socket;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;

import shenzhenuni.com.globalgrow.netty.ObjectMessage;
import shenzhenuni.com.nio.socket.core.CmdExecutor;
import shenzhenuni.com.nio.socket.core.quence.Executor;
import shenzhenuni.com.nio.socket.core.stract.ProtocolCmdExecutor;
import shenzhenuni.com.nio.socket.core.stract.SocketCmdExecutor;

public final class Executorbuilder {

	private Executorbuilder(){}
	
	public static class SocketCmdExecutorBuilder{
		private static SocketCmdExecutor cmdExecutor ;
		private static ReentrantLock lock = new ReentrantLock();

		@SuppressWarnings("unchecked")
		public static CmdExecutor<SocketChannel, ObjectMessage> getCmdExecutor() {
			lock.lock();
			if (cmdExecutor == null) {
				try {
					synchronized (CmdExecutor.class) {
						if (cmdExecutor == null) {
							cmdExecutor = new SocketCmdExecutor();
						}
					}
				} finally {
					lock.unlock();
				}
			}
			return cmdExecutor;
		}
	}
	
	public static class ProtocolCmdExecutorBuilder{
		private static ProtocolCmdExecutor cmdExecutor ;
		private static ReentrantLock lock = new ReentrantLock();

		@SuppressWarnings("unchecked")
		public static CmdExecutor<Protocol, SelectionKey> getCmdExecutor() {
			lock.lock();
			if (cmdExecutor == null) {
				try {
					synchronized (CmdExecutor.class) {
						if (cmdExecutor == null) {
							cmdExecutor = new ProtocolCmdExecutor();
						}
					}
				} finally {
					lock.unlock();
				}
			}
			return cmdExecutor;
		}
	}
	
	public final static Executor bossExecutor = new Executor(4, 12, 1,1,"quence-boss-executor");
	
	public final static Executor workerExecutor = new Executor(6, 12,1,1, "quence-reactor-nio-");
	
}
