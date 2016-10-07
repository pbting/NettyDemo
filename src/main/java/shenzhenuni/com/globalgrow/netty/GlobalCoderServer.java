package shenzhenuni.com.globalgrow.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class GlobalCoderServer {

	public static void main(String[] args) throws Exception {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		EventLoopGroup bossEventGroup = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			serverBootstrap.group(bossEventGroup, worker);
			serverBootstrap.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,1024).option(ChannelOption.TCP_NODELAY,true)
			//绑定IO事件的处理类，一个channel 上注册多个handler,然后根据输入的 Event分发给注册的EventHandler.
			.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					System.out.println("pipeline:"+pipeline);
					//对输入的数据进行解码
					pipeline.addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
					//对输出的数据进行编码
					pipeline.addLast(new ObjectEncoder());
					
					pipeline.addLast(new CoderServerHandler());
					pipeline.addLast(new ChannelHandlerAdapter(){
						@Override
						public void channelInactive(ChannelHandlerContext ctx)
								throws Exception {
							super.channelInactive(ctx);
							System.err.println("server: ChannelHandlerAdapter[ channelInactive ],ctx:"+ctx);
						}
					});
				}
			});
			//3、绑定在某个端口上
			serverBootstrap.bind(10004).sync().channel().closeFuture().sync();
		} finally{
			worker.shutdownGracefully();
			bossEventGroup.shutdownGracefully();
		}
	}
}
