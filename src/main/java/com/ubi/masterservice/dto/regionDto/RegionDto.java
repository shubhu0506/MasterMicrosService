package com.ubi.masterservice.dto.regionDto;

import java.util.Set;

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

	private Set<Integer> eduInstId;
}
