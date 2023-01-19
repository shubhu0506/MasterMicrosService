package com.ubi.masterservice.dto.classDto;

import java.util.HashSet;
import java.util.Set;

import com.ubi.masterservice.entity.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ClassDetailsDto {

	private Long classId;
	private String classCode;
	private String className;
	private int schoolId;
	private int teacherId;
	//private TeacherDto teacherDto;
	Set<Student> student = new HashSet<>();
	

}
