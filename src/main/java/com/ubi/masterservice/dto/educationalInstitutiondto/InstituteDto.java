package com.ubi.masterservice.dto.educationalInstitutiondto;

import com.ubi.masterservice.dto.regionDto.RegionGet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstituteDto {
    private int id;

    private String educationalInstitutionCode;

    private String educationalInstitutionName;

    private String educationalInstitutionType;

    private Long strength;

    private String state;

    private String exemptionFlag;

    private Long vvnAccount;

    private InstituteAdminDto instituteAdminDto;

    private Set<RegionGet> regionDto;
}
