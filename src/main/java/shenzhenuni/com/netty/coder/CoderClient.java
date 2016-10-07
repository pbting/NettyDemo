package shenzhenuni.com.netty.coder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class CoderClient {

	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		
		try {
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(eventLoopGroup);
			
			bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(
							new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())),
							new ObjectEncoder(),
							new ChannelHandlerAdapter(){
						@Override
						public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
							
							System.out.println("revicer server response:"+msg.toString());
						}

						@Override
						public void channelActive(ChannelHandlerContext ctx)
								throws Exception {
							System.out.println("已经建立起连接，开始发送数据：");
							//刚刚建立起连接的，准备好发送的消息
							for(long i =0 ;i < 20 ;i++){
								ctx.write(get(i+1));
							}
							ctx.flush();
						}
						
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
								throws Exception {
							super.exceptionCaught(ctx, cause);
						}	
						
						@Override
						public void channelReadComplete(
										ChannelHandlerContext ctx) throws Exception {
							ctx.flush();
						}
						//根据ID 号生成相关的用户订阅消息
						public SubscribeReq get(long id){
							SubscribeReq subscribeReq = new SubscribeReq();
							subscribeReq.setSubSequence(id);
							subscribeReq.setAddress("江西省吉安市吉安县油田镇龙洲村");
							subscribeReq.setPhoneNumber("15279132865");
							subscribeReq.setUserName(id%2==0? "pbting" : "phcheng");
							return subscribeReq;
						}
					});
				}
			});
			
			bootstrap.connect("127.0.0.1", 10004).sync().channel().closeFuture().sync();
		}finally{
			eventLoopGroup.shutdownGracefully();
		}
	}
}
