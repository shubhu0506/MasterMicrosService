package com.ubi.masterservice.service;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.SchoolCreationDto;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;

public interface SchoolService {

	Response<SchoolRegionDto> addSchool(SchoolCreationDto schoolDto);
	
	Response<PaginationResponse<List<SchoolRegionDto>>> getAllSchools(String fieldName, String searchByField,Integer PageNumber, Integer PageSize);

	Response<PaginationResponse<List<SchoolRegionDto>>> getAllColleges(String fieldName, String searchByField, Integer PageNumber, Integer PageSize);
	
	Response<SchoolRegionDto> getSchoolById(int schoolId);


	public Response<SchoolDto> deleteSchoolById(int schoolId);

	Response<SchoolRegionDto> updateSchool(SchoolCreationDto schoolCreationDto, int schoolId);

	Response<List<SchoolDto>> getSchoolwithSort(String field);

	Response<SchoolRegionDto> getSchoolByPrincipalId(Long principalId);

	Response<SchoolRegionDto> getCollegeByPrincipalId(Long principalId);

	Response<Set<TeacherDto>> getAllTeacherBySchoolId(int schoolId);

	Response<PaginationResponse<List<StudentDetailsDto>>> getStudentsBySchoolId(Integer schoolId, String fieldName, String searchByField, Integer PageNumber, Integer PageSize) throws ParseException;

}
