package com.ubi.masterservice.error;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ubi.masterservice.dto.response.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RestApiErrorHandling extends BaseResponse {

	private HttpStatusCode status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

	private Result<?> result;

	private RestApiErrorHandling() {
		timestamp = LocalDateTime.now();
	}

	RestApiErrorHandling(int statuscode, HttpStatusCode status, String message,Result<?> result) {
		this();
		super.statusCode = statuscode;
		this.status = status;
		super.message = message;
		this.result=result;
	}
}