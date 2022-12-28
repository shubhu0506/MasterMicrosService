package com.ubi.MasterService.util;

import com.ubi.MasterService.error.CustomException;
import com.ubi.MasterService.error.HttpStatusCode;
import com.ubi.MasterService.model.Authority;
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

    public Collection<? extends GrantedAuthority> getAuthorities(ArrayList<String> permissions) {
        Set<Authority> roles = new HashSet<>();
        for(String permission:permissions){
            roles.add(new Authority(permission));
        }
        return roles;
    }
}
