package com.ubi.MasterService.dto.regionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDto {

	private int id;
	
	private String code;
	
	private String name;
}
