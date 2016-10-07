package shenzhenuni.com.netty.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyDiscardServerHandler extends SimpleChannelInboundHandler<Object>{

	@Override
	protected void messageReceived(ChannelHandlerContext chc, Object msg)
			throws Exception {
		
		//服务器端接收信息
		System.out.println("接收到的消息:"+msg.toString());
		
		chc.writeAndFlush(new String("I am from netty server,name is pbting"));
		
	}

}
