package com.ubi.reportservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/class")
public class ClassController
{
	@GetMapping
	@Operation(summary = "Sample Controlelr", security = @SecurityRequirement(name = "bearerAuth"))
	public String getString(){
		return "hello string";
	}
}