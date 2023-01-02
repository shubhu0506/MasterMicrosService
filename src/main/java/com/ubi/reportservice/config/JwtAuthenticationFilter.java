package com.ubi.reportservice.config;


import com.ubi.reportservice.dto.jwt.ValidateJwt;
import com.ubi.reportservice.dto.response.Response;
import com.ubi.reportservice.dto.user.UserPermissionsDto;
import com.ubi.reportservice.error.CustomException;
import com.ubi.reportservice.error.HttpStatusCode;
import com.ubi.reportservice.error.Result;
import com.ubi.reportservice.externalservices.UserFeignService;
import com.ubi.reportservice.util.PermissionUtil;
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
import java.util.Collection;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] PUBLIC_URLS = {
            "/v3{1}.*","/swagger-ui{1}.*","/favicon{1}.*"};

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    PermissionUtil permissionUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
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

            if (responseFromUserService != null && responseFromUserService.hasBody() && responseFromUserService.getStatusCode().is2xxSuccessful()) {
                Response<UserPermissionsDto> responseEntity = responseFromUserService.getBody();
                UserPermissionsDto userPermissionsDto = responseEntity.getResult().getData();
                Collection<? extends GrantedAuthority> permissions = permissionUtil.getAuthorities(userPermissionsDto.getPermissions());

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userPermissionsDto, null, permissions);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else if(responseFromUserService != null && responseFromUserService.hasBody() ){
                throw new CustomException(
                        responseFromUserService.getBody().getStatusCode(),
                        HttpStatusCode.UNAUTHORIZED_EXCEPTION,
                        responseFromUserService.getBody().getMessage(),
                        new Result<>());
            }
            else{
                throw new CustomException(
                        HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                        HttpStatusCode.INTERNAL_SERVER_ERROR,
                        HttpStatusCode.INTERNAL_SERVER_ERROR.getMessage(),
                        new Result<>());
            }
        }

        filterChain.doFilter(request, response);
    }
}



