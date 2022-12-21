package com.ubi.MasterService.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.ubi.MasterService.dto.studentDto.StudentDto;
import com.ubi.MasterService.entity.Student;


@Component
public class StudentMapper {
	 ModelMapper modelMapper= new ModelMapper();
	 
		//entity to DTO Mapping
	 public StudentDto entityToDto(Student student) {
			return modelMapper.map(student, StudentDto.class);
		}
	 
	 public List<StudentDto> entitiesToDtos(List<Student> student) {
	        return student.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toList());
	    }
	 
	 

		// DTO to entity Mapping
		public Student dtoToEntity(StudentDto studentDto) {
			return modelMapper.map(studentDto, Student.class);
		}
		
	    public List<Student> dtosToEntities(List<StudentDto> studentDTOs) {
	        return studentDTOs.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
	    }
	    public Set<StudentDto> entitiesToDto(List<Student> list) {
			return list.stream().filter(Objects::nonNull).map(this::entityToDto).collect(Collectors.toSet());
		}

}
