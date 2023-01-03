package com.ubi.masterservice.dto.educationalInstitutiondto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EducationalInstitutionDto {

	private int id;

	private String educationalInstitutionCode;

	private String educationalInstitutionName;

	private String educationalInstitutionType;

	private Long strength;

	private String state;

	private String exemptionFlag;

	private Long vvnAccount;

	private Set<Integer> regionId;


}
