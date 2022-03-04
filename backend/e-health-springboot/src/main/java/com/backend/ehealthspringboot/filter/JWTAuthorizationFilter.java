package com.backend.ehealthspringboot.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.ehealthspringboot.utility.JWTTokenProvider;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.backend.ehealthspringboot.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter{
	
	private JWTTokenProvider jwtTokenProvider;

    public JWTAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
        } 
		else {
			String authorizationHeader = request.getHeader(AUTHORIZATION);
        	if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
        		filterChain.doFilter(request, response);
                return;
        	}
			String token = authorizationHeader.substring(TOKEN_PREFIX.length());
			String username = jwtTokenProvider.getSubject(token);
//			we alse have to verify that the user  already has an authentication is the security context holder
			if (jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } 
			else {
                SecurityContextHolder.clearContext();
            }
        }
//		let the request continue its flow 
		filterChain.doFilter(request, response);
	
		
	}

}
