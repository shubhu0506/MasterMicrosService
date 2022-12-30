package com.ubi.MasterService.dto.regionDto;

import java.util.HashSet;
import java.util.Set;

import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDetailsDto {
	private int id;
	private String code;
	private String name;
	
	Set<EducationalInstitutionDto> eduInstiDto = new HashSet<>();
	Set<SchoolDto> schoolDto = new HashSet<>();
}
