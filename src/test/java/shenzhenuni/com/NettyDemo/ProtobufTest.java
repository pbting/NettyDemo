package shenzhenuni.com.NettyDemo;

import shenzhenuni.com.netty.protobuf.SubscribeReqProto;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufTest {

	private static byte[] encode(SubscribeReqProto.SubscribeReq req){
		return req.toByteArray();
	}
	
	private static SubscribeReqProto.SubscribeReq decode(byte[] body) 
			throws InvalidProtocolBufferException{
		return SubscribeReqProto.SubscribeReq.parseFrom(body);
	}
	
	private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
		SubscribeReqProto.SubscribeReq.Builder builder = 
				SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqId(1);
		builder.setUserName("pbting");
		builder.setProductName("the seventy road!");
		builder.setAddress("江西省吉安市吉安县");
		
		return builder.build();
	}
	
	public static void main(String[] args) throws InvalidProtocolBufferException {
		//创建一个新的实例
		SubscribeReqProto.SubscribeReq subscribeReq = createSubscribeReq();
		System.out.println("Before encode is:"+subscribeReq.toString());
		SubscribeReqProto.SubscribeReq deSub = decode(encode(subscribeReq));
		System.out.println("after decode is :"+deSub.toString());
		System.out.println("assert is equal :"+(subscribeReq.equals(deSub)));
	}
}
