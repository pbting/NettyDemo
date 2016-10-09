package shenzhenuni.com.nio.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import shenzhenuni.com.nio.socket.cmd.protocol.ProtocolCommand;

public class StateHandler implements Runnable {
	private final SelectionKey key ;
    private Protocol tcpProtocol;
	public StateHandler(Protocol tcpProtocol,SelectionKey key) throws IOException {
		this.tcpProtocol = tcpProtocol;
		this.key = key;
				
	}
	public void run() {
		try {
			new ProtocolCommand().execute(tcpProtocol, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
