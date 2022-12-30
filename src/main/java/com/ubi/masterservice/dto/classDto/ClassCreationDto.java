package com.ubi.MasterService.dto.classDto;

import java.util.Set;

import com.ubi.MasterService.entity.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassCreationDto {
	private String classCode;
	private String className;
	private int schoolId;
	private Set<Student> student;
}
