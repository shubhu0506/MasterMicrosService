package com.ubi.MasterService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.studentDto.StudentDto;
import com.ubi.MasterService.service.StudentServiceImpl;

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
	public ResponseEntity<Response<List<StudentDto>>> getStudents(
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "5", required = false) Integer pageSize) {
		Response<List<StudentDto>> response = service.getStudents(pageNumber, pageSize);
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
	public ResponseEntity<Response> getStudentById(@PathVariable("id") Long id) {
		Response response = service.getStudentById(id);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}


	@PutMapping
	//@Operation(summary = "Update Student", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> updateStudent(@RequestBody StudentDto student) {
		Response<StudentDto> response = service.updateStudent(student);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Change Active Status To True Of Student By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/activate/{id}")
	public ResponseEntity<Response<StudentDto>> activateStudentById(@PathVariable Long id) {
		Response<StudentDto> response = service.changeActiveStatusToTrue(id);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Change Active Status To True Of Student By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/deactivate/{id}")
	public ResponseEntity<Response<StudentDto>> deactivateStudentById(@PathVariable Long id) {
		Response<StudentDto> response = service.changeActiveStatusToFalse(id);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Change Current Status To Promoted Of Student By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/promoted/{id}")
	public ResponseEntity<Response<StudentDto>> changeCurrentStatusToPromoted(@PathVariable Long id) {
		Response<StudentDto> response = service.changeCurrentStatusToPromoted(id);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Change Current Status To Domoted Of Student By Id", security = @SecurityRequirement(name = "bearerAuth"))
	@PatchMapping("/demoted/{id}")
	public ResponseEntity<Response<StudentDto>> changeCurrentStatusToDomoted(@PathVariable Long id) {
		Response<StudentDto> response = service.changeCurrentStatusToDemoted(id);
		return ResponseEntity.ok().body(response);
	}

	//@Operation(summary = "Get By field", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/field")
	public ResponseEntity<Response<List<StudentDto>>> searchRecordsViaQueryField(
			@RequestParam(defaultValue = "*") String gender, @RequestParam(defaultValue = "*") String category,
			@RequestParam(defaultValue = "*") String minority) {

		Response<List<StudentDto>> students = service.findByGenderAndCategoryAndMinority(gender, category, minority);
		return ResponseEntity.ok().body(students);
	}

}
