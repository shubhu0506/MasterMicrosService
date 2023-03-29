package com.ubi.masterservice.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ubi.masterservice.dto.regionDto.*;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.util.PermissionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.entity.School;

@Component
public class RegionMapper {

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	SchoolMapper schoolMapper;

	@Autowired
	PermissionUtil permissionUtil;

	@Autowired
	UserFeignService userFeignService;

	@Autowired
	EducationalInstitutionMapper educationalInstitutionMapper;


	public RegionDto entityToDto(Region region) {
		RegionDto regionDto =  new RegionDto();
		regionDto.setCode(region.getCode());
		regionDto.setName(region.getName());
		regionDto.setId(region.getId());
		regionDto.setCreated(region.getCreated());
		regionDto.setModified(region.getModified());
		regionDto.setCreatedBy(region.getCreatedBy());
		regionDto.setModifiedBy(region.getModifiedBy());
		regionDto.setIsDeleted(region.getIsDeleted());
		if(region.getEducationalInstitiute() != null) {
			regionDto.setEduInstId(region.getEducationalInstitiute().stream().filter(Objects::nonNull).map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
		}
		return regionDto;
	}


	public RegionDto toDto(Region region)
	{
		RegionDto regionDto =  null;
		if(region!=null) {
			regionDto = new RegionDto();
			regionDto.setCode(region.getCode());
			regionDto.setName(region.getName());
			regionDto.setId(region.getId());
			if(region.getEducationalInstitiute() != null) {
				regionDto.setEduInstId(region.getEducationalInstitiute().stream().filter(Objects::nonNull).map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
			}
		}
		return regionDto;
	}

	public RegionDetailsDto toRegionDetails(Region region) {
		RegionDetailsDto regionDetailsDto = new RegionDetailsDto();
		regionDetailsDto.setCode(region.getCode());
		regionDetailsDto.setName(region.getName());
		regionDetailsDto.setId(region.getId());
		if(region.getEducationalInstitiute() != null){
			regionDetailsDto.setEduInstiDto(region.getEducationalInstitiute().stream().filter(Objects::nonNull).map(eduInsti->educationalInstitutionMapper.entityToDto(eduInsti)).collect(Collectors.toSet()));
		}
		//regionDetailsDto.setSchoolDto(region.getSchool().stream().filter(Objects::nonNull).map(school->schoolMapper.entityToDto(school)).collect(Collectors.toSet()));

		RegionAdminDto regionAdminDto = null;
		if(region.getAdminId() != null){
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getRegionAdminById(currJwtToken,region.getAdminId().toString());
			UserDto userDto = regionAdminResponse.getBody().getResult().getData();
			if(userDto != null) {
				regionAdminDto = new RegionAdminDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
			}
		}
		regionDetailsDto.setCreated(region.getCreated());
		regionDetailsDto.setModified(region.getModified());
		regionDetailsDto.setCreatedBy(region.getCreatedBy());
		regionDetailsDto.setModifiedBy(region.getModifiedBy());
		regionDetailsDto.setIsDeleted(region.getIsDeleted());

		regionDetailsDto.setRegionAdminDto(regionAdminDto);

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

	public RegionGet entityToDtos(Region region) {
		RegionGet regionDto =  new RegionGet();
		regionDto.setCode(region.getCode());
		regionDto.setName(region.getName());
		regionDto.setId(region.getId());
		if(region.getAdminId() != null) regionDto.setAdminId(region.getAdminId());
		//regionDto.setSchoollId(region.getSchool().stream().map(school->school.getSchoolId()).collect(Collectors.toSet()));
		//regionDto.setEduInstId(region.getEducationalInstitiute().stream().map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
		return regionDto;
	}

	public Set<RegionGet> entitiesToDtos(Set<Region> region) {
		return region.stream().filter(Objects::nonNull).map(this::entityToDtos).collect(Collectors.toSet());
	}
}
