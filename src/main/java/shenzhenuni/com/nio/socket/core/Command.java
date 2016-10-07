package shenzhenuni.com.nio.socket.core;

import java.nio.channels.SocketChannel;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;

public interface Command {

	public abstract void execute(SocketChannel socket, ObjectMessage message) throws Exception;

}
