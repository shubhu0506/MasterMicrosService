package com.ubi.masterservice.dto.classDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDto {
	private Long userId;
	private String firstName;
	private String lastName;
	private Long classId;
	private Integer schoolOrCollegeId;
}
