package com.ubi.MasterService.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.MasterService.dto.educationalInstitutiondto.EducationRegionGetDto;
import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalRegionDto;
import com.ubi.MasterService.dto.response.Response;

public interface EducationalInstitutionService {

	Response<EducationalRegionDto> addEducationalInstitution(EducationalInstitutionDto educationalInstitutionDto);

	Response<EducationRegionGetDto> getEducationalInstituteByName(String educationalInstitutionName);

	Response<List<EducationRegionGetDto>> getAllEducationalInstitutions(Integer pageNumber, Integer pageSize);

	Response<EducationalInstitutionDto> deleteEducationalInstitution(int id);

	Response<EducationalRegionDto> updateEducationalInstitution(EducationalInstitutionDto educationalInstitutionDto);

	Response<EducationRegionGetDto> getEduInstwithRegion(int id);

	Response<List<EducationalInstitutionDto>> getEduInstwithSort(String field);



}
