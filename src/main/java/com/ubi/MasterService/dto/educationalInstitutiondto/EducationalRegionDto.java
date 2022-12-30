package com.ubi.MasterService.dto.educationalInstitutiondto;

import java.util.Set;

import com.ubi.MasterService.dto.regionDto.RegionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationalRegionDto {
	private EducationalInstitutionDto educationalInstituteDto; 
	private Set<RegionDto> regionDto;
}