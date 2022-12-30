package com.ubi.MasterService.config;

import com.ubi.MasterService.dto.jwt.ValidateJwt;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.user.UserPermissionsDto;
import com.ubi.MasterService.externalServices.UserFeignService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CommonBeanConfig {

	@Bean
	ModelMapper createModelMapperBean(){
		return new ModelMapper();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
