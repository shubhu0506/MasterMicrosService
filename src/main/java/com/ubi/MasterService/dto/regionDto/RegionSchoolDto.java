package com.ubi.MasterService.dto.regionDto;

import java.util.Set;

import com.ubi.MasterService.dto.schoolDto.SchoolDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RegionSchoolDto {
	
	
		private RegionDto regionDto; 
		private Set<SchoolDto> schoolDto;
	}

