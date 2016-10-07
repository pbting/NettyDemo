package shenzhenuni.com.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.discard.DiscardClient;

public class MyDiscardClient {

	private String host;
	
	private int port ;
	
	private int fistMessageSize ;
	
	public MyDiscardClient() {
	}

	public MyDiscardClient(String host, int port, int fistMessageSize) {
		super();
		this.host = host;
		this.port = port;
		this.fistMessageSize = fistMessageSize;
	}

	public void run()throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap();
		
		try {
			//设置好通信的管道类型以及数据时间处理器
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new MyDiscardClientHandler(fistMessageSize));
			
			 // Make the connection attempt.
			ChannelFuture f = bootstrap.connect(host, port).sync();

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally{
			group.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        final String host = "127.0.0.1";
        final int port = 8081;
        final int firstMessageSize = 1024;
        new DiscardClient(host, port, firstMessageSize).run();
    }
}
