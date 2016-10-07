package shenzhenuni.com.nio.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;

public class SelectorProtocol implements Protocol {
	private final static ReentrantLock lock = new ReentrantLock();
	public void handleAccept(SelectionKey key,Selector selector) throws IOException {
		SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
		if(client!=null){
			System.err.println("the channel:"+client+" has connected");
			lock.lock();
				try{
				client.configureBlocking(false);
				// 将选择器注册到连接到的客户端信道，并指定该信道key值的属性为OP_READ，同时为该信道指定关联的事件
				client.register(selector, SelectionKey.OP_READ);
			}finally{
				lock.unlock();
			}
		}
	}

	public void handleRead(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();//特定的一个客户端
		// 获取该信道所关联的附件，这里为缓冲区
		//在这里可能会调用解码器进行解码
		NioDecoder decoder = Reactor.DECODER_MAP.get(key);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteBuffer buf = (ByteBuffer.allocate(1024));
		int readBytes = 0 ;
		int offset = 0;
		while((offset=client.read(buf))>0){//客户端如果一直有数据写的话，服务端就一直在这里读，知道客户端写完成
			readBytes += offset;
			buf.flip();
			baos.write(buf.array());
			buf.clear();
			byte[] data = baos.toByteArray();
			ByteBuffer tmpBuffer = ByteBuffer.allocate(data.length);
			tmpBuffer.put(data);
			tmpBuffer.flip();
			Object message = decoder.decoder(tmpBuffer);//每次读取到的字节数都需要进行一次decoder,因为不知道这个消息什么时候结束
			try{
				//解码完成之后，调用 handler
				if(message!=null){
					NioHandler nioHandler = Reactor.HANDLER_MAP.get(key);
					if(nioHandler != null){
						nioHandler.executor(client, message);
					}else{
						System.err.println("the key "+key+" related nio handler is null:");
					}
				}else{
					System.err.println("the size is less than the real size of data packet");
				}
			}finally{
				tmpBuffer.clear();
				tmpBuffer = null ;
			}
		}
		System.err.println("[ server ] reveive the length of data is:"+readBytes);
	}

	public void handleWrite(SelectionKey key) throws IOException {
		//获取与该信道关联的缓冲区，里面有之前读取到的数据  
	    ByteBuffer buf = (ByteBuffer.allocate(1024));  
	    //重置缓冲区，准备将数据写入信道  
	    buf.flip();   
	    SocketChannel clntChan = (SocketChannel) key.channel();  
	    //将数据写入到信道中  
	    clntChan.write(buf);  
	    if (!buf.hasRemaining()){   
	    //如果缓冲区中的数据已经全部写入了信道，则将该信道感兴趣的操作设置为可读  
	      key.interestOps(SelectionKey.OP_READ);  
	    }  
	    //为读入更多的数据腾出空间  
	    buf.compact();   
	}
}
