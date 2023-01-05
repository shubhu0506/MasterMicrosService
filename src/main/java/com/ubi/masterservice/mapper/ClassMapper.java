package com.ubi.masterservice.mapper;

import java.util.HashSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.classDto.ClassStudentDto;
import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.entity.ClassDetail;
import com.ubi.masterservice.entity.School;
import com.ubi.masterservice.entity.Student;

@Component
public class ClassMapper {
	ModelMapper modelMapper = new ModelMapper();

	// entity to DTO Mapping
	public ClassDto entityToDto(ClassDetail classDetail) {
		ClassDto classDto = null;
		if(classDetail != null){
			classDto = new ClassDto();
			classDto.setClassId(classDetail.getClassId());
			classDto.setClassName(classDetail.getClassName());
			classDto.setClassCode(classDetail.getClassCode());
			classDto.setSchoolId(classDetail.getSchool().getSchoolId());
		}
		return classDto;
		
		
	}

	public List<ClassDto> entitiesToDtos(List<ClassDetail> classDetail) {
		return classDetail.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}

	public Set<ClassDto> entitiesToDto(Set<ClassDetail> classDetail) {
		return classDetail.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
	}

	// DTO to entity Mapping
	public ClassDetail dtoToEntity(ClassDto classDto) {
		return modelMapper.map(classDto, ClassDetail.class);
	}

	public List<ClassDetail> dtosToEntities(List<ClassDto> classDtos) {
		return classDtos.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
	}

	public ClassDto entityToDtos(ClassDetail classDetail)
	{
		ClassDto classDto=modelMapper.map(classDetail, ClassDto.class);
		classDto.setSchoolId(classDetail.getSchool().getSchoolId());
		Set<Long> studentId=classDetail.getStudents().stream().filter(Objects::nonNull).map(classDetails -> classDetails.getStudentId()).collect(Collectors.toSet());
		return classDto;
	}


	public ClassStudentDto toStudentDto(ClassDetail classDetail)
	{
		ClassDto classDto=this.entityToDto(classDetail);
		School school=classDetail.getSchool();
		SchoolDto schoolDto=new SchoolDto();
		classDto.setClassCode(classDetail.getClassCode());
		classDto.setClassId(classDetail.getClassId());
		classDto.setClassName(classDetail.getClassName());
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
		schoolDto.setRegionId(school.getRegion().getId());
		if(school.getEducationalInstitution() != null){
			schoolDto.setEducationalInstitutionId(school.getEducationalInstitution().getId());
		}
		Set<StudentDto> studentDtoSet=new HashSet<>();
		if(classDetail.getStudents()!=null)
		{
			for(Student student: classDetail.getStudents())
			{
				StudentDto studentDto=new StudentDto();
				studentDto.setStudentId(student.getStudentId());
				studentDto.setStudentName(student.getStudentName());
				studentDto.setCategory(student.getCategory());
				studentDto.setCurrentStatus(student.getCurrentStatus());
				studentDto.setDateOfBirth(student.getDateOfBirth());
				studentDto.setFatherName(student.getFatherName());
				studentDto.setFatherOccupation(student.getFatherOccupation());
				studentDto.setGender(student.getGender());
				studentDto.setIsActivate(student.getIsActivate());
				studentDto.setJoiningDate(student.getJoiningDate());
				studentDto.setMinority(student.getMinority());
				studentDto.setMotherName(student.getMotherName());
				studentDto.setMotherOccupation(student.getMotherOccupation());
				studentDto.setStatus(student.getStatus());
				studentDto.setStudentStatus(student.isStudentStatus());
				studentDto.setVerifiedByPrincipal(student.getVerifiedByPrincipal());

				studentDto.setVerifiedByTeacher(student.getVerifiedByTeacher());
				studentDtoSet.add(studentDto);
			}
		}
		return new ClassStudentDto(classDto,schoolDto, studentDtoSet );
	}


}
