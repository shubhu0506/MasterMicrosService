package com.ubi.masterservice.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubi.masterservice.dto.studentDto.StudentDetailsDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;
import com.ubi.masterservice.dto.studentDto.StudentPromoteDemoteDto;
import com.ubi.masterservice.dto.studentDto.StudentVerifyDto;
import com.ubi.masterservice.entity.Student;
import com.ubi.masterservice.entity.StudentPromoteDemote;

@Component
public class StudentMapper {
	ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
	ClassMapper classMapper;

	// entity to DTO Mapping
	public StudentDto entityToDto(Student student) {
		StudentDto studentDto= new StudentDto();
		studentDto.setStudentId(student.getStudentId());
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
		studentDto.setAadhaarNo(student.getAadhaarNo());
		studentDto.setBloodGroup(student.getBloodGroup());
		studentDto.setMotherOccupation(student.getMotherOccupation());
		studentDto.setStudentFirstName(student.getStudentFirstName());
		studentDto.setStudentLastName(student.getStudentLastName());
		studentDto.setVerifiedByPrincipal(student.getVerifiedByPrincipal());
		studentDto.setVerifiedByTeacher(student.getVerifiedByTeacher());
		studentDto.setRollNo(student.getRollNo());
		studentDto.setIsPhysicallyHandicapped(student.getIsPhysicallyHandicapped());
		studentDto.setUniqueId(student.getUniqueId());
		studentDto.setAdmissionNo(student.getAdmissionNo());
		studentDto.setNationality(student.getNationality());
		studentDto.setMobileNo(student.getMobileNo());
		studentDto.setEmail(student.getEmail());
		studentDto.setCreated(student.getCreated());
		studentDto.setCreatedBy(student.getCreatedBy());
		studentDto.setModified(student.getModified());
		studentDto.setModifiedBy(student.getModifiedBy());
		studentDto.setIsDeleted(student.getIsDeleted());
		studentDto.setIsCurrentPaymentCycleFeesPaid(student.getIsCurrentPaymentCycleFeesPaid());
		
		studentDto.setVerifiedByTeacher(student.getVerifiedByTeacher());
		if(student.getClassDetail() != null ) studentDto.setClassId(student.getClassDetail().getClassId());
		//System.out.println(studentDto.toString());
		return studentDto;
	}
	
	public StudentVerifyDto entityToDtoId(StudentVerifyDto student) {
		StudentVerifyDto studentDto=modelMapper.map(student, StudentVerifyDto.class);
		studentDto.setStudentId(student.getStudentId());
		studentDto.setUserId(student.getUserId());
		return studentDto;
	}

	public StudentVerifyDto entityToDtoIds(StudentVerifyDto student) {
		StudentVerifyDto studentDto=modelMapper.map(student, StudentVerifyDto.class);
		studentDto.setStudentId(student.getStudentId());
		studentDto.setUserId(student.getUserId());
		return studentDto;
	}

	public List<StudentDto> entitiesToDtos(List<Student> student) {
		return student.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	}
	
	public Student dtoToEntity(StudentDto studentDto) {
		return modelMapper.map(studentDto, Student.class);
	}

	public List<Student> dtosToEntities(List<StudentDto> studentDTOs) {
		return studentDTOs.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
	}

	public Set<StudentDto> entitiesToDto(List<Student> list) {
		return list.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
	}

	public StudentPromoteDemote dtoToEntity(StudentPromoteDemoteDto studentPromoteDemoteCreationDto) {
		return modelMapper.map(studentPromoteDemoteCreationDto, StudentPromoteDemote.class);
	}

	public StudentPromoteDemoteDto entityToDto(StudentPromoteDemote studentPromoteDemoteCreation) {
		StudentPromoteDemoteDto studentPromoteDemoteCreationDto=modelMapper.map(studentPromoteDemoteCreation, StudentPromoteDemoteDto.class);
		studentPromoteDemoteCreationDto.setUserId(studentPromoteDemoteCreationDto.getUserId());
		studentPromoteDemoteCreationDto.setClassId(studentPromoteDemoteCreationDto.getClassId());
		studentPromoteDemoteCreationDto.setStudentId(studentPromoteDemoteCreationDto.getStudentId());

		return studentPromoteDemoteCreationDto;
	}

	public StudentPromoteDemoteDto entityToDtoId(StudentPromoteDemoteDto student) {

		StudentPromoteDemoteDto studentPromoteDemoteCreationDto=modelMapper.map(student, StudentPromoteDemoteDto.class);
		studentPromoteDemoteCreationDto.setUserId(student.getUserId());
		studentPromoteDemoteCreationDto.setClassId(student.getClassId());
		studentPromoteDemoteCreationDto.setStudentId(student.getStudentId());

		return studentPromoteDemoteCreationDto;
	}
	
	
	public StudentDetailsDto toStudentDetails(Student student) {
		StudentDetailsDto studentDetailsDto = new StudentDetailsDto();
		studentDetailsDto.setStudentId(student.getStudentId());
//		studentDetailsDto.setStudentName(student.getStudentName());
		studentDetailsDto.setStudentFirstName(student.getStudentFirstName());
		studentDetailsDto.setStudentLastName(student.getStudentLastName());
		studentDetailsDto.setDateOfBirth(student.getDateOfBirth());
		studentDetailsDto.setCategory(student.getCategory());
		studentDetailsDto.setMinority(student.getMinority());
		studentDetailsDto.setFatherName(student.getFatherName());
		studentDetailsDto.setFatherOccupation(student.getFatherOccupation());
		studentDetailsDto.setMotherName(student.getMotherName());
		studentDetailsDto.setMotherOccupation(student.getMotherOccupation());
		studentDetailsDto.setGender(student.getGender());
		studentDetailsDto.setAadhaarNo(student.getAadhaarNo());
		studentDetailsDto.setBloodGroup(student.getBloodGroup());
		studentDetailsDto.setJoiningDate(student.getJoiningDate());
		studentDetailsDto.setVerifiedByPrincipal(student.getVerifiedByPrincipal());
		studentDetailsDto.setVerifiedByTeacher(student.getVerifiedByTeacher());
		studentDetailsDto.setIsActivate(student.getIsActivate());
		studentDetailsDto.setCurrentStatus(student.getCurrentStatus());
		studentDetailsDto.setRollNo(student.getRollNo());
		studentDetailsDto.setIsPhysicallyHandicapped(student.getIsPhysicallyHandicapped());
		studentDetailsDto.setUniqueId(student.getUniqueId());
		studentDetailsDto.setAdmissionNo(student.getAdmissionNo());
		studentDetailsDto.setNationality(student.getNationality());
		studentDetailsDto.setMobileNo(student.getMobileNo());
		studentDetailsDto.setEmail(student.getEmail());
		studentDetailsDto.setCreated(student.getCreated());
		studentDetailsDto.setCreatedBy(student.getCreatedBy());
		studentDetailsDto.setModified(student.getModified());
		studentDetailsDto.setModifiedBy(student.getModifiedBy());
		studentDetailsDto.setIsDeleted(student.getIsDeleted());
		studentDetailsDto.setIsCurrentPaymentCycleFeesPaid(student.getIsCurrentPaymentCycleFeesPaid());
		if(student.getClassDetail()!=null) {
        	studentDetailsDto.setClassDto(classMapper.entityToDto(student.getClassDetail()));
		}	
		
		return studentDetailsDto;
	}
	
	
	


}
