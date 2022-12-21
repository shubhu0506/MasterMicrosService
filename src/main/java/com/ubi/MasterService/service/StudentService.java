package com.ubi.MasterService.service;

import java.util.List;

import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.studentDto.StudentDto;


public interface StudentService {
	Response<StudentDto> saveStudent(StudentDto studentDto);

	Response<List<StudentDto>> getStudents(Integer PageNumber, Integer PageSize);

	Response<StudentDto> getStudentById(Long id);

	public Response<StudentDto> deleteById(Long id);

	Response<StudentDto> updateStudent(StudentDto studentDto);
	
	Response<StudentDto> changeActiveStatusToTrue(Long id);

    Response<StudentDto> changeActiveStatusToFalse(Long id);
    
	Response<StudentDto> changeCurrentStatusToPromoted(Long id);

	Response<StudentDto> changeCurrentStatusToDemoted(Long id);
	
	Response<List<StudentDto>> findByGenderAndCategoryAndMinority(String gender,String category, String minority);
	
	
	
	
}
