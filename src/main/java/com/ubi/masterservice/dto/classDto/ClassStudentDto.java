package com.ubi.masterservice.dto.classDto;

import java.util.Set;

import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassStudentDto {

	private ClassDto classDto;
	private SchoolDto schoolDto;
	private Set<StudentDto> studentDto;

}
