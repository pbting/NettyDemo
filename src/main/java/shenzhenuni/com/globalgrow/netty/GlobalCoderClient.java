package shenzhenuni.com.globalgrow.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class GlobalCoderClient {

	public static void main(String[] args) {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup);
			
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY,true);
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.handler(new LoggingHandler());
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					//先注入解码解析器
					pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
					//再注入编码解析器
					pipeline.addLast(new ObjectEncoder());
					pipeline.addLast(new IdleStateHandler(0, 0, 10,TimeUnit.SECONDS));
					// 注入 message receive handler
					pipeline.addLast(new CoderClientHandler());
					pipeline.addLast(new ChannelHandlerAdapter(){
						@Override
						public void channelInactive(ChannelHandlerContext ctx)
								throws Exception {
							//在这里会检测到与客户端断开连接。也即当与客户端断开连接的时候会触发这里的代码执行。
							super.channelInactive(ctx);
							System.err.println("client: ChannelHandlerAdapter[ channelInactive ],ctx:"+ctx);
						}
					});
				}
			});
			bootstrap.connect("127.0.0.1", 10004);
		} finally{
//			eventLoopGroup.shutdownGracefully();
		}
	}
}
