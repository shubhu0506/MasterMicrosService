package com.ubi.masterservice.dto.studentDto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ubi.masterservice.dto.classDto.ClassDto;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor
@NoArgsConstructor
//@ToString
public class StudentDetailsDto extends Auditable {

	private Long studentId;
	private String studentFirstName;
	private String studentLastName;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateOfBirth;

//	private boolean studentStatus;
	private String category;
	private String minority;
	private String fatherName;
	private String fatherOccupation;
	private String motherName;
	private String motherOccupation;
	private String gender;
	private String bloodGroup;
	private Long aadhaarNo;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date joiningDate;

//	private String status;

	private boolean verifiedByTeacher;
	private Boolean verifiedByPrincipal;

	private Boolean isActivate;
	private String currentStatus;
	private Long rollNo;
	private Boolean isPhysicallyHandicapped;
	private String uniqueId;
	private String admissionNo;
	private String nationality;
	
	private ClassDto classDto;

}
