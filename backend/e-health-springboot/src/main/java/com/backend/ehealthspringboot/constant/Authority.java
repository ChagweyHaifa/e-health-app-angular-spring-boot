package com.backend.ehealthspringboot.constant;

public class Authority {
	
    public static final String[] USER_AUTHORITIES = {"user:update"};
    public static final String[] DOCTOR_AUTHORITIES = {"doctor:update","questionResponse:create","questionResponse:update","questionResponse:delete" };
    public static final String[] ADMIN_AUTHORITIES = {"doctor:read","user:read","user:update","doctor:update","questionResponse:delete"};
//    "doctor:read" => get all doctors
//    "user:read" => get users by role
}
