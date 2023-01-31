package com.ubi.masterservice.service;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ubi.masterservice.dto.classDto.TeacherDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.pagination.PaginationResponse;
import com.ubi.masterservice.dto.regionDto.RegionDetailsDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.entity.EducationalInstitution;

public interface EducationalInstitutionService {

	Response<InstituteDto> addEducationalInstitution(InstituteCreationDto instituteCreationDto);

	Response<InstituteDto> getEducationalInstituteByName(String educationalInstitutionName);

	Response<PaginationResponse<List<InstituteDto>>> getAllEducationalInstitutions(String fieldName,String searchByField,Integer pageNumber, Integer pageSize);


	Response<InstituteDto> deleteEducationalInstitution(int id);

	Response<InstituteDto> updateEducationalInstitution(InstituteCreationDto instituteCreationDto,Long instituteId);

	Response<InstituteDto> getEduInstwithRegion(int id);

	Response<List<InstituteDto>> getEduInstwithSort(String field);

	Response<InstituteDto> getInstituteByAdminId(Long adminId);

	Response<PaginationResponse<List<RegionDetailsDto>>>  getAllRegionsByInstituteId(Integer instituteId,String fieldName,String fieldQuery,Integer pageNumber,Integer pageSize);
//	ByteArrayInputStream load();

	Response<Set<TeacherDto>> getAllTeacherByInstituteId(Integer instituteId);

	Response<PaginationResponse<Set<SchoolRegionDto>>> getAllSchoolByInstituteId(Integer instituteId,Boolean isCollege,String fieldName,String fieldQuery,Integer pageNumber,Integer pageSize);

	public Response<PaginationResponse<List<StudentDetailsDto>>> getStudentsByInstituteId(Integer instituteId, String fieldName, String searchByField, Integer PageNumber, Integer PageSize) throws ParseException;
}
