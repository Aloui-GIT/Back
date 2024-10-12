package com.example.generateurformulaire.security.jwt;


import com.example.generateurformulaire.entities.HTTPProtocolResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationHttp403 extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        HTTPProtocolResponse httpResponse = new HTTPProtocolResponse(FORBIDDEN.value(), FORBIDDEN, FORBIDDEN.getReasonPhrase().toUpperCase(), "You need to log in to access this page");
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
