package shenzhenuni.com.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyDiscardClientHandler extends SimpleChannelInboundHandler<Object> {

	 private static final Logger logger = Logger.getLogger(MyDiscardClientHandler.class.getName());
	
	 private int messageSize;
	 private ByteBuf content;
	 private ChannelHandlerContext ctx;
	 
	 
	public MyDiscardClientHandler(int messageSize) {
		if (messageSize <= 0) {
			throw new IllegalArgumentException("messageSize: " + messageSize);
		}
		this.messageSize = messageSize;
	}
	
	/**
	 * 刚刚 建立的时候提供的回调方法
	 */
	@Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        this.ctx = ctx;

        // Initialize the message.
        content = ctx.alloc().directBuffer(messageSize).writeZero(messageSize);

        // Send the initial messages.
      //  generateTraffic();
        ctx.writeAndFlush(new String("[netty client:] Send the initial messages."));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        content.release();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Server is supposed to send nothing, but if it sends something, discard it.
    	System.out.println("I am netty client ,the message is :"+msg.toString());
    	
    	ctx.writeAndFlush(new String("name is netty client."));
    	
    	ctx.close();
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
            Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        logger.log( Level.WARNING,
                "Unexpected exception from downstream.",
                cause);
        ctx.close();
    }

    long counter;

   /* private void generateTraffic() {
        // Flush the outbound buffer to the socket.
        // Once flushed, generate the same amount of traffic again.
        ctx.writeAndFlush(content.duplicate().retain()).addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                generateTraffic();
            }
        }
    };*/
}
