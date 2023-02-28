package com.ubi.masterservice.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Student extends Auditable {

	@Id
	@GeneratedValue
	private Long studentId;
	private String studentFirstName;
	private String studentLastName;
	private Long lastVerifiedByTeacher;
	private Long lastVerifiedByPrincipal;

	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
	@JsonSerialize(using = ToStringSerializer.class)
	@Temporal(TemporalType.DATE)
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
	@Temporal(TemporalType.DATE)
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

	@ManyToOne(fetch = FetchType.LAZY )
	private ClassDetail classDetail;


}