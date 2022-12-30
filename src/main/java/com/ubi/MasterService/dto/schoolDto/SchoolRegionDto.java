package com.ubi.MasterService.dto.schoolDto;

import java.util.Set;

import com.ubi.MasterService.dto.classDto.ClassDto;
import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.MasterService.dto.regionDto.RegionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SchoolRegionDto {

	private SchoolDto schoolDto;
	private  RegionDto regionDto;
	private Set<ClassDto> classDto;
	private EducationalInstitutionDto educationalInstitutionDto;
	
}

