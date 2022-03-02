package com.backend.ehealthspringboot.filter;

import com.backend.ehealthspringboot.domain.HttpResponse;

import org.springframework.stereotype.Component;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import java.io.IOException;
import javax.servlet.ServletException;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static com.backend.ehealthspringboot.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,AccessDeniedException exception) throws IOException, ServletException {
		HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(), UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase().toUpperCase(), ACCESS_DENIED_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
		
	}
}
