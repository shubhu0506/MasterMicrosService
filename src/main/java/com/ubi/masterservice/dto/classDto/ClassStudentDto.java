package com.ubi.masterservice.dto.classDto;

import java.util.Set;

import com.ubi.masterservice.dto.schoolDto.SchoolDto;
import com.ubi.masterservice.dto.studentDto.StudentDto;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassStudentDto extends Auditable {

	private ClassDto classDto;
	private SchoolDto schoolDto;
	private TeacherDto teacherDto;
	private Set<StudentDto> studentDto;

}
