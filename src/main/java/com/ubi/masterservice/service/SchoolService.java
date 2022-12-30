package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;
import com.ubi.MasterService.dto.schoolDto.SchoolRegionDto;

public interface SchoolService {
	
	Response<SchoolRegionDto> addSchool(SchoolDto schoolDto);

	Response<List<SchoolRegionDto>> getAllSchools(Integer PageNumber, Integer PageSize);

	Response<SchoolRegionDto> getSchoolById(int schoolId);
	
	Response<SchoolRegionDto> getSchoolByName(String name);

	public Response<SchoolDto> deleteSchoolById(int schoolId);

    Response<SchoolRegionDto> updateSchool(SchoolDto schoolDto);

	Response<List<SchoolDto>> getSchoolwithSort(String field);

//	ByteArrayInputStream loadSchoolAndClass();
	
	
	
}
