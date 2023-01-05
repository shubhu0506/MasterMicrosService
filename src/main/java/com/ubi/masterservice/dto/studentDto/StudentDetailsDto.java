package com.ubi.masterservice.dto.studentDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ubi.masterservice.dto.classDto.ClassDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StudentDetailsDto {

	private Long studentId;
	private String studentName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;

	private boolean studentStatus;
	private String category;
	private String minority;
	private String fatherName;
	private String fatherOccupation;
	private String motherName;
	private String motherOccupation;
	private String gender;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate joiningDate;

	private String status;

	private boolean verifiedByTeacher;
	private Boolean verifiedByPrincipal;

	private Boolean isActivate;
	private String currentStatus;
	
	private ClassDto classDto;

}
