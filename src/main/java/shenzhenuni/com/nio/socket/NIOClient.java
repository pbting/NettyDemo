package shenzhenuni.com.nio.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import shenzhenuni.com.SerializeUtil;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;
import shenzhenuni.com.globalgrow.netty.Person;

public class NIOClient {

	/** 
     * @param args 
     * @throws IOException  
     */  
    public static void main(String[] args) throws IOException {  
        int port =4455;  
        SocketChannel channel=SocketChannel.open();  
        channel.configureBlocking(false);  
          
        SocketAddress target=new InetSocketAddress("127.0.0.1",port);  
        channel.connect(target);  
        Selector selector = Selector.open();  
        //用于套接字连接操作的操作集位  
        channel.register(selector, SelectionKey.OP_CONNECT);  
        BufferedReader systemIn=new BufferedReader(new InputStreamReader(System.in));  
          
        while(true){  
            int nKeys=selector.select(1000);  
            if(nKeys>0){  
                for(SelectionKey key:selector.selectedKeys()){  
                    if(key.isConnectable()){  
                        SocketChannel sc=(SocketChannel) key.channel();  
                        if(sc.isConnectionPending()){
                        	sc.finishConnect();
                        }
                        
                        sc.configureBlocking(false);
                        System.err.println("----->连接成功。");
                        //在这里不间断的给服务端发送消息
                        Person person = new Person(24, "pbting", "深圳市");
                        byte[] body = SerializeUtil.serialize(person);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(ObjectMessage.HEAD_LENGTH+body.length);
                        byteBuffer.putInt(ObjectMessage.FLAG);
                        byteBuffer.putInt(SerializeUtil.getCheckSum(body));
                        byteBuffer.putInt(body.length);//消息内容体的长度
                        byteBuffer.put(body);
                        byteBuffer.flip();
                        sc.setOption(StandardSocketOptions.TCP_NODELAY,false);
                        System.out.println("client:"+person.toString());
                        sc.write(byteBuffer);//写给服务端
                        sc.register(selector, SelectionKey.OP_READ);  
                    }else if(key.isReadable()){  
                        ByteBuffer buffer=ByteBuffer.allocate(1024);  
                        SocketChannel sc=(SocketChannel) key.channel();  
                        int readBytes=0;  
                        try{  
                            int ret=0;  
                            try{  
                                while((ret=sc.read(buffer))>0){  
                                    readBytes+=ret;  
                                }  
                            }finally{  
                                buffer.flip();  
                            }  
                            if (readBytes > 0) {     
                                System.out.println(Charset.forName("UTF-8").decode(buffer).toString());     
                                buffer = null;     
                            }     
  
                        }finally {     
                            if (buffer != null) {     
                                buffer.clear();     
                            }  
                        }  
                    }  
                }  
                selector.selectedKeys().clear();     
            }else{
            	System.err.println("[ client ]:"+nKeys);
            }  
        }  
    }
}
