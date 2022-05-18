package com.backend.ehealthspringboot.constant;

public class Authority {
	
    public static final String[] USER_AUTHORITIES = {"user:update"};
    public static final String[] DOCTOR_AUTHORITIES = {"doctor:update","questionResponse:create","questionResponse:update","questionResponse:delete" };
    public static final String[] ADMIN_AUTHORITIES = {"user:update","doctor:update","questionResponse:update","questionResponse:delete"};
}
