package com.ubi.masterservice.service;

import java.text.ParseException;
import java.util.List;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.dto.studentDto.StudentPromoteDemoteDto;
import com.ubi.masterservice.dto.studentDto.StudentVerifyDto;
import com.ubi.masterservice.entity.School;


public interface StudentService {
	Response<StudentDto> saveStudent(StudentDto studentDto);

	Response<PaginationResponse<List<StudentDetailsDto>>> getStudents(String fieldName,String searchByField,Integer PageNumber, Integer PageSize) throws ParseException;

	Response<StudentDetailsDto> getStudentById(Long id);

	Response<StudentDetailsDto> getStudentByRollNo(Long rollNo);

	public Response<StudentDto> deleteById(Long id);

	Response<StudentDto> updateStudent(StudentDto studentDto);

	Response<StudentDto> changeActiveStatusToTrue(Long id);

	Response<StudentDto> changeActiveStatusToFalse(Long id);

	Response<StudentDto> changeCurrentStatusToPromoted(Long id);

	Response<StudentDto> changeCurrentStatusToDemoted(Long id);

	Response<List<StudentDto>> findByGenderAndCategoryAndMinority(String gender,String category, String minority);

	Response<List<StudentVerifyDto>> verifiedByTeacher(String userId,StudentVerifyDto studentVerifyDto);

	Response<List<StudentVerifyDto>> verifiedByPrincipal(String userId, StudentVerifyDto studentVerifyDto);

	Response<StudentPromoteDemoteDto> studentPromoted(String userId, StudentPromoteDemoteDto studentPromoteDemoteCreationDto);

	Response<StudentPromoteDemoteDto> studentDemoted(String userId,StudentPromoteDemoteDto studentPromoteDemoteCreationDto);

}
