package shenzhenuni.com.netty.split.stick;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.util.Date;

public class SplitStickServer {

	//开启服务器端的代码
	public void run(int port) throws Exception{
		
		EventLoopGroup bossEvent = new NioEventLoopGroup();
		
		EventLoopGroup worker = new NioEventLoopGroup();
		
		try {
			//1、建立起一个开启服务端的实例
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			//2、绑定相关的线程处理
			serverBootstrap.group(bossEvent, worker);
			
			//3、设置好管道及管道事件处理器
			serverBootstrap.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024).handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024),new StringDecoder(Charset.forName("UTF-8")),new ChannelHandlerAdapter(){
						private int count = 1 ;
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg)
								throws Exception {
							//1、第一种方式：读取客户端发送过来的数据
							/*ByteBuf clientbuff = (ByteBuf)msg;
							
							byte[] datas = new byte[clientbuff.readableBytes()];
							
							//将接受缓冲区中的数据读取到字节数组中
							clientbuff.readBytes(datas);
							
							String values = new String(datas,"UTF-8").substring(0, datas.length-System.getProperty("line.separator").length());*/
						
							//以第二种方式 进行读取数据
							String values = (String)msg;
							//打印客户端发送过来的命令
							System.out.println("[server"+(count++)+":netty] :"+values);
							
							//将服务端的时间发送给客户端
							String currentTime =
									"QUERY TIME ORDER".equalsIgnoreCase(values) ? new Date(System.currentTimeMillis()).toString() : "BAD QUERY ORDER";
							//加上系统的分隔符
							currentTime += System.getProperty("line.separator"); 
									
							//因为这个时候需要发送数据，因此需要事先分配好写好缓存
							ByteBuf writeBuffer = Unpooled.copiedBuffer(currentTime.getBytes());
							
							//这个时候将缓存中的数据刷新并写出去
							ctx.writeAndFlush(writeBuffer);
							
							//END 一次读写数据的交互完成
						}
						
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
								throws Exception {
							super.exceptionCaught(ctx, cause);
							ctx.close();
						}
						
						@Override
						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							ctx.flush();
						}
					});
				}
			});
			System.out.println("start server[A] "+ port);
			//4、对端口号进行绑定
			serverBootstrap.bind(port).sync().channel().closeFuture().sync();
		}finally{
			bossEvent.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		try {
			new SplitStickServer().run(10003);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class ReadClinetChannelHandler extends ChannelHandlerAppender{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//读取客户端发送过来的数据
		ByteBuf clientbuff = (ByteBuf)msg;
		
		byte[] datas = new byte[clientbuff.readableBytes()];
		
		//将接受缓冲区中的数据读取到字节数组中
		clientbuff.readBytes(datas);
		
		String values = new String(datas,"UTF-8").substring(0, datas.length-System.getProperty("line.separator").length());
	
		//打印客户端发送过来的命令
		System.out.println("[server:netty] :"+values);
		
		//将服务端的时间发送给客户端
		String currentTime =
				"QUERY TIME ORDER".equalsIgnoreCase(values) ? new Date(System.currentTimeMillis()).toString() : "BAD QUERY ORDER";
		//加上系统的分隔符
		currentTime += System.getProperty("line.separator"); 
				
		//因为这个时候需要发送数据，因此需要事先分配好写好缓存
		ByteBuf writeBuffer = Unpooled.copiedBuffer(currentTime.getBytes());
		
		//这个时候将缓存中的数据刷新并写出去
		ctx.writeAndFlush(writeBuffer);
		
		//END 一次读写数据的交互完成
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}