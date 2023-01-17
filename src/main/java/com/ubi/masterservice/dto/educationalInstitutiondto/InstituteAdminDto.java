package com.ubi.masterservice.dto.educationalInstitutiondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstituteAdminDto {
    private Long userId;
    private String firstName;
    private String lastName;
}
