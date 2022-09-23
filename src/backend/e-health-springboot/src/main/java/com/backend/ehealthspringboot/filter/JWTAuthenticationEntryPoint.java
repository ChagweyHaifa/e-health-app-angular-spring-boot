package com.backend.ehealthspringboot.filter;

import com.backend.ehealthspringboot.domain.HttpResponse;

import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import java.io.IOException;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static com.backend.ehealthspringboot.constant.SecurityConstant.FORBIDDEN_MESSAGE;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JWTAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
	    HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(), FORBIDDEN, FORBIDDEN.getReasonPhrase().toUpperCase(), FORBIDDEN_MESSAGE);
	    response.setContentType(APPLICATION_JSON_VALUE);
	    response.setStatus(FORBIDDEN.value());
	    OutputStream outputStream = response.getOutputStream();
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.writeValue(outputStream, httpResponse);
	    outputStream.flush();
	}

}
