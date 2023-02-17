package com.ubi.masterservice.service;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionCreationDto;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;

public interface RegionService {

	Response<RegionDetailsDto> addRegion(RegionCreationDto regionCreationDto);

	Response<PaginationResponse<List<RegionDetailsDto>>> getRegionDetails(String fieldName, String searchByField ,Integer PageSize,Integer PageNumber );

	public Response<RegionDetailsDto> getRegionById(int id);

	public Response<RegionDto> deleteRegionById(int id);

	Response<RegionDetailsDto> updateRegionDetails(RegionCreationDto regionCreationDto,Long regionId);

	Response<RegionDetailsDto> removeRegionAdmin(String regionId);

//	ByteArrayInputStream load();

//	ByteArrayInputStream Regionload();

	Response<RegionDto> getRegionByName(String name);

	Response<List<RegionDetailsDto>> getRegionwithSort(String field);

	Response<RegionDetailsDto> getRegionByAdminId(Long adminId);

	Response<Set<SchoolRegionDto>> getSchoolsByRegionId(Long regionId);

	Response<Set<SchoolRegionDto>> getCollegeByRegionId(Long regionId);

	Response<PaginationResponse<List<StudentDetailsDto>>> getStudentsByRegionId(Integer instituteId, String fieldName, String searchByField, Integer PageNumber, Integer PageSize) throws ParseException;

}

