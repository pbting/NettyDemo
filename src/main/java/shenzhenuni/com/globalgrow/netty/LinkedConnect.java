package shenzhenuni.com.globalgrow.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

public class LinkedConnect {
	private LinkedConnect(){}
	
	public static final ConcurrentHashMap<Integer, ChannelHandlerContext> HANDLER_CONTEXT = new ConcurrentHashMap<Integer, ChannelHandlerContext>();
}
