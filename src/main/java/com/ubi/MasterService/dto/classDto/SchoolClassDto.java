package com.ubi.MasterService.dto.classDto;

import java.util.List;

import com.ubi.MasterService.dto.schoolDto.SchoolDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassDto {

	private SchoolDto schoolDto;

	private List<ClassDto> classDto;

	
}
