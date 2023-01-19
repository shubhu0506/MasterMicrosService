package com.ubi.masterservice.dto.schoolDto;

import java.util.Set;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SchoolRegionDto {

	private SchoolDto schoolDto;
	private PrincipalDto principalDto;
	private RegionDto regionDto;
	private Set<ClassDto> classDto;
	private EducationalInstitutionDto educationalInstitutionDto;
}
