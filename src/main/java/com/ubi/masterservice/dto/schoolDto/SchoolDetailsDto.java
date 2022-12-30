package com.ubi.MasterService.dto.schoolDto;

import java.util.HashSet;
import java.util.Set;

import com.ubi.MasterService.dto.classDto.ClassDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolDetailsDto {
	private int schoolId;

	private int code;

	private String name;

	private String email;

	private long contact;

	private String address;

	private String type;

	private int strength;

	private String shift;

	private boolean exemptionFlag;

	private int vvnAccount;

	private int vvnFund;
	
	private int regionId;
	
   Set<ClassDto> classDto=new HashSet<>();
   
   private int educationalInstitutionId;
   
   
}
