package com.ubi.masterservice.mapper;

import java.util.HashSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
		schoolDto.setIsCollege(school.getIsCollege());
		if(school.getPrincipalId() != null)schoolDto.setPrincipalId(school.getPrincipalId());
		
		
		
//		if(school.getPrincipalId()!=null)
//		{
//			schoolDto.getPrincipalId();
//		}

		if(school.getRegion() != null)
		{
			schoolDto.setRegionId(school.getRegion().getId());
		}


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
		SchoolDto schoolDto = modelMapper.map(school, SchoolDto.class);
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

	// DTO to entity Mapping
	public School dtoToEntity(SchoolDto schoolDto) {
		return modelMapper.map(schoolDto, School.class);
	}

	public List<School> dtosToEntities(List<SchoolDto> schoolDTOs) {
		return schoolDTOs.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
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
					ClassDto classDetailDto = new ClassDto();
					classDetailDto.setClassCode(classDetail.getClassCode());
					classDetailDto.setClassName(classDetail.getClassName());
					classDetailDto.setClassId(classDetail.getClassId());
					classDtoSet.add(classDetailDto);
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
			edDto.setVvnAccount(edu.getVvnAccount());
		}
		return new SchoolRegionDto(schoolDto,null, regionDto, classDtoSet, edDto);

	}
}
