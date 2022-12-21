package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.MasterService.dto.regionDto.EIRegionMappingDto;
import com.ubi.MasterService.dto.regionDto.EducationalRegionDto;
import com.ubi.MasterService.dto.response.Response;


public interface EducationalInstitutionService {

	Response<EducationalInstitutionDto> addEducationalInstitution(EducationalInstitutionDto educationalInstitutionDto);

	Response<EducationalInstitutionDto> getSingleEducationalInstitution(int id);

	Response<EducationalInstitutionDto> getEducationalInstituteByName(String educationalInstitutionName);

	Response<List<EducationalInstitutionDto>> getAllEducationalInstitutions(Integer pageNumber, Integer pageSize);

	Response<EducationalInstitutionDto> deleteEducationalInstitution(int id);

	Response<EducationalInstitutionDto> updateEducationalInstitution(
			EducationalInstitutionDto educationalInstitutionDto);

	Response<EducationalRegionDto> addRegion(EIRegionMappingDto eIRegionMappingDto);

	Response<EducationalRegionDto> getEduInstwithRegion(int id);
	
	Response<List<EducationalInstitutionDto>> getEduInstwithSort(String field);
	
//	ByteArrayInputStream load();

	


}
