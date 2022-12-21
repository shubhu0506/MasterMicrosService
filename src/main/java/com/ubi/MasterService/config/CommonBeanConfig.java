package com.ubi.MasterService.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBeanConfig {

	@Bean
	ModelMapper createModelMapperBean() {
		return new ModelMapper();
	}

}
