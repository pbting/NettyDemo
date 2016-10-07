package shenzhenuni.com.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ByteBuffDemo {
	
	public static void main(String[] args) {
		List<Person> list = new ArrayList<Person>();
		
		list.add(new Person(21, "pbting"));
		list.add(new Person(53, "pbting"));
		list.add(new Person(34, "pbting"));
		list.add(new Person(34, "pbting"));
		list.add(new Person(26, "pbting"));
		
		Collections.sort(list);
		for(Person p : list)
			System.out.println(p.toString());
		
		
		System.out.println(list.indexOf(new Person(34, "pbting")));
	}

	private static void byteDemo() {
		ByteBuf byteBuf = Unpooled.buffer(32);//32个字节，存去8个int 值
		byteBuf.writeInt(1);
		byteBuf.writeInt(2);
		byteBuf.writeInt(3);
		byteBuf.writeInt(4);
		byteBuf.writeInt(5);
		byteBuf.writeInt(6);
		while(byteBuf.isReadable()){
			System.out.println(byteBuf.readInt());
			byteBuf.discardReadBytes();
		}
	}
}


class Person implements Comparable<Person>{

	private Integer age = 0 ;
	private String name ;
	
	public Person(Integer age ,String name) {
		this.age =age ;
		this.name = name ;
	}
	
	public int compareTo(Person o) {
		System.out.println("----sort---");
		return this.age > o.age ? -1 : 1 ;
	}

	@Override
	public String toString() {
		return "Person [age=" + age + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if(this.age.compareTo(other.age) ==0){
			return this.name.equals(other.name);
		}else
			return false ;
	}
}