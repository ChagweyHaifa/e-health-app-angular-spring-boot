package com.backend.ehealthspringboot.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.backend.ehealthspringboot.filter.JWTAccessDeniedHandler;
import com.backend.ehealthspringboot.filter.JWTAuthenticationEntryPoint;
import com.backend.ehealthspringboot.filter.JWTAuthorizationFilter;
import static com.backend.ehealthspringboot.constant.SecurityConstant.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	private JWTAuthorizationFilter jwtAuthorizationFilter;
	private JWTAccessDeniedHandler jwtAccessDeniedHandler;
	private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
    public SecurityConfiguration(JWTAuthorizationFilter jwtAuthorizationFilter,
                                 JWTAccessDeniedHandler jwtAccessDeniedHandler,
                                 JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                 @Qualifier("userDetailsService")UserDetailsService userDetailsService,
                                 BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
	
	 @Override
	 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		 auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	 }
	 
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
		 http.csrf().disable().cors().and()
	            .sessionManagement().sessionCreationPolicy(STATELESS)
	            .and().authorizeRequests().antMatchers(PUBLIC_URLS).permitAll()
	            .anyRequest().authenticated()
	            .and()
	            .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
	            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
	            .and()
	            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
	}

     @Bean
     @Override
     public AuthenticationManager authenticationManagerBean() throws Exception {
    	 return super.authenticationManagerBean();
     }

}
