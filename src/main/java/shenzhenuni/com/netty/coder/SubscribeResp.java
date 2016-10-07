package shenzhenuni.com.netty.coder;

import java.io.Serializable;

public class SubscribeResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long subReqId ;
	
	private int subRespCode ;
	
	private String respDesc ;

	public long getSubReqId() {
		return subReqId;
	}

	public void setSubReqId(long subReqId) {
		this.subReqId = subReqId;
	}

	public int getSubRespCode() {
		return subRespCode;
	}

	public void setSubRespCode(int subRespCode) {
		this.subRespCode = subRespCode;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "SubscribeResp [subReqId=" + subReqId + ", subRespCode="
				+ subRespCode + ", respDesc=" + respDesc + "]";
	}
}
