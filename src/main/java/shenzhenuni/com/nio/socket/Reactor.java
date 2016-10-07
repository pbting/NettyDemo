package shenzhenuni.com.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import shenzhenuni.com.nio.socket.core.Command;

public class Reactor implements Runnable {
	
	private Selector selector ;
	private ServerSocketChannel serverSocket ;
	private ThreadPoolExecutor bossExecutor = new ThreadPoolExecutor(4, 12, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	private ThreadPoolExecutor workerExecutor = new ThreadPoolExecutor(6, 12, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(),new DefaultThreads("reactor-nio-"));
	static class DefaultThreads implements ThreadFactory {

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

		DefaultThreads(String prefix) {
			SecurityManager securitymanager = System.getSecurityManager();
			group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
			namePrefix = (new StringBuilder()).append("pool-").append(poolNumber.getAndIncrement()).append("-").append(prefix).append("-thread-").toString();
		}
	}
	private Protocol tcpProtocol = null;
	private NioEncoder nioEncoder = null ;
	private NioDecoder nioDecoder = null ;
	public final static ConcurrentHashMap<SelectionKey, NioEncoder> ENCODER_MAP = new ConcurrentHashMap<SelectionKey, NioEncoder>();
	public final static ConcurrentHashMap<SelectionKey, NioDecoder> DECODER_MAP = new ConcurrentHashMap<SelectionKey, NioDecoder>();
	public final static ConcurrentHashMap<SelectionKey, Command> HANDLER_MAP = new ConcurrentHashMap<SelectionKey, Command>();
	private Class<?> handlerClazz = null ;
	public Reactor(int port,NioEncoder nioEncoder,NioDecoder nioDecoder,Class<?> clazz) throws Exception{
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.configureBlocking(false);
		serverSocket.bind(new InetSocketAddress(port));
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		this.tcpProtocol = new SelectorProtocol();
		assert (nioDecoder != null);
		assert (nioEncoder != null) ;
		assert (clazz != null);
		this.nioEncoder = nioEncoder;
		this.nioDecoder = nioDecoder ;
		this.handlerClazz = clazz ;
		System.err.println("listener on the port:"+port);
	}
	
	public void run() {
		try {
			while(!Thread.interrupted()){
				int selecCount = selector.select();
				System.err.println("the number of select key is :"+selecCount);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				while(iterator.hasNext()){
					SelectionKey key = iterator.next();
					new Dispatch(key).run();
//					bossExecutor.submit(new Dispatch(key));
					iterator.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public class Dispatch implements Runnable{
		private SelectionKey selectionKey;
		public Dispatch(SelectionKey selectionKey){
			this.selectionKey = selectionKey ;
		}
		public void run() {
			try {
				if(selectionKey.isAcceptable()){
					
					System.out.println("**********state:isAcceptable");
					new StateHandler(tcpProtocol, selectionKey,selector,SelectionKey.OP_ACCEPT).run();
//					workerExecutor.submit(new StateHandler(tcpProtocol, selectionKey,selector,SelectionKey.OP_ACCEPT));//多线程只处理accept 的多做[main reactor]
					/**
					 * 说明：
					 * 为什么要单独分一个Reactor来处理监听呢？
					 * 因为像TCP这样需要经过3次握手才能建立连接，这个建立连接的过程也是要耗时间和资源的，单独分一个Reactor来处理，可以提高性能。
					 */
					return ;
				}
				
				if(selectionKey.isReadable()){
					//
					System.out.println("**********isReadable");
					DECODER_MAP.putIfAbsent(this.selectionKey, Reactor.this.nioDecoder);
					try {
						HANDLER_MAP.putIfAbsent(selectionKey, (Command) Reactor.this.handlerClazz.newInstance());
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					new StateHandler(tcpProtocol, selectionKey, SelectionKey.OP_READ).run();
//					workerExecutor.submit(new StateHandler(tcpProtocol, selectionKey, SelectionKey.OP_READ));
					return ;
				}
				
				if(selectionKey.isWritable()){
					System.out.println("**********isWritable");
					ENCODER_MAP.putIfAbsent(this.selectionKey, nioEncoder);
					try {
						HANDLER_MAP.putIfAbsent(selectionKey, (Command) Reactor.this.handlerClazz.newInstance());
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					new StateHandler(tcpProtocol, selectionKey, SelectionKey.OP_WRITE).run();
//					workerExecutor.submit(new StateHandler(tcpProtocol, selectionKey, SelectionKey.OP_WRITE));
					return ;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
