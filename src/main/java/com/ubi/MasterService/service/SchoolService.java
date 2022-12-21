package com.ubi.MasterService.service;

import java.text.ParseException;
import java.util.List;

import com.ubi.MasterService.dto.classDto.SchholClassMappingDto;
import com.ubi.MasterService.dto.classDto.SchoolClassDto;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;

public interface SchoolService {
	
	Response<SchoolDto> addSchool(SchoolDto schoolDto);

	Response<List<SchoolDto>> getAllSchools(Integer PageNumber, Integer PageSize);

	Response<SchoolDto> getSchoolById(int schoolId);
	
	Response<SchoolDto> getSchoolByName(String name);

	public Response<SchoolDto> deleteSchoolById(int schoolId);

	Response<SchoolDto> updateSchool(SchoolDto schoolDto) throws ParseException;
	
	Response<SchoolClassDto> addClass(SchholClassMappingDto schoolClassMappingDto);

	Response<SchoolClassDto> getSchoolwithClass(int id);

	Response<List<SchoolDto>> getSchoolwithSort(String field);
	
	
}
