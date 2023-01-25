package com.ubi.masterservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {
	private Long id;
    private String username;
    @JsonProperty
    private Boolean activateStatus;
    private String roleType;
    private RoleDto roleDto;
    private ContactInfoDto contactInfoDto;
}
