package com.ubi.masterservice.dto.schoolDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipalDto {
	private Long userId;
	private String firstName;
	private String lastName;
	private Integer schoolId;
}
