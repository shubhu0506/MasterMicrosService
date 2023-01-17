package com.ubi.masterservice.dto.regionDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionGet {
	private int id;
	private String code;
	private String name;
}
