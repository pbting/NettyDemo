package shenzhenuni.com;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeUtil {
	public static final ObjectMapper mapper = new ObjectMapper();
		
	public static byte [] serialize(Object obj) throws JsonProcessingException{
		return mapper.writeValueAsBytes(obj);
	}
	
	public static <T> Object deserialize(byte[] byteData, Class<T> cls) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(byteData, cls);
	}
	
	public static int getCheckSum(byte[] body){
		int checkSum = 0 ;
		for(byte b:body){
			checkSum+=b;
		}
		return checkSum;
	}
}
