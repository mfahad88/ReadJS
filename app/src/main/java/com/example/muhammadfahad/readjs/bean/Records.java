package com.example.muhammadfahad.readjs.bean;

import java.io.Serializable;



public class Records implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String MobileNo;

	private String Status;

	private String Version;


	public String getMobileNo() {
		return MobileNo;
	}
	public void setMobileNo(String mobileNo) {
		MobileNo = mobileNo;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}

}
