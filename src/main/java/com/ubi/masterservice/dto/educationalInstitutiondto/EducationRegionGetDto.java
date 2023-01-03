package com.ubi.masterservice.dto.educationalInstitutiondto;

import java.util.Set;

import com.ubi.masterservice.dto.regionDto.RegionGet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationRegionGetDto {

	private EducationalInstitutionDto educationalInstituteDto;

	private Set<RegionGet> regionDto;

	//private Integer totalEducationInstituteCount;
}
