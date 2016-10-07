package shenzhenuni.com.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyEchoClientHandler extends ChannelHandlerAdapter{

	private static final Logger logger = Logger.getLogger(MyEchoClientHandler.class.getName());

	private ByteBuf firstMessage;

	/**
	 * Creates a client-side handler.
	 */
	public MyEchoClientHandler(int firstMessageSize) {
		if (firstMessageSize <= 0) {
			throw new IllegalArgumentException("firstMessageSize: "
					+ firstMessageSize);
		}
		firstMessage = Unpooled.buffer(firstMessageSize);
		firstMessage.writeBytes("彭兵庭".getBytes());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buffer = (ByteBuf)msg;
		
		byte[] values = new byte[buffer.readableBytes()];
		
		buffer.readBytes(values);
		
		String body = new String(values,"UTF-8");
		
		System.out.println(body+":client");
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		logger.log(Level.WARNING, "Unexpected exception from downstream.",cause);
		ctx.close();
	}
}
