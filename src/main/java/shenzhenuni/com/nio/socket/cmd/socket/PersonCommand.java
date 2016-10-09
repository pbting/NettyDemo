package shenzhenuni.com.nio.socket.cmd.socket;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import shenzhenuni.com.SerializeUtil;
import shenzhenuni.com.globalgrow.netty.ObjectMessage;
import shenzhenuni.com.globalgrow.netty.Person;

public class PersonCommand extends SocketAbstractComand {

	@Override
	public void execute(SocketChannel socket, ObjectMessage message) throws Exception {
		try {
			Person person = (Person) SerializeUtil.deserialize(message.getBody(), Person.class);
			System.out.println(person.toString());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
