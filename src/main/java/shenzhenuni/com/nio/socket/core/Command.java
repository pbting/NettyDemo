package shenzhenuni.com.nio.socket.core;

public interface Command<K,M> {

	public abstract void execute(K K, M M) throws Exception;

}
