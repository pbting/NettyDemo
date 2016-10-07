package shenzhenuni.com.nio.socket;

import java.nio.ByteBuffer;

import shenzhenuni.com.SerializeUtil;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;

public class SelectorNioDecoder implements NioDecoder {

	public Object decoder(ByteBuffer buffer) {
		if(buffer.remaining()<4){
			return null ;
		}
		
		if(buffer.remaining()<12){
			return null ;
		}
		
		//读第一个int 数据
		int flag = buffer.getInt();
		if(ObjectMessage.FLAG != flag){
			System.err.println("消息头不正确.....");
			return null;
		}
		int checkCode = buffer.getInt();//第二个 int 是消息内容提要的校验和
		int length = buffer.getInt(); //第三个 int 是 消息内容体的长度
		
		if(buffer.remaining()<length){
			System.err.println("消息内容提要长度不够");
			return null ;
		}
		
		//读取消息内容体
		byte[] body = new byte[length];
		byte[] target = buffer.array();
		System.arraycopy(target, buffer.position(), body, 0, length);
		//检验和
		int tmpCheckCode = SerializeUtil.getCheckSum(body);
		if(checkCode == tmpCheckCode){
			//在这里封装我们的Message object
			ObjectMessage objectMessage = new ObjectMessage();
			objectMessage.setFlag(flag);
			objectMessage.setCheckCode(checkCode);
			objectMessage.setLength(length);
			objectMessage.setBody(body);
			return objectMessage;
		}else{
			System.err.println("the check code is error!");
		}
		return null;
	}
}
