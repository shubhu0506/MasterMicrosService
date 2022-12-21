package com.ubi.MasterService.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubi.MasterService.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.MasterService.dto.regionDto.EducationalRegionDto;
import com.ubi.MasterService.dto.regionDto.RegionDto;
import com.ubi.MasterService.entity.EducationalInstitution;
import com.ubi.MasterService.entity.Region;



@Component
public class EducationalInstitutionMapper {

	@Autowired
	RegionMapper mapper;
	
	ModelMapper modelMapper = new ModelMapper();

	public EducationalInstitutionDto entityToDto(EducationalInstitution educationalInstitution) {
		return modelMapper.map(educationalInstitution, EducationalInstitutionDto.class);
	}

	public List<EducationalInstitutionDto> entitiesToDtos(List<EducationalInstitution> educationalInstitution) {
		return educationalInstitution.stream().filter(Objects::nonNull).map(this::entityToDto)
				.collect(Collectors.toList());
	}
	
	public RegionDto entityToDto(Region region) {
		return modelMapper.map(region, RegionDto.class);
	}
	
	public Set<RegionDto> entitiesToDto(Set<Region> region) {
		return region.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
	}

	// DTO to entity Mapping
	public EducationalInstitution dtoToEntity(EducationalInstitutionDto educationalInstitutionDto) {
		return modelMapper.map(educationalInstitutionDto, EducationalInstitution.class);
	}

	public List<EducationalInstitution> dtosToEntities(List<EducationalInstitutionDto> educationalInstitutionDtos) {
		return educationalInstitutionDtos.stream().filter(Objects::nonNull).map(this::dtoToEntity)
				.collect(Collectors.toList());
	}
	
	public EducationalRegionDto toEducationalRegionDto(EducationalInstitution educationalInstitute)
	{
		EducationalInstitutionDto educationalInstitutionDto = this.entityToDto(educationalInstitute);
		Set<RegionDto> regionDto=mapper.entitiesToDto(educationalInstitute.getRegion());
		return new EducationalRegionDto(educationalInstitutionDto,regionDto);
	}
	
	

}
;