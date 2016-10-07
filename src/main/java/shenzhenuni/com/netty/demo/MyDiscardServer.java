package shenzhenuni.com.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 
 * @author Administrator
 */
public class MyDiscardServer {

	private int port ;
	
	public MyDiscardServer(int port){
		this.port = port ;
	}
	
	/**运行服务端*/
	public void run()throws Exception{
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		
		EventLoopGroup wrokerGroup = new NioEventLoopGroup();
		 try {
	            ServerBootstrap b = new ServerBootstrap();
	            b.group(eventLoopGroup, wrokerGroup).channel(NioServerSocketChannel.class)
	             .childHandler(new ChannelInitializer<SocketChannel>() {
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
	                     ch.pipeline().addLast(new MyDiscardServerHandler());
	                 }
	             });

	            System.out.println("discard server is startting.....");
	            // Bind and start to accept incoming connections.
	            ChannelFuture f = b.bind(port).sync();

	            // Wait until the server socket is closed.
	            // In this example, this does not happen, but you can do that to gracefully
	            // shut down your server.
	            f.channel().closeFuture().sync();
	        } finally {
	        	wrokerGroup.shutdownGracefully();
	        	eventLoopGroup.shutdownGracefully();
	        }
	}
	
	public static void main(String[] args) {
		 try {
			new MyDiscardServer(8081).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
