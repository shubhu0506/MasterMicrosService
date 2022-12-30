package com.ubi.MasterService.externalServices;

import com.ubi.MasterService.dto.jwt.ValidateJwt;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.user.UserPermissionsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="USER-SERVICE")
@Component
public interface UserFeignService {

    @PostMapping("/validate")
    public ResponseEntity<Response<UserPermissionsDto>> validateTokenAndGetUser(@RequestBody ValidateJwt validateJwt);
}
