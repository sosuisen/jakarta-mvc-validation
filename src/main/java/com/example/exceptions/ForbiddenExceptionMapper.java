package com.example.exceptions;

import java.net.URI;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
	@Inject
	private HttpServletRequest req;

	@Override
	public Response toResponse(ForbiddenException exception) {
		// logout
		try {
			req.logout();
			req.getSession().invalidate();
		} catch (ServletException e) {
			log.warn("Cannot logout on ForbiddenException: " + e.getMessage());
		}
		// Show the login screen.
		// Use seeOther() to redirect a request.
		// Any GET request to the forbidden URL will always be forwarded to the login screen
		// because the user is not logged in.
		return Response.seeOther(URI.create(req.getRequestURL().toString() + "?error=forbidden")).build();
	}
}
