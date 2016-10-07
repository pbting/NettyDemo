package shenzhenuni.com.netty.coder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class CoderServer {

	public static void main(String[] args) throws Exception {
		EventLoopGroup bossEvent = new NioEventLoopGroup();
		
		EventLoopGroup worker = new NioEventLoopGroup();
		
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			//1、绑定线程循环组
			serverBootstrap.group(bossEvent, worker);
			
			//2、选择tcp 通信管道类型
			serverBootstrap.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG,1024).handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectDecoder(
							ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())),
							new ObjectEncoder(),new ChannelHandlerAdapter(){
						@Override
						public void channelRead(ChannelHandlerContext ctx,
								Object msg) throws Exception {
							//因为我们设置了序列化解码器，因此在这里我们只关心对象转换后，处理我们的业务逻辑即可
							SubscribeReq subscribeReq = (SubscribeReq)msg ;
							
							if(subscribeReq.getUserName().equalsIgnoreCase("pbting")){
								//只处理用户名为pbting 的订单
								System.out.println("server[revicer]:"+subscribeReq.toString());
								//得到一个服务器端的返回消息
								ctx.writeAndFlush(getSubscribeResp(subscribeReq.getSubSequence(), subscribeReq.getUserName()));
							}
						}
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)
												throws Exception {
							System.out.println(cause.getMessage());
							ctx.close();
						}
						public SubscribeResp getSubscribeResp(long id,String userName){
							SubscribeResp subscribeResp = new SubscribeResp() ;
							
							subscribeResp.setSubReqId(id);
							
							subscribeResp.setSubRespCode(1);
							
							subscribeResp.setRespDesc("welcome to use the netty book,Thanks->"+userName);
							
							return subscribeResp;
						}
					});
				}
			});
			
			//3、绑定在某个端口上
			serverBootstrap.bind(10004).sync().channel().closeFuture().sync();
		} finally {
			worker.shutdownGracefully();
			bossEvent.shutdownGracefully();
		}
	}
}
