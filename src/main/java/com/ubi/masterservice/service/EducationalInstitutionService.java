package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.masterservice.dto.educationalInstitutiondto.EducationRegionGetDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalRegionDto;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;

public interface EducationalInstitutionService {

	Response<EducationalRegionDto> addEducationalInstitution(EducationalInstitutionDto educationalInstitutionDto);

	//Response<EducationRegionGetDto> getEducationalInstituteByName(String educationalInstitutionName);

	Response<PaginationResponse<List<EducationRegionGetDto>>> getAllEducationalInstitutions(String fieldName,String searchByField,Integer pageNumber, Integer pageSize);

	Response<EducationalInstitutionDto> deleteEducationalInstitution(int id);

	Response<EducationalRegionDto> updateEducationalInstitution(EducationalInstitutionDto educationalInstitutionDto);

	Response<EducationRegionGetDto> getEduInstwithRegion(int id);

	Response<List<EducationalInstitutionDto>> getEduInstwithSort(String field);

//	ByteArrayInputStream load();

}
