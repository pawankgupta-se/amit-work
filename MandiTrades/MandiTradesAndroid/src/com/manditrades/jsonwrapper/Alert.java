package com.manditrades.jsonwrapper;

import com.google.gson.annotations.SerializedName;

public class Alert extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	@SerializedName("_id")
	private MTId alertId;

	@SerializedName("mobile_no")
	private String mobileNo;

	@SerializedName("commodity")
	private String commodity;

	@SerializedName("deviceToken")
	private String deviceToken;

	@SerializedName("device_info")
	private String deviceInfo;

	@SerializedName("price")
	private String price;

	@SerializedName("status")
	private String status;

	@SerializedName("date_of_creation")
	private CreationDate dateOfCreation;

	@SerializedName("deviceArn")
	private String deviceArn;

	public MTId getAlertId() {
		return alertId;
	}

	public void setAlertId(MTId alertId) {
		this.alertId = alertId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CreationDate getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(CreationDate dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public String getDeviceArn() {
		return deviceArn;
	}

	public void setDeviceArn(String deviceArn) {
		this.deviceArn = deviceArn;
	}

}
