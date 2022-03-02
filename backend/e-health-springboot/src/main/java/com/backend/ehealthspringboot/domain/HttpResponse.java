package com.backend.ehealthspringboot.domain;

import java.util.Date;
import org.springframework.http.HttpStatus;
import lombok.Data;

@Data
public class HttpResponse {
	
	private Date timeStamp;
	private int httpStatusCode; // 200, 201, 400, 500
	private HttpStatus httpStatus;
	private String reason;
	private String message;
	
	public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

}
