package com.ubi.masterservice.dto.user;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
	
	Long id;
    String roleName;
    String roleType;
    Set<String> permissions;

}
