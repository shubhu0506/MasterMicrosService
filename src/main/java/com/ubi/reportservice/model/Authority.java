package com.ubi.reportservice.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
public class Authority implements GrantedAuthority {

    String authorityName;

    @Override
    public String getAuthority() {
        return this.authorityName;
    }
}
