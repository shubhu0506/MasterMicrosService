package com.ubi.masterservice.externalServices;


import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentFees.StudentFeesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="FEES-SERVICE")
@Component
public interface FeesFeignService {

    @GetMapping("/studentFees/{studentId}")
    public ResponseEntity<Response<StudentFeesDto>> getStudentFeesForCurrentPaymentCycle(@RequestHeader(value = "Authorization", required = true) String authorizationHeaderToken, @PathVariable("studentId") Long studentId);
}
