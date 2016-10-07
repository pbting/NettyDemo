package shenzhenuni.com.NettyDemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class SerializeTest {

	public static void main(String[] args) {
		Person person = new Person("pbting", 78);
		
		ByteArrayOutputStream baoos = new ByteArrayOutputStream();
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baoos);
			
			oos.writeObject(person);//将该对象的二进制流输出到baoos 字节数组流中
			
			oos.flush();
			oos.close();
			
			byte[] result = baoos.toByteArray();
			
			System.out.println("JDK serialize length is :"+result.length);
			
			
			System.err.println("custom serialize length is :"+person.codeC(ByteBuffer.allocate(1024)).length);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Person implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name ;
	private int age ;
	
	public Person(String name,int age) {
		this.name = name ; 
		this.age =age ;
	}
	
	/**
	 * 自定义的编码
	 */
	public byte[] codeC(ByteBuffer buffer){
		buffer.clear();
		byte[] nameValus = this.name.getBytes();
		
		buffer.putInt(nameValus.length);
		
		buffer.put(nameValus);
		
		buffer.putInt(this.age);
		buffer.flip();
		nameValus = null ;
		byte[] result = new byte[buffer.remaining()];
		
		buffer.get(result);
		
		return result;
	}
}