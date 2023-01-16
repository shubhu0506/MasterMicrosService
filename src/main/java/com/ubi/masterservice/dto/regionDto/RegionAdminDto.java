package com.ubi.masterservice.dto.regionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionAdminDto {
    private Long userId;
    private String firstName;
    private String lastName;
}
