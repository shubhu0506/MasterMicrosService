package com.ubi.MasterService.controller;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubi.MasterService.dto.classDto.SchholClassMappingDto;
import com.ubi.MasterService.dto.classDto.SchoolClassDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;
import com.ubi.MasterService.service.SchoolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



@RestController
@RequestMapping("/school")
public class SchoolController {

	Logger logger = LoggerFactory.getLogger(SchoolController.class);

	@Autowired
	private SchoolService schoolService;
	
//	@Operation(summary = "Create New School", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping
	public ResponseEntity<Response<SchoolDto>> addSchool(@Valid @RequestBody SchoolDto schoolDto) {
		Response<SchoolDto> schoolResponse = schoolService.addSchool(schoolDto);
		return ResponseEntity.ok().body(schoolResponse);

	}

//	@Operation(summary = "Get All Schools ", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping
	public ResponseEntity<Response<List<SchoolDto>>> getAllSchools(
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "5", required = false) Integer pageSize) {
		Response<List<SchoolDto>> response = schoolService.getAllSchools(pageNumber, pageSize);
		return ResponseEntity.ok().body(response);

	}
	//@Operation(summary = "Delete School By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@DeleteMapping("/{schoolId}")
	public ResponseEntity<Response<SchoolDto>> deleteSchoolById(@PathVariable("schoolId") int schoolId) 
	{
		Response<SchoolDto> response = schoolService.deleteSchoolById(schoolId);
		return ResponseEntity.ok().body(response);
	}
	
	//@Operation(summary = "Get Single School By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/school/{id}")
	public ResponseEntity<Response<SchoolDto>> getSchoolById(@PathVariable int id) {
	  Response<SchoolDto> response=schoolService.getSchoolById(id);
	  return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Update School By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping("/{schoolId}")
	public ResponseEntity<Response<SchoolDto>> updateSchool(@Valid @RequestBody SchoolDto schoolDto) throws ParseException { // NOSONAR
	  Response<SchoolDto> response=schoolService.updateSchool(schoolDto);
	  return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Get School By Name", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/{name}")
	public ResponseEntity<Response<SchoolDto>> getSchoolByName(@PathVariable("name") String schoolName) {
	  Response<SchoolDto> response=schoolService.getSchoolByName(schoolName);
	  return ResponseEntity.ok().body(response);
	}
	
	//@Operation(summary = "Map School and  Class", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping("/addClass")
	public ResponseEntity<Response<SchoolClassDto>> addClass(@RequestBody SchholClassMappingDto schoolClassDto) {
		Response<SchoolClassDto> response = schoolService.addClass(schoolClassDto);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Get Classes In Schools", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/getsch/{id}")
	public ResponseEntity<Response<SchoolClassDto>> getClassInSchool(@PathVariable int id) {
		Response<SchoolClassDto> response = schoolService.getSchoolwithClass(id);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Get School With Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<SchoolDto>>> getSchoolBySorting(@PathVariable String field) {
		Response<List<SchoolDto>> response = schoolService.getSchoolwithSort(field);
		return ResponseEntity.ok().body(response);
	}
	
	
	
	
	
	
}
