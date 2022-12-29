package com.ubi.MasterService.config;


import com.ubi.MasterService.dto.jwt.ValidateJwt;
import com.ubi.MasterService.dto.response.Response;
import com.ubi.MasterService.dto.user.UserPermissionsDto;
import com.ubi.MasterService.error.CustomException;
import com.ubi.MasterService.error.HttpStatusCode;
import com.ubi.MasterService.error.Result;
import com.ubi.MasterService.externalServices.UserFeignService;
import com.ubi.MasterService.util.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] PUBLIC_URLS = {"/swagger-ui/index.html",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/favicon-32x32.png",
            "/v3/api-docs"};

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    PermissionUtil permissionUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        String currentUri = request.getRequestURI();

        boolean isSwaggerUrl = false;
        for(String publicUri:PUBLIC_URLS){
            if(Pattern.matches(publicUri, currentUri)) {
                isSwaggerUrl = true;
                break;
            }
        }

        if(!isSwaggerUrl){
            if (requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")){
                throw new CustomException(
                        HttpStatusCode.TOKEN_FORMAT_INVALID.getCode(),
                        HttpStatusCode.TOKEN_FORMAT_INVALID,
                        HttpStatusCode.TOKEN_FORMAT_INVALID.getMessage(),
                        new Result<>());
            }

            ValidateJwt validateJwt = new ValidateJwt(requestTokenHeader);
            ResponseEntity<Response<UserPermissionsDto>> responseFromUserService = userFeignService.validateTokenAndGetUser(validateJwt);

            Response<UserPermissionsDto> responseEntity = responseFromUserService.getBody();
            if (responseFromUserService.getStatusCode().is2xxSuccessful()) {
                UserPermissionsDto userPermissionsDto = responseEntity.getResult().getData();
                Collection<? extends GrantedAuthority> permissions = permissionUtil.getAuthorities(userPermissionsDto.getPermissions());

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userPermissionsDto, null, permissions);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else{
                throw new CustomException(
                        responseEntity.getStatusCode(),
                        HttpStatusCode.UNAUTHORIZED_EXCEPTION,
                        responseEntity.getMessage(),
                        new Result<>());
            }
        }

        filterChain.doFilter(request, response);
    }
}



