package com.backend.ehealthspringboot.constant;

public class Authority {
	
    public static final String[] VISITOR_AUTHORITIES = { "user:read" };
    public static final String[] DOCTOR_AUTHORITIES = { "user:read", "user:update" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update" };
}
