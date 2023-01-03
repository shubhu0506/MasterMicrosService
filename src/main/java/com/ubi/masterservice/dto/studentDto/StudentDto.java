package com.ubi.masterservice.dto.studentDto;

import java.time.LocalDate;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentDto {
	@Id
	@GeneratedValue
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

	private Long classId;
}

