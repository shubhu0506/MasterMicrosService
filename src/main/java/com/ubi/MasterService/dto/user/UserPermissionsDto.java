package com.ubi.MasterService.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Data @RequiredArgsConstructor
@AllArgsConstructor
public class UserPermissionsDto {
    Long id;
    String username;
    Boolean isEnable;
    String role;
    ArrayList<String> permissions;
}
