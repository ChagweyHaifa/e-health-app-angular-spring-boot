package com.backend.ehealthspringboot.constant;

public class Authority {
	
    public static final String[] VISITOR_AUTHORITIES = { "review:read","review:create","review:delete"};
    public static final String[] DOCTOR_AUTHORITIES = { "review:read","doctor:update" };
    public static final String[] ADMIN_AUTHORITIES = {"review:read","review:create","review:delete"};
}
