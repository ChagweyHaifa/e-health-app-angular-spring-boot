package com.backend.ehealthspringboot.enumeration;
import static com.backend.ehealthspringboot.constant.Authority.*;
public enum Role {
	
	ROLE_USER(USER_AUTHORITIES),
	ROLE_DOCTOR(DOCTOR_AUTHORITIES ),
	ROLE_ADMIN(ADMIN_AUTHORITIES)
;
	
	private String[] authorities;
	
	Role(String... authorities) {
	    this.authorities = authorities;
	}
	
	public String[] getAuthorities() {
	    return authorities;
	}

}
