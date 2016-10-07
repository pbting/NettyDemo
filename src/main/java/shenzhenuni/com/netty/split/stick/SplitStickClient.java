package shenzhenuni.com.netty.split.stick;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerAppender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class SplitStickClient {

	//运行客户端
	public void run(String host,int port) throws Exception{
		//1、新建一个事件轮回处理
		EventLoopGroup worker = new NioEventLoopGroup();
		
		try {
			//2、开启一个客户端启动引导实例
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(worker);
			
			//3、给这个客户端选择一个和服务器端进行通信的管道
			bootstrap.channel(NioSocketChannel.class).
				option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024),new StringDecoder(Charset.forName("UTF-8")),new ChannelHandlerAdapter(){
						private final byte[] query_order = 
								("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
						private int count = 1 ;
						
						/*
						 *刚刚建立起连接的时候进行进行回调的方法，这个时候在这里可以准备一些需要发送的数据 
						 */
						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							//连续发送100次的时间查询命令，正常情况下是返回100个服务器端的时间
							ByteBuf message = null;
							for(int i=0;i < 20;i++){
								message = Unpooled.buffer(query_order.length);
								message.writeBytes(query_order);//往缓存区里写入数据
								//刷新并写到链路层
								ctx.writeAndFlush(message);
								//System.out.println("正在 发送数据....");
								//Thread.sleep(500);
							}
						}
						
						/*
						 * 在这里，可以读取服务器端返回的数据
						 */
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg)
								throws Exception {
							/*//读取服务器端发送的数据,现在都是以字节数据的形式进行交互
							ByteBuf serverData = (ByteBuf)msg;
							
							byte[] dataBuffer = new byte[serverData.readableBytes()];
							
							serverData.readBytes(dataBuffer);
							
							
							String body = new String(dataBuffer,"UTF-8");*/
							String body = (String)msg;
							
							System.out.println("[client"+(count++)+":netty]:"+body);
							
							//读完之后关闭与服务器端的连接
							//ctx.close();
						}
	
						//若何服务器端交互发生异常，则在这里处理，进行回调处理
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
								throws Exception {
							System.out.println(cause.getMessage());
							ctx.close();
						}
					});
				}
			});
			
			//4.一切准备就绪，就开始和服务器端进行连接
			bootstrap.connect(host, port).sync().channel().closeFuture().sync();
		} finally {
			worker.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		try {
			new SplitStickClient().run("10.0.3.52", 10003);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

//客户端数据处理器
class ClientReadHandler extends ChannelHandlerAppender{
	
	private final byte[] query_order = 
				("QUERY TIME ORDER "+System.getProperty("line.separator")).getBytes();
	
	/*
	 *刚刚建立起连接的时候进行进行回调的方法，这个时候在这里可以准备一些需要发送的数据 
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//连续发送100次的时间查询命令，正常情况下是返回100个服务器端的时间
		ByteBuf message = null;
		for(int i=0;i < 20;i++){
			message = Unpooled.buffer(query_order.length);
			message.writeBytes(query_order);//往缓存区里写入数据
			//刷新并写到链路层
			ctx.writeAndFlush(message);
			System.out.println("正在 发送数据....");
			Thread.sleep(500);
		}
	}
	
	/*
	 * 在这里，可以读取服务器端返回的数据
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//读取服务器端发送的数据,现在都是以字节数据的形式进行交互
		ByteBuf serverData = (ByteBuf)msg;
		
		byte[] dataBuffer = new byte[serverData.readableBytes()];
		
		serverData.readBytes(dataBuffer);
		
		String body = new String(dataBuffer,"UTF-8");
		
		System.out.println("the time from server is:"+body);
	}

	//若何服务器端交互发生异常，则在这里处理，进行回调处理
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println(cause.getMessage());
		ctx.close();
	}
}
