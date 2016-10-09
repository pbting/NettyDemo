package shenzhenuni.com.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import shenzhenuni.com.nio.socket.cmd.protocol.ProtocolCommand;
import shenzhenuni.com.nio.socket.core.Command;

public class Reactor implements Runnable {
	
	private Selector selector ;
	private ServerSocketChannel serverSocket ;
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
					this.execute(key);
					iterator.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private long execute(SelectionKey selectionKey) {
		long start = System.currentTimeMillis();
		if(selectionKey.isAcceptable()){
			
			System.out.println("**********state:isAcceptable");
			this.doHandler(tcpProtocol, selectionKey);
			/**
			 * 说明：
			 * 为什么要单独分一个Reactor来处理监听呢？
			 * 因为像TCP这样需要经过3次握手才能建立连接，这个建立连接的过程也是要耗时间和资源的，单独分一个Reactor来处理，可以提高性能。
			 */
			return System.currentTimeMillis()-start;
		}
		
		if(selectionKey.isReadable()){
			//
			System.out.println("**********isReadable");
			DECODER_MAP.putIfAbsent(selectionKey, Reactor.this.nioDecoder);
			try {
				HANDLER_MAP.putIfAbsent(selectionKey, (Command) Reactor.this.handlerClazz.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			this.doHandler(tcpProtocol, selectionKey);
			return System.currentTimeMillis()-start;
		}
		
		if(selectionKey.isWritable()){
			System.out.println("**********isWritable");
			ENCODER_MAP.putIfAbsent(selectionKey, nioEncoder);
			try {
				HANDLER_MAP.putIfAbsent(selectionKey, (Command) Reactor.this.handlerClazz.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			this.doHandler(tcpProtocol, selectionKey);
			return System.currentTimeMillis()-start;
		}
		return System.currentTimeMillis()-start;
	}
	
	private void doHandler(Protocol protocol,SelectionKey key) {
		try {
			new ProtocolCommand().execute(tcpProtocol, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
