package shenzhenuni.com.globalgrow.netty;

import java.io.Serializable;

public class ObjectMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FLAG = 0x1719;
	private int flag ; //消息头 
	private int checkCode ;//内容校验码
	private int length ;//内容体的长度
	private byte[] body ;
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(int checkCode) {
		this.checkCode = checkCode;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
