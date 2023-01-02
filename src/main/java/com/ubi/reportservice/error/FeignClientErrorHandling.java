package com.ubi.reportservice.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ubi.reportservice.dto.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FeignClientErrorHandling extends BaseResponse {

    private HttpStatusCode status;

    private String messageFromFeignClient;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private FeignClientErrorHandling() {
        timestamp = LocalDateTime.now();
    }

    FeignClientErrorHandling(int statuscode, HttpStatusCode status, String message,String messageFromFeignClient) {
        this();
        super.statusCode = statuscode;
        this.status = status;
        super.message = message;
        this.messageFromFeignClient = messageFromFeignClient;
    }
}