package com.ubi.masterservice.error;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestControllerAdvice {
	private final Logger log = LoggerFactory.getLogger(RestControllerAdvice.class);

	@ExceptionHandler({CustomException.class})
	public ResponseEntity<Object> handleGenericException(CustomException exception) {

		log.info("Custom exception Occured" + exception.getExceptionMessage());

		return new ResponseEntity<>(
				new RestApiErrorHandling(
						exception.getExceptionCode(),
						exception.getStatus(),
						exception.getExceptionMessage(), exception.getResult()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({FeignException.BadRequest.class})
	public ResponseEntity<Object> handleFeignGenericException(FeignException.BadRequest exception) {
		log.info("Exception occured in feign client response");
		this.getCustomExceptionFromString(exception.getMessage());
		FeignClientErrorHandling feignClientErrorHandling = this.getCustomExceptionFromString(exception.getMessage());
		return new ResponseEntity<>(
				feignClientErrorHandling,
				HttpStatus.BAD_REQUEST);
	}

	public FeignClientErrorHandling getCustomExceptionFromString(String exceptionString){
		String value = exceptionString;

		value = value.replaceAll("\\\\", value);
		int index1 = value.indexOf("statusCode");
		String statusCode = value.substring(index1+12,index1+15);

		int index2 = value.indexOf("message");
		StringBuilder message = new StringBuilder();

		for(int i = index2+10;i<value.length();i++){
			if(value.charAt(i) == '\"')break;
			message.append(value.charAt(i));
		}

		int index3 = value.indexOf("status\"");
		StringBuilder status = new StringBuilder();

		for(int i = index3+9;i<value.length();i++){
			if(value.charAt(i) == '\"')break;
			status.append(value.charAt(i));
		}

		return new FeignClientErrorHandling(
				Integer.parseInt(statusCode),
				HttpStatusCode.BAD_REQUEST_EXCEPTION,
				message.toString(),
				message.toString());
	}
}