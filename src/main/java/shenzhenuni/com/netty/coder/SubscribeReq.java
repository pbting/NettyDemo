package shenzhenuni.com.netty.coder;

import java.io.Serializable;

public class SubscribeReq implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long subSequence ;
	
	private String userName ;
	
	private String productName ;
	
	private String phoneNumber ;
	
	private String address ;

	public long getSubSequence() {
		return subSequence;
	}

	public void setSubSequence(long subSequence) {
		this.subSequence = subSequence;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "SubscribeReq [subSequence=" + subSequence + ", userName="
				+ userName + ", productName=" + productName + ", phoneNumber="
				+ phoneNumber + ", address=" + address + "]";
	}
}
