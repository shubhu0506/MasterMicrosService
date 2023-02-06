package com.ubi.masterservice.dto.regionDto;

import java.util.HashSet;
import java.util.Set;

import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDetailsDto extends Auditable {
	private int id;
	private String code;
	private String name;
	private RegionAdminDto regionAdminDto;
	Set<EducationalInstitutionDto> eduInstiDto = new HashSet<>();
}
