package com.ubi.masterservice.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.netflix.discovery.converters.Auto;
import com.ubi.masterservice.dto.educationalInstitutiondto.*;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.util.PermissionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.ubi.masterservice.dto.regionDto.RegionGet;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;

@Component
public class EducationalInstitutionMapper {

	ModelMapper modelMapper = new ModelMapper();


	@Autowired
	PermissionUtil permissionUtil;

	@Autowired
	UserFeignService userFeignService;

	public EducationalInstitutionDto entityToDto(EducationalInstitution educationalInstitution) {
		EducationalInstitutionDto educationalInstitutionDto = modelMapper.map(educationalInstitution, EducationalInstitutionDto.class);

//		EducationalInstitutionDto educationalInstitutionDto=new EducationalInstitutionDto();
//
//		educationalInstitutionDto.setId(educationalInstitution.getId());
//		educationalInstitutionDto.setEducationalInstitutionCode(educationalInstitution.getEducationalInstitutionCode());
//		educationalInstitutionDto.setEducationalInstitutionName(educationalInstitution.getEducationalInstitutionName());
//		educationalInstitutionDto.setEducationalInstitutionType(educationalInstitution.getEducationalInstitutionType());
//		educationalInstitutionDto.setStrength(educationalInstitution.getStrength());
//		educationalInstitutionDto.setState(educationalInstitution.getState());
//		educationalInstitutionDto.setExemptionFlag(educationalInstitution.getExemptionFlag());
//		educationalInstitutionDto.setVvnAccount(educationalInstitution.getVvnAccount());
//		educationalInstitutionDto.setCreated(educationalInstitution.getCreated());
//		educationalInstitutionDto.setModified(educationalInstitution.getModified());
//		educationalInstitutionDto.setCreatedBy(educationalInstitution.getCreatedBy());
//		educationalInstitutionDto.setModifiedBy(educationalInstitution.getModifiedBy());
//		educationalInstitutionDto.setIsDeleted(educationalInstitution.getIsDeleted());

//---------------------


		Set<Integer> regionId = educationalInstitution.getRegion().stream().filter(Objects::nonNull).map(region -> region.getId()).collect(Collectors.toSet());
		educationalInstitutionDto.setRegionId(regionId);
		return educationalInstitutionDto;
	}

//	public List<EducationalInstitutionDto> entitiesToDtos(List<EducationalInstitution> educationalInstitution) {
//		return educationalInstitution.stream().filter(Objects::nonNull).map(this::entityToDto)
//				.collect(Collectors.toList());
//	}

//	public RegionDto entityToDto(Region region) {
//		return modelMapper.map(region, RegionDto.class);
//	}

//	public Set<RegionDto> entitiesToDto(Set<Region> region) {
//		return region.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
//	}

	// DTO to entity Mapping
//	public EducationalInstitution dtoToEntity(EducationalInstitutionDto educationalInstitutionDto) {
//		EducationalInstitution educationalInstitution = modelMapper.map(educationalInstitutionDto, EducationalInstitution.class);
//		for(Integer regionId:educationalInstitutionDto.getRegionId()) {
//			Region region = regionRepository.getReferenceById(regionId);
//			educationalInstitution.getRegion().add(region);
//		}
//		return educationalInstitution;
//	}

//	public List<EducationalInstitution> dtosToEntities(List<EducationalInstitutionDto> educationalInstitutionDtos) {
//		return educationalInstitutionDtos.stream().filter(Objects::nonNull).map(this::dtoToEntity)
//				.collect(Collectors.toList());
//	}

//	public EducationalRegionDto toEducationalRegionDto(EducationalInstitution educationalInstitute)
//	{
//		EducationalInstitutionDto educationalInstitutionDto = this.entityToDto(educationalInstitute);
//
//		Set<RegionDto> regionDtoSet = new HashSet<>();
//		for(Region region:educationalInstitute.getRegion()) {
//			if(region != null) {
//				RegionDto regionDto =  new RegionDto();
//				regionDto.setCode(region.getCode());
//				regionDto.setName(region.getName());
//				regionDto.setId(region.getId());
//				regionDto.setEduInstId(region.getEducationalInstitiute().stream().map(eduInsti->eduInsti.getId()).collect(Collectors.toSet()));
//				regionDtoSet.add(regionDto);
//			}
//		}
//
//		return new EducationalRegionDto(educationalInstitutionDto,regionDtoSet);
//	}




//	public EducationalInstitutionDto entityToDtos(EducationalInstitution educationalInstitution) {
//		EducationalInstitutionDto educationalInstitutionDto = modelMapper.map(educationalInstitution, EducationalInstitutionDto.class);
//		Set<Integer> regionId = educationalInstitution.getRegion().stream().map(region -> region.getId()).collect(Collectors.toSet());
//		educationalInstitutionDto.setRegionId(regionId);
//		return educationalInstitutionDto;
//	}

//	public EducationRegionGetDto toEducationalRegionDtos(EducationalInstitution educationalInstitute)
//	{
//		EducationalInstitutionDto educationalInstitutionDto = this.entityToDtos(educationalInstitute);
//
//		Set<RegionGet> regionDtoSet = new HashSet<>();
//		for(Region region:educationalInstitute.getRegion()) {
//			RegionGet regionDto =  new RegionGet();
//			regionDto.setCode(region.getCode());
//			regionDto.setName(region.getName());
//			regionDto.setId(region.getId());
//			regionDtoSet.add(regionDto);
//		}
//
//		return new EducationRegionGetDto(educationalInstitutionDto,regionDtoSet);
//	}


