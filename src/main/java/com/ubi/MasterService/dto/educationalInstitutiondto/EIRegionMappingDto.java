package com.ubi.MasterService.dto.educationalInstitutiondto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EIRegionMappingDto {
	List<Integer> regionId;
	int educationalInstitutionId;
}
