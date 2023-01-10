package com.ubi.masterservice.entity;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
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
public class Student {

	@Id
	@GeneratedValue
	private Long studentId;
	private String studentName;
	private Long lastVerifiedByTeacher;
	private Long lastVerifiedByPrincipal;

	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
	@JsonSerialize(using = ToStringSerializer.class)
//	@JsonDeserialize(using = LocalDateDeserializer.class)
	private Date dateOfBirth;

	private boolean studentStatus;
	private String category;
	private String minority;
	private String fatherName;
	private String fatherOccupation;
	private String motherName;
	private String motherOccupation;
	private String gender;
	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
//	@JsonDeserialize(using = LocalDateDeserializer.class)
	private Date joiningDate;
	private String status;

	private Boolean verifiedByTeacher;
	private Boolean verifiedByPrincipal;

	private Boolean isActivate;
	private String currentStatus;

	@ManyToOne(fetch = FetchType.LAZY )
	private ClassDetail classDetail;



}
