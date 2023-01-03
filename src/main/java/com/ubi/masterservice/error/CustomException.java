package com.ubi.masterservice.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;
	private final int exceptionCode;
	private final HttpStatusCode status;
	private final String exceptionMessage;
	private final Result<?> result;

}
