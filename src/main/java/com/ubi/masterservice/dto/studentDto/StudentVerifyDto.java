package com.ubi.masterservice.dto.studentDto;

import com.ubi.masterservice.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@SuperBuilder
public class StudentVerifyDto extends Auditable {
	
	@Id
	private Set<Long> studentId;

	private Long userId;


}
