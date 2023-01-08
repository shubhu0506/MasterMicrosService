package com.ubi.masterservice.dto.schoolDto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolDto {

	private int schoolId;

	private int code;

	private String name;

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

	private int regionId;

	private Set<Long> classId;

	private int educationalInstitutionId;

}
