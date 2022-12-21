package com.ubi.MasterService.error;

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
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Object> handleGenericException(CustomException exception) {

		log.info("Custom exception Occured" + exception.getExceptionMessage());
		
		return new ResponseEntity<>(
				new RestApiErrorHandling(
						exception.getExceptionCode(), 
						exception.getStatus(), 
						exception.getExceptionMessage(), exception.getResult()), 
				HttpStatus.BAD_REQUEST);
	}
	
}