package com.ubi.masterservice.dto.schoolDto;

import java.util.HashSet;
import java.util.Set;

import com.ubi.masterservice.dto.classDto.ClassDto;
import com.ubi.masterservice.dto.educationalInstitutiondto.EducationalInstitutionDto;
import com.ubi.masterservice.dto.regionDto.RegionGet;

public class GetSchoolDetails {

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

	private RegionGet regionGet;

	private PrincipalDto principalDto;

	Set<ClassDto> classDto = new HashSet<>();

	private EducationalInstitutionDto educationalInstitutionDto;

}
