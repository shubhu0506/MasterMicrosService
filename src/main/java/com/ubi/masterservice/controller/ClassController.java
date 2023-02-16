package com.ubi.masterservice.controller;

import java.util.List;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.ClassStudentDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.service.ClassServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/class")
public class ClassController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassController.class);

	@Autowired
	private ClassServiceImpl classServiceImpl;

	@PostMapping
	@Operation(summary = "Add Class Details", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<ClassStudentDto>> addClassDetails(@RequestBody ClassDto classDto) {
		Response<ClassStudentDto> response = classServiceImpl.addClassDetails(classDto);
		return ResponseEntity.ok().body(response);
	}


	@Operation(summary = "Get All Class details", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping
	public ResponseEntity<Response<PaginationResponse<List<ClassStudentDto>>>> getClassDetails(
			@RequestParam( defaultValue = "*") String fieldName,
			@RequestParam( defaultValue = "*") String searchByField,
			@RequestParam(value = "PageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "PageSize", defaultValue = "10000000", required = false) Integer pageSize) {
		Response<PaginationResponse<List<ClassStudentDto>>> response = classServiceImpl.getClassDetails(fieldName, searchByField, pageNumber, pageSize);
		return ResponseEntity.ok().body(response);

	}


	@GetMapping("/{classid}")
	@Operation(summary = "Get Class By Id", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<ClassStudentDto>> getClassById(@PathVariable("classid") Long classidL) {
		Response<ClassStudentDto> response = classServiceImpl.getClassById(classidL);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}


	@GetMapping("/get/{className}")
	@Operation(summary = "Get Class By Name", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<ClassStudentDto>> getClassByName(@PathVariable("className") String className) {
		Response<ClassStudentDto> response = classServiceImpl.getClassByName(className);
		if (response.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@DeleteMapping("/{classid}")
	@Operation(summary = "Delete Class By Id", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<ClassDto>> deleteClassId(@PathVariable Long classid) {
		Response<ClassDto> response = classServiceImpl.deleteClassById(classid);
		return ResponseEntity.ok().body(response);

	}

	@PutMapping
	@Operation(summary = "Update Class Details", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<ClassStudentDto>> updateClassDetails(@RequestBody ClassDto classDetail) {
		Response<ClassStudentDto> response = classServiceImpl.updateClassDetails(classDetail);
		return ResponseEntity.ok().body(response);
	}

	@PutMapping("/removeTeacher/{classId}")
	@Operation(summary = "Update Class Details", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response<ClassStudentDto>> removeTeacherFromClass(@PathVariable("classId") String classId) {
		Response<ClassStudentDto> response = classServiceImpl.removeClassTeacher(classId);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "Get Class in Sorting", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/sort/{field}")
	public ResponseEntity<Response<List<ClassDto>>> getClasswithSort(@PathVariable String field) {
		Response<List<ClassDto>> response = classServiceImpl.getClasswithSort(field);
		return ResponseEntity.ok().body(response);
	}

//	@Operation(summary = "Download file ", security = @SecurityRequirement(name = "bearerAuth"))
//	@GetMapping("/download")
//	public ResponseEntity<Resource> getCSVFileData() {
//		String filename = "class.csv";
//		InputStreamResource file = new InputStreamResource(classServiceImpl.load());
//
//		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//				.contentType(MediaType.parseMediaType("application/csv")).body(file);
//	}

	@Operation(summary = "Get Class By Teacher Id", security = @SecurityRequirement(name = "bearerAuth"))
	@GetMapping("/teacher/{teacherId}")
	public ResponseEntity<Response<ClassDto>> getClassByTeacherId(@PathVariable String teacherId) {
		Response<ClassDto> response = classServiceImpl.getClassByTeacherId(Long.parseLong(teacherId));
		return ResponseEntity.ok().body(response);
	}

}
