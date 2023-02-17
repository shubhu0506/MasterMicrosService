package com.ubi.masterservice.dto.studentDto;

import com.ubi.masterservice.model.Auditable;
import lombok.*;

import java.util.Set;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPromoteDemoteDto extends Auditable {
    private Long userId;
    private Long classId;
    private Set<Long> studentId ;
}