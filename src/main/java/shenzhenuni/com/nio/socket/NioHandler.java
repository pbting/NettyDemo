package shenzhenuni.com.nio.socket;

import java.nio.channels.SocketChannel;

public interface NioHandler {

	public void executor(SocketChannel socketChannel,Object message);
}
