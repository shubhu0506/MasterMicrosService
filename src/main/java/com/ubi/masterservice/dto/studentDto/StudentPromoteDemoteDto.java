package com.ubi.masterservice.dto.studentDto;

import lombok.*;

import java.util.Set;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPromoteDemoteDto {
    private Long userId;
    private Long classId;
    private Set<Long> studentId ;
}