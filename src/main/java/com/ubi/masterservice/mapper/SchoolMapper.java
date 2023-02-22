package com.ubi.masterservice.mapper;

import java.util.HashSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ubi.masterservice.dto.regionDto.RegionAdminDto;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.user.UserDto;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.util.PermissionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.regionDto.RegionDto;
import com.ubi.masterservice.dto.schoolDto.PrincipalDto;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.schoolDto.SchoolRegionDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.EducationalInstitution;
import com.ubi.masterservice.entity.Region;
import com.ubi.masterservice.entity.School;

@Component
public class SchoolMapper {

	@Autowired
	ClassMapper mapper;

	@Autowired
	PermissionUtil permissionUtil;

	@Autowired
	UserFeignService userFeignService;

	ModelMapper modelMapper = new ModelMapper();

	// entity to DTO Mapping
	public SchoolDto entityToDto(School school) {

		SchoolDto schoolDto = new SchoolDto();
		schoolDto.setSchoolId(school.getSchoolId());
		schoolDto.setCode(school.getCode());
		schoolDto.setName(school.getName());
		schoolDto.setEmail(school.getEmail());
		schoolDto.setContact(school.getContact());
		schoolDto.setAddress(school.getAddress());
		schoolDto.setType(school.getType());
		schoolDto.setStrength(school.getStrength());
		schoolDto.setShift(school.getShift());
		schoolDto.setExemptionFlag(school.isExemptionFlag());
		schoolDto.setVvnAccount(school.getVvnAccount());
		schoolDto.setVvnFund(school.getVvnFund());
		schoolDto.setFeesType(school.getFeesType());
		schoolDto.setIsCollege(school.getIsCollege());
		schoolDto.setCreated(school.getCreated());
		schoolDto.setCreatedBy(school.getCreatedBy());
		schoolDto.setModified(school.getModified());
		schoolDto.setModifiedBy(school.getModifiedBy());
		schoolDto.setIsDeleted(school.getIsDeleted());

		if(school.getPrincipalId() != null)schoolDto.setPrincipalId(school.getPrincipalId());
		if(school.getRegion() != null) schoolDto.setRegionId(school.getRegion().getId());

		if(school.getClassDetail() != null)
		{
			for (ClassDetail cd : school.getClassDetail())
			{
				schoolDto.setClassId(new HashSet<>());
				if(cd != null) schoolDto.getClassId().add(cd.getClassId());
			}
		}


		if(school.getEducationalInstitution() != null) {
			schoolDto.setEducationalInstitutionId(school.getEducationalInstitution().getId());
		}

		return schoolDto;
	}

	public Set<SchoolDto> entitiesToDtos(Set<School> school) {
		return school.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
	}

	public List<SchoolDto> entitiesToDtos(List<School> school) {
		return school.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}

	public ClassDto entityToDto(ClassDetail classDetail) {
		return modelMapper.map(classDetail, ClassDto.class);
	}

	public SchoolDto entityToDtos(School school) {
		SchoolDto schoolDto = this.entityToDto(school);
		if (school.getRegion() != null) {
			schoolDto.setRegionId(school.getRegion().getId());
		}
		Set<Long> classId = school.getClassDetail().stream().map(classD -> classD.getClassId())
				.collect(Collectors.toSet());
		schoolDto.setClassId(classId);

		if(school.getEducationalInstitution() !=null) {
			schoolDto.setEducationalInstitutionId(school.getEducationalInstitution().getId());
		}

		return schoolDto;
	}

	public List<ClassDto> entitiesToDto(List<ClassDetail> classDetail) {
		return classDetail.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}


	public SchoolRegionDto toSchoolClassDto(School school) {
		SchoolDto schoolDto = this.entityToDto(school);
		Region region = school.getRegion();
		RegionDto regionDto = new RegionDto();
		regionDto.setCode(region.getCode());
		regionDto.setName(region.getName());
		regionDto.setId(region.getId());
			
		Set<ClassDto> classDtoSet = new HashSet<>();
		if(school.getClassDetail() != null) {
			for (ClassDetail classDetail : school.getClassDetail()) {
				if (classDetail != null) {
					ClassDto classDto = new ClassDto();
					classDto.setClassCode(classDetail.getClassCode());
					classDto.setClassName(classDetail.getClassName());
					classDto.setSection(classDetail.getStream());
					classDto.setStream(classDetail.getSection());
					classDto.setClassId(classDetail.getClassId());
					classDto.setSchoolId(school.getSchoolId());
					classDto.setTeacherId(classDetail.getTeacherId());
					classDtoSet.add(classDto);
				}
			}
		}
		EducationalInstitution edu=school.getEducationalInstitution();
		EducationalInstitutionDto edDto=null;
		if(edu != null){
			edDto = new EducationalInstitutionDto();
			edDto.setId(edu.getId());
			edDto.setEducationalInstitutionCode(edu.getEducationalInstitutionCode());
			edDto.setEducationalInstitutionName(edu.getEducationalInstitutionName());
			edDto.setEducationalInstitutionType(edu.getEducationalInstitutionType());
			edDto.setStrength(edu.getStrength());
			edDto.setState(edu.getState());
			edDto.setExemptionFlag(edu.getExemptionFlag());
			if(edu.getAdminId() != null ) edDto.setAdminId(edu.getAdminId());
			edDto.setVvnAccount(edu.getVvnAccount());
			edDto.setRegionId(edu.getRegion().stream().filter(Objects::nonNull).map(tempRegion -> tempRegion.getId()).collect(Collectors.toSet()));
		}
		PrincipalDto principalDto = null;
		if(school.getPrincipalId() != null){
			String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
			ResponseEntity<Response<UserDto>> regionAdminResponse = userFeignService.getPrincipalById(currJwtToken,school.getPrincipalId().toString());
			UserDto userDto = regionAdminResponse.getBody().getResult().getData();
			if(userDto != null) {
				principalDto = new PrincipalDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName(),school.getSchoolId());
			}
		}

		return new SchoolRegionDto(schoolDto,principalDto, regionDto, classDtoSet, edDto);
	}
}
