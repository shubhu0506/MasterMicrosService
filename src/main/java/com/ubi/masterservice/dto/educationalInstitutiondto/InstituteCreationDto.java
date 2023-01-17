package com.ubi.masterservice.dto.educationalInstitutiondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstituteCreationDto {

    private String educationalInstitutionCode;

    private String educationalInstitutionName;

    private String educationalInstitutionType;

    private Long strength;

    private String state;

    private String exemptionFlag;

    private Long vvnAccount;

    private Long adminId;

    private Set<Integer> regionId;
}
