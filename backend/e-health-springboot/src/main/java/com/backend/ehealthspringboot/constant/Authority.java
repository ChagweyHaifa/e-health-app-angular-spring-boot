package com.backend.ehealthspringboot.constant;

public class Authority {
	
    public static final String[] VISITOR_AUTHORITIES = { "rating:read","rating:create","rating:delete","rating:update"};
    public static final String[] DOCTOR_AUTHORITIES = { "rating:read","doctor:update" };
    public static final String[] ADMIN_AUTHORITIES = {"rating:read","rating:create","rating:delete"};
}
