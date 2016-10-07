package shenzhenuni.com.netty.protobuf.sercli;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import shenzhenuni.com.netty.protobuf.SubscribeReqProto;

public class ServerProtoBuf {

	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(boss, worker);
			
			//选择通信的管道
			serverBootstrap.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					//使用Protobuf 的包办处理解码器
					ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
					ch.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance())//从这里可以看出告诉protobuf 从字节码中需要解析的Java 对象是什么
							);
					ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new ServerHandler());
				}
			});
			System.out.println("。。。a成功开启...");
			//2、绑定在指定的端口并且异步等待
			serverBootstrap.bind(10004).sync().channel().closeFuture().sync();
		} finally{
			worker.shutdownGracefully();
			boss.shutdownGracefully();
		}
	}
}