	public EducationalInstitutionDto toDto(EducationalInstitution educationalInstitution)
	{
		EducationalInstitutionDto educationalInstitutionDto =  new EducationalInstitutionDto();
		if(educationalInstitution!=null) {

			educationalInstitutionDto.setId(educationalInstitution.getId());
			educationalInstitutionDto.setEducationalInstitutionCode(educationalInstitution.getEducationalInstitutionCode());
			educationalInstitutionDto.setEducationalInstitutionName(educationalInstitution.getEducationalInstitutionName());
			educationalInstitutionDto.setEducationalInstitutionType(educationalInstitution.getEducationalInstitutionType());
			educationalInstitutionDto.setStrength(educationalInstitution.getStrength());
			educationalInstitutionDto.setState(educationalInstitution.getState());
			educationalInstitutionDto.setExemptionFlag(educationalInstitution.getExemptionFlag());
			educationalInstitutionDto.setVvnAccount(educationalInstitution.getVvnAccount());
		}
		return educationalInstitutionDto;
	}

	public InstituteDto toInstituteDto(EducationalInstitution educationalInstitution){
		InstituteDto instituteDto=new InstituteDto();

		instituteDto.setId(educationalInstitution.getId());
		instituteDto.setEducationalInstitutionCode(educationalInstitution.getEducationalInstitutionCode());
		instituteDto.setEducationalInstitutionName(educationalInstitution.getEducationalInstitutionName());
		instituteDto.setEducationalInstitutionType(educationalInstitution.getEducationalInstitutionType());
		instituteDto.setExemptionFlag(educationalInstitution.getExemptionFlag());
		instituteDto.setStrength(educationalInstitution.getStrength());
		instituteDto.setState(educationalInstitution.getState());
		instituteDto.setVvnAccount(educationalInstitution.getVvnAccount());
		instituteDto.setCreated(educationalInstitution.getCreated());
		instituteDto.setModified(educationalInstitution.getModified());
		instituteDto.setCreatedBy(educationalInstitution.getCreatedBy());
		instituteDto.setModifiedBy(educationalInstitution.getModifiedBy());
		instituteDto.setIsDeleted(educationalInstitution.getIsDeleted());
		instituteDto.setRegionDto(new HashSet<>());

		for(Region region:educationalInstitution.getRegion()){
			RegionGet regionGet = new RegionGet();
			regionGet.setCode(region.getCode());
			regionGet.setId(region.getId());
			regionGet.setName(region.getName());
			if(region.getAdminId() != null) regionGet.setAdminId(region.getAdminId());
			instituteDto.getRegionDto().add(regionGet);
		}

		InstituteAdminDto instituteAdminDto = null;
		if(educationalInstitution.getAdminId() != null){
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getInstituteAdminById(currJwtToken,educationalInstitution.getAdminId().toString());
			UserDto userDto = regionAdminResponse.getBody().getResult().getData();
			if(userDto != null) {
				instituteAdminDto = new InstituteAdminDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
			}
		}

		instituteDto.setInstituteAdminDto(instituteAdminDto);
		return instituteDto;
	}
}
