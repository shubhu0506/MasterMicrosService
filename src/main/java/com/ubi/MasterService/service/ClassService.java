package com.ubi.MasterService.service;

import java.util.List;

import com.ubi.MasterService.dto.classDto.ClassDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.studentDto.StudentDto;




public interface ClassService {

	Response<ClassDto> addClassDetails(ClassDto classDto);
	
	Response<List<ClassDto>> getClassDetails(Integer PageNumber, Integer PageSize);

	public Response<ClassDto> getClassById(Long classidL);
	
	public Response<ClassDto> deleteClassById(Long classidL);

	Response<ClassDto> updateClassDetails(ClassDto classDto);
	
	Response<ClassDto> getClassByName(String className);
	
	Response<List<StudentDto>> getClasswithStudent(Long id);
	
}

