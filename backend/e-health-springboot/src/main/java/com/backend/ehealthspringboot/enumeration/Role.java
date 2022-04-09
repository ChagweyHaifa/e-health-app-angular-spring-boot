package com.backend.ehealthspringboot.enumeration;
import static com.backend.ehealthspringboot.constant.Authority.*;
public enum Role {
	
	ROLE_VISITOR(VISITOR_AUTHORITIES),
	ROLE_DOCTOR(DOCTOR_AUTHORITIES ),
	ROLE_ADMIN(ADMIN_AUTHORITIES);// ROLE_ADMIN ("user:read", "user:create", "user:update")

	
	private String[] authorities;
	
	Role(String... authorities) {
	    this.authorities = authorities;
	}
	
	public String[] getAuthorities() {
	    return authorities;
	}

}
