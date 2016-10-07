package shenzhenuni.com.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.example.echo.EchoServerHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyEchoServerHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(
            EchoServerHandler.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       
        ByteBuf buffer = (ByteBuf)msg;
        byte[] req = new byte[buffer.readableBytes()];
        
        buffer.readBytes(req);
        
        String body = new String(req,"UTF-8");
        
        System.out.println(body+"->server");
        
        //将服务器端的当前时间发送到client
        String currentTime = String.valueOf(System.currentTimeMillis());
        
        ByteBuf response = Unpooled.copiedBuffer(currentTime.getBytes());
        
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       System.out.println("server send message....");
    	ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
