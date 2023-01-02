package com.ubi.reportservice.util;

import com.ubi.reportservice.dto.user.UserPermissionsDto;
import com.ubi.reportservice.error.CustomException;
import com.ubi.reportservice.error.HttpStatusCode;
import com.ubi.reportservice.model.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionUtil {

    public boolean hasPermission(String permissionName){
        for(GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
            if(authority.getAuthority().equals(permissionName)) {
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

    public Collection<Authority> getAuthorities(List<String> permissions) {
        Set<Authority> roles = new HashSet<>();
        for(String permission:permissions){
            roles.add(new Authority(permission));
        }
        return roles;
    }
}
