package shenzhenuni.com.netty.protobuf.sercli;

import shenzhenuni.com.netty.protobuf.SubscribeReqProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("reciver data from server ["+msg+"]");
	}
	
	private static SubscribeReqProto.SubscribeReq createSubscribeReq(int i){
		SubscribeReqProto.SubscribeReq.Builder builder = 
				SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqId(i);
		builder.setUserName("peng-"+i);
		builder.setProductName("the seventy road!");
		builder.setAddress("江西省吉安市吉安县");
		
		return builder.build();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("成功连接...");
		for(int i =1 ;i <= 20; i++){
			ctx.write(createSubscribeReq(i));
		}
		ctx.flush();
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		System.out.println(cause.getMessage());
		ctx.close();
	}
}
