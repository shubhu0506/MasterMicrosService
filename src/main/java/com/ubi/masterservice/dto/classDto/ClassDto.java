package com.ubi.masterservice.dto.classDto;

import com.ubi.masterservice.model.Auditable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto extends Auditable
{
	private Long classId;
	private String classCode;
	private String className;
	private String section;
	private String stream;
	private int schoolId;
	private Long teacherId;
}
