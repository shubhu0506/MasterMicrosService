package com.ubi.reportservice.externalservices;

import com.ubi.reportservice.dto.jwt.ValidateJwt;
import com.ubi.reportservice.dto.response.Response;
import com.ubi.reportservice.dto.user.UserPermissionsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="USER-SERVICE")
@Component
public interface UserFeignService {

    @PostMapping("/validate")
    public ResponseEntity<Response<UserPermissionsDto>> validateTokenAndGetUser(@RequestBody ValidateJwt validateJwt);
}
