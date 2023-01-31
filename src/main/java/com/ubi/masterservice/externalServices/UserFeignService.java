package com.ubi.masterservice.externalServices;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.ubi.masterservice.dto.jwt.ValidateJwt;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.dto.user.UserPermissionsDto;


@FeignClient(name="USER-SERVICE")
@Component
public interface UserFeignService {

    @PostMapping("/validate")
    public ResponseEntity<Response<UserPermissionsDto>> validateTokenAndGetUser(@RequestBody ValidateJwt validateJwt);
    
    @GetMapping("/user/{roleType}/{userId}")
    public ResponseEntity<Response<Boolean>> checkIfUserExists(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable String roleType, @PathVariable String userId);
    
    @GetMapping("/user/principal/{principalId}")
    public ResponseEntity<Response<UserDto>> getPrincipalById(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,@PathVariable String principalId);

    @GetMapping("/user/teacher/{teacherId}")
    public ResponseEntity<Response<UserDto>> getTeacherById(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,@PathVariable String teacherId);

    @GetMapping("/user/regionadmin/{regionAdminId}")
    public ResponseEntity<Response<UserDto>> getRegionAdminById(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,@PathVariable String regionAdminId);

    @GetMapping("/user/instituteadmin/{instituteAdminId}")
    public ResponseEntity<Response<UserDto>> getInstituteAdminById(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,@PathVariable String instituteAdminId);
}
