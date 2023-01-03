package com.ubi.masterservice.controller;

import java.util.List;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
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

import com.ubi.masterservice.dto.educationalInstitutiondto.EducationRegionGetDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalRegionDto;
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
	public ResponseEntity<Response<EducationalRegionDto>> insertEducationalInstitution(
			@RequestBody EducationalInstitutionDto educationalInstitutionDto) { // NOSONAR

		Response<EducationalRegionDto> response = this.educationalInstitutionService
				.addEducationalInstitution(educationalInstitutionDto);

		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Get Educational Institution By Name", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("name/{educationalInstitutionName}")
	public ResponseEntity<Response<EducationRegionGetDto>> getEducationalInstByName(
			@PathVariable String educationalInstitutionName) {
		Response<EducationRegionGetDto> response = educationalInstitutionService
				.getEducationalInstituteByName(educationalInstitutionName);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

	}

	@Operation(summary = "Get All Educational Institution", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping()
	public ResponseEntity<Response<PaginationResponse<List<EducationRegionGetDto>>>> getEducationalInstitutions(
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "5", required = false) Integer pageSize
	)
	{
		Response<PaginationResponse<List<EducationRegionGetDto>>> response = educationalInstitutionService
				.getAllEducationalInstitutions(pageNumber, pageSize);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@Operation(summary = "Delete Educational Institution By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@DeleteMapping("/{id}")
	public ResponseEntity<Response<EducationalInstitutionDto>> deleteEducationalInstById(@PathVariable("id") int id) {

		Response<EducationalInstitutionDto> response = this.educationalInstitutionService
				.deleteEducationalInstitution(id);

		return ResponseEntity.ok().body(response);

	}

	@Operation(summary = "Update Educational Institution with Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PutMapping
	public ResponseEntity<Response<EducationalRegionDto>> updateEducationalInstutions(
			@RequestBody EducationalInstitutionDto educationalInstitutionDto) { // NOSONAR

		Response<EducationalRegionDto> updateEducationalInst = this.educationalInstitutionService
				.updateEducationalInstitution(educationalInstitutionDto);

		return ResponseEntity.ok().body(updateEducationalInst);

	}

	@Operation(summary = "Get Region In EducationalInstitute", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/getEduInst/{id}")
	public ResponseEntity<Response<EducationRegionGetDto>> getRegionInEduIst(@PathVariable int id) {
		Response<EducationRegionGetDto> response = educationalInstitutionService.getEduInstwithRegion(id);
		return ResponseEntity.ok().body(response);
	}

	// -----Sorting

	@Operation(summary = "Get EducationalInstitution in Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<EducationalInstitutionDto>>> getEduIstBySorting(@PathVariable String field) {
		Response<List<EducationalInstitutionDto>> response = educationalInstitutionService.getEduInstwithSort(field);
		return ResponseEntity.ok().body(response);
	}

//	@Operation(summary = "Download file ", security = @SecurityRequirement(name = "bearerAuth"))
//	@GetMapping("/download")
//	public ResponseEntity<Resource> getCSVFileData() {
//		String filename = "education.csv";
//		InputStreamResource file = new InputStreamResource(educationalInstitutionService.load());
//
//		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//				.contentType(MediaType.parseMediaType("application/csv")).body(file);
//	}

}