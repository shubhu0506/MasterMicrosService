package com.ubi.masterservice.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Set;


import com.ubi.masterservice.dto.educationalInstitutiondto.InstituteDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
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

import com.ubi.masterservice.dto.regionDto.RegionCreationDto;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
import com.ubi.masterservice.dto.regionDto.RegionSchoolDto;
import com.ubi.masterservice.dto.regionDto.RegionSchoolMappingDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.service.RegionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/region")
public class RegionController {

	Logger logger = LoggerFactory.getLogger(RegionController.class);

	@Autowired
	private RegionService regionService;

	@Operation(summary = "Create New Region", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping
	public ResponseEntity<Response<RegionDetailsDto>> insertRegion(@RequestBody RegionCreationDto regionCreationDto) {
		Response<RegionDetailsDto> response = this.regionService.addRegion(regionCreationDto);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get Region By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/{id}")
	public ResponseEntity<Response<RegionDetailsDto>> getSingleRegion(@PathVariable int id) {
		Response<RegionDetailsDto> response = regionService.getRegionById(id);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@Operation(summary = "Get All Region", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping()
//	@IsEducationInstituteHQAdmin
	public ResponseEntity<Response<PaginationResponse<List<RegionDetailsDto>>>> getAllRegions(
			@RequestParam( defaultValue = "*") String fieldName,
			@RequestParam( defaultValue = "*") String searchByField,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize  )
	{
		Response<PaginationResponse<List<RegionDetailsDto>>> response = regionService.getRegionDetails(fieldName, searchByField, pageNumber, pageSize  );
		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Delete Region By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@DeleteMapping("/{id}")
	public ResponseEntity<Response<RegionDto>> deleteRegionById(@PathVariable("id") int id) {

		Response<RegionDto> response = this.regionService.deleteRegionById(id);

		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Update Region with Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping("/{regionId}")
	public ResponseEntity<Response<RegionDetailsDto>> updateRegion(@RequestBody RegionCreationDto regionCreationDto,@PathVariable("regionId") Long regionId) { // NOSONAR

		Response<RegionDetailsDto> response = this.regionService.updateRegionDetails(regionCreationDto,regionId);

		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Get Region By Region Name", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/region/{name}")
	public ResponseEntity<Response<RegionDto>> getSingleRegion(@RequestParam String name) {
		Response<RegionDto> response = regionService.getRegionByName(name);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	// -----Sorting

	@Operation(summary = "Get Region in Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<RegionDetailsDto>>> getRegionBySorting(@PathVariable String field) {
		Response<List<RegionDetailsDto>> response = regionService.getRegionwithSort(field);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get Region By Admin Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/admin/{adminId}")
	public ResponseEntity<Response<RegionDetailsDto>> getRegionByAdminId(@PathVariable String adminId) {
		Response<RegionDetailsDto> response = regionService.getRegionByAdminId(Long.parseLong(adminId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Schools By Region Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/school/{regionId}")
	public ResponseEntity<Response<Set<SchoolRegionDto>>>  getAllSchoolsByRegionId(@PathVariable String regionId) {
		Response<Set<SchoolRegionDto>> response = regionService.getSchoolsByRegionId(Long.parseLong(regionId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Colleges By Region Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/college/{regionId}")
	public ResponseEntity<Response<Set<SchoolRegionDto>>>  getAllCollegesByRegionId(@PathVariable String regionId) {
		Response<Set<SchoolRegionDto>> response = regionService.getCollegeByRegionId(Long.parseLong(regionId));
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Student by Region Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/students/{regionId}")
	public ResponseEntity<Response<PaginationResponse<List<StudentDetailsDto>>>> getStudents(
			@PathVariable Integer regionId,
			@RequestParam( defaultValue = "*") String fieldName,
			@RequestParam( defaultValue = "*") String fieldQuery,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize
	) throws ParseException {
		Response<PaginationResponse<List<StudentDetailsDto>>> response = regionService.getStudentsByRegionId(regionId,fieldName,fieldQuery,pageNumber, pageSize);
		return ResponseEntity.ok().body(response);
	}

}