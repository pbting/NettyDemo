package shenzhenuni.com.nio.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class StateHandler implements Runnable {
	private final SelectionKey key ;
    private int state ;
    private Protocol tcpProtocol;
    private Selector selector; 
	public StateHandler(Protocol tcpProtocol,SelectionKey key,Selector selector,int state) throws IOException {
		this.tcpProtocol = tcpProtocol;
		this.key = key;
		this.state = state ;
		this.selector = selector ; 
				
	}
	public StateHandler(Protocol tcpProtocol,SelectionKey key,int state) throws IOException {
		this.tcpProtocol = tcpProtocol;
		this.key = key;
		this.state = state ;
				
	}
	public void run() {
		switch (state) {
		case SelectionKey.OP_ACCEPT:
		{
			try {
				tcpProtocol.handleAccept(key);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
			break;
		case SelectionKey.OP_READ:
		{
			try {
				tcpProtocol.handleRead(key);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			break;
		case SelectionKey.OP_WRITE:
		{
			try {
				tcpProtocol.handleWrite(key);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
			break;
		default:
		{
			System.err.println("donn't konw the state:"+state);
		}
			break;
		}
	}
}
