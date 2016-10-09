package shenzhenuni.com.nio.socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import shenzhenuni.com.nio.socket.cmd.socket.PersonCommand;

public class NioServer {

	private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	public static void main(String[] args) {
		NioEncoder encoder = new SelectNioEncoder();
		NioDecoder decoder = new SelectorNioDecoder();
		try {
			Reactor reactor = new Reactor(4455, encoder, decoder,PersonCommand.class);
			reactor.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
