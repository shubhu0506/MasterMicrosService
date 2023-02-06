package com.ubi.masterservice.dto.regionDto;

import java.util.Set;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDto extends Auditable {

	private int id;

	private String code;

	private String name;

	private Set<Integer> eduInstId;
}
