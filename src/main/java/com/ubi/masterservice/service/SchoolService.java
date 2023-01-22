package com.ubi.masterservice.service;

import java.util.List;
import java.util.Set;

import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.GetSchoolDetails;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.entity.School;

public interface SchoolService {

	Response<SchoolRegionDto> addSchool(SchoolDto schoolDto);
	
	Response<PaginationResponse<List<SchoolRegionDto>>> getAllSchools(Integer PageNumber, Integer PageSize);

	Response<PaginationResponse<List<SchoolRegionDto>>> getAllColleges(Integer PageNumber, Integer PageSize);

	Response<SchoolRegionDto> getSchoolById(int schoolId);

	Response<SchoolRegionDto> getSchoolByName(String name);

	public Response<SchoolDto> deleteSchoolById(int schoolId);

	Response<SchoolRegionDto> updateSchool(SchoolDto schoolDto);

	Response<List<SchoolDto>> getSchoolwithSort(String field);

	Response<SchoolRegionDto> getSchoolByPrincipalId(Long principalId);

	Response<Set<TeacherDto>> getAllTeacherBySchoolId(int schoolId);
}
