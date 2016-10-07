package shenzhenuni.com.netty.protobuf.sercli;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

import shenzhenuni.com.netty.protobuf.SubscribeReqProto;
import shenzhenuni.com.netty.protobuf.SubscribeRespProto;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//在这里就是对接收的消息（解码后），进行具体的业务逻辑处理
		SubscribeReqProto.SubscribeReq subscribeReq = (SubscribeReqProto.SubscribeReq)msg;
		if(subscribeReq.getUserName()!=null&&subscribeReq.getUserName().startsWith("peng")){
			System.out.println("reviver data from client is :"+subscribeReq.toString());
			ctx.writeAndFlush(getResp(subscribeReq.getSubReqId()));
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	private SubscribeRespProto.SubscribeResp getResp(int subId){
		SubscribeRespProto.SubscribeResp.Builder builder =
				SubscribeRespProto.SubscribeResp.newBuilder();
		
		builder.setSubRequiredId(subId);
		builder.setRespCode(2);
		builder.setDesc("恭喜你成功连接netty protobuff 中心！");
		
		return builder.build();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		System.out.println(cause.getMessage());
		ctx.close();
	}
}
