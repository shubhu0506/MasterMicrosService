package com.ubi.masterservice.externalServices;

import com.ubi.masterservice.dto.jwt.ValidateJwt;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.user.UserPermissionsDto;
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
