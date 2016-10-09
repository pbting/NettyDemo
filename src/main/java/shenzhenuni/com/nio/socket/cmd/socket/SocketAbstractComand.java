package shenzhenuni.com.nio.socket.cmd.socket;

import java.nio.channels.SocketChannel;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;
import shenzhenuni.com.nio.socket.core.Command;

public abstract class SocketAbstractComand implements Command<SocketChannel, ObjectMessage>{

	@Override
	public abstract void execute(SocketChannel socket, ObjectMessage message) throws Exception ;
}
