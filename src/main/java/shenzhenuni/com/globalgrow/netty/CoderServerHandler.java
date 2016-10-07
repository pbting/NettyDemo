package shenzhenuni.com.globalgrow.netty;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import shenzhenuni.com.SerializeUtil;

/**
 * 每一个客户端对应一个CoderServerHandler，就想在wen tomcat 中每一个client 都有一个HttpSession
 * @author pengbingting
 *
 */
public class CoderServerHandler extends ChannelHandlerAdapter {
	private final static AtomicInteger sequence = new AtomicInteger();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.err.println("[ server ]"+sequence.incrementAndGet()+"--->channelRead;ctx:"+ctx+";channel:"+ctx.channel());
		
		if(msg instanceof ObjectMessage){
			ObjectMessage message = (ObjectMessage)msg ;
			if(message.getFlag() == ObjectMessage.FLAG){
				int checkCode = message.getCheckCode();
				byte[] body = message.getBody();
				if(message.getLength() == body.length){
					int tmpCheckCode = 0 ;
					for(byte b:body){
						tmpCheckCode += b;
					}
					if(checkCode == tmpCheckCode){
						String info = new String(body,Charset.forName("utf-8"));
						System.out.println(this+"[ server ] receive the message is:"+info+";time:"+new Date().getTime());
					}else{
						System.out.println("校验和不匹配。execept the check code is:"+checkCode+";but caculate the body is:"+tmpCheckCode);
					}
				}else{
					System.err.println("the length is not macthing:execept the length of body is:"+message.getLength()+";but recive is:"+body.length);
				}
			}else{
				System.err.println("消息头不匹配："+Integer.toHexString(message.getFlag()));
			}
		}else{
			System.err.println("transfor message doesn't matching");
		}
		
		super.channelRead(ctx, msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->exceptionCaught");
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->channelActive");
		LinkedConnect.HANDLER_CONTEXT.put(CoderServerHandler.class.hashCode(), ctx);
	}
	private final static AtomicInteger count = new AtomicInteger();
	private final static Random Random = new Random();
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->channelReadComplete;ctx:"+ctx);
		ObjectMessage objectMessage = new ObjectMessage();
		Person person = new Person(Random.nextInt(100), "globalgrow_"+count.incrementAndGet(), "深圳高新中"+Random.nextFloat()+"道");
		byte[] body = SerializeUtil.serialize(person);
		objectMessage.setFlag(ObjectMessage.FLAG);
		objectMessage.setCheckCode(SerializeUtil.getCheckSum(body));
		objectMessage.setLength(body.length);
		objectMessage.setBody(body);
		ctx.write(objectMessage);
		ctx.flush();
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->channelRegistered,ctx:"+ctx);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->channelInactive,ctx:"+ctx);
	}
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		super.write(ctx, msg, promise);
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->write");
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		super.flush(ctx);
		System.out.println("[ server ]"+sequence.incrementAndGet()+"--->flush");
	}
}
