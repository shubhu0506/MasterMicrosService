package com.ubi.MasterService.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubi.MasterService.dto.regionDto.RegionDetailsDto;
import com.ubi.MasterService.dto.regionDto.RegionDto;
import com.ubi.MasterService.dto.regionDto.RegionGet;
import com.ubi.MasterService.dto.regionDto.RegionSchoolDto;
import com.ubi.MasterService.dto.schoolDto.SchoolDto;
import com.ubi.MasterService.entity.Region;
import com.ubi.MasterService.entity.School;

@Component
public class RegionMapper {

	ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
       SchoolMapper schoolMapper;
	
	@Autowired
	EducationalInstitutionMapper educationalInstitutionMapper;
	

	public RegionDto entityToDto(Region region) {
		RegionDto regionDto =  new RegionDto();
		regionDto.setCode(region.getCode());
		regionDto.setName(region.getName());
		regionDto.setId(region.getId());
		regionDto.setSchoollId(region.getSchool().stream().map(school->school.getSchoolId()).collect(Collectors.toSet()));
		regionDto.setEduInstId(region.getEducationalInstitiute().stream().map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
		return regionDto;
	}
	
	public RegionDto toDto(Region region)
	{
		RegionDto regionDto =  new RegionDto();
		if(region!=null) {
		
		regionDto.setCode(region.getCode());
		regionDto.setName(region.getName());
		regionDto.setId(region.getId());
		regionDto.setSchoollId(region.getSchool().stream().map(school->school.getSchoolId()).collect(Collectors.toSet()));
		regionDto.setEduInstId(region.getEducationalInstitiute().stream().map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
		}
		return regionDto;
	}
	
	public RegionDetailsDto toRegionDetails(Region region) {
		RegionDetailsDto regionDetailsDto = new RegionDetailsDto();
		regionDetailsDto.setCode(region.getCode());
		regionDetailsDto.setName(region.getName());
		regionDetailsDto.setId(region.getId());
		regionDetailsDto.setEduInstiDto(region.getEducationalInstitiute().stream().map(eduInsti->educationalInstitutionMapper.entityToDto(eduInsti)).collect(Collectors.toSet()));
		regionDetailsDto.setSchoolDto(region.getSchool().stream().map(school->schoolMapper.entityToDto(school)).collect(Collectors.toSet()));
		return regionDetailsDto;
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
	
	
	
	public RegionGet toDtos(Region region)
	{
		
		RegionGet regionDto =  new RegionGet();
		regionDto.setCode(region.getCode());
		regionDto.setName(region.getName());
		regionDto.setId(region.getId());
		//regionDto.setSchoollId(region.getSchool().stream().map(school->school.getSchoolId()).collect(Collectors.toSet()));
		//regionDto.setEduInstId(region.getEducationalInstitiute().stream().map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
		return regionDto;
	}
	
	
	public RegionGet entityToDtos(Region region) {
		return modelMapper.map(region, RegionGet.class);
	}
	
	public Set<RegionGet> entitiesToDtos(Set<Region> region) {
		return region.stream().filter(Objects::nonNull).map(this::entityToDtos).collect(Collectors.toSet());
	}	
	
	
	public RegionGet toRegionGetDto(Region region) {
		return modelMapper.map(region, RegionGet.class);
	}
}
