package com.ubi.masterservice.util;

import com.ubi.masterservice.dto.user.UserPermissionsDto;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.model.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class PermissionUtil {

    public boolean hasPermission(String permissionName){
        for(GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
            if(authority.getAuthority().equals(permissionName)) {
                System.out.println(authority.getAuthority());
                return true;
            }
        }
        throw new CustomException(
                HttpStatusCode.PERMISSION_DENIED.getCode(),
                HttpStatusCode.PERMISSION_DENIED,
                HttpStatusCode.PERMISSION_DENIED.getMessage(),
                null);
    }

    public String getCurrentUsersToken(){
        UserPermissionsDto userPermissionsDto = (UserPermissionsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPermissionsDto != null) return userPermissionsDto.getJwtToken();
        return null;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(ArrayList<String> permissions) {
        Set<Authority> roles = new HashSet<>();
        for(String permission:permissions){
            roles.add(new Authority(permission));
        }
        return roles;
    }

    public String getCurrentUsersRoleType(){
        UserPermissionsDto userPermissionsDto = (UserPermissionsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPermissionsDto != null) return userPermissionsDto.getRole();
        return null;
    }

    public Long getCurrentUsersid(){
        UserPermissionsDto userPermissionsDto = (UserPermissionsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPermissionsDto != null) return userPermissionsDto.getId();
        return null;
    }
}
