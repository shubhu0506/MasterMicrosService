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
			classDto.setSection(classDetail.getSection());
			classDto.setStream(classDetail.getStream());
			classDto.setCreated(classDetail.getCreated());
			classDto.setModified(classDetail.getModified());
			classDto.setCreatedBy(classDetail.getCreatedBy());
			classDto.setModifiedBy(classDetail.getModifiedBy());
			classDto.setIsDeleted(classDetail.getIsDeleted());
			classDto.setSchoolId(classDetail.getSchool().getSchoolId());
			if(classDetail.getTeacherId() != null)classDto.setTeacherId(classDetail.getTeacherId());
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

	/*public ClassDetailsDto toClassDetails(ClassDetail classDetail) {
	ClassDetailsDto classDetailsDto = new ClassDetailsDto();
	classDetailsDto.setClassCode(classDetail.getClassCode());
	classDetailsDto.setClassName(classDetail.getClassName());
	classDetailsDto.setClassId(classDetail.getClassId());
	//classDetailsDto.setSchoolId(classDetail.getSchool().stream().filter(Objects::nonNull).map(school->schoolMapper.entityToDto(school)).collect(Collectors.toSet()));
	//regionDetailsDto.setSchoolDto(region.getSchool().stream().filter(Objects::nonNull).map(school->schoolMapper.entityToDto(school)).collect(Collectors.toSet()));

	TeacherDto teacherDto = null;
	if(classDetail.getTeacherId() != null){
		String currJwtToken = "Bearer " + permissionUtil.getCurrentUsersToken();
		ResponseEntity<Response<UserDto>> teacherResponse = userFeignService.getTeacherById(currJwtToken,classDetail.getTeacherId().toString());
		UserDto userDto = teacherResponse.getBody().getResult().getData();
		if(userDto != null) {
			teacherDto = new TeacherDto(userDto.getId(),userDto.getContactInfoDto().getFirstName(),userDto.getContactInfoDto().getLastName());
		}
	}
	classDetailsDto.setTeacherDto(teacherDto);

	return classDetailsDto;
}*/
	

	public ClassStudentDto toStudentDto(ClassDetail classDetail)
	{
		ClassDto classDto=this.entityToDto(classDetail);
		School school=classDetail.getSchool();
		SchoolDto schoolDto=new SchoolDto();
		classDto.setClassCode(classDetail.getClassCode());
		classDto.setClassId(classDetail.getClassId());
		classDto.setClassName(classDetail.getClassName());
		classDto.setSection(classDetail.getSection());
		classDto.setStream(classDetail.getStream());
		classDto.setCreated(classDetail.getCreated());
		classDto.setModified(classDetail.getModified());
		classDto.setCreatedBy(classDetail.getCreatedBy());
		classDto.setModifiedBy(classDetail.getModifiedBy());
		classDto.setIsDeleted(classDetail.getIsDeleted());
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
		schoolDto.setFeesCollectionType(school.getFeesCollectionType());
		schoolDto.setFeesCollectionPeriod(school.getFeesCollectionPeriod());
		schoolDto.setRegionId(school.getRegion().getId());
		
		Set<Long> setClassId = new HashSet<Long>();
		setClassId.add(classDetail.getClassId());
		schoolDto.setClassId(setClassId);
		
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
				studentDto.setStudentLastName(student.getStudentLastName());
				studentDto.setStudentFirstName(student.getStudentFirstName());
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
//				studentDto.setStatus(student.getStatus());
//				studentDto.setStudentStatus(student.isStudentStatus());
				studentDto.setVerifiedByPrincipal(student.getVerifiedByPrincipal());

				studentDto.setVerifiedByTeacher(student.getVerifiedByTeacher());
				studentDtoSet.add(studentDto);
			}
		}
		return new ClassStudentDto(classDto,schoolDto,null, studentDtoSet );
	}


}
