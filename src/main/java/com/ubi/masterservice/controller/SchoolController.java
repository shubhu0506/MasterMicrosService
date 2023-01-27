package com.ubi.masterservice.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;


import com.ubi.masterservice.dto.educationalInstitutiondto.InstituteDto;
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.service.SchoolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



@RestController
@RequestMapping("/school")
public class SchoolController {

	Logger logger = LoggerFactory.getLogger(SchoolController.class);

	@Autowired
	private SchoolService schoolService;

	@Operation(summary = "Create New School", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping
	public ResponseEntity<Response<SchoolRegionDto>> addSchool(@Valid @RequestBody SchoolDto schoolDto) {
		Response<SchoolRegionDto> schoolResponse = schoolService.addSchool(schoolDto);
		return ResponseEntity.ok().body(schoolResponse);

	}
	
	@Operation(summary = "Get All Schools ", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping
	public ResponseEntity<Response<PaginationResponse<List<SchoolRegionDto>>>> getAllSchools(
			@RequestParam (defaultValue = "*") String fieldName,
			@RequestParam (defaultValue = "*") String searchByField,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "5", required = false) Integer pageSize) {
		Response<PaginationResponse<List<SchoolRegionDto>>> response = schoolService.getAllSchools(fieldName,searchByField,pageNumber, pageSize);
		return ResponseEntity.ok().body(response);
	}
	
	@Operation(summary = "Get All Colleges ", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/colleges")
	public ResponseEntity<Response<PaginationResponse<List<SchoolRegionDto>>>> getAllColleges(
			@RequestParam (defaultValue = "*") String fieldName,
			@RequestParam (defaultValue = "*") String searchByField,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "5", required = false) Integer pageSize) {
		Response<PaginationResponse<List<SchoolRegionDto>>> response = schoolService.getAllColleges(fieldName, searchByField, pageNumber, pageSize);
		return ResponseEntity.ok().body(response);
	}
	
	@Operation(summary = "Delete School By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@DeleteMapping("/{schoolId}")
	public ResponseEntity<Response<SchoolDto>> deleteSchoolById(@PathVariable("schoolId") int schoolId)
	{
		Response<SchoolDto> response = schoolService.deleteSchoolById(schoolId);
		return ResponseEntity.ok().body(response);
	}
	
	@Operation(summary = "Get Single School By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/school/{id}")
	public ResponseEntity<Response<SchoolRegionDto>> getSchoolById(@PathVariable int id) {
		Response<SchoolRegionDto> response=schoolService.getSchoolById(id);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Update School By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping("/{schoolId}")
	public ResponseEntity<Response<SchoolRegionDto>> updateSchool(@Valid @RequestBody SchoolDto schoolDto) { // NOSONAR
		Response<SchoolRegionDto> response=schoolService.updateSchool(schoolDto);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get School By Name", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/{name}")
	public ResponseEntity<Response<SchoolRegionDto>> getSchoolByName(@PathVariable("name") String schoolName) {
		Response<SchoolRegionDto> response=schoolService.getSchoolByName(schoolName);
		return ResponseEntity.ok().body(response);
	}
	//
//	@Operation(summary = "Map School and  Class", security = @SecurityRequirement(name = "bearerAuth"))
//	@PostMapping("/addClass")
//	public ResponseEntity<Response<SchoolClassDto>> addClass(@RequestBody SchholClassMappingDto schoolClassDto) {
//		Response<SchoolClassDto> response = schoolService.addClass(schoolClassDto);
//		return ResponseEntity.ok().body(response);
//	}
//
//	@Operation(summary = "Get Classes In Schools", security = @SecurityRequirement(name = "bearerAuth"))
//	@GetMapping("/getsch/{id}")
//	@IsPrincipal
//	public ResponseEntity<Response<SchoolClassDto>> getClassInSchool(@PathVariable int id) {
//		Response<SchoolClassDto> response = schoolService.getSchoolwithClass(id);
//		return ResponseEntity.ok().body(response);
//	}
//
	@Operation(summary = "Get School With Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<SchoolDto>>> getSchoolBySorting(@PathVariable String field) {
		Response<List<SchoolDto>> response = schoolService.getSchoolwithSort(field);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get School By Principal Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/principal/{principalId}")
	public ResponseEntity<Response<SchoolRegionDto>> getSchoolByPrincipalId(@PathVariable String principalId) {
		Response<SchoolRegionDto> response = schoolService.getSchoolByPrincipalId(Long.parseLong(principalId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get College By Principal Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/collegeprincipal/{principalId}")
	public ResponseEntity<Response<SchoolRegionDto>> getCollegeByPrincipalId(@PathVariable String principalId) {
		Response<SchoolRegionDto> response = schoolService.getCollegeByPrincipalId(Long.parseLong(principalId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Teachers By School Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/teachers/{schoolId}")
	public ResponseEntity<Response<Set<TeacherDto>>> getTeachersBySchool(@PathVariable String schoolId) {
		Response<Set<TeacherDto>> response = schoolService.getAllTeacherBySchoolId(Integer.parseInt(schoolId));
		return ResponseEntity.ok().body(response);
	}

}

