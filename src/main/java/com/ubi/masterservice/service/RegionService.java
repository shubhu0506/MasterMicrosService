package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.MasterService.dto.regionDto.RegionCreationDto;
import com.ubi.MasterService.dto.regionDto.RegionDetailsDto;
import com.ubi.MasterService.dto.regionDto.RegionDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolMappingDto;
import com.ubi.MasterService.dto.response.Response;

public interface RegionService {

	Response<RegionDetailsDto> addRegion(RegionCreationDto regionCreationDto);

	Response<List<RegionDetailsDto>> getRegionDetails(Integer PageNumber, Integer PageSize);

	public Response<RegionDetailsDto> getRegionById(int id);

	public Response<RegionDto> deleteRegionById(int id);

	Response<RegionDto> updateRegionDetails(RegionDto regionDto);
	
//	ByteArrayInputStream load();
//	
//	ByteArrayInputStream Regionload();
	
	Response<RegionDto> getRegionByName(String name);
	
	Response<RegionSchoolDto> addSchool(RegionSchoolMappingDto regionSchoolMappingDto);

	Response<RegionSchoolDto> getRegionwithSchool(int id);
	
	Response<List<RegionDetailsDto>> getRegionwithSort(String field);

}

