package com.ubi.masterservice.service;

import java.util.List;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.ClassStudentDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.entity.ClassDetail;


public interface ClassService {

	Response<ClassStudentDto> addClassDetails(ClassDto classDto);

	Response<PaginationResponse<List<ClassStudentDto>>> getClassDetails(Integer PageNumber, Integer PageSize);

	public Response<ClassStudentDto> getClassById(Long classid);

	public Response<ClassDto> deleteClassById(Long classid);

	Response<ClassStudentDto> updateClassDetails(ClassDto classDto);

	Response<ClassStudentDto> getClassByName(String className);


	Response<List<ClassDto>> getClasswithSort(String field);

	Response<ClassDto> getClassByTeacherId(Long teacherId);

//	ByteArrayInputStream load();

}

