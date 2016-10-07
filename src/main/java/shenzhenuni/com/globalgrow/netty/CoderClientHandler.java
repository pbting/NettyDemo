package shenzhenuni.com.globalgrow.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import shenzhenuni.com.SerializeUtil;

public class CoderClientHandler extends ChannelHandlerAdapter {
	private final static AtomicInteger sequence = new AtomicInteger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->channelRead;ctx:" + ctx);
		if (msg instanceof ObjectMessage) {
			ObjectMessage message = (ObjectMessage) msg;
			if (message.getFlag() == ObjectMessage.FLAG) {
				int checkCode = message.getCheckCode();
				byte[] body = message.getBody();
				if (message.getLength() == body.length) {
					int tmpCheckCode = 0;
					for (byte b : body) {
						tmpCheckCode += b;
					}
					if (checkCode == tmpCheckCode) {
						Person person = (Person) SerializeUtil.deserialize(message.getBody(), Person.class);
						System.out.println("reveive message from server:"+ person.toString());
					} else {
						System.out.println("校验和不匹配。execept the check code is:"
								+ checkCode + ";but caculate the body is:"+ tmpCheckCode);
					}
				} else {
					System.err.println("the length is not macthing:execept the length of body is:"+ message.getLength()
									+ ";but recive is:"+ body.length);
				}
			} else {
				System.err.println("消息头不匹配："+ Integer.toHexString(message.getFlag()));
			}
		} else {
			System.err.println("transfor message doesn't matching");
		}
		super.channelRead(ctx, msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->exceptionCaught");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[ client ]" + sequence.incrementAndGet()
				+ "--->channelActive;ctx:" + ctx);
		LinkedConnect.HANDLER_CONTEXT.put(CoderClientHandler.class.hashCode(),ctx);
		ObjectMessage message = new ObjectMessage();
		byte[] msgInfor = "我是一个中国人".getBytes();
		message.setFlag(ObjectMessage.FLAG);
		message.setBody(msgInfor);
		message.setLength(msgInfor.length);

		int checkCode = 0;
		for (byte b : msgInfor) {
			checkCode += b;
		}
		message.setCheckCode(checkCode);
		ctx.write(message);
		ctx.flush();
		super.channelActive(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->channelReadComplete,ctx:" + ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->channelRegistered:" + ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->channelInactive,ctx:" + ctx);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		super.write(ctx, msg, promise);
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->write");
	}

	@Override
	public void read(ChannelHandlerContext ctx) throws Exception {
		super.read(ctx);
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->read");
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		super.flush(ctx);
		System.out.println("[ client ]" + sequence.incrementAndGet()+ "--->flush");
	}
	private AtomicInteger count = new AtomicInteger();
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception {
		super.userEventTriggered(ctx, evt);
		if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.ALL_IDLE) {
				System.err.println("all idle is trigger "+count.incrementAndGet());
				ObjectMessage objectMessage = new ObjectMessage();
				objectMessage.setFlag(ObjectMessage.FLAG);
				byte[] infos = "我是一个心跳包".getBytes();
				objectMessage.setBody(infos);
				objectMessage.setCheckCode(SerializeUtil.getCheckSum(infos));
				objectMessage.setLength(infos.length);
				ChannelFuture channelFuture = ctx.channel().writeAndFlush(objectMessage);
				System.out.println("channelFuture:"+channelFuture);
				channelFuture.addListener(new ChannelFutureListener(){

					public void operationComplete(ChannelFuture future)throws Exception {
						if(future.isSuccess()){
							System.err.println("is success:"+new Date().getTime());
						}else{
							System.out.println(" is error");
						}
					}
				});
			}
		}
	}
}
