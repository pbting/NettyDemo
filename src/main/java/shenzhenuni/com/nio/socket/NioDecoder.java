package shenzhenuni.com.nio.socket;

import java.nio.ByteBuffer;

public interface NioDecoder {
	/**
	 * 
	 * @param bytes which has receive the data,you muster handler the Size of each packet,if the size is less than the real you
	 *  should not to do something 
	 * @return 
	 */
	public Object decoder(ByteBuffer buffer);
}
