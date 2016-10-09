package shenzhenuni.com.nio.socket.cmd.protocol;

import java.nio.channels.SelectionKey;
import shenzhenuni.com.nio.socket.Protocol;
import shenzhenuni.com.nio.socket.core.Command;

public class ProtocolCommand implements Command<Protocol, SelectionKey> {

	@Override
	public void execute(Protocol protocol, SelectionKey key) throws Exception {
		if(protocol == null || key == null){
			return ;
		}
		
		if(key.isAcceptable()){
			protocol.handleAccept(key);
			return ;
		}
		
		if(key.isReadable()){
			protocol.handleRead(key);
			return ;
		}
		
		if(key.isWritable()){
			protocol.handleWrite(key);
			return ;
		}
	}
}	
