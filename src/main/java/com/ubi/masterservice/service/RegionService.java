package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionCreationDto;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
import com.ubi.masterservice.dto.regionDto.RegionSchoolDto;
import com.ubi.masterservice.dto.regionDto.RegionSchoolMappingDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.entity.Region;

public interface RegionService {

	Response<RegionDetailsDto> addRegion(RegionCreationDto regionCreationDto);

	Response<PaginationResponse<List<RegionDetailsDto>>> getRegionDetails(String fieldName, String searchByField ,Integer PageSize,Integer PageNumber );

	public Response<RegionDetailsDto> getRegionById(int id);

	public Response<RegionDto> deleteRegionById(int id);

	Response<RegionDetailsDto> updateRegionDetails(RegionCreationDto regionCreationDto,Long regionId);

//	ByteArrayInputStream load();

//	ByteArrayInputStream Regionload();

	Response<RegionDto> getRegionByName(String name);

	Response<List<RegionDetailsDto>> getRegionwithSort(String field);

	Response<RegionDetailsDto> getRegionByAdminId(Long adminId);
}

