package com.ubi.masterservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;
    private String username;
    private Boolean isActivate;
    private String roleType;
    private RoleDto roleDto;
    private ContactInfoDto contactInfoDto;
}
