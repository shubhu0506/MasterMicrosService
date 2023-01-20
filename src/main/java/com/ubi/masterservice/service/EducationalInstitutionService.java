package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.entity.EducationalInstitution;

public interface EducationalInstitutionService {

	Response<InstituteDto> addEducationalInstitution(InstituteCreationDto instituteCreationDto);

	Response<InstituteDto> getEducationalInstituteByName(String educationalInstitutionName);

	Response<PaginationResponse<List<InstituteDto>>> getAllEducationalInstitutions(Integer pageNumber, Integer pageSize);


	Response<InstituteDto> deleteEducationalInstitution(int id);

	Response<InstituteDto> updateEducationalInstitution(InstituteCreationDto instituteCreationDto,Long instituteId);

	Response<InstituteDto> getEduInstwithRegion(int id);

	Response<List<InstituteDto>> getEduInstwithSort(String field);

	Response<InstituteDto> getInstituteByAdminId(Long adminId);

//	ByteArrayInputStream load();

}
