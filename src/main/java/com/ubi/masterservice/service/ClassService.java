package com.ubi.MasterService.service;

import java.util.List;

import com.ubi.MasterService.dto.classDto.ClassDto;
import com.ubi.MasterService.dto.classDto.ClassStudentDto;
import com.ubi.MasterService.dto.response.Response;



public interface ClassService {

	Response<ClassStudentDto> addClassDetails(ClassDto classDto);
	
	Response<List<ClassStudentDto>> getClassDetails(Integer PageNumber, Integer PageSize);

	public Response<ClassStudentDto> getClassById(Long classid);
	
	public Response<ClassDto> deleteClassById(Long classid);

	Response<ClassStudentDto> updateClassDetails(ClassDto classDto);
	
	Response<ClassStudentDto> getClassByName(String className);

	
	Response<List<ClassDto>> getClasswithSort(String field);
	
//	ByteArrayInputStream load();
	
}

