package com.ubi.MasterService.externalServices;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="USER-SERVICE")
public interface UserService {

}
