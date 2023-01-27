package com.ubi.masterservice.dto.schoolDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolPaginationDto {

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
	
	private Long principalId;

	private int regionId;
		
}
