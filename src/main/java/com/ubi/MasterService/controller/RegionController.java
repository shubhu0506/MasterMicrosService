package com.ubi.MasterService.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.ubi.MasterService.dto.regionDto.RegionDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolMappingDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.service.RegionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/region")
public class RegionController {

	Logger logger = LoggerFactory.getLogger(RegionController.class);

	@Autowired
	private RegionService regionService;

	//@Operation(summary = "Create New Region", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping
	public ResponseEntity<Response<RegionDto>> insertRegion(@RequestBody RegionDto regionDto) {

		Response<RegionDto> response = this.regionService.addRegion(regionDto);

		return ResponseEntity.ok().body(response);

	}

	//@Operation(summary = "Get Region By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/{id}")
	public ResponseEntity<Response<RegionDto>> getSingleRegion(@PathVariable int id) {
		Response<RegionDto> response = regionService.getRegionById(id);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	//@Operation(summary = "Get All Region", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping()
	public ResponseEntity<Response<List<RegionDto>>> getPayments(
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "5", required = false) Integer pageSize) {
		Response<List<RegionDto>> response = regionService.getRegionDetails(pageNumber, pageSize);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

	}

	//@Operation(summary = "Delete Region By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@DeleteMapping("/{id}")
	public ResponseEntity<Response<RegionDto>> deletePaymentById(@PathVariable("id") int id) {

		Response<RegionDto> response = this.regionService.deleteRegionById(id);

		return ResponseEntity.ok().body(response);

	}

	//@Operation(summary = "Update Region with Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping
	public ResponseEntity<Response<RegionDto>> updatePayment(@RequestBody RegionDto region) { // NOSONAR

		Response<RegionDto> response = this.regionService.updateRegionDetails(region);

		return ResponseEntity.ok().body(response);

	}

	//@Operation(summary = "Get Region By Region Name", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/region/{name}")
	public ResponseEntity<Response<RegionDto>> getSingleRegion(@RequestParam String name) {
		Response<RegionDto> response = regionService.getRegionByName(name);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

//	@Operation(summary = "Map Region and School", security = @SecurityRequirement(name = "bearerAuth"))
	@PostMapping("/addSchool")
	public ResponseEntity<Response<RegionSchoolDto>> addSchool(@RequestBody RegionSchoolMappingDto regionSchoolDto) {
		Response<RegionSchoolDto> response = regionService.addSchool(regionSchoolDto);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Get School in Region", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/getRegion/{id}")
	public ResponseEntity<Response<RegionSchoolDto>> getSchoolInRegion(@PathVariable int id) {
		Response<RegionSchoolDto> response = regionService.getRegionwithSchool(id);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Get Region in Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<RegionDto>>> getRegionBySorting(@PathVariable String field) {
		Response<List<RegionDto>> response = regionService.getRegionwithSort(field);
		return ResponseEntity.ok().body(response);
	}

}
