package shenzhenuni.com.nio.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface Protocol {

	//accept I/O形式  
    void handleAccept(SelectionKey key,Selector selector) throws IOException;  
    //read I/O形式  
    void handleRead(SelectionKey key) throws IOException;  
    //write I/O形式  
    void handleWrite(SelectionKey key) throws IOException;  
}
