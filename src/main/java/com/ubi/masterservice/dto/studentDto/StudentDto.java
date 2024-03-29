package com.ubi.masterservice.dto.studentDto;

import java.util.Date;

import javax.persistence.Id;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@SuperBuilder
public class StudentDto extends Auditable {
	@Id
	private Long studentId;
	private String studentFirstName;
	private String studentLastName;
	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
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
	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
	private Date joiningDate;

//	private String status;

	private Boolean verifiedByTeacher;
	private Boolean verifiedByPrincipal;

	private Boolean isActivate;
	private String currentStatus;
	private Long rollNo;
	private Boolean isPhysicallyHandicapped;
	private String uniqueId;
	private String admissionNo;
	private String nationality;
	private Long classId;
	private Boolean isCurrentPaymentCycleFeesPaid;
	private Long mobileNo;
	@Email
	private String email;

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Math.toIntExact(studentId);
		return result;
	}
}


