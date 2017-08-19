package com.example.demo.bean;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

	private static final long serialVersionUID = 7823907286705235147L;

	private Boolean isSuccess;

	private String message;

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResponseMessage [isSuccess=" + isSuccess + ", message=" + message + "]";
	}

}
