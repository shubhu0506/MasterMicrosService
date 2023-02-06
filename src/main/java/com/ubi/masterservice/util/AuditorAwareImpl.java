package com.ubi.masterservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Autowired
    PermissionUtil permissionUtil;
    @Override
    public Optional<Long> getCurrentAuditor() {
        if(permissionUtil.getCurrentUsersid() != null) return Optional.of(permissionUtil.getCurrentUsersid());
        return Optional.of(0L);
    }

}