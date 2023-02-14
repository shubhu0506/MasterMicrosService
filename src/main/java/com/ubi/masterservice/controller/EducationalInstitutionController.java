package com.ubi.masterservice.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.service.EducationalInstitutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/educationalInstitution")
public class EducationalInstitutionController {

	@Autowired
	private EducationalInstitutionService educationalInstitutionService;

	Logger logger = LoggerFactory.getLogger(EducationalInstitutionController.class);

	@Operation(summary = "Create New Educational Institution", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping
	public ResponseEntity<Response<InstituteDto>> insertEducationalInstitution(
			@RequestBody InstituteCreationDto instituteCreationDto) { // NOSONAR

		Response<InstituteDto> response = this.educationalInstitutionService
				.addEducationalInstitution(instituteCreationDto);

		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Get Educational Institution By Name", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/{educationalInstitutionName}")
	public ResponseEntity<Response<InstituteDto>> getEducationalInstByName(
			@PathVariable String educationalInstitutionName) {
		Response<InstituteDto> response = educationalInstitutionService
				.getEducationalInstituteByName(educationalInstitutionName);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Get All Educational Institution", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping()
	public ResponseEntity<Response<PaginationResponse<List<InstituteDto>>>> getEducationalInstitutions(
			@RequestParam (defaultValue = "*") String fieldName,
			@RequestParam (defaultValue = "*") String searchByField,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize
		//	@RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
	)
	{
		Response<PaginationResponse<List<InstituteDto>>> response = educationalInstitutionService
				.getAllEducationalInstitutions(fieldName,searchByField,pageNumber, pageSize);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Delete Educational Institution By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@DeleteMapping("/{id}")
	public ResponseEntity<Response<InstituteDto>> deleteEducationalInstById(@PathVariable("id") int id) {
		Response<InstituteDto> response = this.educationalInstitutionService
				.deleteEducationalInstitution(id);

		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Update Educational Institution with Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping("/{instituteId}")
	public ResponseEntity<Response<InstituteDto>> updateEducationalInstutions(
			@RequestBody InstituteCreationDto instituteCreationDto,@PathVariable Long instituteId) { // NOSONAR

		Response<InstituteDto> updateEducationalInst = this.educationalInstitutionService
				.updateEducationalInstitution(instituteCreationDto,instituteId);

		return ResponseEntity.ok().body(updateEducationalInst);

	}

	@Operation(summary = "remove Educational Institution admin with Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping("/removeAdmin/{instituteId}")
	public ResponseEntity<Response<InstituteDto>> removeInstituteAdmin(
			@PathVariable("instituteId") String instituteId) { // NOSONAR

		Response<InstituteDto> updateEducationalInst = this.educationalInstitutionService
				.removeEducationalInstitutionAdmin(instituteId);
		return ResponseEntity.ok().body(updateEducationalInst);
	}

	@Operation(summary = "Get Region In EducationalInstitute", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/getEduInst/{id}")
	public ResponseEntity<Response<InstituteDto>> getRegionInEduIst(@PathVariable("id") int id) {
		Response<InstituteDto> response = educationalInstitutionService.getEduInstwithRegion(id);
		return ResponseEntity.ok().body(response);
	}

	// -----Sorting

	@Operation(summary = "Get EducationalInstitution in Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<InstituteDto>>> getEduIstBySorting(@PathVariable String field) {
		Response<List<InstituteDto>> response = educationalInstitutionService.getEduInstwithSort(field);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get Institute By Admin Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/admin/{adminId}")
	public ResponseEntity<Response<InstituteDto>> getInstituteByAdminId(@PathVariable String adminId) {
		Response<InstituteDto> response = educationalInstitutionService.getInstituteByAdminId(Long.parseLong(adminId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Region Inside Education Institute", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/regions/{instituteId}")
	public ResponseEntity<Response<PaginationResponse<List<RegionDetailsDto>>>> getAllRegionInsideInstitute(@PathVariable String instituteId,
																						 @RequestParam( defaultValue = "*") String fieldName,
																						 @RequestParam( defaultValue = "*") String fieldQuery,
																						 @RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
																						 @RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize ) {
		Response<PaginationResponse<List<RegionDetailsDto>>>  response = educationalInstitutionService.getAllRegionsByInstituteId(Integer.parseInt(instituteId),fieldName,fieldQuery,pageNumber,pageSize);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Teachers Inside Education Institute", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/teachers/{instituteId}")
	public ResponseEntity<Response<Set<TeacherDto>>> getAllTeachersInsideInstitute(@PathVariable String instituteId) {
		Response<Set<TeacherDto>> response = educationalInstitutionService.getAllTeacherByInstituteId(Integer.parseInt(instituteId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Schools Inside Education Institute", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/schools/{instituteId}")
	public ResponseEntity<Response<PaginationResponse<Set<SchoolRegionDto>>>> getAllSchoolsInsideInstitute(@PathVariable String instituteId,
																										   @RequestParam Boolean isCollege,
																										   @RequestParam( defaultValue = "*") String fieldName,
																										   @RequestParam( defaultValue = "*") String fieldQuery,
																										   @RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
																										   @RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize) {
		Response<PaginationResponse<Set<SchoolRegionDto>>> response = educationalInstitutionService.getAllSchoolByInstituteId(Integer.parseInt(instituteId),isCollege,fieldName,fieldQuery,pageNumber,pageSize);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Student by Institute Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/students/{instituteId}")
	public ResponseEntity<Response<PaginationResponse<List<StudentDetailsDto>>>> getStudents(
			@PathVariable Integer instituteId,
			@RequestParam( defaultValue = "*") String fieldName,
			@RequestParam( defaultValue = "*") String fieldQuery,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize
	) throws ParseException {
		Response<PaginationResponse<List<StudentDetailsDto>>> response = educationalInstitutionService.getStudentsByInstituteId(instituteId,fieldName,fieldQuery,pageNumber, pageSize);
		return ResponseEntity.ok().body(response);
	}
}