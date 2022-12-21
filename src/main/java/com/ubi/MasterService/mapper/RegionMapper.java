package com.ubi.MasterService.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubi.MasterService.dto.regionDto.RegionDto;
import com.ubi.MasterService.dto.regionDto.RegionSchoolDto;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;
import com.ubi.MasterService.entity.Region;
import com.ubi.MasterService.entity.School;



@Component
public class RegionMapper {

	ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
       SchoolMapper schoolMapper;
	

	public RegionDto entityToDto(Region region) {
		return modelMapper.map(region, RegionDto.class);
	}
	
	public RegionDto toDto( Region region)
	{
		return new RegionDto(region.getId(),region.getCode(),region.getName());
	}

	public List<RegionDto> entitiesToDtos(List<Region> region) {
		return region.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}
	
	public Set<RegionDto> entitiesToDto(Set<Region> region) {
		return region.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
	}

	public Region dtoToEntity(RegionDto regionDto) {
		return modelMapper.map(regionDto, Region.class);
	}

	public List<Region> dtosToEntities(List<RegionDto> regionDto) {
		return regionDto.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
	}
	
	
	
	public SchoolDto entityToDto(School school) {
		return modelMapper.map(school, SchoolDto.class);
	}
	
	public List<SchoolDto> entitiesToDto(List<School> school) {
		return school.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}
	
	
	
	public RegionSchoolDto toRegionSchoolDto(Region region)
	{
		RegionDto regionDto = this.entityToDto(region);
		Set<SchoolDto> schoolDto=schoolMapper.entitiesToDtos(region.getSchool());
		return new RegionSchoolDto(regionDto,schoolDto);
	}
	
}
