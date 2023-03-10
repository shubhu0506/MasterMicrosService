package com.ubi.masterservice.config;


import com.ubi.masterservice.dto.jwt.ValidateJwt;
import com.ubi.masterservice.dto.response.Response;
import com.ubi.masterservice.dto.user.UserPermissionsDto;
import com.ubi.masterservice.error.CustomException;
import com.ubi.masterservice.error.HttpStatusCode;
import com.ubi.masterservice.error.Result;
import com.ubi.masterservice.externalServices.UserFeignService;
import com.ubi.masterservice.util.PermissionUtil;
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

            Response<UserPermissionsDto> responseEntity = new Response<>();
            if (responseFromUserService.getStatusCode().is2xxSuccessful()) {
                responseEntity = responseFromUserService.getBody();
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