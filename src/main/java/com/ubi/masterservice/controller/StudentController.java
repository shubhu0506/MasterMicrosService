package com.ubi.masterservice.controller;

import java.text.ParseException;
import java.util.List;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.studentDto.StudentPromoteDemoteDto;
import com.ubi.masterservice.dto.studentDto.StudentVerifyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.service.StudentServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentServiceImpl service;

	@PostMapping
	@Operation(summary = "Create New Student", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<StudentDto>> addStudent(@RequestBody StudentDto studentId) {
		Response<StudentDto> response = service.saveStudent(studentId);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get All Student", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping
	public ResponseEntity<Response<PaginationResponse<List<StudentDetailsDto>>>> getStudents(
			@RequestParam (defaultValue = "*") String fieldName,@RequestParam (defaultValue = "*") String searchByField,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize) throws ParseException {
		Response<PaginationResponse<List<StudentDetailsDto>>> response = service.getStudents(fieldName,searchByField,pageNumber, pageSize);
		return ResponseEntity.ok().body(response);

	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete Student By Id", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<StudentDto>> deleteStudent(@PathVariable Long id) {
		Response<StudentDto> response = service.deleteById(id);
		return ResponseEntity.ok().body(response);

	}

	@GetMapping("{id}")
	@Operation(summary = "Get Student By Id", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<StudentDetailsDto>> getStudentById(@PathVariable("id") Long id) {
		Response<StudentDetailsDto> response = service.getStudentById(id);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	//
	@PutMapping
	@Operation(summary = "Update Student", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<StudentDto>> updateStudent(@RequestBody StudentDto student) {
		Response<StudentDto> response = service.updateStudent(student);
		return ResponseEntity.ok().body(response);
	}


	@Operation(summary = "Change deactivate Status To Activate", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/activate/{id}")
	public ResponseEntity<Response<StudentDto>> activateStudentById(@PathVariable Long id) {
		Response<StudentDto> response = service.changeActiveStatusToTrue(id);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Change Active Status To deactivate", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/deactivate/{id}")
	public ResponseEntity<Response<StudentDto>> deactivateStudentById(@PathVariable Long id) {
		Response<StudentDto> response = service.changeActiveStatusToFalse(id);
		return ResponseEntity.ok().body(response);
	}


//	@Operation(summary = "Download Csv", security = @SecurityRequirement(name = "bearerAuth"))
//	@GetMapping("/download")
//	public ResponseEntity<Resource> getFile() {
//		String filename = "Student.csv";
//		InputStreamResource file = new InputStreamResource(service.load());
//
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//				.contentType(MediaType.parseMediaType("application/csv"))
//				.body(file);
//	}


	@Operation(summary = "Get By field", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/field")
	public ResponseEntity<Response<List<StudentDto>>> searchRecordsViaQueryField(@RequestParam  (defaultValue = "*")String gender,
																				 @RequestParam (defaultValue = "*")String category, @RequestParam(defaultValue = "*") String minority) {

		Response<List<StudentDto>> students = service.findByGenderAndCategoryAndMinority(gender, category, minority);
		return ResponseEntity.ok().body(students);
	}

	@Operation(summary = "Verified by Teacher", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/verifiedByTeacher/{userId}")
	public ResponseEntity<Response<List<StudentVerifyDto>>> verifyStudentById(@PathVariable String userId, @RequestBody StudentVerifyDto id) {
		Response<List<StudentVerifyDto>> response = service.verifiedByTeacher(userId,id);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Verified by Principal", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/verifiedByPrincipal/{userId}")
	public ResponseEntity<Response<List<StudentVerifyDto>>> principalverifyStudentById(@PathVariable String userId,@RequestBody StudentVerifyDto id) {
		Response<List<StudentVerifyDto>> response = service.verifiedByPrincipal(userId,id);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Student Promoted User By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping ("/promote/{userId}")
	public ResponseEntity<Response<StudentPromoteDemoteDto>>promoteStudent(@PathVariable String userId, @RequestBody StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {
		Response<StudentPromoteDemoteDto> response = service.studentPromoted(userId, studentPromoteDemoteCreationDto);
		return ResponseEntity.ok().body(response);
	}




	@Operation(summary = "Student Demoted User By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping ("/demote/{userId}")
	public ResponseEntity<Response<StudentPromoteDemoteDto>> demoteStudent(@PathVariable String userId, @RequestBody StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {
		Response<StudentPromoteDemoteDto> response = service.studentDemoted(userId, studentPromoteDemoteCreationDto);
		return ResponseEntity.ok().body(response);
	}


}