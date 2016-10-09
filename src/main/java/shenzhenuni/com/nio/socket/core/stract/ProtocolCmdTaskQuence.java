package shenzhenuni.com.nio.socket.core.stract;

import java.nio.channels.SelectionKey;
import shenzhenuni.com.nio.socket.Protocol;
import shenzhenuni.com.nio.socket.core.AbstractCmdTaskQueue;
import shenzhenuni.com.nio.socket.core.CmdExecutor;

public class ProtocolCmdTaskQuence extends AbstractCmdTaskQueue<Protocol,SelectionKey> {

	public ProtocolCmdTaskQuence(CmdExecutor<Protocol,SelectionKey> cmdExecutor) {
		super(cmdExecutor);
	}
}
