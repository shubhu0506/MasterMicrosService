package com.ubi.masterservice.dto.schoolDto;

import java.util.Set;

import javax.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolCreationDto {

	private int code;

	private String name;

	@Email
	private String email;

	private long contact;

	private String address;

	private String type;

	private int strength;

	private String shift;

	private Boolean isCollege;
	
	private boolean exemptionFlag;

	private int vvnAccount;

	private int vvnFund;

	private String feesCollectionType;
	
	private int feesCollectionPeriod;
	
	private Long principalId;

	private Integer regionId;

	private Set<Long> classId;

	private int educationalInstitutionId;
		
}
